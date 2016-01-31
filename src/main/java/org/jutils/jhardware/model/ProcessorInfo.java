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

import java.util.Map;

/**
 * Model that encapsulates processor information
 * 
 * @author Javier Garcia Alonso
 */
public class ProcessorInfo implements ComponentInfo{
    private String vendorId;
    private String family;
    private String model;
    private String modelName;
    private String stepping;
    private String mhz;
    private String cacheSize;
    private String numCores;
    private String temperature;
    
    private Map<String, String> fullInfo;

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

    public String getNumCores() {
        return numCores;
    }

    public void setNumCores(String numCores) {
        this.numCores = numCores;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Map<String, String> getFullInfo() {
        return fullInfo;
    }

    public void setFullInfo(Map<String, String> fullInfo) {
        this.fullInfo = fullInfo;
    }
}
