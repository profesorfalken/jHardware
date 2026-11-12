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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;

class TemperatureUtilsTest {

    @TempDir
    Path thermalRoot;

    @Test
    void prefersCpuPackageZoneOverAcpi() throws Exception {
        createZone("thermal_zone0", "acpitz", "25000");
        createZone("thermal_zone1", "x86_pkg_temp", "70000");

        assertEquals(Optional.of("70"),
                TemperatureUtils.readLinuxThermalZone(thermalRoot));
    }

    @Test
    void fallsBackToAcpiZone() throws Exception {
        createZone("thermal_zone0", "acpitz", "25000");

        assertEquals(Optional.of("25"),
                TemperatureUtils.readLinuxThermalZone(thermalRoot));
    }

    @Test
    void ignoresInvalidValues() throws Exception {
        createZone("thermal_zone0", "x86_pkg_temp", "not-a-number");
        createZone("thermal_zone1", "acpitz", "30000");

        assertEquals(Optional.of("30"),
                TemperatureUtils.readLinuxThermalZone(thermalRoot));
    }

    @Test
    void missingDirectoryIsEmpty() {
        assertFalse(TemperatureUtils.readLinuxThermalZone(
                thermalRoot.resolve("missing")).isPresent());
        assertFalse(TemperatureUtils.readLinuxThermalZone(null).isPresent());
    }

    private void createZone(String name, String type, String temperature)
            throws Exception {
        Path zone = Files.createDirectories(thermalRoot.resolve(name));
        TestData.write(zone.resolve("type"), type + "\n");
        TestData.write(zone.resolve("temp"), temperature + "\n");
    }
}
