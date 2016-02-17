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
 * Information related to CPU
 *
 * @author Javier Garcia Alonso
 */
public final class WindowsOSInfo extends AbstractOSInfo {

    protected Map<String, String> parseInfo() {
        Map<String, String> osDataMap
                = WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_OPERATINGSYSTEM);

        osDataMap.put("Version", osDataMap.get("Version"));
        osDataMap.put("LastBootTime", parseBootUpTime(osDataMap.get("LastBootUpTime")));
        osDataMap.put("Name", osDataMap.get("Caption"));
        osDataMap.put("Manufacturer", osDataMap.get("Manufacturer"));

        return osDataMap;
    }

    private String parseBootUpTime(String rawBootUpTime) {
        String test = "20160217083430.500000+060";
        String year = test.substring(0, 4);
        String month = test.substring(4, 6);
        String day = test.substring(6, 8);
        String hour = test.substring(8, 10);
        String minute = test.substring(10, 12);
        String seconds = test.substring(12, 14);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.valueOf(year));
        c.set(Calendar.MONTH, Integer.valueOf(month));
        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
        c.set(Calendar.HOUR, Integer.valueOf(hour));
        c.set(Calendar.MINUTE, Integer.valueOf(minute));
        c.set(Calendar.SECOND, Integer.valueOf(seconds));
        
        return c.getTime().toString();
    }
}
