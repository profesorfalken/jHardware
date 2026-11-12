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
package org.jutils.jhardware.info.network.windows;

import java.util.ArrayList;
import java.util.List;
import org.jutils.jhardware.info.network.AbstractNetworkInfo;
import org.jutils.jhardware.model.NetworkInfo;
import org.jutils.jhardware.util.NetworkInfoCollector;

/**
 * Network information for Windows.
 *
 * <p>Interfaces and addresses come from the JDK, which is locale-independent.
 * Per-interface traffic counters are not exposed by the JDK; the snapshot
 * includes a warning instead of attaching global {@code netstat} totals to
 * every interface (the old behavior was misleading).</p>
 */
public final class WindowsNetworkInfo extends AbstractNetworkInfo {

    @Override
    protected NetworkInfo parseNetworkInfo() {
        NetworkInfoCollector.Result collected = NetworkInfoCollector.collect(null);
        List<String> warnings = new ArrayList<>(collected.warnings());
        warnings.add("Per-interface traffic counters are not available on Windows");
        return build(new NetworkInfoCollector.Result(collected.interfaces(), warnings));
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return NetworkInfoCollector.toMap(parseNetworkInfo().getNetworkInterfaces());
    }

}
