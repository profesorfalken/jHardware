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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParsedDataTest {

    @Test
    void storesValuesAndWarnings() {
        ParsedData data = ParsedData.empty()
                .put("a", "1")
                .put("ignored", null)
                .put(null, "ignored")
                .warn("warning");

        assertEquals("1", data.get("a"));
        assertEquals(1, data.values().size());
        assertEquals(1, data.warnings().size());
        assertEquals(42, data.getInt("missing", 42));
        assertEquals(42L, data.getLong("missing", 42L));
        assertFalse(data.isEmpty());
    }

    @Test
    void mergeCombinesValuesAndWarnings() {
        ParsedData target = ParsedData.empty().put("a", "1").warn("one");
        ParsedData source = ParsedData.empty().put("b", "2").warn("two");

        target.merge(source);

        assertEquals("1", target.get("a"));
        assertEquals("2", target.get("b"));
        assertEquals(2, target.warnings().size());
    }

    @Test
    void viewsAreUnmodifiable() {
        ParsedData data = ParsedData.empty().put("a", "1").warn("warning");
        assertThrows(UnsupportedOperationException.class,
                () -> data.values().put("b", "2"));
        assertThrows(UnsupportedOperationException.class,
                () -> data.warnings().add("another"));
    }

    @Test
    void emptyHasNoValuesOrWarnings() {
        ParsedData data = ParsedData.empty();
        assertTrue(data.isEmpty());
        assertTrue(data.values().isEmpty());
        assertTrue(data.warnings().isEmpty());
    }
}
