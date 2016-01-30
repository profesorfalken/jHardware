/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jhardware.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Javier
 */
public class HardwareInfoUtils {
    public static Stream<String> readFile(String filePath){
        Path path = Paths.get(filePath);
         
        Stream<String> fileLines = null;
        try {
            fileLines = Files.lines(path);
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return fileLines;
    }
}
