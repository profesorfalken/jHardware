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

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jutils.jhardware.info.processor.AbstractProcessorInfo;

/**
 * Information related to CPU
 *
 * @author Javier Garcia Alonso
 */
public final class WindowsProcessorInfo extends AbstractProcessorInfo {

    /**
     *
     * @return
     */
    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> processorDataMap
                = WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_PROCESSOR);

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

        //Temperature
        //First, try with jSensors
        //First try: jSensors
        boolean foundByjSensors = false;
        List<Cpu> cpus = JSensors.get.components().cpus;
        if (cpus.size() > 0) {
            //For the moment jHardware only handles 1 processor
            Cpu cpu = cpus.get(0);
            if (cpu.sensors.temperatures != null && cpu.sensors.temperatures.size() > 0) {
                for (Temperature temp : cpu.sensors.temperatures) {
                    if (temp.value != null) {
                        processorDataMap.put("temperature",
                                String.valueOf(temp.value.intValue()));
                        foundByjSensors = true;
                    }
                }
            }
        }

        //First try with Win32_TemperatureProbe 
        //TODO: should be nice but it is not supported
        //https://msdn.microsoft.com/en-us/library/aa394493(v=vs.85).aspx
        //Most of the information that the Win32_TemperatureProbe WMI class provides
        //comes from SMBIOS. Real-time readings for the CurrentReading property 
        //cannot be extracted from SMBIOS tables. For this reason, 
        //current implementations of WMI do not populate the CurrentReading property. 
        //The CurrentReading property's presence is reserved for future use.
        /*Map<String, String> temperatureProbeDataMap = 
         WMI4Java.get().VBSEngine().getWMIObject("Win32_TemperatureProbe");*/
        //We take the temperature from MSAcpi_ThermalZoneTemperature
        //This is not optimal since most manufacturers does not fill this information.
        //Moreover, the values are not updated in real time.
        //It does not even give a temperature separated by core. I have to improve this somehow...
        if (!foundByjSensors) {
            Map<String, String> temperatureDataMap
                    = WMI4Java.get().VBSEngine().namespace("root/wmi").getWMIObject("MSAcpi_ThermalZoneTemperature");

            if (temperatureDataMap.containsKey("CurrentTemperature")) {
                //Convert from tens of kelvin to centigrades
                processorDataMap.put("temperature",
                        String.valueOf(Integer.valueOf(processorDataMap.get("CurrentTemperature")) / 10 - 273));
            } else {
                processorDataMap.put("Temperature",
                        "NOT DETECTED");
            }
        }

        return processorDataMap;
    }
}
