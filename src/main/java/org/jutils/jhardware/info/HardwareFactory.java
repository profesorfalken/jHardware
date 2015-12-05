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
package org.jutils.jhardware.info;

import org.jutils.jhardware.info.processor.UnixProcessorInfo;
import org.jutils.jhardware.info.processor.WindowsProcessorInfo;
import org.jutils.jhardware.util.OSDetector;

/**
 * Factory class to get the right information
 * 
 * @author Javier Garcia Alonso
 */
public class HardwareFactory {	

   public static HardwareInfo getInfo(InfoType type){     
      switch (type) {
          case PROCESSOR:
              if (OSDetector.isWindows()) {
                return new WindowsProcessorInfo();
            } else if (OSDetector.isUnix()) {
                return new UnixProcessorInfo();
            }
          case MOTHERBOARD:
              return null;//new MotherboardInfo();
          default:
              throw new IllegalArgumentException("Type of hardware not supported: " + type);
      }        
   }
}
