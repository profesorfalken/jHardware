/*
 * Copyright 2016 Javier Garcia Alonso.
 *
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

import java.util.List;

/**
 * Model that encapsulates graphics card information
 *
 * @author Javier Garcia Alonso
 */
public class GraphicsCardInfo implements ComponentInfo {

    List<GraphicsCard> graphicsCards;

    public List<GraphicsCard> getGraphicsCards() {
        return graphicsCards;
    }

    public void setGraphicsCards(List<GraphicsCard> graphicsCards) {
        this.graphicsCards = graphicsCards;
    }

}
