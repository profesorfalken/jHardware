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
package org.jutils.jhardware.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jutils.jhardware.TestData;

class DirectXInfoLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesDxDiagDisplayDevices() throws Exception {
        String xml = String.join("\n",
                "<DxDiag>",
                "  <DisplayDevices>",
                "    <DisplayDevice>",
                "      <CardName>Test GPU</CardName>",
                "      <MonitorName>Test Monitor</MonitorName>",
                "      <CurrentMode>1920 x 1080 (32 bit) (60Hz)</CurrentMode>",
                "    </DisplayDevice>",
                "  </DisplayDevices>",
                "</DxDiag>");
        Path file = tempDir.resolve("dxdiag.xml");
        TestData.write(file, xml);

        DirectXInfoLoader.Result result = DirectXInfoLoader.parse(file);

        assertEquals(1, result.displays().size());
        assertEquals("Test GPU", result.displays().get(0).get("CardName"));
        assertEquals("1920 x 1080 (32 bit) (60Hz)",
                result.displays().get(0).get("CurrentMode"));
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void malformedXmlReturnsWarningsInsteadOfThrowing() throws Exception {
        Path file = tempDir.resolve("broken.xml");
        TestData.write(file, "<DxDiag>");

        DirectXInfoLoader.Result result = DirectXInfoLoader.parse(file);

        assertTrue(result.displays().isEmpty());
        assertFalse(result.warnings().isEmpty());
    }
}
