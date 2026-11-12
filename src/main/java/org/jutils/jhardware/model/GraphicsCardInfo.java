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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Graphics cards attached to the system.
 */
public class GraphicsCardInfo extends AbstractComponentInfo {

    private static final long serialVersionUID = 1L;

    private ArrayList<GraphicsCard> graphicsCards = new ArrayList<>();

    /**
     * @return an unmodifiable list of graphics cards, never {@code null}
     */
    public List<GraphicsCard> getGraphicsCards() {
        return Collections.unmodifiableList(graphicsCards);
    }

    /**
     * @param graphicsCards cards to store; {@code null} is treated as empty
     */
    public void setGraphicsCards(List<GraphicsCard> graphicsCards) {
        this.graphicsCards = graphicsCards == null
                ? new ArrayList<>() : new ArrayList<>(graphicsCards);
    }
}
