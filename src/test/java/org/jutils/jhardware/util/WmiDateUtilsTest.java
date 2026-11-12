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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class WmiDateUtilsTest {

    @Test
    void formatsDateOnly() {
        assertEquals("2025-08-09",
                WmiDateUtils.toIsoDate("20250809000000.000000+000"));
        assertEquals("2025-08-09",
                WmiDateUtils.toIsoDate("20250809"));
    }

    @Test
    void formatsDateTimeWithOffset() {
        assertEquals("2026-11-11T15:58:22+01:00",
                WmiDateUtils.toIsoDateTime("20261111155822.000000+060"));
        assertEquals("2026-11-11T15:58:22-03:00",
                WmiDateUtils.toIsoDateTime("20261111155822.000000-180"));
    }

    @Test
    void formatsDateTimeWithoutOffset() {
        assertEquals("2026-11-11T15:58:22",
                WmiDateUtils.toIsoDateTime("20261111155822.000000"));
    }

    @Test
    void returnsOriginalValueWhenParsingFails() {
        assertEquals("not-a-date", WmiDateUtils.toIsoDateTime("not-a-date"));
        assertEquals("not-a-date", WmiDateUtils.toIsoDate("not-a-date"));
        assertNull(WmiDateUtils.toIsoDate(null));
        assertNull(WmiDateUtils.toIsoDateTime(null));
    }
}
