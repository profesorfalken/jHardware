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

import java.util.Arrays;
import java.util.List;

/**
 * Abstraction over external command execution. Implementations must never
 * block indefinitely and must capture both output streams.
 *
 * <p>The interface is intentionally small so tests can provide deterministic
 * results without spawning processes.</p>
 */
@FunctionalInterface
public interface CommandRunner {

    /**
     * Runs a command and waits for it to finish.
     *
     * @param command command and arguments; must not be empty
     * @return the captured result, never {@code null}
     */
    CommandResult run(List<String> command);

    /**
     * Convenience overload.
     *
     * @param command command and arguments
     * @return the captured result
     */
    default CommandResult run(String... command) {
        return run(Arrays.asList(command));
    }
}
