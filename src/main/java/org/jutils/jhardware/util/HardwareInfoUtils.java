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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Small, dependency-free helpers shared by the platform implementations.
 *
 * <p>File and command failures are reported as empty results or warnings;
 * library code does not write to the host application's logging system.</p>
 */
public class HardwareInfoUtils {

    /** Marker returned by the legacy {@link #extractText(String, String)}. */
    public static final String NOT_FOUND = "NOT_FOUND";

    private HardwareInfoUtils() {
    }

    /**
     * Reads all lines of a UTF-8 text file.
     *
     * @param filePath absolute path
     * @return the lines, or an empty list when the file cannot be read
     */
    public static List<String> readLines(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return Arrays.asList();
        }
        try {
            return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException | InvalidPathException | SecurityException ex) {
            return Arrays.asList();
        }
    }

    /**
     * Reads the first non-empty line of a UTF-8 text file.
     *
     * @param filePath absolute path
     * @return the first non-empty line, if any
     */
    public static Optional<String> readFirstLine(String filePath) {
        return readLines(filePath).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .findFirst();
    }

    /**
     * Reads a value from a sysfs-style file and trims it.
     *
     * @param path file path
     * @return the trimmed content, or empty when unavailable
     */
    public static Optional<String> readValue(Path path) {
        if (path == null) {
            return Optional.empty();
        }
        try {
            String value = new String(Files.readAllBytes(path),
                    StandardCharsets.UTF_8).trim();
            return value.isEmpty() ? Optional.empty() : Optional.of(value);
        } catch (IOException | SecurityException ex) {
            return Optional.empty();
        }
    }

    /**
     * Applies a regular expression and returns its first capturing group.
     *
     * @param text  input text, may be {@code null}
     * @param regex expression with at least one capturing group
     * @return the captured value, or empty when there is no match
     */
    public static Optional<String> extract(String text, String regex) {
        if (text == null || regex == null) {
            return Optional.empty();
        }
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.ofNullable(matcher.group(1));
    }

    /**
     * Legacy variant that returns {@link #NOT_FOUND} instead of throwing.
     *
     * @param text  input text
     * @param regex expression with at least one capturing group
     * @return the captured value or {@link #NOT_FOUND}
     */
    public static String extractText(String text, String regex) {
        return extract(text, regex).orElse(NOT_FOUND);
    }

    /**
     * Converts an underscore-separated identifier to camel case.
     *
     * @param value input, may be {@code null}
     * @return the camel-case value, never {@code null}
     */
    public static String toCamelCase(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder(value.length());
        for (String part : value.split("_")) {
            if (part.isEmpty()) {
                continue;
            }
            result.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase(java.util.Locale.ROOT));
            }
        }
        return result.toString();
    }

    /**
     * Removes all whitespace characters.
     *
     * @param value input, may be {@code null}
     * @return the value without whitespace, or {@code null}
     */
    public static String removeAllSpaces(String value) {
        return value == null ? null : value.replaceAll("\\s+", "");
    }

    /**
     * Parses a key/value block, for example {@code "Vendor: Dell"}.
     *
     * @param lines     source lines
     * @param separator separator between key and value
     * @return an insertion-ordered map
     */
    public static Map<String, String> parseKeyValueLines(List<String> lines, String separator) {
        Map<String, String> values = new LinkedHashMap<>();
        if (lines == null || separator == null) {
            return values;
        }
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            int index = line.indexOf(separator);
            if (index <= 0) {
                continue;
            }
            String key = line.substring(0, index).trim();
            String value = line.substring(index + separator.length()).trim();
            if (!key.isEmpty()) {
                values.put(key, value);
            }
        }
        return values;
    }

    /**
     * @param value  first candidate
     * @param others fallback candidates
     * @return the first non-blank value, or {@code null}
     */
    public static String firstNonBlank(String value, String... others) {
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        for (String candidate : others) {
            if (candidate != null && !candidate.trim().isEmpty()) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Removes matching single or double quotes around a value.
     *
     * @param value input, may be {@code null}
     * @return the unquoted value, or {@code null}
     */
    public static String stripQuotes(String value) {
        if (value == null || value.length() < 2) {
            return value;
        }
        char first = value.charAt(0);
        char last = value.charAt(value.length() - 1);
        if ((first == '"' || first == '\'') && first == last) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Parses an integer, returning a fallback instead of throwing.
     *
     * @param value        input
     * @param defaultValue value returned when parsing fails
     * @return the parsed value or the fallback
     */
    public static int parseInt(String value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * Parses a long, returning a fallback instead of throwing.
     *
     * @param value        input
     * @param defaultValue value returned when parsing fails
     * @return the parsed value or the fallback
     */
    public static long parseLong(String value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * @param lines lines to join
     * @return the joined lines using {@code \n}
     */
    public static String joinLines(List<String> lines) {
        return lines == null ? "" : String.join("\n", lines);
    }

    /**
     * @param path path, may be {@code null}
     * @return the file name, or an empty string when unavailable
     */
    public static String fileName(Path path) {
        Path fileName = path == null ? null : path.getFileName();
        return fileName == null ? "" : fileName.toString();
    }

    /**
     * @param filePath file to read
     * @return a stream of lines, empty when the file is unavailable
     * @deprecated use {@link #readLines(String)}
     */
    @Deprecated
    public static Stream<String> readFile(String filePath) {
        return readLines(filePath).stream();
    }

    /**
     * @param filePath file to read
     * @return the first line or {@code null}
     * @deprecated use {@link #readFirstLine(String)}
     */
    @Deprecated
    public static String getSingleValueFromFile(String filePath) {
        return readFirstLine(filePath).orElse(null);
    }

    /**
     * @param command command to execute
     * @return stdout when the command succeeded, otherwise {@code null}
     * @deprecated inject a {@link CommandRunner} instead
     */
    @Deprecated
    public static String executeCommand(String... command) {
        CommandResult result = SystemCommandRunner.defaultRunner().run(command);
        if (result.exitCode() < 0 && !result.timedOut()) {
            return null;
        }
        return result.stdout();
    }

    /**
     * Checks whether password-less {@code sudo} is available.
     *
     * <p>This legacy helper is the only place where jHardware may execute
     * {@code sudo}; the built-in providers never use it. It is kept for
     * compatibility with 0.8.x and should not be called by new code.</p>
     *
     * @return {@code true} when {@code sudo -n true} succeeds
     * @deprecated the built-in providers never escalate privileges
     */
    @Deprecated
    public static boolean isSudo() {
        CommandResult result = SystemCommandRunner.defaultRunner()
                .run("sudo", "-n", "true");
        return result.isSuccess() && result.stdout().trim().isEmpty();
    }

}
