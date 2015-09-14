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

/**
 * Extension to traditional {@link DataSource} overloading existing API
 * methods with the addition of transaction management parameters.
 * Transactional DataSource API that will be used for batch requests.
 */
public interface TransactionalDataSource extends DataSource {

    /**
     * Creates an entity in the data storage.
     *
     * @param uri             The OData URI.
     * @param entity          The entity to create.
     * @param entityDataModel The entity data model.
     * @param transactionID   Unique transaction ID to be used in maintaining the transaction.
     * @return The created entity.
     * @throws ODataException If the operation fails.
     */
    Object create(ODataUri uri, Object entity, EntityDataModel entityDataModel,
                  String transactionID) throws ODataException;

    /**
     * Updates an entity in the data storage.
     *
     * @param uri             The OData URI.
     * @param entity          The entity to update.
     * @param entityDataModel The entity data model.
     * @param transactionID   Unique transaction ID to be used in maintaining the transaction.
     * @return The updated entity.
     * @throws ODataException If the operation fails.
     */
    Object update(ODataUri uri, Object entity, EntityDataModel entityDataModel,
                  String transactionID) throws ODataException;

    /**
     * Deletes an entity in the data storage.
     *
     * @param uri             The OData URI which identifies the entity to delete.
     * @param entityDataModel The entity data model.
     * @param transactionID   Unique transaction ID to be used in maintaining the transaction.
     * @throws ODataException If the operation fails.
     */
    void delete(ODataUri uri, EntityDataModel entityDataModel, String transactionID) throws ODataException;

    /**
     * Creates a new transaction in the data source with given ID.
     *
     * @param transactionID Unique transaction ID to be used in creating the transaction in Data Source..
     */
    void startTransaction(String transactionID);

    /**
     * Ends the transaction in the data source committing all of the processed CRUD operations.
     *
     * @param transactionID Unique transaction ID to be used in committing the transaction.
     * @param isSucccess    True -&gt; Commit the transaction, False -&gt; Rollback the transaction.
     */
    void endTransaction(String transactionID, boolean isSucccess);


}
