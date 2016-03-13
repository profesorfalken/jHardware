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
package org.jutils.jhardware.info.bios.unix;

import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.bios.AbstractBiosInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to BIOS
 *
 * @author Javier Garcia Alonso
 */
public final class UnixBiosInfo extends AbstractBiosInfo {

    private static final String DMIPATH = "/sys/devices/virtual/dmi/id/";

    private static String getBiosData() {
        String fullData = "";
        if (HardwareInfoUtils.isSudo()) {
            fullData += HardwareInfoUtils.executeCommand("sudo", "dmidecode", "--type", "0");
        } else {
            fullData += "\tRelease Date: " + HardwareInfoUtils.executeCommand("cat", DMIPATH + "bios_date");
            fullData += "\tVendor: " + HardwareInfoUtils.executeCommand("cat", DMIPATH + "bios_vendor");
            fullData += "\tVersion: " + HardwareInfoUtils.executeCommand("cat", DMIPATH + "bios_version");
        }

        return fullData;
    }

    /**
     *
     * @return
     */
    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> biosDataMap = new HashMap<>();
        String[] dataStringLines = getBiosData().split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("\t")) {
                String[] dataStringInfo = dataLine.split(":");
                if (dataStringInfo.length == 2) {
                    biosDataMap.put(dataStringInfo[0].trim(), dataStringInfo[1].trim());
                } else if (dataStringInfo.length == 1 && "\tCharacteristics".equals(dataStringInfo[0])) {
                    biosDataMap.put(dataStringInfo[0].trim(), getCharacteristics(dataStringLines));
                }
            }
        }

        return biosDataMap;
    }

    private static String getCharacteristics(String[] dataStringLines) {
        StringBuilder characteristics = new StringBuilder();
        for (final String characteristicsLine : dataStringLines) {
            if (characteristicsLine.trim().length() > 0 && characteristicsLine.startsWith("\t\t")) {
                characteristics.append(characteristicsLine.trim()).append("\n");
            }
        }
        return characteristics.toString();
    }
}
