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
package org.jutils.jhardware.info.memory;

import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.MemoryInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Base class for memory providers.
 */
public abstract class AbstractMemoryInfo implements HardwareInfo {

    @Override
    public MemoryInfo getInfo() {
        return build(parseInfoData());
    }

    /**
     * Legacy parsing hook.
     *
     * @return raw memory values
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
    protected MemoryInfo buildFromDataMap(Map<String, String> dataMap) {
        ParsedData safe = ParsedData.empty().putAll(dataMap);
        MemoryInfo info = new MemoryInfo();
        info.setFullInfo(safe.values());
        info.setTotalMemory(safe.get("MemTotal"));
        info.setFreeMemory(safe.get("MemFree"));
        info.setAvailableMemory(safe.get("MemAvailable"));
        info.setTotalMemoryBytes(bytes(safe.get("MemTotalBytes")));
        info.setFreeMemoryBytes(bytes(safe.get("MemFreeBytes")));
        info.setAvailableMemoryBytes(bytes(safe.get("MemAvailableBytes")));
        return info;
    }

    protected MemoryInfo build(ParsedData data) {
        ParsedData safe = data == null ? ParsedData.empty() : data;
        MemoryInfo info = buildFromDataMap(safe.values());
        info.setFullInfo(safe.values());
        info.setWarnings(safe.warnings());
        return info;
    }

    private static Long bytes(String value) {
        long parsed = HardwareInfoUtils.parseLong(value, -1L);
        return parsed < 0L ? null : parsed;
    }
}
