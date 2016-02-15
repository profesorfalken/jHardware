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
package org.jutils.jhardware.info.os.unix;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jutils.jhardware.info.motherboard.AbstractMotherboardInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to CPU
 *
 * @author Javier Garcia Alonso
 */
public final class UnixOsInfo extends AbstractMotherboardInfo {

    private final static String OS_RELEASE = "/etc/os-release";

    private String getOSLsbReleaseData() {
        String fullData = "";

        fullData += HardwareInfoUtils.executeCommand("lsb_release", "-a");

        return fullData;
    }

    private String getOSRunningTimeData() {
        String fullData = "";

        fullData += HardwareInfoUtils.executeCommand("last", "-x");

        return fullData;
    }

    private String getOSReleaseData() {
        Stream<String> streamProcessorInfo = HardwareInfoUtils.readFile(OS_RELEASE);
        final StringBuilder buffer = new StringBuilder();

        streamProcessorInfo.forEach(new Consumer<String>() {
            public void accept(String line) {
                buffer.append(line).append("\r\n");
            }
        });

        return buffer.toString();
    }

    protected Map<String, String> parseInfo() {
        Map<String, String> motherboardDataMap = new HashMap<String, String>();
      
        return motherboardDataMap;
    }
}
