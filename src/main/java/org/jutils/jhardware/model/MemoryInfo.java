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
package org.jutils.jhardware.model;

/**
 * Physical memory information.
 *
 * <p>The legacy string fields keep the raw platform representation. The
 * {@code *Bytes} fields provide a normalized value in bytes and are
 * {@code null} when it cannot be computed.</p>
 */
public class MemoryInfo extends AbstractComponentInfo {

    private static final long serialVersionUID = 1L;

    private String totalMemory;
    private String freeMemory;
    private String availableMemory;
    private Long totalMemoryBytes;
    private Long freeMemoryBytes;
    private Long availableMemoryBytes;

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }

    public String getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(String availableMemory) {
        this.availableMemory = availableMemory;
    }

    public Long getTotalMemoryBytes() {
        return totalMemoryBytes;
    }

    public void setTotalMemoryBytes(Long totalMemoryBytes) {
        this.totalMemoryBytes = totalMemoryBytes;
    }

    public Long getFreeMemoryBytes() {
        return freeMemoryBytes;
    }

    public void setFreeMemoryBytes(Long freeMemoryBytes) {
        this.freeMemoryBytes = freeMemoryBytes;
    }

    public Long getAvailableMemoryBytes() {
        return availableMemoryBytes;
    }

    public void setAvailableMemoryBytes(Long availableMemoryBytes) {
        this.availableMemoryBytes = availableMemoryBytes;
    }
}
