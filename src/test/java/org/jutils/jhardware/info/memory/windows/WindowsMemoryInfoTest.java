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
package org.jutils.jhardware.info.memory.windows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.info.ParsedData;

class WindowsMemoryInfoTest {

    @Test
    void usesComputerSystemTotalPhysicalMemory() {
        ParsedData data = WindowsMemoryInfo.parseWmi(
                TestData.map("FreePhysicalMemory", "4194304"),
                TestData.map("AvailableKBytes", "8388608"),
                TestData.map("TotalPhysicalMemory", "34359738368"),
                Arrays.asList());

        assertEquals("34359738368", data.get("MemTotalBytes"));
        assertEquals(String.valueOf(4194304L * 1024L), data.get("MemFreeBytes"));
        assertEquals(String.valueOf(8388608L * 1024L), data.get("MemAvailableBytes"));
        assertEquals("34359738368 bytes", data.get("MemTotal"));
    }

    @Test
    void sumsPhysicalMemoryModulesWhenNeeded() {
        ParsedData data = WindowsMemoryInfo.parseWmi(
                TestData.map(),
                TestData.map(),
                TestData.map(),
                Arrays.asList(TestData.map("Capacity", "17179869184"),
                        TestData.map("Capacity", "17179869184")));

        assertEquals("34359738368", data.get("MemTotalBytes"));
    }

    @Test
    void fallsBackToTotalVisibleMemorySize() {
        ParsedData data = WindowsMemoryInfo.parseWmi(
                TestData.map("TotalVisibleMemorySize", "33554432"),
                TestData.map(),
                TestData.map(),
                Arrays.asList());

        assertEquals("34359738368", data.get("MemTotalBytes"));
    }

    @Test
    void missingTotalProducesAWarning() {
        ParsedData data = WindowsMemoryInfo.parseWmi(
                TestData.map(), TestData.map(), TestData.map(), Arrays.asList());
        assertNull(data.get("MemTotalBytes"));
        assertFalse(data.warnings().isEmpty());
    }

    @Test
    void missingAvailableFallsBackToFree() {
        ParsedData data = WindowsMemoryInfo.parseWmi(
                TestData.map("FreePhysicalMemory", "1024"),
                TestData.map(),
                TestData.map("TotalPhysicalMemory", "4096"),
                Arrays.asList());

        assertEquals(String.valueOf(1024L * 1024L), data.get("MemAvailableBytes"));
        assertTrue(data.get("MemAvailable").contains("bytes"));
    }
}
