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
package com.sdl.odata.api.processor.datasource;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.link.ODataLink;

/**
 * The general API of the data source.
 * It can handle the webservice entity and performs CRUD operations.
 * <p>
 * Reference : OData Version 4.0 Part 1: Protocol. Paragraph 11
 * <p>
 *
 */
public interface DataSource {

    /**
     * Creates an entity in the data storage.
     *
     * @param uri             The OData URI.
     * @param entity          The entity to create.
     * @param entityDataModel The entity data model.
     * @return The created entity.
     * @throws ODataException If the operation fails.
     */
    Object create(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException;

    /**
     * Updates an entity in the data storage.
     *
     * @param uri             The OData URI.
     * @param entity          The entity to update.
     * @param entityDataModel The entity data model.
     * @param partialUpdate Whether this is partial or full update
     * @return The updated entity.
     * @throws ODataException If the operation fails.
     */
    Object update(ODataUri uri, Object entity, EntityDataModel entityDataModel, boolean partialUpdate) throws ODataException;

    /**
     * Deletes an entity in the data storage.
     *
     * @param uri             The OData URI which identifies the entity to delete.
     * @param entityDataModel The entity data model.
     * @throws ODataException If the operation fails.
     */
    void delete(ODataUri uri, EntityDataModel entityDataModel) throws ODataException;

    /**
     * Creates a link by updating the navigation property that the URI resolves to, by setting it to (in case of a
     * single value navigation property) or adding (in case of a collection navigation property) the entity specified
     * by type name and key. The URI must be a ".../$ref" URI.
     *
     * @param uri The ".../$ref" URI to the navigation property that stores the link.
     * @param link The `ODataLink` object that points to the target entity.
     * @param entityDataModel The entity data model.
     * @throws ODataException If the operation fails.
     */
    void createLink(ODataUri uri, ODataLink link, EntityDataModel entityDataModel)
            throws ODataException;

    /**
     * Deletes a link by updating the navigation property that the URI resolves to, by setting it to null (in case of a
     * single value navigation property) or removing (in case of a collection navigation property) the entity specified
     * by type name and key. The URI must be a ".../$ref" URI.
     *
     * @param uri The ".../$ref" URI to the navigation property that stores the link.
     * @param link The `ODataLink` object that points to the target entity.
     * @param entityDataModel The entity data model.
     * @throws ODataException If the operation fails.
     */
    void deleteLink(ODataUri uri, ODataLink link, EntityDataModel entityDataModel)
            throws ODataException;

    /**
     * Starts a DataSource transaction and returns the transactional datasource against which all
     * data operations for the active transaction can be completed.
     *
     * The returned TransactionalDatasource is not re-usable and every invocation of 'startTransaction()' should
     * return a new active transaction. All data operations belogning to the transaction need to be completed against
     * the instance of the TransactionalDataSource.
     *
     * @return The newly created active DataSource transaction instance
     */
    TransactionalDataSource startTransaction();
}
