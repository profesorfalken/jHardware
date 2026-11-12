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
package org.jutils.jhardware.info.display.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsDisplayInfoTest {

    @Test
    void parsesCurrentMode() {
        WindowsDisplayInfo.Mode mode = WindowsDisplayInfo
                .parseMode("1920 x 1080 (32 bit) (60Hz)");

        assertEquals("1920x1080", mode.resolution());
        assertEquals("60", mode.refresh());
    }

    @Test
    void invalidModeReturnsNull() {
        assertNull(WindowsDisplayInfo.parseMode(null));
        assertNull(WindowsDisplayInfo.parseMode("unknown"));
    }

    @Test
    void parsesMultipleDirectXDisplays() {
        List<Map<String, String>> displays = Arrays.asList(
                TestData.map("MonitorName", "Monitor A",
                        "CurrentMode", "1920 x 1080 (32 bit) (60Hz)"),
                TestData.map("CardName", "Card B",
                        "CurrentMode", "2560 x 1440 (32 bit) (144Hz)"));

        ParsedData data = WindowsDisplayInfo.parseDirectX(displays);

        assertEquals(2, data.getInt("numOfDisplays", 0));
        assertEquals("Monitor A", data.get("name_0"));
        assertEquals("1920x1080", data.get("current_res_0"));
        assertEquals("60", data.get("current_refresh_rate_0"));
        assertEquals("Card B", data.get("name_1"));
        assertEquals("2560x1440", data.get("current_res_1"));
        assertEquals("144", data.get("current_refresh_rate_1"));
    }

    @Test
    void emptyDirectXDataProducesNoDisplays() {
        ParsedData data = WindowsDisplayInfo.parseDirectX(Arrays.asList());
        assertEquals(0, data.getInt("numOfDisplays", 0));
    }
}
