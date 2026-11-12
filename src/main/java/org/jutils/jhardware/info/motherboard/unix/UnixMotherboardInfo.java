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
package org.jutils.jhardware.info.motherboard.unix;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.info.motherboard.AbstractMotherboardInfo;
import org.jutils.jhardware.util.CommandResult;
import org.jutils.jhardware.util.CommandRunner;
import org.jutils.jhardware.util.DmiOutputParser;
import org.jutils.jhardware.util.HardwareInfoUtils;
import org.jutils.jhardware.util.SystemCommandRunner;

/**
 * Motherboard information from the DMI sysfs files, with an optional
 * {@code dmidecode --type 2} fallback.
 */
public final class UnixMotherboardInfo extends AbstractMotherboardInfo {

    private static final Path DEFAULT_DMI_ROOT = Paths.get("/sys/class/dmi/id");

    private final Path dmiRoot;
    private final CommandRunner runner;

    public UnixMotherboardInfo() {
        this(DEFAULT_DMI_ROOT, SystemCommandRunner.defaultRunner());
    }

    public UnixMotherboardInfo(CommandRunner runner) {
        this(DEFAULT_DMI_ROOT, runner);
    }

    UnixMotherboardInfo(Path dmiRoot, CommandRunner runner) {
        this.dmiRoot = dmiRoot == null ? DEFAULT_DMI_ROOT : dmiRoot;
        this.runner = runner == null ? SystemCommandRunner.defaultRunner() : runner;
    }

    @Override
    protected ParsedData parseInfoData() {
        ParsedData data = ParsedData.empty();
        read(data, "board_name", "Product Name");
        read(data, "board_vendor", "Vendor");
        read(data, "board_version", "Version");

        boolean missing = data.get("Product Name") == null
                || data.get("Vendor") == null
                || data.get("Version") == null;
        if (missing) {
            CommandResult result = runner.run("dmidecode", "--type", "2");
            if (result.isSuccess() && !result.stdout().trim().isEmpty()) {
                Map<String, String> values = DmiOutputParser.parse(result.stdout());
                data.putAll(values);
                data.put("Vendor", HardwareInfoUtils.firstNonBlank(
                        data.get("Vendor"), values.get("Manufacturer"), values.get("Vendor")));
                data.put("Product Name", HardwareInfoUtils.firstNonBlank(
                        data.get("Product Name"), values.get("Product Name"),
                        values.get("Product")));
                data.put("Version", HardwareInfoUtils.firstNonBlank(
                        data.get("Version"), values.get("Version")));
            } else if (data.isEmpty()) {
                data.warn("Motherboard information unavailable: DMI files are not "
                        + "readable and dmidecode could not be executed without privileges");
            } else {
                data.warn("Some motherboard fields are not available without privileges");
            }
        }
        if (data.isEmpty()) {
            data.warn("No motherboard information could be retrieved");
        }
        return data;
    }

    private void read(ParsedData data, String fileName, String key) {
        HardwareInfoUtils.readValue(dmiRoot.resolve(fileName))
                .ifPresent(value -> data.put(key, value));
    }
    @Override
    @Deprecated
    protected java.util.Map<String, String> parseInfo() {
        return parseInfoData().values();
    }

}
