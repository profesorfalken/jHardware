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
package org.jutils.jhardware.info.processor.windows;

import com.profesorfalken.wmi4java.WMIClass;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.processor.AbstractProcessorInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.TemperatureUtils;
import org.jutils.jhardware.util.WmiSupport;

/**
 * Processor information from WMI ({@code Win32_Processor}).
 */
public final class WindowsProcessorInfo extends AbstractProcessorInfo {

    private static final Pattern DESCRIPTION = Pattern.compile(
            "Family\\s+(\\S+)\\s+Model\\s+(\\S+)\\s+Stepping\\s+(\\S+)",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected ParsedData parseInfoData() {
        Map<String, String> wmi = WmiSupport.object(WMIClass.WIN32_PROCESSOR);
        ParsedData data = parseWmi(wmi);
        if (wmi.isEmpty()) {
            data.warn("WMI class Win32_Processor returned no data");
        }
        data.put("temperature", TemperatureUtils.getCpuTemperatureForWindows());
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
        String description = wmi.get("Description");
        if (description != null) {
            Matcher matcher = DESCRIPTION.matcher(description);
            if (matcher.find()) {
                data.put("cpu family", matcher.group(1));
                data.put("model", matcher.group(2));
                data.put("stepping", matcher.group(3));
            } else {
                data.warn("Could not parse the CPU description: " + description);
            }
        }
        data.put("model name", wmi.get("Name"));
        data.put("cpu MHz", wmi.get("MaxClockSpeed"));
        data.put("vendor_id", wmi.get("Manufacturer"));
        data.put("cpu cores", HardwareInfoUtils.firstNonBlank(
                wmi.get("NumberOfCores"), wmi.get("NumberOfLogicalProcessors")));
        String cache = HardwareInfoUtils.firstNonBlank(
                wmi.get("L3CacheSize"), wmi.get("L2CacheSize"));
        if (cache != null) {
            data.put("cache size", cache + " KB");
        }
        return data;
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
