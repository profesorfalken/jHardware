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
package org.jutils.jhardware.info.display.unix;

import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.display.AbstractDisplayInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to BIOS
 *
 * @author Javier Garcia Alonso
 */
public final class UnixDisplayInfo extends AbstractDisplayInfo {

    private static final String XRANDR_COMMAND = "xrandr";
    private static final String XRANDR_PARAM = "-q";

    private static String getDisplayData() {
        return HardwareInfoUtils.executeCommand(XRANDR_COMMAND, XRANDR_PARAM);        
    }

    /**
     *
     * @return
     */
    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> displayDataMap = new HashMap<>();
        String[] dataStringLines = getDisplayData().split("\\r?\\n");

        int count = -1;
        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("Screen")) {
                count++; 
                displayDataMap.put("name_" + count, dataLine.substring(0, dataLine.indexOf(":")));
                displayDataMap.put("current_res_" + count, HardwareInfoUtils.extractText(dataLine, "Current (.+?),"));
            } else if (dataLine.startsWith("\t")) {
                String[] availableResData = dataLine.split("\t");
                displayDataMap.put("available_res_" + count, availableResData[0] + "x" + availableResData[1] + ";");
            }
        }
        displayDataMap.put("numOfDisplays", String.valueOf(count + 1));

        return displayDataMap;
    }
}
