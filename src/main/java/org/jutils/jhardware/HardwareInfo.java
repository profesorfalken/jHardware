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

import org.jutils.jhardware.info.InfoType;
import org.jutils.jhardware.model.BiosInfo;
import org.jutils.jhardware.model.ComponentInfo;
import org.jutils.jhardware.model.DisplayInfo;
import org.jutils.jhardware.model.GraphicsCardInfo;
import org.jutils.jhardware.model.MemoryInfo;
import org.jutils.jhardware.model.MotherboardInfo;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.model.OSInfo;
import org.jutils.jhardware.model.ProcessorInfo;

/**
 * Entry point for hardware snapshots.
 *
 * <p>The static methods use a shared {@link HardwareInfoService} configured
 * for the current machine. Applications that need to inject a command runner,
 * target a different OS family or run tests should instantiate
 * {@link HardwareInfoService} directly.</p>
 *
 * <p>Every snapshot is a one-shot read: no values are cached. Snapshot objects
 * expose the raw payload in {@link ComponentInfo#getFullInfo()} and any
 * non-fatal problem in {@link ComponentInfo#getWarnings()}.</p>
 */
public class HardwareInfo {

    private static final HardwareInfoService SERVICE = new HardwareInfoService();

    private HardwareInfo() {
    }

    /**
     * @return the shared service
     */
    public static HardwareInfoService getService() {
        return SERVICE;
    }

    /**
     * @return CPU information
     */
    public static ProcessorInfo getProcessorInfo() {
        return SERVICE.getProcessorInfo();
    }

    /**
     * @return physical memory information
     */
    public static MemoryInfo getMemoryInfo() {
        return SERVICE.getMemoryInfo();
    }

    /**
     * @return BIOS information
     */
    public static BiosInfo getBiosInfo() {
        return SERVICE.getBiosInfo();
    }

    /**
     * @return motherboard information
     */
    public static MotherboardInfo getMotherboardInfo() {
        return SERVICE.getMotherboardInfo();
    }

    /**
     * @return operating-system information
     */
    public static OSInfo getOSInfo() {
        return SERVICE.getOSInfo();
    }

    /**
     * @return network interfaces
     */
    public static NetworkInfo getNetworkInfo() {
        return SERVICE.getNetworkInfo();
    }

    /**
     * @return connected displays
     */
    public static DisplayInfo getDisplayInfo() {
        return SERVICE.getDisplayInfo();
    }

    /**
     * @return graphics cards
     */
    public static GraphicsCardInfo getGraphicsCardInfo() {
        return SERVICE.getGraphicsCardInfo();
    }

    /**
     * @param type component type
     * @return the component snapshot
     */
    public static ComponentInfo getInfo(InfoType type) {
        return SERVICE.getInfo(type);
    }

    /**
     * Prints a full report to standard output.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        HardwareInfoService service = new HardwareInfoService();
        print(service.getProcessorInfo());
        print(service.getMemoryInfo());
        print(service.getBiosInfo());
        print(service.getMotherboardInfo());
        print(service.getOSInfo());
        print(service.getNetworkInfo());
        print(service.getDisplayInfo());
        print(service.getGraphicsCardInfo());
    }

    private static void print(ComponentInfo info) {
        System.out.println("=== " + info.getClass().getSimpleName() + " ===");
        for (String warning : info.getWarnings()) {
            System.out.println("WARN: " + warning);
        }
        info.getFullInfo().forEach((key, value) ->
                System.out.println(key + ": " + value));
        System.out.println();
    }

}
