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
package org.jutils.jhardware.util;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.model.NetworkInterfaceInfo;

/**
 * Builds network interface snapshots from the JDK and, on Linux, from sysfs.
 *
 * <p>This replaces the old {@code ifconfig}/{@code ipconfig} parsers, which
 * were locale-dependent and failed silently when net-tools was not installed.
 * Traffic counters are read from {@code /sys/class/net/<if>/statistics} when
 * available; other platforms leave them {@code null}.</p>
 */
public final class NetworkInfoCollector {

    private NetworkInfoCollector() {
    }

    /**
     * @param sysClassNet sysfs network root (for example
     *                    {@code /sys/class/net}); may be {@code null}
     * @return collected interfaces and warnings
     */
    public static Result collect(Path sysClassNet) {
        List<String> warnings = new ArrayList<>();
        List<NetworkInterfaceInfo> interfaces = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces();
            if (enumeration == null) {
                warnings.add("The JVM did not report any network interfaces");
                return new Result(Arrays.asList(), warnings);
            }
            while (enumeration.hasMoreElements()) {
                interfaces.add(toInfo(enumeration.nextElement(), sysClassNet));
            }
        } catch (SocketException ex) {
            warnings.add("Could not enumerate network interfaces: " + ex.getMessage());
        }
        interfaces.sort(Comparator.comparing(NetworkInterfaceInfo::getName,
                Comparator.nullsLast(String::compareTo)));
        if (interfaces.isEmpty()) {
            warnings.add("No network interfaces were found");
        }
        return new Result(interfaces, warnings);
    }

    /**
     * Converts a list of interfaces into the raw key/value representation used
     * by {@code ComponentInfo#getFullInfo()}.
     *
     * @param interfaces interfaces
     * @return raw map
     */
    public static Map<String, String> toMap(List<NetworkInterfaceInfo> interfaces) {
        Map<String, String> values = new LinkedHashMap<>();
        List<NetworkInterfaceInfo> safe = interfaces == null ? Arrays.asList() : interfaces;
        values.put("interfacesLength", String.valueOf(safe.size()));
        for (int i = 0; i < safe.size(); i++) {
            NetworkInterfaceInfo info = safe.get(i);
            int index = i + 1;
            put(values, "interface_" + index, info.getName());
            put(values, "type_" + index, info.getType());
            put(values, "ipv4_" + index, info.getIpv4());
            put(values, "ipv6_" + index, info.getIpv6());
            put(values, "mac_" + index, info.getMacAddress());
            put(values, "received_packets_" + index, info.getReceivedPackets());
            put(values, "transmitted_packets_" + index, info.getTransmittedPackets());
            put(values, "received_bytes_" + index, info.getReceivedBytes());
            put(values, "transmitted_bytes_" + index, info.getTransmittedBytes());
        }
        return values;
    }

    private static NetworkInterfaceInfo toInfo(NetworkInterface networkInterface,
            Path sysClassNet) {
        NetworkInterfaceInfo info = new NetworkInterfaceInfo();
        info.setName(networkInterface.getName());
        info.setType(typeOf(networkInterface, sysClassNet));
        info.setMacAddress(formatMac(networkInterface));
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            if (address == null || address.getAddress() == null) {
                continue;
            }
            String hostAddress = address.getAddress().getHostAddress();
            if (hostAddress == null) {
                continue;
            }
            int scope = hostAddress.indexOf('%');
            if (scope > 0) {
                hostAddress = hostAddress.substring(0, scope);
            }
            if (address.getAddress() instanceof java.net.Inet4Address) {
                if (info.getIpv4() == null) {
                    info.setIpv4(hostAddress);
                }
            } else if (address.getAddress() instanceof java.net.Inet6Address) {
                if (info.getIpv6() == null) {
                    info.setIpv6(hostAddress);
                }
            }
        }
        if (sysClassNet != null) {
            Path statistics = sysClassNet.resolve(networkInterface.getName())
                    .resolve("statistics");
            if (Files.isDirectory(statistics)) {
                info.setReceivedPackets(readStat(statistics, "rx_packets"));
                info.setTransmittedPackets(readStat(statistics, "tx_packets"));
                info.setReceivedBytes(readStat(statistics, "rx_bytes"));
                info.setTransmittedBytes(readStat(statistics, "tx_bytes"));
            }
        }
        return info;
    }

    private static String typeOf(NetworkInterface networkInterface, Path sysClassNet) {
        try {
            if (networkInterface.isLoopback()) {
                return "loopback";
            }
            if (sysClassNet != null && Files.exists(sysClassNet
                    .resolve(networkInterface.getName()).resolve("wireless"))) {
                return "wireless";
            }
            if (sysClassNet != null && Files.isDirectory(sysClassNet
                    .resolve(networkInterface.getName()).resolve("bridge"))) {
                return "bridge";
            }
            if (networkInterface.isPointToPoint()) {
                return "point-to-point";
            }
            if (networkInterface.isVirtual()) {
                return "virtual";
            }
        } catch (SocketException ignored) {
            // Fall through to the generic type.
        }
        return "ethernet";
    }

    private static String formatMac(NetworkInterface networkInterface) {
        try {
            byte[] address = networkInterface.getHardwareAddress();
            if (address == null || address.length == 0) {
                return null;
            }
            StringBuilder builder = new StringBuilder(address.length * 3);
            for (byte value : address) {
                if (builder.length() > 0) {
                    builder.append(':');
                }
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (SocketException ex) {
            return null;
        }
    }

    private static String readStat(Path statistics, String name) {
        return HardwareInfoUtils.readValue(statistics.resolve(name)).orElse(null);
    }

    private static void put(Map<String, String> values, String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    /**
     * Collected interfaces and warnings.
     */
    public static final class Result {

        private final List<NetworkInterfaceInfo> interfaces;
        private final List<String> warnings;

        /**
         * @param interfaces interfaces
         * @param warnings   non-fatal warnings
         */
        public Result(List<NetworkInterfaceInfo> interfaces, List<String> warnings) {
            this.interfaces = interfaces == null
                    ? java.util.Collections.<NetworkInterfaceInfo>emptyList()
                    : java.util.Collections.unmodifiableList(
                            new ArrayList<>(interfaces));
            this.warnings = warnings == null
                    ? java.util.Collections.<String>emptyList()
                    : java.util.Collections.unmodifiableList(
                            new ArrayList<>(warnings));
        }

        public List<NetworkInterfaceInfo> interfaces() {
            return java.util.Collections.unmodifiableList(
                    new ArrayList<>(interfaces));
        }

        public List<String> warnings() {
            return java.util.Collections.unmodifiableList(
                    new ArrayList<>(warnings));
        }
    }
}
