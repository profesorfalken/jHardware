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
package org.jutils.jhardware.info.os;

import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.model.OSInfo;

/**
 * Information related to Operating System
 * 
 * @author Javier Garcia Alonso
 */
public abstract class AbstractOSInfo implements HardwareInfo { 

    /**
     *
     * @return
     */
    @Override
    public OSInfo getInfo() {
        return buildFromDataMap(parseInfo());
    }
    
    protected abstract Map<String, String> parseInfo();
    
    protected OSInfo buildFromDataMap(Map<String, String> dataMap) {
        OSInfo info = new OSInfo();
        info.setFullInfo(dataMap);
        
        if (dataMap != null && !dataMap.isEmpty()) {
            info.setName(dataMap.get("Name"));
            info.setManufacturer(dataMap.get("Manufacturer"));
            info.setVersion(dataMap.get("Version"));
            info.setLastBootTime(dataMap.get("LastBootTime"));
        }
        
        return info;
    }
}
