/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jhardware.info;

/**
 *
 * @author Javier
 */
public interface HardwareDataCollector {

    String getGeneralData();

    String getMemoryData();

    String getMotherBoardData();

    String getProcessorData();
    
}
