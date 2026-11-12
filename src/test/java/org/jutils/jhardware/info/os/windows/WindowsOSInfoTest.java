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
package org.jutils.jhardware.info.os.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsOSInfoTest {

    @Test
    void parsesWmiPropertiesAndBootTime() {
        ParsedData data = WindowsOSInfo.parseWmi(TestData.map(
                "Caption", "Microsoft Windows 11 Pro",
                "Manufacturer", "Microsoft Corporation",
                "Version", "10.0.22631",
                "LastBootUpTime", "20261111155822.000000+060"));

        assertEquals("Microsoft Windows 11 Pro", data.get("Name"));
        assertEquals("Microsoft Corporation", data.get("Manufacturer"));
        assertEquals("10.0.22631", data.get("Version"));
        assertEquals("2026-11-11T15:58:22+01:00", data.get("LastBootTime"));
    }

    @Test
    void emptyMapProducesEmptyData() {
        assertTrue(WindowsOSInfo.parseWmi(TestData.map()).isEmpty());
        assertTrue(WindowsOSInfo.parseWmi(null).isEmpty());
    }

    @Test
    void missingCaptionProducesAWarning() {
        ParsedData data = WindowsOSInfo.parseWmi(TestData.map("Version", "10.0"));
        assertNull(data.get("Name"));
        assertFalse(data.warnings().isEmpty());
    }
}
