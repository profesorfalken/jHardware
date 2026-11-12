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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class that stores the raw payload and warnings shared by all models.
 */
public abstract class AbstractComponentInfo implements ComponentInfo {

    private static final long serialVersionUID = 1L;

    private LinkedHashMap<String, String> fullInfo = new LinkedHashMap<>();
    private ArrayList<String> warnings = new ArrayList<>();

    @Override
    public Map<String, String> getFullInfo() {
        return Collections.unmodifiableMap(fullInfo);
    }

    /**
     * @param fullInfo raw payload; {@code null} is treated as an empty map
     */
    public void setFullInfo(Map<String, String> fullInfo) {
        this.fullInfo = fullInfo == null
                ? new LinkedHashMap<>() : new LinkedHashMap<>(fullInfo);
    }

    @Override
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    /**
     * @param warnings non-fatal warnings; {@code null} is treated as empty
     */
    public void setWarnings(List<String> warnings) {
        ArrayList<String> copy = new ArrayList<>();
        if (warnings != null) {
            for (String warning : warnings) {
                if (warning != null && !warning.trim().isEmpty()) {
                    copy.add(warning);
                }
            }
        }
        this.warnings = copy;
    }
}
