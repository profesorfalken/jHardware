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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jutils.jhardware.info.network.AbstractNetworkInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to Network
 *
 * @author Javier Garcia Alonso
 */
public final class UnixNetworkInfo extends AbstractNetworkInfo {

    private String getNetworkData() {
        String networkData = HardwareInfoUtils.executeCommand("ifconfig", "-a");

        return networkData;
    }

    protected Map<String, String> parseInfo() {
        Map<String, String> networkDataMap = new HashMap<String, String>();

        String networkData = getNetworkData();
        String[] dataStringLines = networkData.split("\\r?\\n");

        int count = 0;
        for (final String dataLine : dataStringLines) {
            if (!dataLine.startsWith(" ")) {
                count++;
                networkDataMap.put("interface_" + count, extractUntilSpace(dataLine));
                networkDataMap.put("type_" + count, extractText(dataLine, "Link encap:", "  "));
            } else {
                String lineType = extractUntilSpace(dataLine);
                if ("inet".equals(lineType)) {
                    networkDataMap.put("ipv4_" + count, extractText(dataLine, "addr:", " "));
                } else if ("inet6".equals(lineType)) {
                    networkDataMap.put("ipv6_" + count, extractText(dataLine, "addr:", " "));
                } else if ("RX".equals(lineType)) {
                    if (dataLine.trim().startsWith("RX packets")) {
                        networkDataMap.put("received_packets_" + count, extractText(dataLine, "packets:", " "));
                    } else {
                        networkDataMap.put("received_bytes_" + count, extractText(dataLine, "RX bytes:", " "));
                        networkDataMap.put("transmitted_bytes_" + count, extractText(dataLine, "TX bytes:", " "));
                    }
                } else if ("TX".equals(lineType)) {
                    networkDataMap.put("transmitted_packets_" + count, extractText(dataLine, "packets:", " "));
                }
            }
        }
        networkDataMap.put("interfacesLength", String.valueOf(count));

        return networkDataMap;
    }

    private String extractText(String text, String startTag, String endTag) {
        if (text.trim().isEmpty()) {
            return "NOT FOUND";
        }
        
        final Pattern pattern = Pattern.compile(startTag + "(.+?)" + endTag);
        final Matcher matcher = pattern.matcher(text);
        
        matcher.find();
        if (matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return "NOT FOUND";
    }

    private String extractUntilSpace(String text) {
        if (text.trim().isEmpty()) {
            return "NOT FOUND";
        }
        
        final Pattern pattern = Pattern.compile("([^\\s]+)");
        final Matcher matcher = pattern.matcher(text);
        matcher.find();
        if (matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return "NOT FOUND";
    }
}
