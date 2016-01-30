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
package org.jutils.jhardware.info.memory.windows;

import org.jutils.jhardware.info.processor.*;
import java.util.HashMap;
import java.util.Map;
import org.jutils.jhardware.info.memory.AbstractMemoryInfo;

/**
 * Information related to CPU
 * 
 * @author Javier Garcia Alonso
 */
public final class WindowsMemoryInfo extends AbstractMemoryInfo {

    protected Map<String, String> parseInfo(String rawData) {
        Map<String, String> processorDataMap = new HashMap<String, String>();
        String[] dataStringLines = rawData.split("\\r?\\n");

        //Line 1 CPUs infos
        String lineInfos = dataStringLines[0];
        String[] infos = lineInfos.split("\\s+");
        processorDataMap.put("cpu family", infos[2]);
        processorDataMap.put("model", infos[4]);
        processorDataMap.put("stepping", infos[6]);

        processorDataMap.put("model name", dataStringLines[1]);
        processorDataMap.put("cpu MHz", dataStringLines[2]);
        processorDataMap.put("vendor_id", dataStringLines[3]);
        processorDataMap.put("cpu cores", dataStringLines[4]);

        return processorDataMap;
    }
}
