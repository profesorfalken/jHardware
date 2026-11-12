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
package org.jutils.jhardware.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jutils.jhardware.TestData;

class SerializationTest {

    @Test
    void processorInfoRoundTrips() throws Exception {
        ProcessorInfo info = new ProcessorInfo();
        info.setModelName("Test CPU");
        info.setNumCores("4");
        info.setFullInfo(TestData.map("model name", "Test CPU"));
        info.setWarnings(Arrays.asList("warning"));

        ProcessorInfo copy = roundTrip(info);

        assertEquals("Test CPU", copy.getModelName());
        assertEquals("4", copy.getNumCores());
        assertEquals("Test CPU", copy.getFullInfo().get("model name"));
        assertEquals(Arrays.asList("warning"), copy.getWarnings());
    }

    @Test
    void displayInfoRoundTrips() throws Exception {
        Display display = new Display();
        display.setName("eDP-1");
        display.setCurrentResolution("1920x1080");
        display.setSupportedResolutions(new String[]{"1920x1080@60"});
        DisplayInfo info = new DisplayInfo();
        info.setDisplayDevices(Arrays.asList(display));

        DisplayInfo copy = roundTrip(info);

        assertEquals(1, copy.getDisplayDevices().size());
        assertEquals("eDP-1", copy.getDisplayDevices().get(0).getName());
        assertEquals("1920x1080@60",
                copy.getDisplayDevices().get(0).getSupportedResolutions()[0]);
    }

    @Test
    void graphicsCardInfoRoundTrips() throws Exception {
        GraphicsCard card = new GraphicsCard();
        card.setName("GPU");
        card.setTemperature("50");
        GraphicsCardInfo info = new GraphicsCardInfo();
        info.setGraphicsCards(Arrays.asList(card));

        GraphicsCardInfo copy = roundTrip(info);

        assertEquals("GPU", copy.getGraphicsCards().get(0).getName());
        assertEquals("50", copy.getGraphicsCards().get(0).getTemperature());
    }

    @Test
    void networkInfoRoundTrips() throws Exception {
        NetworkInterfaceInfo nic = new NetworkInterfaceInfo();
        nic.setName("eth0");
        nic.setIpv4("192.0.2.10");
        NetworkInfo info = new NetworkInfo();
        info.setNetworkInterfaces(Arrays.asList(nic));

        NetworkInfo copy = roundTrip(info);

        assertEquals(1, copy.getNetworkInterfaces().size());
        assertEquals("eth0", copy.getNetworkInterfaces().get(0).getName());
        assertEquals("192.0.2.10", copy.getNetworkInterfaces().get(0).getIpv4());
    }

    @Test
    void allTopLevelModelsAreSerializable() throws Exception {
        MemoryInfo memory = new MemoryInfo();
        memory.setTotalMemoryBytes(1024L);
        BiosInfo bios = new BiosInfo();
        bios.setVersion("1.0");
        MotherboardInfo board = new MotherboardInfo();
        board.setName("Board");
        OSInfo os = new OSInfo();
        os.setName("Linux");

        assertEquals("1.0", roundTrip(bios).getVersion());
        assertEquals("Board", roundTrip(board).getName());
        assertEquals("Linux", roundTrip(os).getName());
        assertEquals(1024L, roundTrip(memory).getTotalMemoryBytes());
    }

    @Test
    void fullInfoAndWarningsAreImmutable() {
        ProcessorInfo info = new ProcessorInfo();
        info.setFullInfo(TestData.map("a", "b"));
        info.setWarnings(Arrays.asList("warning"));

        assertThrows(UnsupportedOperationException.class,
                () -> info.getFullInfo().put("c", "d"));
        assertThrows(UnsupportedOperationException.class,
                () -> info.getWarnings().add("another"));
    }

    @Test
    void nullCollectionsBecomeEmpty() {
        ProcessorInfo info = new ProcessorInfo();
        info.setFullInfo(null);
        info.setWarnings(null);

        assertTrue(info.getFullInfo().isEmpty());
        assertTrue(info.getWarnings().isEmpty());
    }

    @Test
    void blankWarningsAreDiscarded() {
        ProcessorInfo info = new ProcessorInfo();
        info.setWarnings(java.util.Arrays.asList("warning", "", "  ", null));
        assertFalse(info.getWarnings().isEmpty());
        assertEquals(1, info.getWarnings().size());
    }

    private static <T> T roundTrip(T value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            @SuppressWarnings("unchecked")
            T copy = (T) in.readObject();
            return copy;
        }
    }
}
