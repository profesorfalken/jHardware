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
package org.jutils.jhardware.info.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.model.Display;
import org.jutils.jhardware.model.DisplayInfo;

/**
 * Information related to Displays
 * 
 * @author Javier Garcia Alonso
 */
public abstract class AbstractDisplayInfo implements HardwareInfo { 

    /**
     *
     * @return
     */
    @Override
    public DisplayInfo getInfo() {
        return buildFromDataMap(parseInfo());
    }
    
    protected abstract Map<String, String> parseInfo();
    
    protected DisplayInfo buildFromDataMap(Map<String, String> dataMap) {
        DisplayInfo info = new DisplayInfo();
        
        List<Display> displayList = new ArrayList<>();
        if (dataMap != null && !dataMap.isEmpty()) {
            int numOfDisplays = Integer.parseInt(dataMap.get("numOfDisplays"));
            for (int i = 0; i < numOfDisplays; i++) {
                Display display = new Display();
                
                display.setName(dataMap.get("name_" + i));
                display.setCurrentResolution(dataMap.get("current_res_" + i));
                display.setRefreshRate(dataMap.get("current_refresh_rate_" + i));                
                display.setSupportedResolutions(dataMap.get("available_res_" + i).split(";"));
                
                displayList.add(display);
            }
        }
        info.setDisplayDevices(displayList);
        
        return info;
    }
}
