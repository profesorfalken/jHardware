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
package org.jutils.jhardware.info.network.windows;

import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.network.AbstractNetworkInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to CPU
 *
 * @author Javier Garcia Alonso
 */
public final class WindowsNetworkInfo extends AbstractNetworkInfo {

    private String getNetworkData() {
        String networkData = HardwareInfoUtils.executeCommand("ipconfig", "/all");

        return networkData;
    }

    protected Map<String, String> parseInfo() {
        Map<String, String> networkDataMap = new HashMap<String, String>();

        String lsbRelease = getNetworkData();
        String[] dataStringLines = lsbRelease.split("\\r?\\n");

        boolean reading = false;
        int count = 0;
        for (final String dataLine : dataStringLines) {
            if (!dataLine.startsWith(" ")) {
                reading = false;
                if (!dataLine.contains("Windows IP Configuration")) {
                    count++;
                    reading = true;
                    networkDataMap.put("interface_" + count, dataLine);
                }
                continue;
            }

            if (reading) {
                if (dataLine.contains("IP Address")) {
                    networkDataMap.put("ipv4_" + count, dataLine.split(":")[1]);
                }
            }
        }

        networkDataMap.put("interfacesLength", String.valueOf(count));

        return networkDataMap;
    }
}
