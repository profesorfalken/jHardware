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
package org.jutils.jhardware.model;

/**
 * Processor (CPU) information.
 */
public class ProcessorInfo extends AbstractComponentInfo {

    private static final long serialVersionUID = 1L;

    private String vendorId;
    private String family;
    private String model;
    private String modelName;
    private String stepping;
    private String mhz;
    private String cacheSize;
    private String numCores;
    private String temperature;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getStepping() {
        return stepping;
    }

    public void setStepping(String stepping) {
        this.stepping = stepping;
    }

    /**
     * @return current clock speed in MHz, as reported by the platform
     */
    public String getMhz() {
        return mhz;
    }

    public void setMhz(String mhz) {
        this.mhz = mhz;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * @return number of physical cores when it can be computed, otherwise the
     *         number of logical processors
     */
    public String getNumCores() {
        return numCores;
    }

    public void setNumCores(String numCores) {
        this.numCores = numCores;
    }

    /**
     * @return temperature in degrees Celsius, or {@code NOT DETECTED}
     */
    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
