/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jhardware.info.graphicscard;

import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.model.GraphicsCardInfo;

/**
 * Information related to graphics card
 * 
 * @author Javier Garcia Alonso.
 */
public abstract class AbstractGraphicsCardInfo implements HardwareInfo {

    @Override
    public GraphicsCardInfo getInfo() {
        return buildFromDataMap(parseInfo());
    }
    
    protected abstract Map<String, String> parseInfo();
    
    protected GraphicsCardInfo buildFromDataMap(Map<String, String> dataMap) {
       GraphicsCardInfo info = new GraphicsCardInfo();
        //info.setFullInfo(dataMap);
        
        if (dataMap != null && !dataMap.isEmpty()) {
            info.setName(dataMap.get("name"));
            info.setManufacturer(dataMap.get("manufacturer"));
            info.setDacType(dataMap.get("dac_type"));
            info.setDeviceType(dataMap.get("device_type"));
            info.setTemperature(dataMap.get("temperature"));
            info.setFanSpeed(dataMap.get("fan_speed"));
        }
        
        return info;
    }
}
