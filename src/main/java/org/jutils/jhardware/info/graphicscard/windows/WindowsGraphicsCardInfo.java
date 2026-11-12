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
package org.jutils.jhardware.info.graphicscard.windows;

import com.profesorfalken.wmi4java.WMIClass;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.graphicscard.AbstractGraphicsCardInfo;
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.DirectXInfoLoader;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.JsensorsSupport;
import org.jutils.jhardware.util.SystemCommandRunner;
import org.jutils.jhardware.util.WmiSupport;

/**
 * Graphics-card information for Windows, from {@code dxdiag} with a WMI
 * fallback.
 */
public class WindowsGraphicsCardInfo extends AbstractGraphicsCardInfo {

    private final CommandRunner runner;

    public WindowsGraphicsCardInfo() {
        this(SystemCommandRunner.defaultRunner());
    }

    public WindowsGraphicsCardInfo(CommandRunner runner) {
        this.runner = runner == null ? SystemCommandRunner.defaultRunner() : runner;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = ParsedData.empty();
        DirectXInfoLoader.Result directX = DirectXInfoLoader.get.getResult(runner);
        if (!directX.displays().isEmpty()) {
            data.merge(parseDirectX(directX.displays()));
        } else {
            data.merge(parseWmiVideoControllers());
        }
        data.addWarnings(directX.warnings());

        int count = data.getInt("numOfGraphicsCards", 0);
        for (int i = 0; i < count; i++) {
            String temperature = JsensorsSupport.gpuTemperature(i);
            if (temperature != null) {
                data.put("temperature_" + i, temperature);
            }
            String fanSpeed = JsensorsSupport.gpuFanSpeed(i);
            if (fanSpeed != null) {
                data.put("fan_speed_" + i, fanSpeed);
            }
        }
        if (count == 0) {
            data.warn("No graphics card was reported by dxdiag or WMI");
        }
        return data;
    }

    /**
     * @param displays dxdiag {@code DisplayDevice} maps
     * @return parsed data
     */
    static ParsedData parseDirectX(List<Map<String, String>> displays) {
        ParsedData data = ParsedData.empty();
        if (displays == null) {
            return data;
        }
        Map<String, Map<String, String>> unique = new LinkedHashMap<>();
        for (Map<String, String> display : displays) {
            String name = display.get("CardName");
            String key = HardwareInfoUtils.firstNonBlank(name, "unknown")
                    + "|" + HardwareInfoUtils.firstNonBlank(
                            display.get("ChipType"), "")
                    + "|" + HardwareInfoUtils.firstNonBlank(
                            display.get("Manufacturer"), "");
            unique.putIfAbsent(key, display);
        }
        int count = 0;
        for (Map<String, String> display : unique.values()) {
            data.put("name_" + count, display.get("CardName"));
            data.put("manufacturer_" + count, display.get("Manufacturer"));
            data.put("chip_type_" + count, display.get("ChipType"));
            data.put("dac_type_" + count, display.get("DACType"));
            data.put("device_type_" + count, display.get("DeviceType"));
            count++;
        }
        data.put("numOfGraphicsCards", String.valueOf(count));
        return data;
    }

    private static ParsedData parseWmiVideoControllers() {
        ParsedData data = ParsedData.empty();
        List<Map<String, String>> controllers = WmiSupport.list(
                WMIClass.WIN32_VIDEOCONTROLLER);
        int count = 0;
        for (Map<String, String> controller : controllers) {
            data.put("name_" + count, controller.get("Name"));
            data.put("manufacturer_" + count, controller.get("AdapterCompatibility"));
            data.put("chip_type_" + count, controller.get("VideoProcessor"));
            data.put("dac_type_" + count, controller.get("AdapterDACType"));
            data.put("device_type_" + count, "VideoController");
            count++;
        }
        data.put("numOfGraphicsCards", String.valueOf(count));
        return data;
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
