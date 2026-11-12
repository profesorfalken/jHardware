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
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.Display;
import org.jutils.jhardware.model.DisplayInfo;

/**
 * Base class for display providers.
 */
public abstract class AbstractDisplayInfo implements HardwareInfo {

    @Override
    public DisplayInfo getInfo() {
        return build(parseInfoData());
    }

    /**
     * Legacy parsing hook.
     *
     * @return raw display values
     * @deprecated override {@link #parseInfoData()} instead
     */
    @Deprecated
    protected abstract Map<String, String> parseInfo();

    /**
     * New parsing hook. The default implementation adapts the legacy
     * {@link #parseInfo()} method.
     *
     * @return parsed data
     */
    protected ParsedData parseInfoData() {
        return ParsedData.empty().putAll(parseInfo());
    }

    /**
     * Legacy model builder.
     *
     * @param dataMap raw values
     * @return the model
     * @deprecated use {@link #build(ParsedData)}
     */
    @Deprecated
    protected DisplayInfo buildFromDataMap(Map<String, String> dataMap) {
        ParsedData safe = ParsedData.empty().putAll(dataMap);
        DisplayInfo info = new DisplayInfo();
        info.setFullInfo(safe.values());

        int count = safe.getInt("numOfDisplays", 0);
        List<Display> displays = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Display display = new Display();
            display.setName(safe.get("name_" + i));
            display.setCurrentResolution(safe.get("current_res_" + i));
            display.setRefreshRate(safe.get("current_refresh_rate_" + i));
            display.setSupportedResolutions(splitModes(safe.get("available_res_" + i)));
            displays.add(display);
        }
        info.setDisplayDevices(displays);
        return info;
    }

    protected DisplayInfo build(ParsedData data) {
        ParsedData safe = data == null ? ParsedData.empty() : data;
        DisplayInfo info = buildFromDataMap(safe.values());
        info.setFullInfo(safe.values());
        info.setWarnings(safe.warnings());
        return info;
    }

    private static String[] splitModes(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return new String[0];
        }
        List<String> modes = new ArrayList<>();
        for (String mode : raw.split(";")) {
            if (!mode.trim().isEmpty()) {
                modes.add(mode.trim());
            }
        }
        return modes.toArray(new String[0]);
    }
}
