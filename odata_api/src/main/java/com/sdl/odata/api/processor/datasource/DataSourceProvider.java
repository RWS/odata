/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;

/**
 * Data source provider capable of providing a data source which can handle OData entities.
 */
public interface DataSourceProvider {

    /**
     * Checks whether this data source provider can provide a suitable data source to handle the given entity type.
     *
     * @param requestContext  The OData request context.
     * @param entityType      The given entity type.
     * @return {@code true} if the given data source provider can provide
     * a suitable data source, {@code false} otherwise.
     * @throws ODataDataSourceException in case something goes wrong.
     */
    boolean isSuitableFor(ODataRequestContext requestContext, String entityType) throws ODataDataSourceException;

    /**
     * Get the data source associated with this data source provider.
     *
     * @param requestContext  The OData request context.
     * @return The data source.
     */
    DataSource getDataSource(ODataRequestContext requestContext);

    /**
     * Gets a strategy for executing the specified query operation or {@code null} if this data source provider cannot
     * provide a strategy for the query operation.
     *
     * @param requestContext          The OData request context.
     * @param operation               The query operation to get a strategy for.
     * @param expectedODataEntityType The fully-qualified name of the entity type that are expected as a result of this
     *                                query.
     * @return A {@code QueryOperationStrategy} to execute the query operation or {@code null}.
     * @throws ODataException If an error occurs that prevents the framework from working normally.
     */
    QueryOperationStrategy getStrategy(ODataRequestContext requestContext, QueryOperation operation,
                                       TargetType expectedODataEntityType) throws ODataException;
}
