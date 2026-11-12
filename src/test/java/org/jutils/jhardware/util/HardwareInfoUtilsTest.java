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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;

class HardwareInfoUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void extractReturnsFirstCapturingGroup() {
        assertEquals(Optional.of("1920x1080"),
                HardwareInfoUtils.extract("mode 1920x1080+0+0", "(\\d+x\\d+)"));
    }

    @Test
    void extractReturnsEmptyWhenThereIsNoMatch() {
        assertFalse(HardwareInfoUtils.extract("hello", "(x)").isPresent());
        assertFalse(HardwareInfoUtils.extract(null, "(x)").isPresent());
    }

    @Test
    void extractTextDoesNotThrowWhenThereIsNoMatch() {
        assertEquals(HardwareInfoUtils.NOT_FOUND,
                HardwareInfoUtils.extractText("hello", "(x)"));
        assertEquals(HardwareInfoUtils.NOT_FOUND,
                HardwareInfoUtils.extractText(null, "(x)"));
    }

    @Test
    void toCamelCaseHandlesEmptyPartsAndEmptyInput() {
        assertEquals("OsPrettyName", HardwareInfoUtils.toCamelCase("OS_PRETTY_NAME"));
        assertEquals("", HardwareInfoUtils.toCamelCase(""));
        assertEquals("", HardwareInfoUtils.toCamelCase(null));
        assertEquals("Abc", HardwareInfoUtils.toCamelCase("_abc_"));
    }

    @Test
    void readsFirstLineAndLinesFromFile() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        TestData.write(file, "\n  first  \nsecond\n");

        assertEquals(Optional.of("first"),
                HardwareInfoUtils.readFirstLine(file.toString()));
        assertEquals(Arrays.asList("", "  first  ", "second"),
                HardwareInfoUtils.readLines(file.toString()));
    }

    @Test
    void missingFileIsAnEmptyResult() {
        assertTrue(HardwareInfoUtils.readLines("/does/not/exist").isEmpty());
        assertFalse(HardwareInfoUtils.readFirstLine("/does/not/exist").isPresent());
        assertNull(HardwareInfoUtils.getSingleValueFromFile("/does/not/exist"));
    }

    @Test
    void parsesKeyValueLines() {
        Map<String, String> values = HardwareInfoUtils.parseKeyValueLines(
                Arrays.asList("Vendor: Dell Inc.", "Version: 1.43.0", "ignored"),
                ":");
        assertEquals("Dell Inc.", values.get("Vendor"));
        assertEquals("1.43.0", values.get("Version"));
        assertEquals(2, values.size());
    }

    @Test
    void parseNumbersFallBackInsteadOfThrowing() {
        assertEquals(42, HardwareInfoUtils.parseInt("42", 0));
        assertEquals(7, HardwareInfoUtils.parseInt("not-a-number", 7));
        assertEquals(7, HardwareInfoUtils.parseInt(null, 7));
        assertEquals(42L, HardwareInfoUtils.parseLong("42", 0L));
        assertEquals(7L, HardwareInfoUtils.parseLong("x", 7L));
    }

    @Test
    void stripsMatchingQuotes() {
        assertEquals("value", HardwareInfoUtils.stripQuotes("\"value\""));
        assertEquals("value", HardwareInfoUtils.stripQuotes("'value'"));
        assertEquals("\"value", HardwareInfoUtils.stripQuotes("\"value"));
        assertNull(HardwareInfoUtils.stripQuotes(null));
    }

    @Test
    void removeAllSpacesKeepsNull() {
        assertEquals("abc", HardwareInfoUtils.removeAllSpaces(" a b\tc "));
        assertNull(HardwareInfoUtils.removeAllSpaces(null));
    }

    @Test
    void deprecatedExecuteCommandDoesNotThrowForMissingExecutable() {
        assertNull(HardwareInfoUtils.executeCommand("jhardware-command-that-does-not-exist"));
    }

    @Test
    void emptyCommandIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> SystemCommandRunner.defaultRunner().run(Arrays.asList()));
    }
}
