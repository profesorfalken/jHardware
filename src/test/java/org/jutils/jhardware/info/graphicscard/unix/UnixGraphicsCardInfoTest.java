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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.GraphicsCard;
import org.jutils.jhardware.model.GraphicsCardInfo;
import org.jutils.jhardware.util.FakeCommandRunner;

class UnixGraphicsCardInfoTest {

    private static final String LSHW = String.join("\n", "*-display", "     description: VGA compatible controller", "     product: WhiskeyLake-U GT2 [UHD Graphics 620]", "     vendor: Intel Corporation", "     physical id: 2", "     bus info: pci@0000:00:02.0", "     version: 02", "*-display", "     description: 3D controller", "     product: GM204M [GeForce GTX 970M]", "     vendor: NVIDIA Corporation");

    private static final String LSPCI = String.join("\n", "00:02.0 \"VGA compatible controller [0300]\" \"Intel Corporation [8086]\" \"WhiskeyLake-U GT2 [UHD Graphics 620] [8086:3ea0]\" -r02 \"Dell [1028]\" \"Device [0904]\"", "01:00.0 \"3D controller [0302]\" \"NVIDIA Corporation [10de]\" \"GM204M [GeForce GTX 970M] [10de:1618]\" -ra1 \"Dell [1028]\" \"Device [0904]\"", "00:1f.3 \"Audio device [0403]\" \"Intel Corporation [8086]\" \"Cannon Point-LP HD Audio [8086:9dc8]\" -r30 \"Dell [1028]\" \"Device [0904]\"");

    @TempDir
    Path tempDir;

    @Test
    void parsesLshwDisplays() {
        ParsedData data = UnixGraphicsCardInfo.parseLshw(LSHW);

        assertEquals(2, data.getInt("numOfGraphicsCards", 0));
        assertEquals("WhiskeyLake-U GT2 [UHD Graphics 620]", data.get("name_0"));
        assertEquals("Intel Corporation", data.get("manufacturer_0"));
        assertEquals("VGA compatible controller", data.get("chip_type_0"));
        assertEquals("GM204M [GeForce GTX 970M]", data.get("name_1"));
    }

    @Test
    void parsesLspciAndIgnoresAudioDevices() {
        ParsedData data = UnixGraphicsCardInfo.parseLspci(LSPCI);

        assertEquals(2, data.getInt("numOfGraphicsCards", 0));
        assertEquals("Intel Corporation", data.get("manufacturer_0"));
        assertEquals("WhiskeyLake-U GT2 [UHD Graphics 620]", data.get("name_0"));
        assertEquals("NVIDIA Corporation", data.get("manufacturer_1"));
        assertEquals("GM204M [GeForce GTX 970M]", data.get("name_1"));
    }

    @Test
    void parsesSysfsCards() throws Exception {
        Path card = Files.createDirectories(tempDir.resolve("card0/device"));
        TestData.write(card.resolve("vendor"), "0x8086\n");
        TestData.write(card.resolve("device"), "0x3ea0\n");
        TestData.write(card.resolve("uevent"), "DRIVER=i915\nPCI_CLASS=30000\n");

        ParsedData data = UnixGraphicsCardInfo.parseSysfs(tempDir);

        assertEquals(1, data.getInt("numOfGraphicsCards", 0));
        assertEquals("PCI 0x8086:0x3ea0 (i915)", data.get("name_0"));
        assertEquals("0x8086", data.get("manufacturer_0"));
    }

    @Test
    void fallsBackFromLshwToLspci() {
        FakeCommandRunner runner = new FakeCommandRunner(command -> {
            if (command.contains("lshw")) {
                return FakeCommandRunner.result(command, -1, "", "lshw missing");
            }
            return FakeCommandRunner.result(command, 0, LSPCI, "");
        });

        GraphicsCardInfo info = new UnixGraphicsCardInfo(
                runner, tempDir.resolve("missing-drm")).getInfo();

        assertEquals(2, info.getGraphicsCards().size());
        GraphicsCard first = info.getGraphicsCards().get(0);
        assertEquals("WhiskeyLake-U GT2 [UHD Graphics 620]", first.getName());
        assertEquals("Intel Corporation", first.getManufacturer());
    }

    @Test
    void noDataReturnsEmptyListWithoutThrowing() {
        GraphicsCardInfo info = new UnixGraphicsCardInfo(
                FakeCommandRunner.failure("no tools"),
                tempDir.resolve("missing-drm")).getInfo();

        assertTrue(info.getGraphicsCards().isEmpty());
        assertFalse(info.getWarnings().isEmpty());
    }
}
