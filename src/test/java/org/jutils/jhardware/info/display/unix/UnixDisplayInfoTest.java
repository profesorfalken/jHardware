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
package org.jutils.jhardware.info.display.unix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.Display;
import org.jutils.jhardware.model.DisplayInfo;
import org.jutils.jhardware.util.FakeCommandRunner;

class UnixDisplayInfoTest {

    private static final String XRANDR = String.join("\n", "Screen 0: minimum 8 x 8, current 1920 x 1080, maximum 16384 x 16384", "eDP-1 connected primary 1920x1080+0+0 (normal left inverted right x axis y axis) 309mm x 174mm", "   1920x1080     60.00*+  59.97    50.00", "   1680x1050     59.95", "DP-1 disconnected (normal left inverted right x axis y axis)", "HDMI-1 connected 2560x1440+1920+0 (normal left inverted right x axis y axis) 597mm x 336mm", "   2560x1440     59.95*+");

    @TempDir
    Path tempDir;

    @Test
    void parsesXrandrOutput() {
        ParsedData data = UnixDisplayInfo.parseXrandr(XRANDR);

        assertEquals(2, data.getInt("numOfDisplays", 0));
        assertEquals("eDP-1", data.get("name_0"));
        assertEquals("1920x1080", data.get("current_res_0"));
        assertEquals("60.00", data.get("current_refresh_rate_0"));
        assertTrue(data.get("available_res_0").contains("1920x1080@60.00"));
        assertTrue(data.get("available_res_0").contains("1680x1050@59.95"));
        assertEquals("HDMI-1", data.get("name_1"));
        assertEquals("2560x1440", data.get("current_res_1"));
        assertEquals("59.95", data.get("current_refresh_rate_1"));
    }

    @Test
    void ignoresDisconnectedOutputs() {
        ParsedData data = UnixDisplayInfo.parseXrandr(XRANDR);
        assertEquals(2, data.getInt("numOfDisplays", 0));
        assertFalse(data.values().containsValue("DP-1"));
    }

    @Test
    void parsesDrmConnectorsAndIgnoresDisconnectedOnes() throws Exception {
        Path connected = Files.createDirectories(tempDir.resolve("card0-eDP-1"));
        TestData.write(connected.resolve("status"), "connected\n");
        TestData.write(connected.resolve("modes"), "1920x1080\n1680x1050\n");
        Path disconnected = Files.createDirectories(tempDir.resolve("card0-DP-1"));
        TestData.write(disconnected.resolve("status"), "disconnected\n");

        ParsedData data = UnixDisplayInfo.parseDrm(tempDir);

        assertEquals(1, data.getInt("numOfDisplays", 0));
        assertEquals("eDP-1", data.get("name_0"));
        assertEquals("1920x1080", data.get("current_res_0"));
        assertEquals("1920x1080;1680x1050", data.get("available_res_0"));
    }

    @Test
    void fallsBackToDrmWhenXrandrIsMissing() {
        UnixDisplayInfo provider = new UnixDisplayInfo(
                FakeCommandRunner.failure("xrandr missing"), tempDir);
        DisplayInfo info = provider.getInfo();

        assertNotNull(info);
        assertTrue(info.getWarnings().stream()
                .anyMatch(warning -> warning.contains("xrandr")));
    }

    @Test
    void connectedOutputWithoutModeLinesDoesNotThrow() {
        UnixDisplayInfo provider = new UnixDisplayInfo(
                FakeCommandRunner.success("eDP-1 connected 1920x1080+0+0\n"),
                tempDir.resolve("missing-drm"));
        DisplayInfo info = provider.getInfo();

        assertEquals(1, info.getDisplayDevices().size());
        Display display = info.getDisplayDevices().get(0);
        assertEquals("eDP-1", display.getName());
        assertEquals("1920x1080", display.getCurrentResolution());
        assertEquals(0, display.getSupportedResolutions().length);
    }

    @Test
    void noDisplayReturnsEmptyListAndWarning() {
        UnixDisplayInfo provider = new UnixDisplayInfo(
                FakeCommandRunner.success("Screen 0: minimum 8 x 8\n"),
                tempDir.resolve("missing-drm"));
        DisplayInfo info = provider.getInfo();

        assertTrue(info.getDisplayDevices().isEmpty());
        assertFalse(info.getWarnings().isEmpty());
    }
}
