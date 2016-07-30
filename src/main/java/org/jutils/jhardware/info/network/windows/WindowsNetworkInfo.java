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

    private static String getNetworkData() {
        return HardwareInfoUtils.executeCommand("ipconfig", "/all");
    }

    private static String getNetstatData() {
        return HardwareInfoUtils.executeCommand("netstat", "-e");
    }

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> networkDataMap = new HashMap<>();

        String receivedBytes = null;
        String transmittedBytes = null;
        String receivedPackets = null;
        String transmittedPackets = null;
        String netstatData = getNetstatData();
        String[] dataStringLines = netstatData.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("Bytes") || dataLine.startsWith("Octets")) {
                String[] infos = dataLine.split("\\s+");
                receivedBytes = infos[infos.length-2];
                transmittedBytes = infos[infos.length-1];
            } else if (dataLine.startsWith("Unicast") || dataLine.contains("monodiffusion")) {
                String[] infos = dataLine.split("\\s+");
                receivedPackets = infos[infos.length-2];
                transmittedPackets = infos[infos.length-1];
            }
        }

        String networkData = getNetworkData();
        dataStringLines = networkData.split("\\r?\\n");

        boolean reading = false;
        int count = 0;
        for (final String dataLine : dataStringLines) {
            if (!dataLine.trim().isEmpty() && !dataLine.startsWith(" ") && !dataLine.startsWith("\t")) {
                reading = false;
                if (!multiContains(dataLine, "Windows", "IP")) {
                    count++;
                    reading = true;
                    networkDataMap.put("interface_" + count, dataLine);
                }
                continue;
            }

            if (reading) {
                if (dataLine.contains("IP Address") || dataLine.contains("IPv4")) {
                    networkDataMap.put("ipv4_" + count, getValueFromDataLine(dataLine));
                }

                if (multiContains(dataLine, "IPv6", "Address") || multiContains(dataLine, "IPv6", "Adresse")) {
                    networkDataMap.put("ipv6_" + count, getValueFromDataLine(dataLine));
                }

                if (dataLine.toLowerCase().contains("gateway") || dataLine.toLowerCase().contains("passerelle")) {
                    if (getValueFromDataLine(dataLine) != null) {
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

    private static String getValueFromDataLine(String dataLine) {
        String[] data = dataLine.split(":", 2);

        if (data.length > 1 && !data[1].isEmpty()) {
            return data[1];
        }
        return null;
    }

    private static boolean multiContains(String baseString, String... args) {
        if (baseString == null || baseString.isEmpty()) {
            return false;
        }
        for (final String arg : args) {
            if (!baseString.contains(arg)) {
                return false;
            }
        }

        return true;
    }
}
