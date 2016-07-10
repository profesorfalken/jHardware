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
package org.jutils.jhardware.info.network.unix;

import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.network.AbstractNetworkInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to Network
 *
 * @author Javier Garcia Alonso
 */
public final class UnixNetworkInfo extends AbstractNetworkInfo {        

    private static String getNetworkData() {
        return HardwareInfoUtils.executeCommand("ifconfig", "-a");
    }

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> networkDataMap = new HashMap<>();

        String networkData = getNetworkData();
        
        if (networkData != null) {
            String[] dataStringLines = networkData.split("\\r?\\n");

            int count = 0;
            for (final String dataLine : dataStringLines) {
                if (!dataLine.startsWith(" ")) {
                    count++;
                    networkDataMap.put("interface_" + count, HardwareInfoUtils.extractText(dataLine, "([^\\s]+)"));
                    networkDataMap.put("type_" + count, HardwareInfoUtils.extractText(dataLine, "Link encap:(.+?)  "));
                } else {
                    updateNetworkData(networkDataMap, count, dataLine);
                }
            }
            networkDataMap.put("interfacesLength", String.valueOf(count));
        }

        return networkDataMap;
    }

    private static void updateNetworkData(Map<String, String> networkDataMap, int count, String dataLine) {
        String lineType = HardwareInfoUtils.extractText(dataLine, "([^\\s]+)");
        if (null != lineType) {
            switch (lineType) {
                case "inet":
                    networkDataMap.put("ipv4_" + count, HardwareInfoUtils.extractText(dataLine, "addr:(.+?) "));
                    break;
                case "inet6":
                    networkDataMap.put("ipv6_" + count, HardwareInfoUtils.extractText(dataLine, "addr:(.+?) "));
                    break;
                case "RX":
                    if (dataLine.trim().startsWith("RX packets")) {
                        networkDataMap.put("received_packets_" + count, HardwareInfoUtils.extractText(dataLine, "packets:(.+?) "));
                    } else {
                        networkDataMap.put("received_bytes_" + count, HardwareInfoUtils.extractText(dataLine, "RX bytes:(.+?) "));
                        networkDataMap.put("transmitted_bytes_" + count, HardwareInfoUtils.extractText(dataLine, "TX bytes:(.+?) "));
                    }
                    break;
                case "TX":
                    networkDataMap.put("transmitted_packets_" + count, HardwareInfoUtils.extractText(dataLine, "packets:(.+?) "));
                    break;
                default:
                    break;
            }
        }
    }

    
}
