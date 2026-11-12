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

import java.util.Locale;

/**
 * Operating-system families understood by jHardware.
 *
 * <p>Only {@link #LINUX} and {@link #WINDOWS} are implemented. macOS and
 * unknown systems produce a clear {@code UnsupportedOperatingSystemException}
 * instead of failing later with a {@code NullPointerException} or a generic
 * {@code UnsupportedOperationException}.</p>
 */
public enum OsFamily {
    WINDOWS,
    LINUX,
    MACOS,
    UNKNOWN;

    /**
     * Detects the current family from {@code os.name}.
     *
     * @return the detected family, never {@code null}
     */
    public static OsFamily current() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            return WINDOWS;
        }
        if (osName.contains("mac") || osName.contains("darwin")) {
            return MACOS;
        }
        if (osName.contains("nux") || osName.contains("nix")
                || osName.contains("aix") || osName.contains("linux")) {
            return LINUX;
        }
        return UNKNOWN;
    }

    public boolean isWindows() {
        return this == WINDOWS;
    }

    public boolean isLinux() {
        return this == LINUX;
    }

    public boolean isUnix() {
        return this == LINUX || this == MACOS;
    }
}
