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
import java.util.stream.Stream;

/**
 *
 * @author Javier Garcia Alonso
 */
public class HardwareInfoUtils {

    private static final String CRLF = "\r\n";

    //Hide constructor
    private HardwareInfoUtils() {

    }

    public static Stream<String> readFile(String filePath) {
        Path path = Paths.get(filePath);

        Stream<String> fileLines = null;
        try {
            fileLines = Files.lines(path);
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fileLines;
    }

    public static String executeCommand(String... command) {
        StringBuilder commandOutput = new StringBuilder();        

        try {
            Process process = Runtime.getRuntime().exec(command);

            readData(process, commandOutput);
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return commandOutput.toString();
    }

    private static void readData(Process process, StringBuilder commandOutput) throws IOException {
        BufferedReader processOutput = null;
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (process.exitValue() == 0) {
                processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } else {
                processOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }

            String line;
            while ((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandOutput.append(line).append(CRLF);
                }
            }
        } finally {
             if (processOutput != null) {
                try {
                    processOutput.close();
                } catch (IOException ex) {
                    Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
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
}
