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
package org.jutils.jhardware.info.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.model.NetworkInterfaceInfo;

/**
 * Information related to Network
 * 
 * @author Javier Garcia Alonso
 */
public abstract class AbstractNetworkInfo implements HardwareInfo { 

    /**
     *
     * @return
     */
    @Override
    public NetworkInfo getInfo() {
        return buildFromDataMap(parseInfo());
    }
    
    protected abstract Map<String, String> parseInfo();
    
    protected NetworkInfo buildFromDataMap(Map<String, String> dataMap) {
        NetworkInfo info = new NetworkInfo();
        
        List<NetworkInterfaceInfo> interfacesList = new ArrayList<>();
        if (!dataMap.isEmpty()) {
            int interfacesLength = Integer.parseInt(dataMap.get("interfacesLength"));
            for (int i = 1; i<=interfacesLength; i++) {
                NetworkInterfaceInfo interfaceInfo = new NetworkInterfaceInfo();
                interfaceInfo.setName(dataMap.get("interface_" + i));
                interfaceInfo.setType(dataMap.get("type_" + i));
                interfaceInfo.setIpv4(dataMap.get("ipv4_" + i));
                interfaceInfo.setIpv6(dataMap.get("ipv6_" + i));
                interfaceInfo.setReceivedPackets(dataMap.get("received_packets_" + i));
                interfaceInfo.setTransmittedPackets(dataMap.get("transmitted_packets_" + i));
                interfaceInfo.setReceivedBytes(dataMap.get("received_bytes_" + i));
                interfaceInfo.setTransmittedBytes(dataMap.get("transmitted_bytes_" + i));
                interfacesList.add(interfaceInfo);
            }
        }
        
        info.setNetworkInterfaces(interfacesList);
        
        return info;
    }
}
