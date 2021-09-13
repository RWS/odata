/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import java.util.List;

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
     * Convert an OData entities list to a Datasource entities list.
     *
     * @param odataEntities   The list of OData entities.
     * @param entityDataModel The entity data model.
     * @return The list of Datasource entities.
     * @throws ODataDataSourceException If an error happens while converting the entity.
     */
    List<U> convertODataEntitiesListToDS(List<T> odataEntities, EntityDataModel entityDataModel)
            throws ODataDataSourceException;

    /**
     * Convert a Datasource entity to an OData entity.
     *
     * @param <R>              The odata entity result type
     * @param dsEntity         The Datasource entity.
     * @param odataEntityClass The class of the OData entity to create.
     * @param entityDataModel  The entity data model.
     * @return The OData entity.
     * @throws ODataDataSourceException If an error happens while converting the entity.
     */
    <R extends T> R convertDSEntityToOData(U dsEntity, Class<R> odataEntityClass, EntityDataModel entityDataModel)
            throws ODataDataSourceException;

    /**
     * Convert a Datasource entities list to an OData entities list.
     *
     * @param <R>              The odata entity result type
     * @param dsEntities       The list of Datasource entities.
     * @param odataEntityClass The class of the OData entity to create.
     * @param entityDataModel  The entity data model.
     * @return The list of OData entities.
     * @throws ODataDataSourceException If an error happens while converting the entity.
     */
    <R extends T> List<R> convertDSEntitiesListToOData(List<U> dsEntities, Class<R> odataEntityClass,
                                                             EntityDataModel entityDataModel)
            throws ODataDataSourceException;
}
