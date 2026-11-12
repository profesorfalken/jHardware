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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result of running an external command.
 */
public final class CommandResult {

    private final List<String> command;
    private final int exitCode;
    private final String stdout;
    private final String stderr;
    private final boolean timedOut;
    private final Duration duration;

    /**
     * @param command  command line that was executed
     * @param exitCode process exit code, or {@code -1} when the process could
     *                 not be started or was killed
     * @param stdout   captured standard output
     * @param stderr   captured standard error
     * @param timedOut whether the command exceeded the configured timeout
     * @param duration wall-clock time spent running the command
     */
    public CommandResult(List<String> command, int exitCode, String stdout,
            String stderr, boolean timedOut, Duration duration) {
        this.command = Collections.unmodifiableList(new ArrayList<>(
                Objects.requireNonNull(command, "command")));
        this.exitCode = exitCode;
        this.stdout = stdout == null ? "" : stdout;
        this.stderr = stderr == null ? "" : stderr;
        this.timedOut = timedOut;
        this.duration = duration == null ? Duration.ZERO : duration;
    }

    public List<String> command() {
        return command;
    }

    public int exitCode() {
        return exitCode;
    }

    public String stdout() {
        return stdout;
    }

    public String stderr() {
        return stderr;
    }

    public boolean timedOut() {
        return timedOut;
    }

    public Duration duration() {
        return duration;
    }

    /**
     * @return {@code true} when the command finished with exit code zero and
     *         did not time out
     */
    public boolean isSuccess() {
        return !timedOut && exitCode == 0;
    }

    /**
     * Alias for {@link #stdout()}.
     *
     * @return the captured standard output
     */
    public String output() {
        return stdout;
    }

    @Override
    public String toString() {
        return "CommandResult{command=" + command + ", exitCode=" + exitCode
                + ", timedOut=" + timedOut + ", duration=" + duration + '}';
    }
}
