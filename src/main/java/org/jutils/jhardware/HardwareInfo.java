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
package org.jutils.jhardware;

import org.jutils.jhardware.info.HardwareFactory;
import org.jutils.jhardware.info.InfoType;
import org.jutils.jhardware.model.BiosInfo;
import org.jutils.jhardware.model.MemoryInfo;
import org.jutils.jhardware.model.ProcessorInfo;

/**
 * Static class that allows to query for Hardware details.<p>
 * Each method return the information related with a different component 
 * (CPU, memory, motherboard....). There are some important data that comes
 * typed in the response. This is the information that is common in different 
 * system. The complete and specific information is returned in a Map.
 * 
 * @author Javier Garcia Alonso
 */
public class HardwareInfo {
    
    /**
     * Gets information related with CPU
     * 
     * @return object with typed common data and a map with full data
     */
    public static ProcessorInfo getProcessorInfo() {
        return (ProcessorInfo)HardwareFactory.get(InfoType.PROCESSOR).getInfo();
    }
    
    /**
     * Gets information related with memory
     * 
     * @return object with typed common data and a map with full data
     */
    public static MemoryInfo getMemoryInfo() {
        return (MemoryInfo)HardwareFactory.get(InfoType.MEMORY).getInfo();
    }
    
    /**
     * Gets information related with BIOS
     * 
     * @return object with typed common data and a map with full data
     */
    public static BiosInfo getBiosInfo() {
        return (BiosInfo)HardwareFactory.get(InfoType.BIOS).getInfo();
    }
}
