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
package org.jutils.jhardware.info.motherboard.unix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.model.MotherboardInfo;
import org.jutils.jhardware.util.FakeCommandRunner;

class UnixMotherboardInfoTest {

    @TempDir
    Path tempDir;

    @Test
    void readsDmiFiles() throws Exception {
        TestData.write(tempDir.resolve("board_name"), "07WDVW\n");
        TestData.write(tempDir.resolve("board_vendor"), "Dell Inc.\n");
        TestData.write(tempDir.resolve("board_version"), "A01\n");
        FakeCommandRunner runner = FakeCommandRunner.failure("should not run");

        MotherboardInfo info = new UnixMotherboardInfo(tempDir, runner).getInfo();

        assertEquals("07WDVW", info.getName());
        assertEquals("Dell Inc.", info.getManufacturer());
        assertEquals("A01", info.getVersion());
        assertEquals(0, runner.invocations());
        assertTrue(info.getWarnings().isEmpty());
    }

    @Test
    void fallsBackToDmidecodeAndMapsManufacturerToVendor() {
        FakeCommandRunner runner = FakeCommandRunner.success(String.join("\n", "Base Board Information", "\tManufacturer: Dell Inc.", "\tProduct Name: 07WDVW", "\tVersion: A01"));

        MotherboardInfo info = new UnixMotherboardInfo(tempDir, runner).getInfo();

        assertEquals("07WDVW", info.getName());
        assertEquals("Dell Inc.", info.getManufacturer());
        assertEquals("A01", info.getVersion());
    }

    @Test
    void reportsWarningsWhenNothingIsAvailable() {
        MotherboardInfo info = new UnixMotherboardInfo(tempDir,
                FakeCommandRunner.failure("no privileges")).getInfo();

        assertFalse(info.getWarnings().isEmpty());
    }
}
