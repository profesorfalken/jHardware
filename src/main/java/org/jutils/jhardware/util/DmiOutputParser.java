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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extracts the key/value fields of one {@code dmidecode} section.
 *
 * <p>dmidecode is only used as a fallback when the readable files under
 * {@code /sys/class/dmi/id} are not available. jHardware never runs it through
 * {@code sudo}: a library must not escalate privileges.</p>
 */
public final class DmiOutputParser {

    private DmiOutputParser() {
    }

    /**
     * @param output raw dmidecode output
     * @return field map, preserving order
     */
    public static Map<String, String> parse(String output) {
        Map<String, String> values = new LinkedHashMap<>();
        if (output == null || output.trim().isEmpty()) {
            return values;
        }
        String multilineKey = null;
        StringBuilder multiline = new StringBuilder();
        for (String line : output.split("\\R")) {
            if (line.startsWith("\t\t")) {
                if (multilineKey != null) {
                    multiline.append(line.trim()).append('\n');
                }
                continue;
            }
            if (line.startsWith("\t")) {
                if (multilineKey != null) {
                    values.put(multilineKey, multiline.toString().trim());
                    multilineKey = null;
                    multiline.setLength(0);
                }
                String trimmed = line.trim();
                if ("Characteristics".equals(trimmed) || "Features".equals(trimmed)) {
                    multilineKey = trimmed;
                    continue;
                }
                int separator = line.indexOf(':');
                if (separator > 0) {
                    String key = line.substring(0, separator).trim();
                    String value = line.substring(separator + 1).trim();
                    if (!key.isEmpty()) {
                        if (("Characteristics".equals(key) || "Features".equals(key))
                                && value.isEmpty()) {
                            multilineKey = key;
                        } else {
                            values.put(key, value);
                        }
                    }
                }
                continue;
            }
            if (multilineKey != null) {
                values.put(multilineKey, multiline.toString().trim());
                multilineKey = null;
                multiline.setLength(0);
            }
        }
        if (multilineKey != null) {
            values.put(multilineKey, multiline.toString().trim());
        }
        return values;
    }
}
