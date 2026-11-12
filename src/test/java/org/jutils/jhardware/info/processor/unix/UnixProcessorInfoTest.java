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
package org.jutils.jhardware.info.processor.unix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.info.ParsedData;

class UnixProcessorInfoTest {

    private static final String CPUINFO = String.join("\n", "processor\t: 0", "vendor_id\t: GenuineIntel", "cpu family\t: 6", "model\t\t: 142", "model name\t: Intel(R) Core(TM) i7-8665U CPU @ 1.90GHz", "stepping\t: 12", "cpu MHz\t\t: 1900.000", "cache size\t: 8192 KB", "physical id\t: 0", "core id\t\t: 0", "cpu cores\t: 2", "", "processor\t: 1", "vendor_id\t: GenuineIntel", "cpu family\t: 6", "model\t\t: 142", "model name\t: Intel(R) Core(TM) i7-8665U CPU @ 1.90GHz", "stepping\t: 12", "cpu MHz\t\t: 2000.000", "cache size\t: 8192 KB", "physical id\t: 0", "core id\t\t: 1", "cpu cores\t: 2");

    @Test
    void parsesFieldsFromTheFirstProcessorBlock() {
        ParsedData data = UnixProcessorInfo.parseCpuInfo(Arrays.asList(CPUINFO.split("\\R")));

        assertEquals("GenuineIntel", data.get("vendor_id"));
        assertEquals("6", data.get("cpu family"));
        assertEquals("142", data.get("model"));
        assertEquals("Intel(R) Core(TM) i7-8665U CPU @ 1.90GHz",
                data.get("model name"));
        assertEquals("12", data.get("stepping"));
        assertEquals("1900.000", data.get("cpu MHz"));
        assertEquals("8192 KB", data.get("cache size"));
    }

    @Test
    void computesPhysicalCoresAndLogicalProcessors() {
        ParsedData data = UnixProcessorInfo.parseCpuInfo(Arrays.asList(CPUINFO.split("\\R")));

        assertEquals("2", data.get("cpu cores"));
        assertEquals("2", data.get("logical processors"));
    }

    @Test
    void countsBlocksWhenPhysicalIdsAreMissing() {
        String output = String.join("\n", "processor\t: 0", "model name\t: CPU A", "", "processor\t: 1", "model name\t: CPU A", "", "processor\t: 2", "model name\t: CPU A");

        ParsedData data = UnixProcessorInfo.parseCpuInfo(Arrays.asList(output.split("\\R")));

        assertEquals("3", data.get("cpu cores"));
        assertEquals("3", data.get("logical processors"));
    }

    @Test
    void emptyInputProducesAWarning() {
        ParsedData data = UnixProcessorInfo.parseCpuInfo(Arrays.asList());

        assertTrue(data.isEmpty());
        assertFalse(data.warnings().isEmpty());
    }

    @Test
    void missingModelNameProducesAWarning() {
        ParsedData data = UnixProcessorInfo.parseCpuInfo(
                Arrays.asList("processor", "vendor_id\t: GenuineIntel"));

        assertTrue(data.get("model name") == null);
        assertFalse(data.warnings().isEmpty());
    }
}
