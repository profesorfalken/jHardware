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
package org.jutils.jhardware.info.memory.unix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.info.ParsedData;

class UnixMemoryInfoTest {

    @Test
    void parsesMemoryValuesAndConvertsToBytes() {
        String output = String.join("\n", "MemTotal:       32662336 kB", "MemFree:         1447704 kB", "MemAvailable:   19120828 kB", "Buffers:            1488 kB", "Cached:          19962616 kB");

        ParsedData data = UnixMemoryInfo.parseMeminfo(Arrays.asList(output.split("\\R")));

        assertEquals("32662336 kB", data.get("MemTotal"));
        assertEquals("1447704 kB", data.get("MemFree"));
        assertEquals("19120828 kB", data.get("MemAvailable"));
        assertEquals(String.valueOf(32662336L * 1024L), data.get("MemTotalBytes"));
        assertEquals(String.valueOf(1447704L * 1024L), data.get("MemFreeBytes"));
        assertEquals(String.valueOf(19120828L * 1024L), data.get("MemAvailableBytes"));
    }

    @Test
    void computesMemAvailableWhenMissing() {
        String output = String.join("\n", "MemTotal:       1000 kB", "MemFree:         100 kB", "Buffers:          10 kB", "Cached:          200 kB");

        ParsedData data = UnixMemoryInfo.parseMeminfo(Arrays.asList(output.split("\\R")));

        assertEquals(String.valueOf(310L * 1024L), data.get("MemAvailableBytes"));
        assertTrue(data.get("MemAvailable").contains("bytes"));
        assertFalse(data.warnings().isEmpty());
    }

    @Test
    void missingMemTotalProducesAWarning() {
        ParsedData data = UnixMemoryInfo.parseMeminfo(Arrays.asList("MemFree: 1 kB"));
        assertNull(data.get("MemTotal"));
        assertFalse(data.warnings().isEmpty());
    }

    @Test
    void parseBytesUnderstandsUnits() {
        assertEquals(1024L, UnixMemoryInfo.parseBytes("1 kB"));
        assertEquals(1024L * 1024L, UnixMemoryInfo.parseBytes("1 MB"));
        assertEquals(1024L * 1024L * 1024L, UnixMemoryInfo.parseBytes("1 GB"));
        assertEquals(123L, UnixMemoryInfo.parseBytes("123"));
        assertEquals(-1L, UnixMemoryInfo.parseBytes(null));
        assertEquals(-1L, UnixMemoryInfo.parseBytes("not a value"));
    }
}
