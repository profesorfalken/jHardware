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

import java.io.Serializable;

/**
 * A single display device.
 */
public class Display implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String currentResolution;
    private String refreshRate;
    private String[] supportedResolutions = new String[0];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return current resolution in {@code WIDTHxHEIGHT} form
     */
    public String getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(String currentResolution) {
        this.currentResolution = currentResolution;
    }

    /**
     * @return refresh rate in Hz, when available
     */
    public String getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }

    /**
     * @return supported modes in {@code WIDTHxHEIGHT@REFRESH} form; never
     *         {@code null}
     */
    public String[] getSupportedResolutions() {
        return supportedResolutions.clone();
    }

    /**
     * @param supportedResolutions supported modes; {@code null} is treated as
     *                             empty
     */
    public void setSupportedResolutions(String[] supportedResolutions) {
        this.supportedResolutions = supportedResolutions == null
                ? new String[0] : supportedResolutions.clone();
    }
}
