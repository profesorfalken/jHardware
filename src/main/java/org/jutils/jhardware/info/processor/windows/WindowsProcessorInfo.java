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
package org.jutils.jhardware.info.processor.windows;

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.processor.AbstractProcessorInfo;

/**
 * Information related to CPU
 * 
 * @author Javier Garcia Alonso
 */
public final class WindowsProcessorInfo extends AbstractProcessorInfo {

    protected Map<String, String> parseInfo() {
        Map<String, String> processorDataMap = 
                WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_PROCESSOR);  

        //Line 1 CPUs infos
        String lineInfos = processorDataMap.get("Description");
        String[] infos = lineInfos.split("\\s+");
        processorDataMap.put("cpu family", infos[2]);
        processorDataMap.put("model", infos[4]);
        processorDataMap.put("stepping", infos[6]);

        processorDataMap.put("model name", processorDataMap.get("Name"));
        processorDataMap.put("cpu MHz", processorDataMap.get("MaxClockSpeed"));
        processorDataMap.put("vendor_id", processorDataMap.get("Manufacturer"));
        processorDataMap.put("cpu cores", processorDataMap.get("NumberOfCores"));

        return processorDataMap;
    }
}
