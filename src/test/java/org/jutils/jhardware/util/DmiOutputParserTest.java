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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DmiOutputParserTest {

    @Test
    void parsesBiosFieldsAndMultilineCharacteristics() {
        String output = String.join("\n", "# dmidecode 3.3", "Handle 0x0000, DMI type 0, 26 bytes", "BIOS Information", "\tVendor: Dell Inc.", "\tVersion: 1.43.0", "\tRelease Date: 09/08/2025", "\tCharacteristics:", "\t\tPCI is supported", "\t\tBIOS is upgradeable");

        Map<String, String> values = DmiOutputParser.parse(output);

        assertEquals("Dell Inc.", values.get("Vendor"));
        assertEquals("1.43.0", values.get("Version"));
        assertEquals("09/08/2025", values.get("Release Date"));
        assertTrue(values.get("Characteristics").contains("PCI is supported"));
        assertTrue(values.get("Characteristics").contains("BIOS is upgradeable"));
    }

    @Test
    void parsesMotherboardFieldsAndFeatures() {
        String output = String.join("\n", "Handle 0x0002, DMI type 2, 15 bytes", "Base Board Information", "\tManufacturer: Dell Inc.", "\tProduct Name: 07WDVW", "\tVersion: A01", "\tFeatures:", "\t\tBoard is a hosting board");

        Map<String, String> values = DmiOutputParser.parse(output);

        assertEquals("Dell Inc.", values.get("Manufacturer"));
        assertEquals("07WDVW", values.get("Product Name"));
        assertEquals("A01", values.get("Version"));
        assertTrue(values.get("Features").contains("hosting board"));
    }

    @Test
    void handlesNullAndEmptyInput() {
        assertTrue(DmiOutputParser.parse(null).isEmpty());
        assertTrue(DmiOutputParser.parse("").isEmpty());
        assertTrue(DmiOutputParser.parse("   ").isEmpty());
    }
}
