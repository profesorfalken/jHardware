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
package org.jutils.jhardware.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutable accumulator used by platform parsers.
 *
 * <p>It carries the raw key/value payload plus a list of non-fatal warnings.
 * Setters in the public models receive immutable copies of both.</p>
 */
public final class ParsedData {

    private final Map<String, String> values = new LinkedHashMap<>();
    private final List<String> warnings = new ArrayList<>();

    /**
     * @return an empty accumulator
     */
    public static ParsedData empty() {
        return new ParsedData();
    }

    /**
     * Stores a value, ignoring {@code null} keys and values.
     *
     * @param key   key
     * @param value value
     * @return this instance
     */
    public ParsedData put(String key, String value) {
        if (key != null && value != null) {
            values.put(key, value);
        }
        return this;
    }

    /**
     * Stores all entries from a map.
     *
     * @param map source map, may be {@code null}
     * @return this instance
     */
    public ParsedData putAll(Map<String, String> map) {
        if (map != null) {
            map.forEach(this::put);
        }
        return this;
    }

    /**
     * Adds a warning.
     *
     * @param warning warning text, ignored when blank
     * @return this instance
     */
    public ParsedData warn(String warning) {
        if (warning != null && !warning.trim().isEmpty()) {
            warnings.add(warning);
        }
        return this;
    }

    /**
     * Adds several warnings.
     *
     * @param newWarnings warnings to add
     * @return this instance
     */
    public ParsedData addWarnings(Collection<String> newWarnings) {
        if (newWarnings != null) {
            newWarnings.forEach(this::warn);
        }
        return this;
    }

    /**
     * Merges values and warnings from another accumulator.
     *
     * @param other source, may be {@code null}
     * @return this instance
     */
    public ParsedData merge(ParsedData other) {
        if (other != null) {
            putAll(other.values);
            addWarnings(other.warnings);
        }
        return this;
    }

    public String get(String key) {
        return values.get(key);
    }

    public int getInt(String key, int defaultValue) {
        return org.jutils.jhardware.util.HardwareInfoUtils
                .parseInt(values.get(key), defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return org.jutils.jhardware.util.HardwareInfoUtils
                .parseLong(values.get(key), defaultValue);
    }

    /**
     * @return an unmodifiable view of the values
     */
    public Map<String, String> values() {
        return Collections.unmodifiableMap(values);
    }

    /**
     * @return an unmodifiable view of the warnings
     */
    public List<String> warnings() {
        return Collections.unmodifiableList(warnings);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
