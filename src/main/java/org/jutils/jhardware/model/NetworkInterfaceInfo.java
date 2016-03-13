/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jhardware.model;

/**
 *
 * @author Javier Garcia Alonso
 */
public class NetworkInterfaceInfo implements ComponentInfo{
    private String name;
    private String type;
    private String ipv4;
    private String ipv6;
    private String receivedPackets;
    private String transmittedPackets;
    private String receivedBytes;
    private String transmittedBytes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public String getReceivedPackets() {
        return receivedPackets;
    }

    public void setReceivedPackets(String receivedPackets) {
        this.receivedPackets = receivedPackets;
    }

    public String getTransmittedPackets() {
        return transmittedPackets;
    }

    public void setTransmittedPackets(String transmittedPackets) {
        this.transmittedPackets = transmittedPackets;
    }

    public String getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(String receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public String getTransmittedBytes() {
        return transmittedBytes;
    }

    public void setTransmittedBytes(String transmittedBytes) {
        this.transmittedBytes = transmittedBytes;
    }    
}
