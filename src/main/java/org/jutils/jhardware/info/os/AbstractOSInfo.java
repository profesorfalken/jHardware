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
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.OSInfo;

/**
 * Base class for operating-system providers.
 */
public abstract class AbstractOSInfo implements HardwareInfo {

    @Override
    public OSInfo getInfo() {
        return build(parseInfoData());
    }

    /**
     * Legacy parsing hook.
     *
     * @return raw OS values
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
    protected OSInfo buildFromDataMap(Map<String, String> dataMap) {
        ParsedData safe = ParsedData.empty().putAll(dataMap);
        OSInfo info = new OSInfo();
        info.setFullInfo(safe.values());
        info.setName(safe.get("Name"));
        info.setManufacturer(safe.get("Manufacturer"));
        info.setVersion(safe.get("Version"));
        info.setLastBootTime(safe.get("LastBootTime"));
        return info;
    }

    protected OSInfo build(ParsedData data) {
        ParsedData safe = data == null ? ParsedData.empty() : data;
        OSInfo info = buildFromDataMap(safe.values());
        info.setFullInfo(safe.values());
        info.setWarnings(safe.warnings());
        return info;
    }
}
