/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.api.processor.datasource.factory;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;

/**
 * Factory which can provide implementations of
 * {@link DataSource} is able to handle OData
 * entities.
 */
public interface DataSourceFactory {

    /**
     * Gets a data source able to handle entities of the given entity type.
     *
     * @param requestContext  The OData request context.
     * @param entityType      The given entity type.
     * @throws ODataDataSourceException if unable to get a datasource
     * @return The data source.
     */
    DataSource getDataSource(ODataRequestContext requestContext, String entityType)
            throws ODataDataSourceException;

    /**
     * Gets a strategy for executing the specified query operation or {@code null} if no strategy could be created
     * for the query operation.
     *
     * @param requestContext          The OData request context.
     * @param operation               The query operation to get a strategy for.
     * @param expectedODataEntityType The fully-qualified name of the entity type that are expected as a result of this
     *                                query.
     * @return A {@code QueryOperationStrategy} to execute the query operation or {@code null}.
     * @throws ODataException if unable to get a datasource strategy
     */
    QueryOperationStrategy getStrategy(ODataRequestContext requestContext, QueryOperation operation,
                                       TargetType expectedODataEntityType) throws ODataException;
}
