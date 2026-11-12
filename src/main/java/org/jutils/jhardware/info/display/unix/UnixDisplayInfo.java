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
package org.jutils.jhardware.info.display.unix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.display.AbstractDisplayInfo;
import org.jutils.jhardware.util.CommandResult;
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.SystemCommandRunner;

/**
 * Display information for Linux.
 *
 * <p>{@code xrandr} is preferred because it provides refresh rates. When it is
 * not installed (common on Wayland-only systems) the DRM sysfs connectors are
 * used, so the call returns a snapshot instead of throwing a
 * {@code NullPointerException}.</p>
 */
public final class UnixDisplayInfo extends AbstractDisplayInfo {

    private static final Path DEFAULT_DRM_ROOT = Paths.get("/sys/class/drm");
    private static final Pattern MODE_PATTERN = Pattern.compile(
            "^\\s+(\\d{3,5}x\\d{3,5})[iI]?\\s+([0-9.]+)([*+]*).*$");
    private static final Pattern CONNECTED_MODE_PATTERN = Pattern.compile(
            "(\\d{3,5}x\\d{3,5})\\+\\d+\\+\\d+");
    private static final Pattern SCREEN_CURRENT_PATTERN = Pattern.compile(
            "current\\s+(\\d+\\s*x\\s*\\d+)");

    private final CommandRunner runner;
    private final Path drmRoot;

    public UnixDisplayInfo() {
        this(SystemCommandRunner.defaultRunner(), DEFAULT_DRM_ROOT);
    }

    public UnixDisplayInfo(CommandRunner runner) {
        this(runner, DEFAULT_DRM_ROOT);
    }

    UnixDisplayInfo(CommandRunner runner, Path drmRoot) {
        this.runner = runner == null ? SystemCommandRunner.defaultRunner() : runner;
        this.drmRoot = drmRoot == null ? DEFAULT_DRM_ROOT : drmRoot;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = ParsedData.empty();
        CommandResult result = runner.run("xrandr", "-q");
        if (result.isSuccess() && !result.stdout().trim().isEmpty()) {
            ParsedData parsed = parseXrandr(result.stdout());
            if (parsed.getInt("numOfDisplays", 0) > 0) {
                return parsed;
            }
            data.warn("xrandr did not report any connected display");
        } else {
            data.warn("xrandr is not available: " + firstLine(result.stderr()));
        }
        data.merge(parseDrm(drmRoot));
        return data;
    }

    /**
     * Parses {@code xrandr -q} output.
     *
     * @param output command output
     * @return parsed data
     */
    static ParsedData parseXrandr(String output) {
        ParsedData data = ParsedData.empty();
        List<List<String>> modes = new ArrayList<>();
        String screenResolution = null;
        int count = -1;

        for (String raw : output.split("\\R")) {
            if (raw.trim().isEmpty()) {
                continue;
            }
            if (raw.startsWith("Screen")) {
                Matcher screenMatcher = SCREEN_CURRENT_PATTERN.matcher(raw);
                if (screenMatcher.find()) {
                    screenResolution = screenMatcher.group(1).replaceAll("\\s+", "");
                }
                continue;
            }
            if (!Character.isWhitespace(raw.charAt(0))) {
                if (!raw.contains(" connected") || raw.contains(" disconnected")) {
                    continue;
                }
                String trimmed = raw.trim();
                String name = trimmed.split("\\s+")[0];
                count++;
                data.put("name_" + count, name);
                String current = HardwareInfoUtils
                        .extract(trimmed, "(\\d{3,5}x\\d{3,5})\\+\\d+\\+\\d+")
                        .orElse(null);
                if (current != null) {
                    data.put("current_res_" + count, current);
                }
                modes.add(new ArrayList<>());
                continue;
            }
            if (count < 0) {
                continue;
            }
            Matcher modeMatcher = MODE_PATTERN.matcher(raw);
            if (!modeMatcher.matches()) {
                continue;
            }
            String resolution = modeMatcher.group(1);
            String refresh = modeMatcher.group(2);
            String flags = modeMatcher.group(3) == null ? "" : modeMatcher.group(3);
            modes.get(count).add(resolution + "@" + refresh);
            if (flags.contains("*")) {
                data.put("current_res_" + count, resolution);
                data.put("current_refresh_rate_" + count, refresh);
            }
        }

        if (count < 0) {
            if (screenResolution != null) {
                data.put("name_0", "Screen");
                data.put("current_res_0", screenResolution);
                count = 0;
            } else {
                return data.warn("xrandr reported no connected outputs");
            }
        }

        for (int i = 0; i <= count; i++) {
            if (i >= modes.size()) {
                continue;
            }
            List<String> available = modes.get(i);
            if (!available.isEmpty()) {
                data.put("available_res_" + i, String.join(";", available));
            }
            if (data.get("current_res_" + i) == null && !available.isEmpty()) {
                String first = available.get(0);
                int at = first.indexOf('@');
                data.put("current_res_" + i, at < 0 ? first : first.substring(0, at));
            }
        }
        if (data.get("current_res_0") == null && screenResolution != null) {
            data.put("current_res_0", screenResolution);
        }
        data.put("numOfDisplays", String.valueOf(count + 1));
        return data;
    }

    /**
     * Parses connected DRM connectors.
     *
     * @param drmRoot usually {@code /sys/class/drm}
     * @return parsed data
     */
    static ParsedData parseDrm(Path drmRoot) {
        ParsedData data = ParsedData.empty();
        if (drmRoot == null || !Files.isDirectory(drmRoot)) {
            return data.warn("The DRM sysfs directory is not available");
        }
        List<Path> connectors = new ArrayList<>();
        try (java.util.stream.Stream<Path> paths = Files.list(drmRoot)) {
            paths.filter(Files::isDirectory)
                    .filter(path -> HardwareInfoUtils.fileName(path).matches("card\\d+-.+"))
                    .sorted()
                    .forEach(connectors::add);
        } catch (IOException | SecurityException ex) {
            return data.warn("Could not list DRM connectors: " + ex.getMessage());
        }

        int count = 0;
        for (Path connector : connectors) {
            String status = HardwareInfoUtils.readValue(connector.resolve("status"))
                    .orElse("");
            if (!"connected".equalsIgnoreCase(status)) {
                continue;
            }
            String name = HardwareInfoUtils.fileName(connector)
                    .replaceFirst("^card\\d+-", "");
            List<String> modes = HardwareInfoUtils
                    .readLines(connector.resolve("modes").toString());
            data.put("name_" + count, name);
            if (!modes.isEmpty()) {
                data.put("current_res_" + count, modes.get(0).trim());
                java.util.LinkedHashSet<String> available = new java.util.LinkedHashSet<>();
                for (String mode : modes) {
                    if (!mode.trim().isEmpty()) {
                        available.add(mode.trim());
                    }
                }
                data.put("available_res_" + count, String.join(";", available));
            }
            count++;
        }
        if (count == 0) {
            data.warn("No connected display was found in DRM sysfs");
        }
        data.put("numOfDisplays", String.valueOf(count));
        return data;
    }

    private static String firstLine(String value) {
        int newline = value.indexOf('\n');
        return newline < 0 ? value : value.substring(0, newline);
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
