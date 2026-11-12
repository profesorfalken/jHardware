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

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Sensors;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.util.List;

/**
 * Defensive facade over jSensors.
 *
 * <p>jSensors loads native libraries through JNA; a missing library or an
 * unsupported kernel produces {@link LinkageError} or runtime exceptions.
 * Sensors are optional for jHardware, so every failure degrades to an empty
 * result instead of breaking the component snapshot.</p>
 */
public final class JsensorsSupport {

    private JsensorsSupport() {
    }

    public static List<Cpu> cpus() {
        try {
            Components components = JSensors.get.components();
            if (components == null || components.cpus == null) {
                return java.util.Collections.emptyList();
            }
            return java.util.Collections.unmodifiableList(
                    new java.util.ArrayList<>(components.cpus));
        } catch (RuntimeException | LinkageError ex) {
            return java.util.Collections.emptyList();
        }
    }

    public static List<Gpu> gpus() {
        try {
            Components components = JSensors.get.components();
            if (components == null || components.gpus == null) {
                return java.util.Collections.emptyList();
            }
            return java.util.Collections.unmodifiableList(
                    new java.util.ArrayList<>(components.gpus));
        } catch (RuntimeException | LinkageError ex) {
            return java.util.Collections.emptyList();
        }
    }

    /**
     * @return the first CPU temperature in Celsius, or {@code null}
     */
    public static String cpuTemperature() {
        List<Cpu> cpus = cpus();
        if (cpus.isEmpty()) {
            return null;
        }
        return firstTemperature(cpus.get(0).sensors);
    }

    /**
     * @param index GPU index
     * @return the GPU temperature in Celsius, or {@code null}
     */
    public static String gpuTemperature(int index) {
        List<Gpu> gpus = gpus();
        if (index < 0 || index >= gpus.size()) {
            return null;
        }
        return firstTemperature(gpus.get(index).sensors);
    }

    /**
     * @param index GPU index
     * @return the first fan speed, or {@code null}
     */
    public static String gpuFanSpeed(int index) {
        List<Gpu> gpus = gpus();
        if (index < 0 || index >= gpus.size()) {
            return null;
        }
        Sensors sensors = gpus.get(index).sensors;
        if (sensors == null || sensors.fans == null || sensors.fans.isEmpty()) {
            return null;
        }
        for (Fan fan : sensors.fans) {
            if (fan != null && fan.value != null && Double.isFinite(fan.value)) {
                return String.valueOf(Math.round(fan.value));
            }
        }
        return null;
    }

    private static String firstTemperature(Sensors sensors) {
        if (sensors == null || sensors.temperatures == null) {
            return null;
        }
        for (Temperature temperature : sensors.temperatures) {
            if (temperature != null && temperature.value != null
                    && Double.isFinite(temperature.value)) {
                return String.valueOf(Math.round(temperature.value));
            }
        }
        return null;
    }
}
