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
package org.jutils.jhardware.info.motherboard.windows;

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import java.util.Map;
import org.jutils.jhardware.info.motherboard.AbstractMotherboardInfo;

/**
 * Information related to CPU
 * 
 * @author Javier Garcia Alonso
 */
public final class WindowsMotherboardInfo extends AbstractMotherboardInfo {

    protected Map<String, String> parseInfo() {
        Map<String, String> processorDataMap = 
                WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_PERFFORMATTEDDATA_PERFOS_MEMORY);
        processorDataMap.putAll(WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_PHYSICALMEMORY));        

        processorDataMap.put("MemAvailable", processorDataMap.get("AvailableKBytes"));
        processorDataMap.put("MemFree", 
                WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_OPERATINGSYSTEM).get("FreePhysicalMemory"));
        processorDataMap.put("MemTotal", WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_OPERATINGSYSTEM).get("FreePhysicalMemory"));

        return processorDataMap;
    }
}
