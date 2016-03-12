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
 * Information related to Network
 *
 * @author Javier Garcia Alonso
 */
public final class WindowsNetworkInfo extends AbstractNetworkInfo {

    private String getNetworkData() {
        return HardwareInfoUtils.executeCommand("ipconfig", "/all");
    }
    
    private String getNetstatData() {
        return HardwareInfoUtils.executeCommand("netstat", "-e");
    }

    protected Map<String, String> parseInfo() {
        Map<String, String> networkDataMap = new HashMap<String, String>();
        
        String receivedBytes = null;
        String transmittedBytes = null;
        String receivedPackets = null;
        String transmittedPackets = null;
        String netstatData = getNetstatData();
        String[] dataStringLines = netstatData.split("\\r?\\n");
        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("Bytes")) {
                String[] infos = dataLine.split("\\s+");
                receivedBytes = infos[1];
                transmittedBytes = infos[2];
            } else if (dataLine.startsWith("Unicast packets")) {
                String[] infos = dataLine.split("\\s+");
                receivedPackets = infos[2];
                transmittedPackets = infos[3];
            }
        }

        String networkData = getNetworkData();
        dataStringLines = networkData.split("\\r?\\n");

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
                
                if (dataLine.contains("Link-local IPv6 Address")) {
                    networkDataMap.put("ipv6_" + count, dataLine.split(":")[1]);
                }
                
                if (dataLine.contains("Default Gateway")) {
                    if (!dataLine.split(":")[1].isEmpty()) {
                        networkDataMap.put("received_packets_" + count, receivedPackets);
                        networkDataMap.put("transmitted_packets_" + count, transmittedPackets);
                        networkDataMap.put("received_bytes_" + count, receivedBytes);
                        networkDataMap.put("transmitted_bytes_" + count, transmittedBytes);
                    }
                }
            }
        }

        networkDataMap.put("interfacesLength", String.valueOf(count));

        return networkDataMap;
    }
}
