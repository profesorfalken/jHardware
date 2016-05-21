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
package org.jutils.jhardware.info.display.windows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.display.AbstractDisplayInfo;
import org.jutils.jhardware.util.DirectXInfoLoader;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to Displays
 * 
 * @author Javier Garcia Alonso
 */
public final class WindowsDisplayInfo extends AbstractDisplayInfo {

    /**
     *
     * @return
     */
    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> displayDataMap = new HashMap<>();
        
        DirectXInfoLoader directXinfo = DirectXInfoLoader.get;
        List<Map<String, String>> rawDisplayInfoMap = directXinfo.getDisplayInfo();
        
        int numDevice = 0;
        for (final Map<String, String> displayInfoMap : rawDisplayInfoMap) {            
            displayDataMap.put("name_" + numDevice, displayInfoMap.get("MonitorName"));
            displayDataMap.put("current_res_" + numDevice, getResolution(displayInfoMap.get("CurrentMode")));
            displayDataMap.put("current_refresh_rate_" + numDevice, getRefreshRate(displayInfoMap.get("CurrentMode")));
            displayDataMap.put("available_res_" + numDevice, "");
            
            numDevice++;
        }
        
        displayDataMap.put("numOfDisplays", String.valueOf(rawDisplayInfoMap.size()));
        
        return displayDataMap;
    }
    
    private String getResolution(String currentMode) {
        return currentMode.substring(0, currentMode.indexOf("(")).replaceAll("\\s+","");
    }
    
    private String getRefreshRate(String currentMode) {
        return HardwareInfoUtils.extractText(currentMode, "\\) \\((.+?)Hz");
    }
}
