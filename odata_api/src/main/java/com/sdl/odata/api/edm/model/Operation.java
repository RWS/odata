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
package com.sdl.odata.api.edm.model;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;

/**
 * The basic interface for operations like Actions, ActionImports, Functions, FunctionImports.
 *
 * @param <T> The result Type of performed operation.
 */
public interface Operation<T> {

    /**
     * The main method for operation.
     *
     * @param requestContext  The OData request context.
     * @param dataSourceFactory The Data Source Factory.
     * @return The result of operation or {@code null} if the operation doesn't return any value.
     * @throws ODataException if unable to execute the operation
     */
    T doOperation(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory)
            throws ODataException;
}
