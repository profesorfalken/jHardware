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
package org.jutils.jhardware;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small helpers that keep the tests readable on Java 8, which lacks the
 * collection factory methods and file-writing convenience methods available in
 * newer JDKs.
 */
public final class TestData {

    private TestData() {
    }

    /**
     * @param pairs alternating keys and values
     * @param <K>   key type
     * @param <V>   value type
     * @return a mutable map with the given entries
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> map(Object... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("map requires key/value pairs");
        }
        Map<K, V> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            values.put((K) pairs[i], (V) pairs[i + 1]);
        }
        return values;
    }

    /**
     * Writes a UTF-8 text file.
     *
     * @param path    destination
     * @param content file content
     * @throws IOException when the file cannot be written
     */
    public static void write(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
