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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.model.OSInfo;
import org.jutils.jhardware.model.ProcessorInfo;
import org.jutils.jhardware.util.OsFamily;

/**
 * Smoke tests that exercise the real Linux providers. They are skipped on
 * other platforms; parser behavior is covered by the unit tests.
 */
@EnabledOnOs(OS.LINUX)
class HardwareInfoIntegrationTest {

    @Test
    void readsCoreComponentsFromTheRealSystem() {
        HardwareInfoService service = new HardwareInfoService();

        ProcessorInfo processor = service.getProcessorInfo();
        assertNotNull(processor.getModelName());
        assertNotNull(processor.getNumCores());
        assertFalse(processor.getFullInfo().isEmpty());

        assertNotNull(service.getMemoryInfo().getTotalMemoryBytes());
        assertTrue(service.getMemoryInfo().getTotalMemoryBytes() > 0);

        OSInfo os = service.getOSInfo();
        assertNotNull(os.getName());

        NetworkInfo network = service.getNetworkInfo();
        assertFalse(network.getNetworkInterfaces().isEmpty());
    }

    @Test
    void optionalComponentsDoNotThrowWhenToolsAreMissing() {
        HardwareInfoService service = new HardwareInfoService();
        assertNotNull(service.getDisplayInfo());
        assertNotNull(service.getGraphicsCardInfo());
        assertNotNull(service.getBiosInfo());
        assertNotNull(service.getMotherboardInfo());
    }

    @Test
    void staticFacadeDelegatesToTheDefaultService() {
        assertNotNull(HardwareInfo.getMemoryInfo().getTotalMemoryBytes());
        assertTrue(HardwareInfo.getProcessorInfo().getFullInfo().size() > 0);
        assertNotNull(HardwareInfo.getOSInfo());
    }

    @Test
    void serviceReportsTheCurrentFamily() {
        assertTrue(OsFamily.current().isLinux());
    }
}
