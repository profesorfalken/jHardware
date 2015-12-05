/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jhardware;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.jutils.jhardware.model.ProcessorInfo;

/**
 *
 * @author javier
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
        System.out.println("Testing getProcessorInfos...");
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
        
    }

}
