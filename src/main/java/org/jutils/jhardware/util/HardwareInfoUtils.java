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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 * @author Javier Garcia Alonso
 */
public class HardwareInfoUtils {

    private static final String CRLF = "\r\n";

    private static final String NOT_FOUND = "NOT_FOUND";

    private HardwareInfoUtils() {
        //Hide constructor
    }

    public static Stream<String> readFile(String filePath) {
        Path path = Paths.get(filePath);

        try {
            return Files.lines(path);
        } catch (IOException ioEx) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ioEx);
            return Stream.empty();
        }
    }

    public static String getSingleValueFromFile(String filePath) {
        try (Stream<String> streamProcessorInfo = readFile(filePath)) {
            return streamProcessorInfo.findFirst().get();
        }
    }

    public static String executeCommand(String... command) {
        String commandOutput = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // redirect error stream to output stream

            commandOutput = readData(processBuilder.start());
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return commandOutput;
    }

    private static String readData(Process process) {
        StringBuilder commandOutput = new StringBuilder();
        BufferedReader processOutput = null;

        try {
             processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandOutput.append(line).append(CRLF);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (processOutput != null) {
                    processOutput.close();
                }
            } catch (IOException ioe) {
                Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ioe);
            }
        }

        return commandOutput.toString();
    }

    public static boolean isSudo() {
        return executeCommand("sudo", "-n", "true").length() == 0;
    }

    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase()
                + s.substring(1).toLowerCase();
    }

    public static String removeAllSpaces(String s) {
        return s.replaceAll("\\s+", "");
    }

    public static String extractText(String text, String regex) {
        if (text.trim().isEmpty()) {
            return NOT_FOUND;
        }

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);

        matcher.find();
        if (matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return NOT_FOUND;
    }
}
