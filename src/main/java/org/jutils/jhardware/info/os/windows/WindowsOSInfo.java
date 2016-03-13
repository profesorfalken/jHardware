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
package org.jutils.jhardware.info.os.windows;

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import java.util.Calendar;
import java.util.Map;
import org.jutils.jhardware.info.os.AbstractOSInfo;

/**
 * Information related to Operating System
 *
 * @author Javier Garcia Alonso
 */
public final class WindowsOSInfo extends AbstractOSInfo {

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> osDataMap
                = WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_OPERATINGSYSTEM);

        osDataMap.put("Version", osDataMap.get("Version"));
        osDataMap.put("LastBootTime", normalizeBootUpDate(osDataMap.get("LastBootUpTime")));
        osDataMap.put("Name", osDataMap.get("Caption"));
        osDataMap.put("Manufacturer", osDataMap.get("Manufacturer"));

        return osDataMap;
    }

    private String normalizeBootUpDate(String rawBootUpTime) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.valueOf(rawBootUpTime.substring(0, 4)));
        c.set(Calendar.MONTH, Integer.valueOf(rawBootUpTime.substring(4, 6)));
        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(rawBootUpTime.substring(6, 8)));
        c.set(Calendar.HOUR, Integer.valueOf(rawBootUpTime.substring(8, 10)));
        c.set(Calendar.MINUTE, Integer.valueOf(rawBootUpTime.substring(10, 12)));
        c.set(Calendar.SECOND, Integer.valueOf(rawBootUpTime.substring(12, 14)));
        
        return c.getTime().toString();
    }
}
