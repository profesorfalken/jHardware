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
package org.jutils.jhardware.info.graphicscard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jutils.jhardware.info.HardwareInfo;
import org.jutils.jhardware.info.ParsedData;
import org.jutils.jhardware.model.GraphicsCard;
import org.jutils.jhardware.model.GraphicsCardInfo;

/**
 * Base class for graphics-card providers.
 */
public abstract class AbstractGraphicsCardInfo implements HardwareInfo {

    @Override
    public GraphicsCardInfo getInfo() {
        return build(parseInfoData());
    }

    /**
     * Legacy parsing hook.
     *
     * @return raw graphics-card values
     * @deprecated override {@link #parseInfoData()} instead
     */
    @Deprecated
    protected abstract Map<String, String> parseInfo();

    /**
     * New parsing hook. The default implementation adapts the legacy
     * {@link #parseInfo()} method.
     *
     * @return parsed data
     */
    protected ParsedData parseInfoData() {
        return ParsedData.empty().putAll(parseInfo());
    }

    /**
     * Legacy model builder.
     *
     * @param dataMap raw values
     * @return the model
     * @deprecated use {@link #build(ParsedData)}
     */
    @Deprecated
    protected GraphicsCardInfo buildFromDataMap(Map<String, String> dataMap) {
        ParsedData safe = ParsedData.empty().putAll(dataMap);
        GraphicsCardInfo info = new GraphicsCardInfo();
        info.setFullInfo(safe.values());

        int count = safe.getInt("numOfGraphicsCards", 0);
        List<GraphicsCard> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            GraphicsCard card = new GraphicsCard();
            card.setName(safe.get("name_" + i));
            card.setManufacturer(safe.get("manufacturer_" + i));
            card.setChipType(safe.get("chip_type_" + i));
            card.setDacType(safe.get("dac_type_" + i));
            card.setDeviceType(safe.get("device_type_" + i));
            card.setTemperature(safe.get("temperature_" + i));
            card.setFanSpeed(safe.get("fan_speed_" + i));
            cards.add(card);
        }
        info.setGraphicsCards(cards);
        return info;
    }

    protected GraphicsCardInfo build(ParsedData data) {
        ParsedData safe = data == null ? ParsedData.empty() : data;
        GraphicsCardInfo info = buildFromDataMap(safe.values());
        info.setFullInfo(safe.values());
        info.setWarnings(safe.warnings());
        return info;
    }
}
