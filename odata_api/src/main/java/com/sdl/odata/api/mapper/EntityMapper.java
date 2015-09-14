/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
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
package com.sdl.odata.api.mapper;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;

/**
 * Converts OData entities to Datasource entities and vice versa.
 *
 * @param <T> Type of OData entities that this mapper handles.
 * @param <U> Type of Datasource entities that this mapper handles.
 */
public interface EntityMapper<T, U> {

    /**
     * Convert an OData entity to a Datasource entity.
     *
     * @param odataEntity     The OData entity.
     * @param entityDataModel The entity data model.
     * @return The Datasource entity.
     * @throws ODataDataSourceException If an error happens while converting the entity.
     */
    U convertODataEntityToDS(T odataEntity, EntityDataModel entityDataModel) throws ODataDataSourceException;

    /**
     * Convert a Datasource entity to an OData entity.
     *
     * @param <R> The odata entity result type
     * @param dsEntity        The Datasource entity.
     * @param odataEntityClass The class of the OData entity to create.
     * @param entityDataModel  The entity data model.
     * @return The OData entity.
     * @throws ODataDataSourceException If an error happens while converting the entity.
     */
    <R extends T> R convertDSEntityToOData(U dsEntity, Class<R> odataEntityClass, EntityDataModel entityDataModel)
            throws ODataDataSourceException;
}
