/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jhardware.info;

import java.util.Map;
import org.jutils.jhardware.model.ComponentInfo;
import org.jutils.jhardware.util.OSDetector;

/**
 *
 * @author Javier
 */
public abstract class AbstractHardwareInfo implements HardwareInfo {

    abstract public ComponentInfo getInfo();
    
    abstract protected Map<String, String> parseInfo(String data);
    
    abstract protected ComponentInfo buildFromDataMap(Map<String, String> dataMap);
    
    protected HardwareDataCollector getHardwareDataCollector() {
        if (OSDetector.isWindows()) {
            return WindowsHardwareDataCollector.get();
        }
        return UnixHardwareDataCollector.get();
    }
}
