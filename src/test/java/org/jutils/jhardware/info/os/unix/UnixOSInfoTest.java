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
package org.jutils.jhardware.info.os.unix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;
import org.jutils.jhardware.model.OSInfo;
import org.jutils.jhardware.util.FakeCommandRunner;

class UnixOSInfoTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesOsReleaseQuotesCommentsAndEscapes() {
        Map<String, String> values = UnixOSInfo.parseOsRelease(Arrays.asList(
                "# comment",
                "NAME=\"Omarchy\"",
                "PRETTY_NAME=\"Omarchy 4.5\"",
                "ID=omarchy",
                "VERSION_ID=\"4.5.0\"",
                "MESSAGE='it\\'s fine'",
                ""));

        assertEquals("Omarchy", values.get("NAME"));
        assertEquals("Omarchy 4.5", values.get("PRETTY_NAME"));
        assertEquals("omarchy", values.get("ID"));
        assertEquals("4.5.0", values.get("VERSION_ID"));
        assertEquals("it's fine", values.get("MESSAGE"));
    }

    @Test
    void readsBootTimeFromProcStat() throws Exception {
        Path procStat = tempDir.resolve("stat");
        TestData.write(procStat, "cpu 1 2 3\nbtime 1700000000\n");

        assertEquals(Optional.of(1700000000L), UnixOSInfo.readBootTime(procStat));
    }

    @Test
    void buildsOsInfoFromOsRelease() throws Exception {
        Path osRelease = tempDir.resolve("os-release");
        TestData.write(osRelease, String.join("\n", "NAME=\"Omarchy\"", "PRETTY_NAME=\"Omarchy\"", "ID=omarchy", "VERSION_ID=\"4.5.0\""));
        Path procStat = tempDir.resolve("stat");
        TestData.write(procStat, "btime 1700000000\n");

        UnixOSInfo provider = new UnixOSInfo(osRelease, procStat,
                FakeCommandRunner.failure("lsb_release missing"));
        OSInfo info = provider.getInfo();

        assertEquals("Omarchy", info.getName());
        assertEquals("Omarchy", info.getManufacturer());
        assertEquals("4.5.0", info.getVersion());
        assertEquals(Instant.ofEpochSecond(1700000000L).toString(),
                info.getLastBootTime());
        assertTrue(info.getWarnings().isEmpty());
    }

    @Test
    void fallsBackToLsbReleaseWhenOsReleaseIsMissing() {
        Path missing = tempDir.resolve("missing");
        Path procStat = tempDir.resolve("stat");
        FakeCommandRunner runner = FakeCommandRunner.success(String.join("\n", "Distributor ID:\tUbuntu", "Description:\tUbuntu 24.04 LTS", "Release:\t24.04"));

        OSInfo info = new UnixOSInfo(missing, procStat, runner).getInfo();

        assertEquals("Ubuntu 24.04 LTS", info.getName());
        assertEquals("Ubuntu", info.getManufacturer());
        assertEquals("24.04", info.getVersion());
        assertTrue(info.getWarnings().stream()
                .anyMatch(warning -> warning.contains("lsb_release")));
    }

    @Test
    void warnsWhenNothingCanBeDetermined() {
        Path missing = tempDir.resolve("missing");
        OSInfo info = new UnixOSInfo(missing, missing,
                FakeCommandRunner.failure("no lsb_release")).getInfo();

        assertEquals(null, info.getName());
        assertTrue(info.getWarnings().size() >= 2);
    }
}
