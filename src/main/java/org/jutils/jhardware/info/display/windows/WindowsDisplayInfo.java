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
package org.jutils.jhardware.info.display.windows;

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jutils.jhardware.info.display.AbstractDisplayInfo;
import org.jutils.jhardware.util.DirectXInfoLoader;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to Displays
 *
 * @author Javier Garcia Alonso
 */
public final class WindowsDisplayInfo extends AbstractDisplayInfo {

    /**
     *
     * @return
     */
    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> displayDataMap = getInfoFromWin32DesktopMonitor();
        
        /*if (!checkData(displayDataMap)) {
            completeWithDXData(displayDataMap);
        }*/
        
        addSupportedResolutions(displayDataMap);
        
        return null;
    }

    private Map<String, String> getInfoFromDXDiag() {
        Map<String, String> displayDataMap = new HashMap<>();

        DirectXInfoLoader directXinfo = DirectXInfoLoader.get;
        List<Map<String, String>> rawDisplayInfoMap = directXinfo.getDisplayInfo();

        int numDevice = 0;
        for (final Map<String, String> displayInfoMap : rawDisplayInfoMap) {
            displayDataMap.put("name_" + numDevice, displayInfoMap.get("MonitorName"));
            displayDataMap.put("current_res_" + numDevice, getResolution(displayInfoMap.get("CurrentMode")));
            displayDataMap.put("current_refresh_rate_" + numDevice, getRefreshRate(displayInfoMap.get("CurrentMode")));

            numDevice++;
        }

        displayDataMap.put("numOfDisplays", String.valueOf(rawDisplayInfoMap.size()));

        return displayDataMap;
    }
    
    private Map<String, String> getInfoFromWin32DesktopMonitor() {
        Map<String, String> displayDataMap = new HashMap<>();
        String rawdisplayData
                = WMI4Java.get().VBSEngine()
                .properties(Arrays.asList("Name", "ScreenWidth", "ScreenHeight"))
                .getRawWMIObjectOutput(WMIClass.WIN32_DESKTOPMONITOR);
        String[] dataStringLines = rawdisplayData.split("\\r?\\n");
        int numDevice = -1;
        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("Name")) {
                numDevice++;
                displayDataMap.put("name_" + numDevice, dataLine.split(":", 2)[1]);
            } else if (dataLine.startsWith("ScreenWidth")) {
                displayDataMap.put("current_res_" + numDevice, dataLine.split(":", 2)[1]);
            } else if (dataLine.startsWith("ScreenHeight")) {
                displayDataMap.put("current_res_" + numDevice,
                        HardwareInfoUtils.removeAllSpaces(displayDataMap.get("current_res_" + numDevice)
                                + "x" + dataLine.split(":", 2)[1]));
            }
        }

        return displayDataMap;
    }
    
    private void addSupportedResolutions(Map<String, String> displayDataMap) {
        Set<String> supportedResolutions = new HashSet<>();
        StringBuilder allSupportedResolutions = new StringBuilder();
        String rawdisplayData
                = WMI4Java.get().VBSEngine()
                .properties(Arrays.asList("HorizontalResolution", "VerticalResolution", "RefreshRate"))
                .getRawWMIObjectOutput(WMIClass.CIM_VIDEOCONTROLLERRESOLUTION);
        String[] dataStringLines = rawdisplayData.split("\\r?\\n");
        String hRes = "";
        String vRes = "";       
        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("HorizontalResolution")) {
                hRes = dataLine.split(":", 2)[1];
            } else if (dataLine.startsWith("VerticalResolution")) {
                vRes = dataLine.split(":", 2)[1];
            } else if (dataLine.startsWith("RefreshRate")) {
                supportedResolutions.add(
                        HardwareInfoUtils.removeAllSpaces(hRes + "x" + vRes + "x" + dataLine.split(":", 2)[1]));                
            }
        }
        
        supportedResolutions.stream().forEach((supportedResolution) -> {
            allSupportedResolutions.append(supportedResolution).append(";");
        });
        
        displayDataMap.put("available_res_0", allSupportedResolutions.toString());
    }

    private String getResolution(String currentMode) {
        return HardwareInfoUtils.removeAllSpaces(
                currentMode.substring(0, currentMode.indexOf("(")));
    }

    private String getRefreshRate(String currentMode) {
        return HardwareInfoUtils.extractText(currentMode, "\\) \\((.+?)Hz");
    }
}
