/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jhardware.jni;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class HardwareDataCollector
 * 
 * @author javier
 */
public class HardwareDataCollectorTest {
    
    public HardwareDataCollectorTest() {
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
     * Test of getGeneralData method, of class HardwareDataCollector.
     */
    @Test
    public void testGetGeneralData() {
        //Pass - Not implemented
    }

    /**
     * Test of getProcessorData method, of class HardwareDataCollector.
     */
    @Test
    public void testGetProcessorData() {
        HardwareDataCollector instance = HardwareDataCollector.INSTANCE;
        String result = instance.getProcessorData();        
        
        assertTrue(result != null);
        
        System.out.println("CPU data output: \n" + result);
    }

    /**
     * Test of getMotherBoardData method, of class HardwareDataCollector.
     */
    @Test
    public void testGetMotherBoardData() {
        //Pass - Not implemented
    }
    
}
