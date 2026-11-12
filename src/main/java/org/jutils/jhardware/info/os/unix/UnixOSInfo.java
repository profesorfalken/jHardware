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
package org.jutils.jhardware.info.os.unix;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.os.AbstractOSInfo;
import org.jutils.jhardware.util.CommandResult;
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.SystemCommandRunner;

/**
 * Operating-system information for Linux.
 *
 * <p>{@code /etc/os-release} is the primary source; {@code lsb_release} is
 * only a fallback for old distributions. The boot time comes from
 * {@code /proc/stat} instead of the fragile {@code last -x} output.</p>
 */
public final class UnixOSInfo extends AbstractOSInfo {

    private static final Path DEFAULT_OS_RELEASE = Paths.get("/etc/os-release");
    private static final Path DEFAULT_PROC_STAT = Paths.get("/proc/stat");

    private final Path osReleasePath;
    private final Path procStatPath;
    private final CommandRunner runner;

    public UnixOSInfo() {
        this(DEFAULT_OS_RELEASE, DEFAULT_PROC_STAT, SystemCommandRunner.defaultRunner());
    }

    public UnixOSInfo(CommandRunner runner) {
        this(DEFAULT_OS_RELEASE, DEFAULT_PROC_STAT, runner);
    }

    UnixOSInfo(Path osReleasePath, Path procStatPath, CommandRunner runner) {
        this.osReleasePath = osReleasePath == null ? DEFAULT_OS_RELEASE : osReleasePath;
        this.procStatPath = procStatPath == null ? DEFAULT_PROC_STAT : procStatPath;
        this.runner = runner == null ? SystemCommandRunner.defaultRunner() : runner;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = ParsedData.empty();
        List<String> lines = HardwareInfoUtils.readLines(osReleasePath.toString());

        if (!lines.isEmpty()) {
            Map<String, String> release = parseOsRelease(lines);
            release.forEach((key, value) -> data.put(
                    HardwareInfoUtils.toCamelCase("OS_" + key), value));
            data.put("Name", HardwareInfoUtils.firstNonBlank(
                    release.get("PRETTY_NAME"), release.get("NAME")));
            data.put("Manufacturer", HardwareInfoUtils.firstNonBlank(
                    release.get("NAME"), release.get("ID")));
            data.put("Version", HardwareInfoUtils.firstNonBlank(
                    release.get("VERSION_ID"), release.get("VERSION"),
                    release.get("BUILD_ID")));
        } else {
            data.warn("/etc/os-release is not available; falling back to lsb_release");
            CommandResult result = runner.run("lsb_release", "-a");
            if (result.isSuccess() && !result.stdout().trim().isEmpty()) {
                Map<String, String> lsb = HardwareInfoUtils
                        .parseKeyValueLines(Arrays.asList(result.stdout().split("\\R")), ":");
                data.putAll(lsb);
                data.put("Manufacturer", lsb.get("Distributor ID"));
                data.put("Name", HardwareInfoUtils.firstNonBlank(
                        lsb.get("Description"), lsb.get("Distributor ID")));
                data.put("Version", lsb.get("Release"));
            } else {
                data.warn("lsb_release is not available");
            }
        }

        readBootTime(procStatPath).ifPresent(seconds -> data.put("LastBootTime",
                Instant.ofEpochSecond(seconds).toString()));

        if (data.get("Name") == null) {
            data.warn("The operating-system name could not be determined");
        }
        return data;
    }

    /**
     * Parses an {@code os-release} file.
     *
     * @param lines file lines
     * @return keys and unquoted values
     */
    static Map<String, String> parseOsRelease(List<String> lines) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = trimmed.substring(0, separator).trim();
            String value = HardwareInfoUtils.stripQuotes(
                    trimmed.substring(separator + 1).trim());
            value = value.replace("\\\"", "\"")
                    .replace("\\'", "'")
                    .replace("\\\\", "\\");
            if (!key.isEmpty()) {
                values.put(key, value);
            }
        }
        return values;
    }

    /**
     * Reads the boot time from {@code /proc/stat}.
     *
     * @param procStat path to {@code /proc/stat}
     * @return seconds since the epoch, if present
     */
    static Optional<Long> readBootTime(Path procStat) {
        for (String line : HardwareInfoUtils.readLines(procStat.toString())) {
            if (line.startsWith("btime ")) {
                long seconds = HardwareInfoUtils.parseLong(
                        line.substring("btime ".length()).trim(), -1L);
                return seconds < 0L ? Optional.empty() : Optional.of(seconds);
            }
        }
        return Optional.empty();
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
