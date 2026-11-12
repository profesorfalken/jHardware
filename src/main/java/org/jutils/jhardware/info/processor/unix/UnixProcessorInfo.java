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
package org.jutils.jhardware.info.processor.unix;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.processor.AbstractProcessorInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.TemperatureUtils;

/**
 * Processor information from {@code /proc/cpuinfo}.
 */
public final class UnixProcessorInfo extends AbstractProcessorInfo {

    private static final Path DEFAULT_CPUINFO = Paths.get("/proc/cpuinfo");

    private final Path cpuInfoPath;

    public UnixProcessorInfo() {
        this(DEFAULT_CPUINFO);
    }

    /**
     * @param cpuInfoPath custom path, useful for tests
     */
    UnixProcessorInfo(Path cpuInfoPath) {
        this.cpuInfoPath = cpuInfoPath == null ? DEFAULT_CPUINFO : cpuInfoPath;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = parseCpuInfo(HardwareInfoUtils
                .readLines(cpuInfoPath.toString()));
        data.put("temperature", TemperatureUtils.getCpuTemperatureForLinux());
        return data;
    }

    /**
     * Parses the content of {@code /proc/cpuinfo}.
     *
     * @param lines file lines
     * @return parsed data
     */
    static ParsedData parseCpuInfo(List<String> lines) {
        ParsedData data = ParsedData.empty();
        List<Map<String, String>> blocks = new ArrayList<>();
        Map<String, String> current = new LinkedHashMap<>();
        for (String raw : lines) {
            if (raw == null) {
                continue;
            }
            if (raw.trim().isEmpty()) {
                if (!current.isEmpty()) {
                    blocks.add(current);
                    current = new LinkedHashMap<>();
                }
                continue;
            }
            int separator = raw.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String key = raw.substring(0, separator).trim();
            String value = raw.substring(separator + 1).trim();
            if (!key.isEmpty()) {
                current.put(key, value);
            }
        }
        if (!current.isEmpty()) {
            blocks.add(current);
        }
        if (blocks.isEmpty()) {
            return data.warn("/proc/cpuinfo is empty or unreadable");
        }

        Map<String, String> first = blocks.get(0);
        data.putAll(first);

        Set<String> physicalCores = new LinkedHashSet<>();
        for (Map<String, String> block : blocks) {
            String physicalId = block.get("physical id");
            String coreId = block.get("core id");
            if (physicalId != null && coreId != null) {
                physicalCores.add(physicalId + ":" + coreId);
            }
        }
        int cores = physicalCores.isEmpty()
                ? HardwareInfoUtils.parseInt(first.get("cpu cores"), blocks.size())
                : physicalCores.size();
        data.put("cpu cores", String.valueOf(cores));
        data.put("logical processors", String.valueOf(blocks.size()));

        if (data.get("model name") == null) {
            data.warn("The CPU model name was not reported by /proc/cpuinfo");
        }
        return data;
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

    /**
     * Returns the raw content of {@code /proc/cpuinfo}.
     *
     * @return the file content, using CRLF line endings
     * @deprecated use {@link #parseCpuInfo(java.util.List)} or
     *             {@link #getInfo()}
     */
    @Deprecated
    public static String getProcessorData() {
        StringBuilder buffer = new StringBuilder();
        for (String line : HardwareInfoUtils.readLines(DEFAULT_CPUINFO.toString())) {
            buffer.append(line).append("\r\n");
        }
        return buffer.toString();
    }

}
