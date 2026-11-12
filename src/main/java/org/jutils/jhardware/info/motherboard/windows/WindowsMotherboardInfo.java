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
package org.jutils.jhardware.info.motherboard.windows;

import com.profesorfalken.wmi4java.WMIClass;
import java.util.Map;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.motherboard.AbstractMotherboardInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.WmiSupport;

/**
 * Motherboard information from WMI ({@code Win32_BaseBoard}).
 */
public final class WindowsMotherboardInfo extends AbstractMotherboardInfo {

    @Override
    protected ParsedData parseInfoData() {
        Map<String, String> wmi = WmiSupport.object(WMIClass.WIN32_BASEBOARD);
        ParsedData data = parseWmi(wmi);
        if (data.isEmpty()) {
            data.warn("WMI class Win32_BaseBoard returned no data");
        }
        return data;
    }

    /**
     * @param wmi raw WMI properties
     * @return parsed data
     */
    static ParsedData parseWmi(Map<String, String> wmi) {
        ParsedData data = ParsedData.empty();
        if (wmi == null || wmi.isEmpty()) {
            return data;
        }
        data.putAll(wmi);
        data.put("Product Name", HardwareInfoUtils.firstNonBlank(
                wmi.get("Product"), wmi.get("Name")));
        data.put("Vendor", wmi.get("Manufacturer"));
        data.put("Version", wmi.get("Version"));
        return data;
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
