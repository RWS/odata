/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.edm.factory;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;

/**
 * Factory for {@code EntityDataModel} instances.
 *
 */
public interface EntityDataModelFactory {

    /**
     * Builds an entity data model.
     *
     * @return The entity data model.
     * @throws ODataEdmException in case it is not possible to build the Entity Data Model.
     */
    EntityDataModel buildEntityDataModel() throws ODataEdmException;
}
