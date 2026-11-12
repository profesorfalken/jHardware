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

import org.jutils.jhardware.info.HardwareFactory;
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
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.OsFamily;
import org.jutils.jhardware.util.SystemCommandRunner;

/**
 * Configurable entry point for hardware snapshots.
 *
 * <p>Unlike the static {@link HardwareInfo} facade, this service accepts an
 * explicit {@link OsFamily} and {@link CommandRunner}, which makes platform
 * selection and parser behavior testable without spawning real commands.</p>
 */
public final class HardwareInfoService {

    private final OsFamily osFamily;
    private final CommandRunner commandRunner;

    /**
     * Creates a service for the current machine.
     */
    public HardwareInfoService() {
        this(OsFamily.current(), SystemCommandRunner.defaultRunner());
    }

    /**
     * @param osFamily      target OS family
     * @param commandRunner command runner, {@code null} for the default
     */
    public HardwareInfoService(OsFamily osFamily, CommandRunner commandRunner) {
        this.osFamily = osFamily == null ? OsFamily.current() : osFamily;
        this.commandRunner = commandRunner == null
                ? SystemCommandRunner.defaultRunner() : commandRunner;
    }

    public OsFamily getOsFamily() {
        return osFamily;
    }

    public CommandRunner getCommandRunner() {
        return commandRunner;
    }

    public ProcessorInfo getProcessorInfo() {
        return (ProcessorInfo) getInfo(InfoType.PROCESSOR);
    }

    public MemoryInfo getMemoryInfo() {
        return (MemoryInfo) getInfo(InfoType.MEMORY);
    }

    public BiosInfo getBiosInfo() {
        return (BiosInfo) getInfo(InfoType.BIOS);
    }

    public MotherboardInfo getMotherboardInfo() {
        return (MotherboardInfo) getInfo(InfoType.MOTHERBOARD);
    }

    public OSInfo getOSInfo() {
        return (OSInfo) getInfo(InfoType.OS);
    }

    public NetworkInfo getNetworkInfo() {
        return (NetworkInfo) getInfo(InfoType.NETWORK);
    }

    public DisplayInfo getDisplayInfo() {
        return (DisplayInfo) getInfo(InfoType.DISPLAY);
    }

    public GraphicsCardInfo getGraphicsCardInfo() {
        return (GraphicsCardInfo) getInfo(InfoType.GRAPHICSCARD);
    }

    /**
     * @param type component type
     * @return the component snapshot
     */
    public ComponentInfo getInfo(InfoType type) {
        return HardwareFactory.get(type, osFamily, commandRunner).getInfo();
    }
}
