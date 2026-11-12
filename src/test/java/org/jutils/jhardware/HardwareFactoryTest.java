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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.jutils.jhardware.info.HardwareFactory;
import org.jutils.jhardware.info.InfoType;
import org.jutils.jhardware.info.memory.unix.UnixMemoryInfo;
import org.jutils.jhardware.info.memory.windows.WindowsMemoryInfo;
import org.jutils.jhardware.util.FakeCommandRunner;
import org.jutils.jhardware.util.OsFamily;

class HardwareFactoryTest {

    private final FakeCommandRunner runner = FakeCommandRunner.failure("not used");

    @Test
    void macOSAndUnknownSystemsFailWithAClearException() {
        assertThrows(UnsupportedOperatingSystemException.class,
                () -> HardwareFactory.get(InfoType.MEMORY, OsFamily.MACOS, runner));
        assertThrows(UnsupportedOperatingSystemException.class,
                () -> HardwareFactory.get(InfoType.MEMORY, OsFamily.UNKNOWN, runner));
    }

    @Test
    void selectsTheImplementationForEachFamily() {
        assertTrue(HardwareFactory.get(InfoType.MEMORY, OsFamily.LINUX, runner)
                instanceof UnixMemoryInfo);
        assertTrue(HardwareFactory.get(InfoType.MEMORY, OsFamily.WINDOWS, runner)
                instanceof WindowsMemoryInfo);
    }

    @Test
    void rejectsNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> HardwareFactory.get(null, OsFamily.LINUX, runner));
    }

    @Test
    void serviceUsesInjectedOsFamily() {
        HardwareInfoService service = new HardwareInfoService(OsFamily.LINUX, runner);
        assertEquals(OsFamily.LINUX, service.getOsFamily());
        assertTrue(service.getInfo(InfoType.MEMORY) instanceof org.jutils.jhardware.model.MemoryInfo);
    }

    @Test
    void serviceRejectsUnsupportedFamily() {
        HardwareInfoService service = new HardwareInfoService(OsFamily.MACOS, runner);
        assertThrows(UnsupportedOperatingSystemException.class,
                service::getMemoryInfo);
    }
}
