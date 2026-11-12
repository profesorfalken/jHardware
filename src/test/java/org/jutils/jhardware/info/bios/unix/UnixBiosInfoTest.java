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
package org.jutils.jhardware.info.bios.unix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.model.BiosInfo;
import org.jutils.jhardware.util.FakeCommandRunner;

class UnixBiosInfoTest {

    @TempDir
    Path tempDir;

    @Test
    void readsDmiFilesWithoutSpawningCommands() throws Exception {
        TestData.write(tempDir.resolve("bios_date"), "09/08/2025\n");
        TestData.write(tempDir.resolve("bios_vendor"), "Dell Inc.\n");
        TestData.write(tempDir.resolve("bios_version"), "1.43.0\n");
        FakeCommandRunner runner = FakeCommandRunner.failure("should not run");

        BiosInfo info = new UnixBiosInfo(tempDir, runner).getInfo();

        assertEquals("09/08/2025", info.getDate());
        assertEquals("Dell Inc.", info.getManufacturer());
        assertEquals("1.43.0", info.getVersion());
        assertEquals(0, runner.invocations());
        assertTrue(info.getWarnings().isEmpty());
    }

    @Test
    void fallsBackToDmidecode() {
        FakeCommandRunner runner = FakeCommandRunner.success(String.join("\n", "BIOS Information", "\tVendor: Dell Inc.", "\tVersion: 1.43.0", "\tRelease Date: 09/08/2025"));

        BiosInfo info = new UnixBiosInfo(tempDir, runner).getInfo();

        assertEquals("Dell Inc.", info.getManufacturer());
        assertEquals("1.43.0", info.getVersion());
        assertEquals("09/08/2025", info.getDate());
        assertEquals(1, runner.invocations());
    }

    @Test
    void reportsWarningsWhenNothingIsAvailable() {
        BiosInfo info = new UnixBiosInfo(tempDir,
                FakeCommandRunner.failure("dmidecode requires root")).getInfo();

        assertFalse(info.getWarnings().isEmpty());
        assertEquals(null, info.getManufacturer());
    }
}
