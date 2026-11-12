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
package org.jutils.jhardware.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * CPU temperature discovery.
 *
 * <p>jSensors is tried first because it understands the platform-specific
 * sensor layout. On Linux the sysfs thermal zones are used as a fallback,
 * preferring CPU package zones over the generic ACPI zone.</p>
 */
public class TemperatureUtils {

    public static final String NOT_DETECTED = "NOT DETECTED";

    private static final Path THERMAL_ROOT = Paths.get("/sys/class/thermal");

    public TemperatureUtils() {
    }

    /**
     * @return CPU temperature in Celsius, or {@link #NOT_DETECTED}
     */
    public static String getCpuTemperatureForLinux() {
        String sensorsValue = JsensorsSupport.cpuTemperature();
        if (sensorsValue != null) {
            return sensorsValue;
        }
        return readLinuxThermalZone(THERMAL_ROOT).orElse(NOT_DETECTED);
    }

    /**
     * @return CPU temperature in Celsius, or {@link #NOT_DETECTED}
     */
    public static String getCpuTemperatureForWindows() {
        String sensorsValue = JsensorsSupport.cpuTemperature();
        if (sensorsValue != null) {
            return sensorsValue;
        }

        Map<String, String> values = WmiSupport.object("root/wmi",
                "MSAcpi_ThermalZoneTemperature");
        String raw = values.get("CurrentTemperature");
        if (raw == null) {
            return NOT_DETECTED;
        }
        try {
            double tenthsKelvin = Double.parseDouble(raw.trim());
            double celsius = tenthsKelvin / 10.0 - 273.15;
            if (celsius < -50.0 || celsius > 200.0) {
                return NOT_DETECTED;
            }
            return String.valueOf(Math.round(celsius));
        } catch (NumberFormatException ex) {
            return NOT_DETECTED;
        }
    }

    /**
     * Reads the best thermal zone under a root directory.
     *
     * @param thermalRoot directory containing {@code thermal_zoneN} entries
     * @return temperature in Celsius, or empty
     */
    static Optional<String> readLinuxThermalZone(Path thermalRoot) {
        if (thermalRoot == null || !Files.isDirectory(thermalRoot)) {
            return Optional.empty();
        }
        List<Zone> zones = new ArrayList<>();
        try (java.util.stream.Stream<Path> paths = Files.list(thermalRoot)) {
            paths.filter(Files::isDirectory)
                    .filter(path -> HardwareInfoUtils.fileName(path).startsWith("thermal_zone"))
                    .sorted(Comparator.comparing(path -> HardwareInfoUtils.fileName(path)))
                    .forEach(path -> readZone(path).ifPresent(zones::add));
        } catch (IOException | SecurityException ex) {
            return Optional.empty();
        }
        return zones.stream()
                .max(Comparator.comparingInt(Zone::score))
                .map(zone -> String.valueOf(Math.round(zone.celsius())));
    }

    private static Optional<Zone> readZone(Path zonePath) {
        Optional<String> type = HardwareInfoUtils.readValue(zonePath.resolve("type"));
        Optional<String> raw = HardwareInfoUtils.readValue(zonePath.resolve("temp"));
        if (!raw.isPresent()) {
            return Optional.empty();
        }
        try {
            double value = Double.parseDouble(raw.get());
            double celsius = value >= 1_000.0 ? value / 1_000.0 : value;
            if (celsius < -50.0 || celsius > 200.0) {
                return Optional.empty();
            }
            return Optional.of(new Zone(type.orElse(""), celsius, score(type.orElse(""))));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private static int score(String type) {
        String normalized = type.toLowerCase(Locale.ROOT);
        if (normalized.contains("x86_pkg_temp")) {
            return 100;
        }
        if (normalized.contains("coretemp") || normalized.contains("k10temp")
                || normalized.contains("cpu")) {
            return 90;
        }
        if (normalized.contains("soc")) {
            return 80;
        }
        if (normalized.contains("acpitz")) {
            return 10;
        }
        return 50;
    }

    private static final class Zone {

        private final String type;
        private final double celsius;
        private final int score;

        Zone(String type, double celsius, int score) {
            this.type = type;
            this.celsius = celsius;
            this.score = score;
        }

        String type() {
            return type;
        }

        double celsius() {
            return celsius;
        }

        int score() {
            return score;
        }
    }
}
