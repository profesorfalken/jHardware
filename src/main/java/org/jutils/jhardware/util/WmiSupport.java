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

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import java.util.List;
import java.util.Map;

/**
 * Defensive facade over WMI4Java.
 *
 * <p>WMI can fail for many reasons (disabled service, permissions, missing
 * PowerShell, localized provider). Windows implementations treat an empty map
 * as "information unavailable" and add a warning to the snapshot.</p>
 */
public final class WmiSupport {

    private WmiSupport() {
    }

    /**
     * @param wmiClass WMI class
     * @return the first object as a map, or an empty map
     */
    public static Map<String, String> object(WMIClass wmiClass) {
        try {
            Map<String, String> values = WMI4Java.get().VBSEngine().getWMIObject(wmiClass);
            return values == null
                    ? java.util.Collections.<String, String>emptyMap() : values;
        } catch (RuntimeException ex) {
            return java.util.Collections.emptyMap();
        }
    }

    /**
     * @param wmiClass WMI class
     * @return every object as a list of maps, or an empty list
     */
    public static List<Map<String, String>> list(WMIClass wmiClass) {
        try {
            List<Map<String, String>> values = WMI4Java.get().VBSEngine()
                    .getWMIObjectList(wmiClass);
            return values == null
                    ? java.util.Collections.<Map<String, String>>emptyList()
                    : java.util.Collections.unmodifiableList(
                            new java.util.ArrayList<>(values));
        } catch (RuntimeException ex) {
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Reads a class from a non-default namespace.
     *
     * @param namespace WMI namespace, for example {@code root/wmi}
     * @param wmiClass  WMI class name
     * @return the first object as a map, or an empty map
     */
    public static Map<String, String> object(String namespace, String wmiClass) {
        try {
            Map<String, String> values = WMI4Java.get().namespace(namespace)
                    .VBSEngine().getWMIObject(wmiClass);
            return values == null
                    ? java.util.Collections.<String, String>emptyMap() : values;
        } catch (Exception ex) {
            return java.util.Collections.emptyMap();
        }
    }
}
