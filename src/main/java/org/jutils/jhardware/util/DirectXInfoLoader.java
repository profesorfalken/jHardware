/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jhardware.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Loads display information from {@code dxdiag} on Windows.
 *
 * <p>The loader is lazy, caches the first result and never blocks forever:
 * the command has a timeout and the output file is polled with a hard
 * deadline. XML parsing disables DTDs and external entities.</p>
 */
public enum DirectXInfoLoader {

    /** Shared lazy loader. */
    get;

    private static final Duration FILE_WAIT = Duration.ofSeconds(30);
    private static final long POLL_MILLIS = 200L;

    private final Object lock = new Object();
    private volatile Result cached;

    /**
     * Result of a dxdiag load.
     */
    public static final class Result {

        private final List<Map<String, String>> displays;
        private final List<String> warnings;

        /**
         * @param displays one map per {@code DisplayDevice}
         * @param warnings non-fatal problems
         */
        public Result(List<Map<String, String>> displays, List<String> warnings) {
            this.displays = displays == null
                    ? java.util.Collections.<Map<String, String>>emptyList()
                    : java.util.Collections.unmodifiableList(
                            new ArrayList<>(displays));
            this.warnings = warnings == null
                    ? java.util.Collections.<String>emptyList()
                    : java.util.Collections.unmodifiableList(
                            new ArrayList<>(warnings));
        }

        public List<Map<String, String>> displays() {
            return java.util.Collections.unmodifiableList(
                    new ArrayList<>(displays));
        }

        public List<String> warnings() {
            return java.util.Collections.unmodifiableList(
                    new ArrayList<>(warnings));
        }
    }

    /**
     * @return display payload using the default command runner
     */
    public List<Map<String, String>> getDisplayInfo() {
        return getResult(SystemCommandRunner.defaultRunner()).displays();
    }

    /**
     * @param runner command runner used to invoke {@code dxdiag}
     * @return display payload
     */
    public List<Map<String, String>> getDisplayInfo(CommandRunner runner) {
        return getResult(runner).displays();
    }

    /**
     * @param runner command runner used to invoke {@code dxdiag}
     * @return cached result, loading it on first use
     */
    public Result getResult(CommandRunner runner) {
        Result local = cached;
        if (local != null) {
            return local;
        }
        synchronized (lock) {
            if (cached == null) {
                cached = load(runner == null ? SystemCommandRunner.defaultRunner() : runner);
            }
            return cached;
        }
    }

    /**
     * Clears the cache. Useful for tests and for long-running tools that want
     * to refresh display data.
     */
    public void clearCache() {
        synchronized (lock) {
            cached = null;
        }
    }

    private Result load(CommandRunner runner) {
        List<String> warnings = new ArrayList<>();
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("jhardware-dxdiag-", ".xml");
            CommandResult result = runner.run("dxdiag", "/x",
                    tempFile.toAbsolutePath().toString());
            if (result.timedOut()) {
                warnings.add("dxdiag timed out after " + result.duration().toMillis() + " ms");
                return new Result(Arrays.asList(), warnings);
            }
            if (!result.isSuccess()) {
                warnings.add("dxdiag failed with exit code " + result.exitCode()
                        + (result.stderr().trim().isEmpty() ? "" : ": " + firstLine(result.stderr())));
                return new Result(Arrays.asList(), warnings);
            }
            if (!waitForFile(tempFile)) {
                warnings.add("dxdiag did not produce an XML report");
                return new Result(Arrays.asList(), warnings);
            }
            Result parsed = parse(tempFile);
            warnings.addAll(parsed.warnings());
            return new Result(parsed.displays(), warnings);
        } catch (IOException ex) {
            warnings.add("Could not create dxdiag output file: " + ex.getMessage());
            return new Result(Arrays.asList(), warnings);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // Best-effort cleanup.
                }
            }
        }
    }

    private static boolean waitForFile(Path file) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(FILE_WAIT.toMillis());
        while (System.nanoTime() < deadline) {
            try {
                if (Files.size(file) > 0) {
                    return true;
                }
            } catch (IOException ex) {
                return false;
            }
            try {
                Thread.sleep(POLL_MILLIS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * Parses a dxdiag XML report.
     *
     * @param file XML file
     * @return parsed result
     */
    static Result parse(Path file) {
        List<String> warnings = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            harden(factory);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.toFile());
            document.getDocumentElement().normalize();

            NodeList roots = document.getElementsByTagName("DisplayDevices");
            if (roots.getLength() == 0) {
                warnings.add("dxdiag report has no DisplayDevices section");
                return new Result(Arrays.asList(), warnings);
            }
            Node root = roots.item(0);
            List<Map<String, String>> displays = new ArrayList<>();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE
                        || !"DisplayDevice".equals(node.getNodeName())) {
                    continue;
                }
                Map<String, String> display = new LinkedHashMap<>();
                NodeList values = node.getChildNodes();
                for (int j = 0; j < values.getLength(); j++) {
                    Node value = values.item(j);
                    if (value.getNodeType() == Node.ELEMENT_NODE) {
                        display.put(value.getNodeName(),
                                value.getTextContent() == null
                                        ? "" : value.getTextContent().trim());
                    }
                }
                displays.add(display);
            }
            return new Result(displays, warnings);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            warnings.add("Could not parse dxdiag report: " + ex.getMessage());
            return new Result(Arrays.asList(), warnings);
        }
    }

    private static void harden(DocumentBuilderFactory factory) {
        trySetFeature(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
        trySetFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        trySetFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
    }

    private static void trySetFeature(DocumentBuilderFactory factory, String feature,
            boolean value) {
        try {
            factory.setFeature(feature, value);
        } catch (ParserConfigurationException ignored) {
            // The parser does not support this feature; not fatal.
        }
    }

    private static String firstLine(String value) {
        int newline = value.indexOf('\n');
        return newline < 0 ? value : value.substring(0, newline);
    }
}
