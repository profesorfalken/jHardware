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
package org.jutils.jhardware.info.graphicscard.windows;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Gpu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.graphicscard.AbstractGraphicsCardInfo;
import org.jutils.jhardware.util.DirectXInfoLoader;

/**
 *
 * @author javier
 */
public class WindowsGraphicsCardInfo extends AbstractGraphicsCardInfo {

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> graphicsCardDataMap = new HashMap<>();

        DirectXInfoLoader directXinfo = DirectXInfoLoader.get;
        List<Map<String, String>> rawDisplayInfoMap = directXinfo.getDisplayInfo();
        List<Gpu> gpus = JSensors.get.components().gpus;

        int numDevice = 0;
        for (final Map<String, String> displayInfoMap : rawDisplayInfoMap) {
            graphicsCardDataMap.put("name_" + numDevice, displayInfoMap.get("CardName"));
            graphicsCardDataMap.put("manufacturer_" + numDevice, displayInfoMap.get("Manufacturer"));
            graphicsCardDataMap.put("chip_type_" + numDevice, displayInfoMap.get("ChipType"));
            graphicsCardDataMap.put("dac_type_" + numDevice, displayInfoMap.get("DACType"));
            graphicsCardDataMap.put("device_type_" + numDevice, displayInfoMap.get("DeviceType"));

            //Get Temperature and fan speed from jSensors
            if (gpus.size() > numDevice) {
                Gpu gpu = gpus.get(numDevice);
                if (gpu.sensors.temperatures != null && !gpu.sensors.temperatures.isEmpty()) {
                    graphicsCardDataMap.put("temperature_" + numDevice, 
                            String.valueOf(gpu.sensors.temperatures.get(0).value.intValue()));
                }
                if (gpu.sensors.fans != null && !gpu.sensors.fans.isEmpty()) {
                    graphicsCardDataMap.put("fan_speed_" + numDevice, 
                            String.valueOf(gpu.sensors.fans.get(0).value.intValue()));
                }
            }

            numDevice++;
        }
        
        graphicsCardDataMap.put("numOfGraphicsCards", String.valueOf(numDevice));
        
        return graphicsCardDataMap;
    }

}
