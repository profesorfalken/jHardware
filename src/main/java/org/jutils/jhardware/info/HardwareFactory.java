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
package org.jutils.jhardware.info;

import org.jutils.jhardware.info.bios.unix.UnixBiosInfo;
import org.jutils.jhardware.info.bios.windows.WindowsBiosInfo;
import org.jutils.jhardware.info.memory.unix.UnixMemoryInfo;
import org.jutils.jhardware.info.memory.windows.WindowsMemoryInfo;
import org.jutils.jhardware.info.motherboard.unix.UnixMotherboardInfo;
import org.jutils.jhardware.info.motherboard.windows.WindowsMotherboardInfo;
import org.jutils.jhardware.info.network.unix.UnixNetworkInfo;
import org.jutils.jhardware.info.network.windows.WindowsNetworkInfo;
import org.jutils.jhardware.info.os.unix.UnixOSInfo;
import org.jutils.jhardware.info.os.windows.WindowsOSInfo;
import org.jutils.jhardware.info.processor.unix.UnixProcessorInfo;
import org.jutils.jhardware.info.processor.windows.WindowsProcessorInfo;
import org.jutils.jhardware.util.OSDetector;

/**
 * Factory class to get the right information
 *
 * @author Javier Garcia Alonso
 */
public class HardwareFactory {

    /**
     * Hide constructor
     */
    private HardwareFactory() {
    }

    public static HardwareInfo get(InfoType type) {
        if (OSDetector.isWindows()) {
            return getWindowsInfo(type);
        } else if (OSDetector.isUnix()) {
            return getUnixInfo(type);
        } else {
            throw new UnsupportedOperationException("Your Operating System is not supported");
        }
    }

    private static HardwareInfo getWindowsInfo(InfoType type) {
        switch (type) {
            case PROCESSOR:
                return new WindowsProcessorInfo();
            case MEMORY:
                return new WindowsMemoryInfo();
            case BIOS:
                return new WindowsBiosInfo();
            case MOTHERBOARD:
                return new WindowsMotherboardInfo();
            case OS:
                return new WindowsOSInfo();
            case NETWORK:
                return new WindowsNetworkInfo();
            default:
                throw new IllegalArgumentException("Type of hardware not supported: " + type);
        }
    }

    private static HardwareInfo getUnixInfo(InfoType type) {
        switch (type) {
            case PROCESSOR:
                return new UnixProcessorInfo();
            case MEMORY:
                return new UnixMemoryInfo();
            case BIOS:
                return new UnixBiosInfo();
            case MOTHERBOARD:
                return new UnixMotherboardInfo();
            case OS:
                return new UnixOSInfo();
            case NETWORK:
                return new UnixNetworkInfo();
            default:
                throw new IllegalArgumentException("Type of hardware not supported: " + type);
        }
    }
}
