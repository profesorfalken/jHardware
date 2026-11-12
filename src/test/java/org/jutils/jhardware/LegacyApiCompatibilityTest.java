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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jutils.jhardware.info.processor.AbstractProcessorInfo;
import org.jutils.jhardware.info.processor.unix.UnixProcessorInfo;
import org.jutils.jhardware.model.ComponentInfo;
import org.jutils.jhardware.model.NetworkInterfaceInfo;
import org.jutils.jhardware.model.ProcessorInfo;
import org.jutils.jhardware.util.DirectXInfoLoader;

/**
 * Guards the protected/public API that existed in 0.8.x.
 */
class LegacyApiCompatibilityTest {

    @Test
    void legacyParseInfoOverrideStillWorks() {
        ProcessorInfo info = new LegacyProcessor().getInfo();

        assertEquals("Legacy CPU", info.getModelName());
        assertEquals("Legacy CPU", info.getFullInfo().get("model name"));
        assertTrue(info.getWarnings().isEmpty());
    }

    @Test
    void legacyBuildFromDataMapOverrideStillWorks() {
        assertEquals("legacy-builder", new LegacyBuilder().getInfo().getModelName());
    }

    @Test
    void componentInfoHasCompatibleDefaultMethods() {
        ComponentInfo info = new ComponentInfo() {
        };

        assertTrue(info.getFullInfo().isEmpty());
        assertTrue(info.getWarnings().isEmpty());
    }

    @Test
    void networkInterfaceInfoIsStillAComponentInfo() {
        assertTrue(new NetworkInterfaceInfo() instanceof ComponentInfo);
    }

    @Test
    void directXInfoLoaderKeepsItsEnumApi() {
        assertSame(DirectXInfoLoader.get, DirectXInfoLoader.valueOf("get"));
        assertEquals(1, DirectXInfoLoader.values().length);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void legacyProcessorDataMethodStillExists() {
        assertTrue(UnixProcessorInfo.getProcessorData().contains("processor"));
    }

    @Test
    void unsupportedOperatingSystemIsStillAnUnsupportedOperationException() {
        assertTrue(new UnsupportedOperatingSystemException("test")
                instanceof UnsupportedOperationException);
    }

    private static final class LegacyProcessor extends AbstractProcessorInfo {

        @Override
        protected Map<String, String> parseInfo() {
            Map<String, String> values = new LinkedHashMap<>();
            values.put("model name", "Legacy CPU");
            return values;
        }
    }

    private static final class LegacyBuilder extends AbstractProcessorInfo {

        @Override
        protected Map<String, String> parseInfo() {
            return new LinkedHashMap<>();
        }

        @Override
        protected ProcessorInfo buildFromDataMap(Map<String, String> dataMap) {
            ProcessorInfo info = new ProcessorInfo();
            info.setModelName("legacy-builder");
            return info;
        }
    }
}
