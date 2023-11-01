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
package com.sdl.odata.api.edm.registry;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;

import java.util.List;

/**
 * The OData Edm Registry
 *
 * With the help of ODataEdmRegistry you are able to get the EntityDataModel based on registered classes.
 */
public interface ODataEdmRegistry {

    /**
     * Register classes for the entity data model.
     * @param classes The list of entity classes to register
     */
    void registerClasses(List<Class<?>> classes);

    /**
     * Gets the entity data model based on register classes.
     * @return entityDataModel
     * @throws ODataException If unable to get the entity data model
     */
    EntityDataModel getEntityDataModel() throws ODataException;
}
