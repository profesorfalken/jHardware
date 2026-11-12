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
import java.util.List;

/**
 * Display devices attached to the system.
 */
public class DisplayInfo extends AbstractComponentInfo {

    private static final long serialVersionUID = 1L;

    private ArrayList<Display> displayDevices = new ArrayList<>();

    /**
     * @return an unmodifiable list of displays, never {@code null}
     */
    public List<Display> getDisplayDevices() {
        return Collections.unmodifiableList(displayDevices);
    }

    /**
     * @param displayDevices displays to store; {@code null} is treated as empty
     */
    public void setDisplayDevices(List<Display> displayDevices) {
        this.displayDevices = displayDevices == null
                ? new ArrayList<>() : new ArrayList<>(displayDevices);
    }
}
