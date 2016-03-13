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
package org.jutils.jhardware.info.motherboard.unix;

import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.motherboard.AbstractMotherboardInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to Motherboard
 *
 * @author Javier Garcia Alonso
 */
public final class UnixMotherboardInfo extends AbstractMotherboardInfo {

    private static final String DMIPATH = "/sys/devices/virtual/dmi/id/";

    private static String getMotherboardData() {
        String fullData = "";
        if (HardwareInfoUtils.isSudo()) {
            fullData += HardwareInfoUtils.executeCommand("sudo", "dmidecode", "--type", "2");
        } else {
            fullData += "\tProduct Name: " + HardwareInfoUtils.executeCommand("cat", DMIPATH + "board_name");
            fullData += "\tVendor: " + HardwareInfoUtils.executeCommand("cat", DMIPATH + "board_vendor");
            fullData += "\tVersion: " + HardwareInfoUtils.executeCommand("cat", DMIPATH + "board_version");
        }

        return fullData;
    }

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> motherboardDataMap = new HashMap<>();
        String[] dataStringLines = getMotherboardData().split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("\t")) {
                String[] dataStringInfo = dataLine.split(":");
                if (dataStringInfo.length == 2) {
                    motherboardDataMap.put(dataStringInfo[0].trim(), dataStringInfo[1].trim());
                } else if (dataStringInfo.length == 1 && "\tFeatures".equals(dataStringInfo[0])) {                    
                    motherboardDataMap.put(dataStringInfo[0].trim(), getFeatures(dataStringLines));
                }
            }
        }
        return motherboardDataMap;
    }

    private static String getFeatures(String[] dataStringLines) {
        StringBuilder features = new StringBuilder();
        for (final String characteristicsLine : dataStringLines) {
            if (characteristicsLine.trim().length() > 0 && characteristicsLine.startsWith("\t\t")) {
                features.append(characteristicsLine.trim()).append("\n");
            }
        }
        return features.toString();
    }
}
