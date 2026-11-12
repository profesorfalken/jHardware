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
package org.jutils.jhardware.info.processor.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsProcessorInfoTest {

    @Test
    void parsesWmiProperties() {
        Map<String, String> wmi = TestData.map(
                "Description", "Intel64 Family 6 Model 142 Stepping 12",
                "Name", "Intel(R) Core(TM) i7-8665U CPU @ 1.90GHz",
                "MaxClockSpeed", "1900",
                "Manufacturer", "GenuineIntel",
                "NumberOfCores", "4",
                "NumberOfLogicalProcessors", "8",
                "L3CacheSize", "8192");

        ParsedData data = WindowsProcessorInfo.parseWmi(wmi);

        assertEquals("6", data.get("cpu family"));
        assertEquals("142", data.get("model"));
        assertEquals("12", data.get("stepping"));
        assertEquals("Intel(R) Core(TM) i7-8665U CPU @ 1.90GHz",
                data.get("model name"));
        assertEquals("1900", data.get("cpu MHz"));
        assertEquals("GenuineIntel", data.get("vendor_id"));
        assertEquals("4", data.get("cpu cores"));
        assertEquals("8192 KB", data.get("cache size"));
        assertTrue(data.warnings().isEmpty());
    }

    @Test
    void unknownDescriptionProducesAWarning() {
        ParsedData data = WindowsProcessorInfo.parseWmi(TestData.map(
                "Description", "Some virtual CPU",
                "Name", "Virtual CPU",
                "NumberOfCores", "2"));

        assertFalse(data.warnings().isEmpty());
        assertEquals("Virtual CPU", data.get("model name"));
    }

    @Test
    void emptyWmiMapProducesEmptyData() {
        assertTrue(WindowsProcessorInfo.parseWmi(TestData.map()).isEmpty());
        assertTrue(WindowsProcessorInfo.parseWmi(null).isEmpty());
    }
}
