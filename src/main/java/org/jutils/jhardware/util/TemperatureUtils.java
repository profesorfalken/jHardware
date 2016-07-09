/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jhardware.util;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.profesorfalken.wmi4java.WMI4Java;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This class handles component temperatures<p>
 *
 * It tries first using jSensors. If it fails, it tries with an OS specific
 * method.
 *
 * @author Javier Garcia Alonso
 */
public class TemperatureUtils {
    private static final String NOT_DETECTED = "NOT DETECTED";
    
    private static final String CPUTEMP_THERMAL_ROOT = "/sys/class/thermal/thermal_zone";
    private static final String CPUTEMP_THERMAL_FILE = "temp";

    private static String getCpuTemperatureByjSensors() {
        List<Cpu> cpus = JSensors.get.components().cpus;
        if (cpus.size() > 0) {
            //For the moment jHardware only handles 1 temperature of processor
            Cpu cpu = cpus.get(0);
            if (cpu.sensors.temperatures != null && cpu.sensors.temperatures.size() > 0) {
                for (Temperature temp : cpu.sensors.temperatures) {
                    if (temp.value != null) {
                        //We cast and round to integer and convert to String
                        return String.valueOf(temp.value.intValue());
                    }
                }
            }
        }
        return NOT_DETECTED;
    }

    public static String getCpuTemperatureForWindows() {
        //First, try by jSensors
        String temperature = getCpuTemperatureByjSensors();

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
        if (NOT_DETECTED.equals(temperature)) {
            Map<String, String> temperatureDataMap
                    = WMI4Java.get().VBSEngine().namespace("root/wmi").getWMIObject("MSAcpi_ThermalZoneTemperature");

            if (temperatureDataMap.containsKey("CurrentTemperature")) {
                //Convert from tens of kelvin to centigrades
                temperature = String.valueOf(Integer.valueOf(temperatureDataMap.get("CurrentTemperature")) / 10 - 273);
            } else {
                temperature = NOT_DETECTED;
            }
        }
        
        return temperature;
    }

    public static String getCpuTemperatureForLinux() {
        //First, try by jSensors
        String temperature = getCpuTemperatureByjSensors();
        
        //https://www.kernel.org/doc/Documentation/thermal/sysfs-api.txt
        //Second try: /sys/class/thermal
        if (NOT_DETECTED.equals(temperature)) {
            int sensorIndex = 0;
            while (true) {
                if (new File(CPUTEMP_THERMAL_ROOT + sensorIndex).exists()) {
                    String tempFile = CPUTEMP_THERMAL_ROOT + sensorIndex++ + "/" + CPUTEMP_THERMAL_FILE;
                    String value = HardwareInfoUtils.getSingleValueFromFile(tempFile);
                    if (value != null && !value.isEmpty()) {
                        temperature = String.valueOf(Integer.valueOf(value.trim()) / 1000);
                        break;
                    }
                } else {
                    temperature = NOT_DETECTED;
                    break;
                }
            }
        }
        
        return temperature;
    }
}
