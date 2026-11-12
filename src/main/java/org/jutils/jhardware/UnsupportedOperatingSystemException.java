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
package org.jutils.jhardware;

/**
 * Thrown when the current operating system has no jHardware implementation.
 */
public class UnsupportedOperatingSystemException extends HardwareInfoException {

    private static final long serialVersionUID = 1L;

    public UnsupportedOperatingSystemException(String message) {
        super(message);
    }
}
