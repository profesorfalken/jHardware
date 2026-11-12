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
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.NetworkInfoCollector;

/**
 * Base class for network providers.
 */
public abstract class AbstractNetworkInfo implements HardwareInfo {

    @Override
    public NetworkInfo getInfo() {
        return parseNetworkInfo();
    }

    /**
     * Legacy parsing hook.
     *
     * @return raw network values
     * @deprecated override {@link #parseNetworkInfo()} instead
     */
    @Deprecated
    protected abstract Map<String, String> parseInfo();

    /**
     * New parsing hook. The default implementation adapts the legacy
     * {@link #parseInfo()} method.
     *
     * @return the snapshot
     */
    protected NetworkInfo parseNetworkInfo() {
        return buildFromDataMap(parseInfo());
    }

    /**
     * Legacy model builder.
     *
     * @param dataMap raw values
     * @return the model
     * @deprecated use {@link #build(NetworkInfoCollector.Result)}
     */
    @Deprecated
    protected NetworkInfo buildFromDataMap(Map<String, String> dataMap) {
        NetworkInfo info = new NetworkInfo();
        info.setFullInfo(dataMap);

        List<NetworkInterfaceInfo> interfaces = new ArrayList<>();
        if (dataMap != null && !dataMap.isEmpty()) {
            int length = HardwareInfoUtils.parseInt(dataMap.get("interfacesLength"), 0);
            for (int i = 1; i <= length; i++) {
                NetworkInterfaceInfo nic = new NetworkInterfaceInfo();
                nic.setName(dataMap.get("interface_" + i));
                nic.setType(dataMap.get("type_" + i));
                nic.setIpv4(dataMap.get("ipv4_" + i));
                nic.setIpv6(dataMap.get("ipv6_" + i));
                nic.setMacAddress(dataMap.get("mac_" + i));
                nic.setReceivedPackets(dataMap.get("received_packets_" + i));
                nic.setTransmittedPackets(dataMap.get("transmitted_packets_" + i));
                nic.setReceivedBytes(dataMap.get("received_bytes_" + i));
                nic.setTransmittedBytes(dataMap.get("transmitted_bytes_" + i));
                interfaces.add(nic);
            }
        }
        info.setNetworkInterfaces(interfaces);
        return info;
    }

    /**
     * Builds a snapshot from a collector result.
     *
     * @param result collected interfaces and warnings
     * @return the snapshot
     */
    protected NetworkInfo build(NetworkInfoCollector.Result result) {
        NetworkInfo info = new NetworkInfo();
        info.setNetworkInterfaces(result.interfaces());
        info.setFullInfo(NetworkInfoCollector.toMap(result.interfaces()));
        info.setWarnings(result.warnings());
        return info;
    }
}
