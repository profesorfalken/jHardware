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

import com.profesorfalken.wmi4java.WMIClass;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.display.AbstractDisplayInfo;
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.DirectXInfoLoader;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.SystemCommandRunner;
import org.jutils.jhardware.util.WmiSupport;

/**
 * Display information for Windows, from {@code dxdiag} with a WMI fallback.
 */
public final class WindowsDisplayInfo extends AbstractDisplayInfo {

    private static final Pattern RESOLUTION = Pattern.compile(
            "(\\d{3,5})\\s*x\\s*(\\d{3,5})");
    private static final Pattern REFRESH = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)\\s*Hz");

    private final CommandRunner runner;

    public WindowsDisplayInfo() {
        this(SystemCommandRunner.defaultRunner());
    }

    public WindowsDisplayInfo(CommandRunner runner) {
        this.runner = runner == null ? SystemCommandRunner.defaultRunner() : runner;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = ParsedData.empty();
        DirectXInfoLoader.Result directX = DirectXInfoLoader.get.getResult(runner);
        if (!directX.displays().isEmpty()) {
            data.merge(parseDirectX(directX.displays()));
        } else {
            data.merge(parseWmiDesktopMonitors());
        }
        data.addWarnings(directX.warnings());
        applySupportedResolutions(data);
        if (data.getInt("numOfDisplays", 0) == 0) {
            data.warn("No display device was reported by dxdiag or WMI");
        }
        return data;
    }

    /**
     * @param displays dxdiag {@code DisplayDevice} maps
     * @return parsed data
     */
    static ParsedData parseDirectX(List<Map<String, String>> displays) {
        ParsedData data = ParsedData.empty();
        if (displays == null) {
            return data;
        }
        int count = 0;
        for (Map<String, String> display : displays) {
            String name = HardwareInfoUtils.firstNonBlank(
                    display.get("MonitorName"), display.get("CardName"));
            data.put("name_" + count, name);
            String currentMode = display.get("CurrentMode");
            Mode mode = parseMode(currentMode);
            if (mode != null) {
                data.put("current_res_" + count, mode.resolution());
                data.put("current_refresh_rate_" + count, mode.refresh());
            } else if (currentMode != null && !currentMode.trim().isEmpty()) {
                data.warn("Could not parse the display mode: " + currentMode);
            }
            count++;
        }
        data.put("numOfDisplays", String.valueOf(count));
        return data;
    }

    /**
     * @param currentMode value such as {@code 1920 x 1080 (32 bit) (60Hz)}
     * @return parsed mode, or {@code null}
     */
    static Mode parseMode(String currentMode) {
        if (currentMode == null) {
            return null;
        }
        Matcher resolution = RESOLUTION.matcher(currentMode);
        if (!resolution.find()) {
            return null;
        }
        String refresh = HardwareInfoUtils.extract(currentMode,
                "(\\d+(?:\\.\\d+)?)\\s*Hz").orElse(null);
        return new Mode(resolution.group(1) + "x" + resolution.group(2), refresh);
    }

    private static ParsedData parseWmiDesktopMonitors() {
        ParsedData data = ParsedData.empty();
        List<Map<String, String>> monitors = WmiSupport.list(WMIClass.WIN32_DESKTOPMONITOR);
        Map<String, String> controller = WmiSupport.object(WMIClass.WIN32_VIDEOCONTROLLER);
        String refresh = controller.get("CurrentRefreshRate");
        int count = 0;
        for (Map<String, String> monitor : monitors) {
            data.put("name_" + count, monitor.get("Name"));
            String width = monitor.get("ScreenWidth");
            String height = monitor.get("ScreenHeight");
            if (width != null && height != null) {
                data.put("current_res_" + count,
                        HardwareInfoUtils.removeAllSpaces(width + "x" + height));
            }
            data.put("current_refresh_rate_" + count, refresh);
            count++;
        }
        data.put("numOfDisplays", String.valueOf(count));
        return data;
    }

    private static void applySupportedResolutions(ParsedData data) {
        int displays = data.getInt("numOfDisplays", 0);
        if (displays == 0) {
            return;
        }
        Set<String> modes = new LinkedHashSet<>();
        for (Map<String, String> resolution : WmiSupport.list(
                WMIClass.CIM_VIDEOCONTROLLERRESOLUTION)) {
            String width = resolution.get("HorizontalResolution");
            String height = resolution.get("VerticalResolution");
            String refresh = resolution.get("RefreshRate");
            if (width != null && height != null && refresh != null) {
                modes.add(width + "x" + height + "@" + refresh);
            }
        }
        if (modes.isEmpty()) {
            return;
        }
        String joined = String.join(";", modes);
        for (int i = 0; i < displays; i++) {
            data.put("available_res_" + i, joined);
        }
        if (displays > 1) {
            data.warn("Supported resolutions are adapter-wide and may include "
                    + "modes not supported by every monitor");
        }
    }

    /**
     * A display mode.
     */
    static final class Mode {

        private final String resolution;
        private final String refresh;

        /**
         * @param resolution {@code WIDTHxHEIGHT}
         * @param refresh    refresh rate in Hz, may be {@code null}
         */
        Mode(String resolution, String refresh) {
            this.resolution = resolution;
            this.refresh = refresh;
        }

        String resolution() {
            return resolution;
        }

        String refresh() {
            return refresh;
        }
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
