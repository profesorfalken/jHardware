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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link CommandRunner} backed by {@link ProcessBuilder}.
 *
 * <p>It captures stdout and stderr separately, closes the child's stdin,
 * enforces a timeout and never throws for a missing executable or a non-zero
 * exit code. That makes command failures ordinary data for callers.</p>
 */
public final class SystemCommandRunner implements CommandRunner {

    /** Default timeout for a single command. */
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private static final long READER_JOIN_MILLIS = 2_000L;

    private static final SystemCommandRunner DEFAULT
            = new SystemCommandRunner(DEFAULT_TIMEOUT);

    private final Duration timeout;

    public SystemCommandRunner() {
        this(DEFAULT_TIMEOUT);
    }

    /**
     * @param timeout maximum time to wait for a command; must be positive
     */
    public SystemCommandRunner(Duration timeout) {
        Objects.requireNonNull(timeout, "timeout");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        this.timeout = timeout;
    }

    /**
     * @return a shared runner using {@link #DEFAULT_TIMEOUT}
     */
    public static SystemCommandRunner defaultRunner() {
        return DEFAULT;
    }

    public Duration getTimeout() {
        return timeout;
    }

    @Override
    public CommandResult run(List<String> command) {
        Objects.requireNonNull(command, "command");
        if (command.isEmpty()) {
            throw new IllegalArgumentException("command must not be empty");
        }

        long startNanos = System.nanoTime();
        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            process = builder.start();
            process.getOutputStream().close();

            StreamCollector stdout = new StreamCollector(process.getInputStream());
            StreamCollector stderr = new StreamCollector(process.getErrorStream());
            stdout.start();
            stderr.start();

            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                process.waitFor(2, TimeUnit.SECONDS);
                return new CommandResult(command, -1, stdout.await(), stderr.await(),
                        true, elapsed(startNanos));
            }

            return new CommandResult(command, process.exitValue(), stdout.await(),
                    stderr.await(), false, elapsed(startNanos));
        } catch (IOException ex) {
            return new CommandResult(command, -1, "", ex.toString(), false,
                    elapsed(startNanos));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            return new CommandResult(command, -1, "", "interrupted", false,
                    elapsed(startNanos));
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private static Duration elapsed(long startNanos) {
        return Duration.ofNanos(System.nanoTime() - startNanos);
    }

    /**
     * Consumes one stream on a daemon thread so both pipes are drained
     * concurrently and the child cannot deadlock on a full buffer.
     */
    private static final class StreamCollector extends Thread {

        private final InputStream input;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        StreamCollector(InputStream input) {
            super("jhardware-command-reader");
            setDaemon(true);
            this.input = input;
        }

        @Override
        public void run() {
            try (InputStream stream = input) {
                byte[] chunk = new byte[8192];
                int read;
                while ((read = stream.read(chunk)) >= 0) {
                    buffer.write(chunk, 0, read);
                }
            } catch (IOException ignored) {
                // A closed pipe is expected when a command is killed; the
                // bytes collected so far are still useful.
            }
        }

        String await() {
            try {
                join(READER_JOIN_MILLIS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
