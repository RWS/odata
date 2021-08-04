/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.api.edm.model;

import java.util.List;

/**
 * OData singleton.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 13.3
 *
 */
public interface Singleton {

    /**
     * Returns the name of the singleton.
     *
     * @return The name of the singleton.
     */
    String getName();

    /**
     * Returns the fully-qualified name of the entity type of this singleton.
     *
     * @return The fully-qualified name of the entity type of this singleton.
     */
    String getTypeName();

    /**
     * Returns the navigation property bindings in this singleton.
     *
     * @return The navigation property bindings in this singleton.
     */
    List<NavigationPropertyBinding> getNavigationPropertyBindings();
}
