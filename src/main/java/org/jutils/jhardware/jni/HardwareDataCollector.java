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
package org.jutils.jhardware.jni;

import java.io.IOException;
import org.jutils.jhardware.util.NativeUtils;
import org.jutils.jhardware.util.OSDetector;

/**
 * JNI interface to get SO information
 * 
 * @author Javier Garcia Alonso
 */
public enum HardwareDataCollector {

    INSTANCE;

    public native String getGeneralData();

    public native String getProcessorData();

    public native String getMotherBoardData();
    
    static {
        try {
            if (OSDetector.isUnix()) {
                NativeUtils.loadLibraryFromJar("/libJHardware.so");
            } else if (OSDetector.isWindows()) {
                NativeUtils.loadLibraryFromJar("/jHardware.dll");
            }
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }
}
