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
package org.jutils.jhardware.info.os.unix;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jutils.jhardware.info.os.AbstractOSInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;

/**
 * Information related to Operating System
 *
 * @author Javier Garcia Alonso
 */
public final class UnixOSInfo extends AbstractOSInfo {

    private final static String OS_RELEASE = "/etc/os-release";

    private String getOSLsbReleaseData() {
        String fullData = "";

        fullData += HardwareInfoUtils.executeCommand("lsb_release", "-a");

        return fullData;
    }

    private String getOSStartTimeData() {
        String fullData = "";

        fullData += HardwareInfoUtils.executeCommand("last", "-x");

        return fullData;
    }

    private String getOSReleaseData() {
        Stream<String> streamProcessorInfo = HardwareInfoUtils.readFile(OS_RELEASE);
        final StringBuilder buffer = new StringBuilder();

        streamProcessorInfo.forEach((String line) -> 
            buffer.append(line).append("\r\n")
        );

        return buffer.toString();
    }

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> osDataMap = new HashMap<>();
        
        String lsbRelease = getOSLsbReleaseData();
        String[] dataStringLines = lsbRelease.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            String[] dataStringInfo = dataLine.split(":");
            osDataMap.put(dataStringInfo[0].trim(), (dataStringInfo.length == 2) ? dataStringInfo[1].trim() : "");
        }
        
        String osRelease = getOSReleaseData();
        dataStringLines = osRelease.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            String[] dataStringInfo = dataLine.split("=");
            osDataMap.put(HardwareInfoUtils.toCamelCase("OS_" + dataStringInfo[0].trim()), 
                    (dataStringInfo.length == 2) ? dataStringInfo[1].trim().replaceAll("\"", "") : "");
        }
        
        String startTimeFullData = getOSStartTimeData();
        dataStringLines = startTimeFullData.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            if (dataLine.startsWith("reboot")) {
                osDataMap.put("LastBootTime", normalizeBootUpDate(dataLine.substring(39, 55)));
                break;
            }
        }
        
        //Set named data
        osDataMap.put("Manufacturer", osDataMap.get("Distributor ID"));
        osDataMap.put("Name", osDataMap.get("Description"));
        osDataMap.put("Version", osDataMap.get("Release"));
      
        return osDataMap;
    }
    
    private String normalizeBootUpDate(String rawBootUpdate) {
         DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm yyyy", Locale.ENGLISH);
         Date returnedDate;
         try{
             returnedDate = df.parse(rawBootUpdate + " " + Calendar.getInstance().get(Calendar.YEAR));
         } catch(ParseException pe) {
             return rawBootUpdate;
         }
         
         return returnedDate.toString();
    }
}