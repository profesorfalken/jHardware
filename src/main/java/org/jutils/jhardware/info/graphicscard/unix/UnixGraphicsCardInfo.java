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
package org.jutils.jhardware.info.graphicscard.unix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.graphicscard.AbstractGraphicsCardInfo;
import org.jutils.jhardware.util.CommandResult;
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.JsensorsSupport;
import org.jutils.jhardware.util.SystemCommandRunner;

/**
 * Graphics-card information for Linux.
 *
 * <p>It tries {@code lshw}, then {@code lspci}, then the DRM sysfs. This
 * removes the hard dependency on lshw that used to return an empty list on
 * minimal installations.</p>
 */
public class UnixGraphicsCardInfo extends AbstractGraphicsCardInfo {

    private static final Path DEFAULT_DRM_ROOT = Paths.get("/sys/class/drm");
    private static final Pattern QUOTED_FIELD = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern PCI_ID_SUFFIX = Pattern.compile(
            "\\s*\\[[0-9a-fA-F]{4}(?::[0-9a-fA-F]{4})?\\]\\s*$");

    private final CommandRunner runner;
    private final Path drmRoot;

    public UnixGraphicsCardInfo() {
        this(SystemCommandRunner.defaultRunner(), DEFAULT_DRM_ROOT);
    }

    public UnixGraphicsCardInfo(CommandRunner runner) {
        this(runner, DEFAULT_DRM_ROOT);
    }

    UnixGraphicsCardInfo(CommandRunner runner, Path drmRoot) {
        this.runner = runner == null ? SystemCommandRunner.defaultRunner() : runner;
        this.drmRoot = drmRoot == null ? DEFAULT_DRM_ROOT : drmRoot;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = ParsedData.empty();

        CommandResult lshw = runner.run("lshw", "-C", "video");
        if (lshw.isSuccess() && lshw.stdout().contains("*-display")) {
            data.merge(parseLshw(lshw.stdout()));
        } else {
            CommandResult lspci = runner.run("lspci", "-mm", "-nn");
            if (lspci.isSuccess() && !lspci.stdout().trim().isEmpty()) {
                data.merge(parseLspci(lspci.stdout()));
            } else {
                data.merge(parseSysfs(drmRoot));
            }
        }

        int count = data.getInt("numOfGraphicsCards", 0);
        for (int i = 0; i < count; i++) {
            String temperature = JsensorsSupport.gpuTemperature(i);
            if (temperature != null) {
                data.put("temperature_" + i, temperature);
            }
            String fanSpeed = JsensorsSupport.gpuFanSpeed(i);
            if (fanSpeed != null) {
                data.put("fan_speed_" + i, fanSpeed);
            }
        }
        if (count == 0) {
            data.warn("No graphics card was detected through lshw, lspci or DRM sysfs");
        }
        return data;
    }

    /**
     * Parses {@code lshw -C video} output.
     *
     * @param output command output
     * @return parsed data
     */
    static ParsedData parseLshw(String output) {
        ParsedData data = ParsedData.empty();
        int count = -1;
        boolean inDisplay = false;
        for (String raw : output.split("\\R")) {
            String line = raw.trim();
            if (line.startsWith("*-display")) {
                count++;
                inDisplay = true;
                continue;
            }
            if (line.startsWith("*-")) {
                inDisplay = false;
                continue;
            }
            if (!inDisplay) {
                continue;
            }
            if (line.startsWith("product:")) {
                data.put("name_" + count, valueAfterColon(line));
            } else if (line.startsWith("vendor:")) {
                data.put("manufacturer_" + count, valueAfterColon(line));
            } else if (line.startsWith("description:")) {
                data.put("chip_type_" + count, valueAfterColon(line));
            }
        }
        if (count >= 0) {
            data.put("numOfGraphicsCards", String.valueOf(count + 1));
        }
        return data;
    }

    /**
     * Parses {@code lspci -mm -nn} output.
     *
     * @param output command output
     * @return parsed data
     */
    static ParsedData parseLspci(String output) {
        ParsedData data = ParsedData.empty();
        int count = 0;
        for (String line : output.split("\\R")) {
            if (!line.contains("VGA compatible controller")
                    && !line.contains("3D controller")
                    && !line.contains("Display controller")) {
                continue;
            }
            List<String> fields = new ArrayList<>();
            Matcher matcher = QUOTED_FIELD.matcher(line);
            while (matcher.find()) {
                fields.add(matcher.group(1));
            }
            if (fields.size() < 3) {
                continue;
            }
            data.put("chip_type_" + count, stripPciId(fields.get(0)));
            data.put("manufacturer_" + count, stripPciId(fields.get(1)));
            data.put("name_" + count, stripPciId(fields.get(2)));
            count++;
        }
        data.put("numOfGraphicsCards", String.valueOf(count));
        return data;
    }

    /**
     * Fallback based on {@code /sys/class/drm/cardN/device}.
     *
     * @param drmRoot usually {@code /sys/class/drm}
     * @return parsed data
     */
    static ParsedData parseSysfs(Path drmRoot) {
        ParsedData data = ParsedData.empty();
        if (drmRoot == null || !Files.isDirectory(drmRoot)) {
            return data.warn("The DRM sysfs directory is not available");
        }
        List<Path> cards = new ArrayList<>();
        try (java.util.stream.Stream<Path> paths = Files.list(drmRoot)) {
            paths.filter(Files::isDirectory)
                    .filter(path -> HardwareInfoUtils.fileName(path).matches("card\\d+"))
                    .sorted()
                    .forEach(cards::add);
        } catch (IOException | SecurityException ex) {
            return data.warn("Could not list DRM cards: " + ex.getMessage());
        }

        int count = 0;
        for (Path card : cards) {
            Path device = card.resolve("device");
            String vendor = HardwareInfoUtils.readValue(device.resolve("vendor"))
                    .orElse(null);
            String id = HardwareInfoUtils.readValue(device.resolve("device"))
                    .orElse(null);
            if (vendor == null && id == null) {
                continue;
            }
            String driver = HardwareInfoUtils.readValue(device.resolve("uevent"))
                    .map(uevent -> HardwareInfoUtils.extract(uevent, "DRIVER=(.+)")
                            .orElse(null))
                    .orElse(null);
            String name = "PCI " + (vendor == null ? "?" : vendor)
                    + ":" + (id == null ? "?" : id);
            if (driver != null) {
                name += " (" + driver + ")";
            }
            data.put("name_" + count, name);
            data.put("manufacturer_" + count, vendor);
            data.put("chip_type_" + count, "PCI display controller");
            count++;
        }
        if (count == 0) {
            data.warn("No DRM card was found in sysfs");
        }
        data.put("numOfGraphicsCards", String.valueOf(count));
        return data;
    }

    private static String valueAfterColon(String line) {
        int separator = line.indexOf(':');
        return separator < 0 ? "" : line.substring(separator + 1).trim();
    }

    private static String stripPciId(String value) {
        return PCI_ID_SUFFIX.matcher(value).replaceFirst("").trim();
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
