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
package org.jutils.jhardware.info.network.unix;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.jutils.jhardware.info.network.AbstractNetworkInfo;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.util.NetworkInfoCollector;

/**
 * Linux network information from the JDK and sysfs traffic counters.
 */
public final class UnixNetworkInfo extends AbstractNetworkInfo {

    private static final Path DEFAULT_SYS_CLASS_NET = Paths.get("/sys/class/net");

    private final Path sysClassNet;

    public UnixNetworkInfo() {
        this(DEFAULT_SYS_CLASS_NET);
    }

    UnixNetworkInfo(Path sysClassNet) {
        this.sysClassNet = sysClassNet == null ? DEFAULT_SYS_CLASS_NET : sysClassNet;
    }

    @Override
    protected NetworkInfo parseNetworkInfo() {
        return build(NetworkInfoCollector.collect(sysClassNet));
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return NetworkInfoCollector.toMap(parseNetworkInfo().getNetworkInterfaces());
    }

}
