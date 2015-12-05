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
package org.jutils.jhardware.info.processor;

import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.jni.HardwareDataCollector;
import org.jutils.jhardware.model.ProcessorInfo;

/**
 * Information related to CPU
 * 
 * @author Javier Garcia Alonso
 */
public abstract class AbstractProcessorInfo implements HardwareInfo {

    /**
     * Get CPU information from OS
     * 
     * @return 
     */
    @Override
    public ProcessorInfo getInfo() {
        String processorData = HardwareDataCollector.INSTANCE.getProcessorData();
        Map<String, String> dataMap = parseInfo(processorData);

        return buildFromDataMap(dataMap);
    }
    
    abstract protected Map<String, String> parseInfo(String data);
    
    private ProcessorInfo buildFromDataMap(Map<String, String> dataMap) {
        ProcessorInfo info = new ProcessorInfo();
        if (dataMap != null && !dataMap.isEmpty()) {
            info.setCacheSize(dataMap.get("cache size"));
            info.setFamily(dataMap.get("cpu family"));
            info.setMhz(dataMap.get("cpu MHz"));
            info.setModel(dataMap.get("model"));
            info.setModelName(dataMap.get("model name"));
            info.setNumCores(dataMap.get("cpu cores"));
            info.setStepping(dataMap.get("stepping"));
            info.setTemperature(dataMap.get("Temperature"));
            info.setVendorId(dataMap.get("vendor_id"));
        }
        
        return info;
    }
}
