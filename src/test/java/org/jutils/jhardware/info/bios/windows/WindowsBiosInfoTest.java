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
package org.jutils.jhardware.info.bios.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsBiosInfoTest {

    @Test
    void parsesWmiPropertiesAndReleaseDate() {
        Map<String, String> wmi = TestData.map(
                "Manufacturer", "Dell Inc.",
                "SMBIOSBIOSVersion", "1.43.0",
                "ReleaseDate", "20250809000000.000000+000");

        ParsedData data = WindowsBiosInfo.parseWmi(wmi);

        assertEquals("Dell Inc.", data.get("Vendor"));
        assertEquals("1.43.0", data.get("Version"));
        assertEquals("2025-08-09", data.get("Release Date"));
        assertTrue(data.warnings().isEmpty());
    }

    @Test
    void missingVendorProducesAWarning() {
        ParsedData data = WindowsBiosInfo.parseWmi(
                TestData.map("Version", "1.0"));

        assertFalse(data.warnings().isEmpty());
    }
}
