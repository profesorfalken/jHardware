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
package org.jutils.jhardware.info.graphicscard.unix;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Gpu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.graphicscard.AbstractGraphicsCardInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 *
 * @author javier
 */
public class UnixGraphicsCardInfo extends AbstractGraphicsCardInfo {

    private static final String LSHW_COMMAND = "lshw";
    private static final String LSHW_PARAMS = "-C video";

    private static String getGraphicsCardData() {
        return HardwareInfoUtils.executeCommand(LSHW_COMMAND, LSHW_PARAMS);
    }

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> graphicsCardDataMap = new HashMap<>();        

        String[] dataStringLines = getGraphicsCardData().split("\\r?\\n");

        int count = -1;
        for (final String dataLine : dataStringLines) {
            String line = dataLine.trim();
            if (line.startsWith("*-display")) {
                count++;
            } else if (line.startsWith("product:")) {
                graphicsCardDataMap.put("name_" + count, line.split(":", 2)[1]);
            } else if (line.startsWith("vendor:")) {
                graphicsCardDataMap.put("manufacturer_" + count, line.split(":", 2)[1]);
            } else if (line.startsWith("description:")) {
                graphicsCardDataMap.put("chip_type_" + count, line.split(":", 2)[1]);
            }
        }

        //Get Temperature and fan speed from jSensors
        List<Gpu> gpus = JSensors.get.components().gpus;
        for (int i = 0; i < count; i++) {
            if (gpus.size() > i) {
                Gpu gpu = gpus.get(i);
                if (gpu.sensors.temperatures != null && !gpu.sensors.temperatures.isEmpty()) {
                    graphicsCardDataMap.put("temperature_" + i,
                            String.valueOf(gpu.sensors.temperatures.get(0).value.intValue()));
                }
                if (gpu.sensors.fans != null && !gpu.sensors.fans.isEmpty()) {
                    graphicsCardDataMap.put("fan_" + i,
                            String.valueOf(gpu.sensors.fans.get(0).value.intValue()));
                }
            }
        }

        graphicsCardDataMap.put("numOfGraphicsCards", String.valueOf(count + 1));

        return graphicsCardDataMap;
    }

}
