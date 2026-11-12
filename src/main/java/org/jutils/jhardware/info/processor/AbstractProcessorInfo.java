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
package org.jutils.jhardware.info.processor;

import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.ProcessorInfo;

/**
 * Base class for processor providers.
 *
 * <p>The original protected API ({@link #parseInfo()} and
 * {@link #buildFromDataMap(Map)}) is retained for subclasses written against
 * 0.8.x. New implementations should override {@link #parseInfoData()} so they
 * can also report warnings.</p>
 */
public abstract class AbstractProcessorInfo implements HardwareInfo {

    @Override
    public ProcessorInfo getInfo() {
        return build(parseInfoData());
    }

    /**
     * Legacy parsing hook.
     *
     * @return raw processor values
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
    protected ProcessorInfo buildFromDataMap(Map<String, String> dataMap) {
        ParsedData safe = ParsedData.empty().putAll(dataMap);
        ProcessorInfo info = new ProcessorInfo();
        info.setFullInfo(safe.values());
        info.setCacheSize(safe.get("cache size"));
        info.setFamily(safe.get("cpu family"));
        info.setMhz(safe.get("cpu MHz"));
        info.setModel(safe.get("model"));
        info.setModelName(safe.get("model name"));
        info.setNumCores(safe.get("cpu cores"));
        info.setStepping(safe.get("stepping"));
        info.setTemperature(safe.get("temperature"));
        info.setVendorId(safe.get("vendor_id"));
        return info;
    }

    protected ProcessorInfo build(ParsedData data) {
        ParsedData safe = data == null ? ParsedData.empty() : data;
        ProcessorInfo info = buildFromDataMap(safe.values());
        info.setFullInfo(safe.values());
        info.setWarnings(safe.warnings());
        return info;
    }
}
