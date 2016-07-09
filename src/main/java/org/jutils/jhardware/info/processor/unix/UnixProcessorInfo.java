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
package org.jutils.jhardware.info.processor.unix;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.jutils.jhardware.info.processor.AbstractProcessorInfo;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.TemperatureUtils;

/**
 * Information related to CPU
 *
 * @author Javier Garcia Alonso
 */
public final class UnixProcessorInfo extends AbstractProcessorInfo {

    private static final String CPUINFO = "/proc/cpuinfo";    

    public static String getProcessorData() {
        Stream<String> streamProcessorInfo = HardwareInfoUtils.readFile(CPUINFO);
        final StringBuilder buffer = new StringBuilder();

        streamProcessorInfo.forEach((String line)
                -> buffer.append(line).append("\r\n")
        );
        return buffer.toString();
    }

    @Override
    protected Map<String, String> parseInfo() {
        Map<String, String> processorDataMap = new HashMap<>();
        String[] dataStringLines = getProcessorData().split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            String[] dataStringInfo = dataLine.split(":");
            processorDataMap.put(dataStringInfo[0].trim(),
                    (dataStringInfo.length == 2) ? dataStringInfo[1].trim() : "");
        }
        
        processorDataMap.put("temperature", TemperatureUtils.getCpuTemperatureForLinux());

        return processorDataMap;
    }
}
