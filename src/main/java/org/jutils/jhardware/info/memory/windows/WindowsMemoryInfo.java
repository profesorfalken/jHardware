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
package org.jutils.jhardware.info.memory.windows;

import com.profesorfalken.wmi4java.WMIClass;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.memory.AbstractMemoryInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.WmiSupport;

/**
 * Memory information from WMI.
 */
public final class WindowsMemoryInfo extends AbstractMemoryInfo {

    @Override
    protected ParsedData parseInfoData() {
        Map<String, String> os = WmiSupport.object(WMIClass.WIN32_OPERATINGSYSTEM);
        Map<String, String> perf = WmiSupport.object(
                WMIClass.WIN32_PERFFORMATTEDDATA_PERFOS_MEMORY);
        Map<String, String> computer = WmiSupport.object(WMIClass.WIN32_COMPUTERSYSTEM);
        List<Map<String, String>> modules = WmiSupport.list(WMIClass.WIN32_PHYSICALMEMORY);
        ParsedData data = parseWmi(os, perf, computer, modules);
        if (data.isEmpty()) {
            data.warn("WMI memory classes returned no data");
        }
        return data;
    }

    /**
     * @param os        {@code Win32_OperatingSystem}
     * @param perf      {@code Win32_PerfFormattedData_PerfOS_Memory}
     * @param computer  {@code Win32_ComputerSystem}
     * @param modules   {@code Win32_PhysicalMemory} instances
     * @return parsed data
     */
    static ParsedData parseWmi(Map<String, String> os, Map<String, String> perf,
            Map<String, String> computer, List<Map<String, String>> modules) {
        ParsedData data = ParsedData.empty();
        data.putAll(os);
        data.putAll(perf);
        data.putAll(computer);

        long totalBytes = HardwareInfoUtils.parseLong(
                computer == null ? null : computer.get("TotalPhysicalMemory"), -1L);
        if (totalBytes < 0L && modules != null) {
            long sum = 0L;
            for (Map<String, String> module : modules) {
                long capacity = HardwareInfoUtils.parseLong(
                        module.get("Capacity"), -1L);
                if (capacity > 0L) {
                    sum += capacity;
                }
            }
            totalBytes = sum > 0L ? sum : -1L;
        }
        if (totalBytes < 0L && os != null) {
            long totalKb = HardwareInfoUtils.parseLong(
                    os.get("TotalVisibleMemorySize"), -1L);
            totalBytes = totalKb < 0L ? -1L : totalKb * 1024L;
        }

        long freeBytes = kilobytesToBytes(os == null
                ? null : os.get("FreePhysicalMemory"));
        long availableBytes = kilobytesToBytes(perf == null
                ? null : perf.get("AvailableKBytes"));
        if (availableBytes < 0L) {
            availableBytes = freeBytes;
        }

        put(data, "MemTotal", "MemTotalBytes", totalBytes);
        put(data, "MemFree", "MemFreeBytes", freeBytes);
        put(data, "MemAvailable", "MemAvailableBytes", availableBytes);

        if (totalBytes < 0L) {
            data.warn("Total physical memory could not be determined");
        }
        return data;
    }

    private static long kilobytesToBytes(String raw) {
        long kilobytes = HardwareInfoUtils.parseLong(raw, -1L);
        return kilobytes < 0L ? -1L : kilobytes * 1024L;
    }

    private static void put(ParsedData data, String stringKey, String bytesKey, long bytes) {
        if (bytes >= 0L) {
            data.put(stringKey, bytes + " bytes");
            data.put(bytesKey, String.valueOf(bytes));
        }
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
