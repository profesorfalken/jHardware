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

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.jutils.jhardware.model.BiosInfo;
import org.jutils.jhardware.model.Display;
import org.jutils.jhardware.model.DisplayInfo;
import org.jutils.jhardware.model.GraphicsCard;
import org.jutils.jhardware.model.GraphicsCardInfo;
import org.jutils.jhardware.model.MemoryInfo;
import org.jutils.jhardware.model.MotherboardInfo;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.model.NetworkInterfaceInfo;
import org.jutils.jhardware.model.OSInfo;
import org.jutils.jhardware.model.ProcessorInfo;

/**
 * Tests jHardware
 * 
 * @author Javier Garcia Alonso
 */
public class HardwareInfoTest {

    public HardwareInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getProcessorInfos method, of class HardwareInfo.
     */
    @org.junit.Test
    public void testGetProcessorInfo() {               
        System.out.println("Testing getProcessorInfo...");
        System.out.println("====================================");
        ProcessorInfo info = HardwareInfo.getProcessorInfo();

        assertTrue(info != null && info.getModelName() != null);

        System.out.println("Cache size: " + info.getCacheSize());        
        System.out.println("Family: " + info.getFamily());
        System.out.println("Speed (Mhz): " + info.getMhz());
        System.out.println("Model: " + info.getModel());
        System.out.println("Model name: " + info.getModelName());
        System.out.println("Number of cores: " + info.getNumCores());
        System.out.println("Stepping: " + info.getStepping());
        System.out.println("Temperature: " + info.getTemperature());
        System.out.println("Vendor Id: " + info.getVendorId());
        
        Set<Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
        for (final Entry<String, String> fullInfo : fullInfos) {
            System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
        }
        
        System.out.println("End testing getProcessorInfo...");
        System.out.println("====================================");
    }

    /**
     * Test of getMemoryInfos method, of class HardwareInfo.
     */
    @org.junit.Test
    public void testGetMemoryInfo() {               
        System.out.println("Testing getMemoryInfo...");
        System.out.println("====================================");
        MemoryInfo info = HardwareInfo.getMemoryInfo();

        assertTrue(info != null);
        
        System.out.println("Available Memory: " + info.getAvailableMemory());        
        System.out.println("Free Memory: " + info.getFreeMemory());
        System.out.println("Total Memory: " + info.getTotalMemory());
        
        Set<Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
        for (final Entry<String, String> fullInfo : fullInfos) {
            System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
        }
        
        System.out.println("End testing getMemoryInfo...");
        System.out.println("====================================");
    }
    
    /**
     * Test of getBiosInfo method, of class HardwareInfo.
     */
    @org.junit.Test
    public void testGetBiosInfo() {               
        System.out.println("Testing testGetBiosInfo...");
        System.out.println("====================================");
        BiosInfo info = HardwareInfo.getBiosInfo();

        assertTrue(info != null);
        
        System.out.println("BIOS manufacturer: " + info.getManufacturer());        
        System.out.println("Last update : " + info.getDate());
        System.out.println("Version: " + info.getVersion());
        
        Set<Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
        for (final Entry<String, String> fullInfo : fullInfos) {
            System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
        }
        
        System.out.println("End testing testGetBiosInfo...");
        System.out.println("====================================");
    }
    
    /**
     * Test of getBiosInfo method, of class HardwareInfo.
     */
    @org.junit.Test
    public void testGetMotherboardInfo() {               
        System.out.println("Testing testGetMotherboardInfo...");
        System.out.println("====================================");
        MotherboardInfo info = HardwareInfo.getMotherboardInfo();

        assertTrue(info != null);
        
        System.out.println("Manufacturer: " + info.getManufacturer());        
        System.out.println("Name : " + info.getName());
        System.out.println("Version: " + info.getVersion());
        
        Set<Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
        for (final Entry<String, String> fullInfo : fullInfos) {
            System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
        }
        
        System.out.println("End testing testGetMotherboardInfo...");
        System.out.println("====================================");
    }
    
    /**
     * Test of getBiosInfo method, of class HardwareInfo.
     */
    @org.junit.Test
    public void testGetOSInfo() {               
        System.out.println("Testing testGetOSInfo...");
        System.out.println("====================================");
        OSInfo info = HardwareInfo.getOSInfo();

        assertTrue(info != null && info.getManufacturer() != null);
        
        System.out.println("Manufacturer: " + info.getManufacturer());        
        System.out.println("Name: " + info.getName());
        System.out.println("Version: " + info.getVersion());
        System.out.println("Last boot time: " + info.getLastBootTime());
        
        Set<Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
        for (final Entry<String, String> fullInfo : fullInfos) {
            System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
        }
        
        System.out.println("End testing testOSInfo...");
        System.out.println("====================================");
    }
    
    /**
     * Test of getNetworkInfo method, of class HardwareInfo.
     */
    @org.junit.Test
    public void getNetworkInfo() {               
        System.out.println("Testing getNetworkInfo...");
        System.out.println("====================================");
        NetworkInfo info = HardwareInfo.getNetworkInfo();
        
        assertTrue(info != null && info.getNetworkInterfaces() != null);

        List<NetworkInterfaceInfo> networkInterfaces = info.getNetworkInterfaces();
        
        for (final NetworkInterfaceInfo interfaceInfo : networkInterfaces) {
            System.out.println("----------Interface: " + interfaceInfo.getName() + "---------");
            System.out.println("Ipv4: " + interfaceInfo.getIpv4());
            System.out.println("Ipv6: " + interfaceInfo.getIpv6());
            System.out.println("Received Packets: " + interfaceInfo.getReceivedPackets());
            System.out.println("Transmitted Packets: " + interfaceInfo.getTransmittedPackets());
            System.out.println("Received Bytes: " + interfaceInfo.getReceivedBytes());
            System.out.println("Transmitted Bytes: " + interfaceInfo.getTransmittedBytes());
            System.out.println("------------------------------------------------------");
        }
        
        System.out.println("End testing getNetworkInfo...");
        System.out.println("====================================");
    }
    
    /**
     * Test of getDisplayInfo method, of class HardwareInfo.
     */
    @org.junit.Test
    public void getDisplayInfo() {               
        System.out.println("Testing getDisplayInfo...");
        System.out.println("====================================");
        DisplayInfo info = HardwareInfo.getDisplayInfo();
        
        assertTrue(info != null && info.getDisplayDevices() != null);

        List<Display> displays = info.getDisplayDevices();
        
        for (final Display display : displays) {
            System.out.println("----------Display: " + display.getName() + "---------");
            System.out.println("Current Resolution: " + display.getCurrentResolution());
            System.out.println("Current Refresh Rate: " + display.getRefreshRate());
            System.out.println("Available Resolutions: " + Arrays.toString(display.getSupportedResolutions()));
            System.out.println("------------------------------------------------------");
        }
        
        System.out.println("End testing getDisplayInfo...");
        System.out.println("====================================");
    }
    
    /**
     * Test of getGraphicsCardsInfo method, of class HardwareInfo.
     */
    @org.junit.Test
    public void getGraphicsCardsInfo() {               
        System.out.println("Testing getGraphicsCardsInfo...");
        System.out.println("====================================");
        GraphicsCardInfo info = HardwareInfo.getGraphicsCardInfo();
        
        assertTrue(info != null && info.getGraphicsCards() != null);

        List<GraphicsCard> graphicsCards = info.getGraphicsCards();
        
        for (final GraphicsCard graphicsCard : graphicsCards) {
            System.out.println("----------Graphics Card: " + graphicsCard.getName() + "---------");
            System.out.println("Manufacturer: " + graphicsCard.getManufacturer());
            System.out.println("Device Type: " + graphicsCard.getDeviceType());
            System.out.println("DAC: " + graphicsCard.getDacType());
            System.out.println("Chip type: " + graphicsCard.getChipType());
            System.out.println("Temperature: " + graphicsCard.getTemperature());
            System.out.println("Fan Speed: " + graphicsCard.getFanSpeed());
            System.out.println("------------------------------------------------------");
        }
      
        System.out.println("End testing getGraphicsCardsInfo...");
        System.out.println("====================================");
    }
}
