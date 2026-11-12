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
package org.jutils.jhardware.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Common contract for every hardware component snapshot.
 *
 * <p>Typed getters are provided for the fields that are common across
 * platforms. The complete, platform-specific payload is available through
 * {@link #getFullInfo()}. When a platform cannot provide some data, the
 * corresponding getter is {@code null} (or an empty list) and
 * {@link #getWarnings()} explains why.</p>
 */
public interface ComponentInfo extends Serializable {

    /**
     * @return an unmodifiable map with every raw key/value pair retrieved for
     *         this component; never {@code null}
     */
    default Map<String, String> getFullInfo() {
        return Collections.emptyMap();
    }

    /**
     * @return an unmodifiable list of non-fatal problems found while retrieving
     *         the information; never {@code null}
     */
    default List<String> getWarnings() {
        return Collections.emptyList();
    }
}
