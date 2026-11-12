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
package org.jutils.jhardware.info.graphicscard.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsGraphicsCardInfoTest {

    @Test
    void deduplicatesCardsReportedOncePerMonitor() {
        List<Map<String, String>> displays = Arrays.asList(
                TestData.map("CardName", "Intel UHD Graphics 620",
                        "Manufacturer", "Intel Corporation",
                        "ChipType", "Intel UHD Graphics Family",
                        "DACType", "Internal",
                        "DeviceType", "Full Device"),
                TestData.map("CardName", "Intel UHD Graphics 620",
                        "Manufacturer", "Intel Corporation",
                        "ChipType", "Intel UHD Graphics Family",
                        "DACType", "Internal",
                        "DeviceType", "Full Device"),
                TestData.map("CardName", "NVIDIA GeForce GTX 970M",
                        "Manufacturer", "NVIDIA",
                        "ChipType", "GeForce GTX 970M",
                        "DACType", "Integrated RAMDAC",
                        "DeviceType", "Full Device"));

        ParsedData data = WindowsGraphicsCardInfo.parseDirectX(displays);

        assertEquals(2, data.getInt("numOfGraphicsCards", 0));
        assertEquals("Intel UHD Graphics 620", data.get("name_0"));
        assertEquals("NVIDIA GeForce GTX 970M", data.get("name_1"));
    }

    @Test
    void emptyDataProducesNoCards() {
        ParsedData data = WindowsGraphicsCardInfo.parseDirectX(Arrays.asList());
        assertEquals(0, data.getInt("numOfGraphicsCards", 0));
    }
}
