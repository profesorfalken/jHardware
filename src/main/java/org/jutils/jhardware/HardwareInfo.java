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

import java.util.Map;
import java.util.Set;
import org.jutils.jhardware.info.HardwareFactory;
import org.jutils.jhardware.info.InfoType;
import org.jutils.jhardware.model.BiosInfo;
import org.jutils.jhardware.model.DisplayInfo;
import org.jutils.jhardware.model.MemoryInfo;
import org.jutils.jhardware.model.MotherboardInfo;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.model.OSInfo;
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
     * Hide constructor
     */
    private HardwareInfo() {
    }
    
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
    
    /**
     * Gets information related with the motherboard
     * 
     * @return object with typed common data and a map with full data
     */
    public static MotherboardInfo getMotherboardInfo() {
        return (MotherboardInfo)HardwareFactory.get(InfoType.MOTHERBOARD).getInfo();
    }
    
    /**
     * Gets information related with the Operating System
     * 
     * @return object with typed common data and a map with full data
     */
    public static OSInfo getOSInfo() {
        return (OSInfo)HardwareFactory.get(InfoType.OS).getInfo();
    }
    
    /**
     * Gets information related with the Network
     * 
     * @return object with typed common data and a map with full data
     */
    public static NetworkInfo getNetworkInfo() {
        return (NetworkInfo)HardwareFactory.get(InfoType.NETWORK).getInfo();
    }
    
    /**
     * Gets information related with the Display
     * 
     * @return object with typed common data and a map with full data
     */
    public static DisplayInfo getDisplayInfo() {
        return (DisplayInfo)HardwareFactory.get(InfoType.DISPLAY).getInfo();
    }
    
    public static void main(String [] args) {
        ProcessorInfo info = HardwareInfo.getProcessorInfo();

        System.out.println("Cache size: " + info.getCacheSize());        
        System.out.println("Family: " + info.getFamily());
        System.out.println("Speed (Mhz): " + info.getMhz());
        System.out.println("Model: " + info.getModel());
        System.out.println("Model name: " + info.getModelName());
        System.out.println("Number of cores: " + info.getNumCores());
        System.out.println("Stepping: " + info.getStepping());
        System.out.println("Temperature: " + info.getTemperature());
        System.out.println("Vendor Id: " + info.getVendorId());
        
        Set<Map.Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
        fullInfos.stream().forEach((fullInfo) -> {
            System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
        });
    }
}
