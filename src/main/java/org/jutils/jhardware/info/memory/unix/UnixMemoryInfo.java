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
package org.jutils.jhardware.info.memory.unix;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.memory.AbstractMemoryInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Memory information from {@code /proc/meminfo}.
 */
public final class UnixMemoryInfo extends AbstractMemoryInfo {

    private static final Path DEFAULT_MEMINFO = Paths.get("/proc/meminfo");
    private static final Pattern VALUE_PATTERN
            = Pattern.compile("(\\d+)\\s*([kKmMgG]?[bB]?)?");

    private final Path memInfoPath;

    public UnixMemoryInfo() {
        this(DEFAULT_MEMINFO);
    }

    /**
     * @param memInfoPath custom path, useful for tests
     */
    UnixMemoryInfo(Path memInfoPath) {
        this.memInfoPath = memInfoPath == null ? DEFAULT_MEMINFO : memInfoPath;
    }

    @Override
    protected ParsedData parseInfoData() {
        return parseMeminfo(HardwareInfoUtils.readLines(memInfoPath.toString()));
    }

    /**
     * Parses the content of {@code /proc/meminfo}.
     *
     * @param lines file lines
     * @return parsed data, including normalized byte values
     */
    static ParsedData parseMeminfo(List<String> lines) {
        ParsedData data = ParsedData.empty();
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            int separator = line.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            if (!key.isEmpty()) {
                data.put(key, value);
            }
        }

        putBytes(data, "MemTotal", "MemTotalBytes");
        putBytes(data, "MemFree", "MemFreeBytes");
        putBytes(data, "MemAvailable", "MemAvailableBytes");

        if (data.get("MemAvailable") == null) {
            long free = parseBytes(data.get("MemFree"));
            long buffers = parseBytes(data.get("Buffers"));
            long cached = parseBytes(data.get("Cached"));
            if (free >= 0L && buffers >= 0L && cached >= 0L) {
                long available = free + buffers + cached;
                data.put("MemAvailable", available + " bytes");
                data.put("MemAvailableBytes", String.valueOf(available));
                data.warn("MemAvailable was missing; computed from MemFree+Buffers+Cached");
            } else {
                data.warn("MemAvailable is not reported by /proc/meminfo");
            }
        }

        if (data.get("MemTotal") == null) {
            data.warn("MemTotal is not reported by /proc/meminfo");
        }
        return data;
    }

    private static void putBytes(ParsedData data, String sourceKey, String targetKey) {
        long bytes = parseBytes(data.get(sourceKey));
        if (bytes >= 0L) {
            data.put(targetKey, String.valueOf(bytes));
        }
    }

    /**
     * Converts a {@code /proc/meminfo} value to bytes.
     *
     * @param raw value such as {@code "32662336 kB"}
     * @return bytes, or {@code -1} when the value cannot be parsed
     */
    static long parseBytes(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return -1L;
        }
        Matcher matcher = VALUE_PATTERN.matcher(raw.trim());
        if (!matcher.find()) {
            return -1L;
        }
        long value;
        try {
            value = Long.parseLong(matcher.group(1));
        } catch (NumberFormatException ex) {
            return -1L;
        }
        String unit = matcher.group(2) == null
                ? "" : matcher.group(2).toLowerCase(Locale.ROOT);
        switch (unit) {
            case "kb":
                return value * 1024L;
            case "mb":
                return value * 1024L * 1024L;
            case "gb":
                return value * 1024L * 1024L * 1024L;
            default:
                return value;
        }
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
