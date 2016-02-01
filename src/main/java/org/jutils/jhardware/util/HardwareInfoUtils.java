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
            BufferedReader processOutput
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandOutput.append(line).append(CRLF);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return commandOutput.toString();
    }
}
