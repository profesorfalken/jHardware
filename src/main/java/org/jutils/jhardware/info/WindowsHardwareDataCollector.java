/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jhardware.info;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author javier
 */
public class WindowsHardwareDataCollector implements HardwareDataCollector{
    private final static String MEMINFO = "/proc/meminfo";
    private final static String CPUINFO = "/proc/cpuinfo";
    
    private static WindowsHardwareDataCollector INSTANCE = new WindowsHardwareDataCollector();
    
    private WindowsHardwareDataCollector(){}
    
    public static WindowsHardwareDataCollector get() {
        return INSTANCE;
    }
    
    public String getGeneralData() {
        return "";
    }

    public String getProcessorData() {
        Stream<String> streamProcessorInfo = readFile(CPUINFO);
        final StringBuilder buffer = new StringBuilder();
        
        streamProcessorInfo.forEach(new Consumer<String>() {
            public void accept(String line) {
                buffer.append(line).append("\r\n");                
            }
        });
        return buffer.toString();
    }
    
    public String getMemoryData(){
        Stream<String> streamMemoryInfo = readFile(MEMINFO);
        final StringBuilder buffer = new StringBuilder();
        
        streamMemoryInfo.forEach(new Consumer<String>() {
            public void accept(String line) {
                buffer.append(line).append("\r\n");                
            }
        });
        return buffer.toString();
    }

    public String getMotherBoardData(){
        return "";
    }
    
    static private Stream<String> readFile(String filePath){
        Path path = Paths.get(filePath);
         
        Stream<String> fileLines = null;
        try {
            fileLines = Files.lines(path);
        } catch (IOException ex) {
            Logger.getLogger(WindowsHardwareDataCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return fileLines;
    }
}
