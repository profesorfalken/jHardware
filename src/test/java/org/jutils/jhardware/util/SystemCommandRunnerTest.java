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
package org.jutils.jhardware.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

class SystemCommandRunnerTest {

    @Test
    void capturesStandardStreams() {
        CommandResult result = SystemCommandRunner.defaultRunner()
                .run("java", "-version");
        assertTrue(result.isSuccess());
        assertFalse(result.stdout().trim().isEmpty() && result.stderr().trim().isEmpty());
        assertNotNull(result.duration());
    }

    @Test
    void missingExecutableIsReportedAsFailure() {
        CommandResult result = SystemCommandRunner.defaultRunner()
                .run("jhardware-command-that-does-not-exist");
        assertFalse(result.isSuccess());
        assertFalse(result.timedOut());
        assertFalse(result.stderr().trim().isEmpty());
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void nonZeroExitCodeIsPreserved() {
        CommandResult result = SystemCommandRunner.defaultRunner()
                .run("sh", "-c", "exit 3");
        assertFalse(result.isSuccess());
        assertTrue(result.exitCode() == 3);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void timeoutKillsTheProcess() {
        SystemCommandRunner runner = new SystemCommandRunner(Duration.ofMillis(200));
        CommandResult result = runner.run("sh", "-c", "sleep 5");
        assertTrue(result.timedOut());
        assertFalse(result.isSuccess());
        assertTrue(result.duration().getSeconds() < 5);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void rejectsEmptyCommand() {
        SystemCommandRunner runner = SystemCommandRunner.defaultRunner();
        try {
            runner.run(Arrays.asList());
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }
}
