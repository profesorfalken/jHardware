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

import org.jutils.jhardware.UnsupportedOperatingSystemException;
import org.jutils.jhardware.info.bios.unix.UnixBiosInfo;
import org.jutils.jhardware.info.bios.windows.WindowsBiosInfo;
import org.jutils.jhardware.info.display.unix.UnixDisplayInfo;
import org.jutils.jhardware.info.display.windows.WindowsDisplayInfo;
import org.jutils.jhardware.info.graphicscard.unix.UnixGraphicsCardInfo;
import org.jutils.jhardware.info.graphicscard.windows.WindowsGraphicsCardInfo;
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
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.OsFamily;
import org.jutils.jhardware.util.SystemCommandRunner;

/**
 * Selects the implementation for the current operating system.
 */
public class HardwareFactory {

    private HardwareFactory() {
    }

    /**
     * @param type component type
     * @return the implementation for the current OS
     * @throws UnsupportedOperatingSystemException when the OS is not supported
     */
    public static HardwareInfo get(InfoType type) {
        return get(type, OsFamily.current(), SystemCommandRunner.defaultRunner());
    }

    /**
     * @param type component type
     * @param os   target family
     * @param runner command runner used by implementations that need it
     * @return the implementation
     * @throws UnsupportedOperatingSystemException when the OS is not supported
     */
    public static HardwareInfo get(InfoType type, OsFamily os, CommandRunner runner) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        OsFamily family = os == null ? OsFamily.current() : os;
        CommandRunner commandRunner = runner == null
                ? SystemCommandRunner.defaultRunner() : runner;
        switch (family) {
            case WINDOWS:
                return windows(type, commandRunner);
            case LINUX:
                return linux(type, commandRunner);
            case MACOS:
                throw new UnsupportedOperatingSystemException(
                        "macOS is not supported by jHardware 1.0.0; supported systems "
                        + "are Linux and Windows");
            case UNKNOWN:
                throw new UnsupportedOperatingSystemException(
                        "Unsupported operating system: " + System.getProperty("os.name"));
            default:
                throw new UnsupportedOperatingSystemException(
                        "Unsupported operating system family: " + family);
        }
    }

    private static HardwareInfo windows(InfoType type, CommandRunner runner) {
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
            case DISPLAY:
                return new WindowsDisplayInfo(runner);
            case GRAPHICSCARD:
                return new WindowsGraphicsCardInfo(runner);
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private static HardwareInfo linux(InfoType type, CommandRunner runner) {
        switch (type) {
            case PROCESSOR:
                return new UnixProcessorInfo();
            case MEMORY:
                return new UnixMemoryInfo();
            case BIOS:
                return new UnixBiosInfo(runner);
            case MOTHERBOARD:
                return new UnixMotherboardInfo(runner);
            case OS:
                return new UnixOSInfo(runner);
            case NETWORK:
                return new UnixNetworkInfo();
            case DISPLAY:
                return new UnixDisplayInfo(runner);
            case GRAPHICSCARD:
                return new UnixGraphicsCardInfo(runner);
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}
