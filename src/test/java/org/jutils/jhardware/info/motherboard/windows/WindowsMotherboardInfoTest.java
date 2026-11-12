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
package org.jutils.jhardware.info.motherboard.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsMotherboardInfoTest {

    @Test
    void parsesWmiProperties() {
        Map<String, String> wmi = TestData.map(
                "Manufacturer", "Dell Inc.",
                "Product", "07WDVW",
                "Version", "A01");

        ParsedData data = WindowsMotherboardInfo.parseWmi(wmi);

        assertEquals("Dell Inc.", data.get("Vendor"));
        assertEquals("07WDVW", data.get("Product Name"));
        assertEquals("A01", data.get("Version"));
        assertTrue(data.warnings().isEmpty());
    }

    @Test
    void emptyWmiMapProducesEmptyData() {
        assertTrue(WindowsMotherboardInfo.parseWmi(TestData.map()).isEmpty());
    }
}
