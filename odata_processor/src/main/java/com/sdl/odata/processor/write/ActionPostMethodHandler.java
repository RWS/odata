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
package com.sdl.odata.processor.write;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.processor.ProcessorConfiguration;

/**
 * The hander for action and action import invocation request.
 */
public class ActionPostMethodHandler extends WriteMethodHandler {

    /**
     * Creates an instance of {@code ActionPostMethodHandler}.
     *
     * @param requestContext    The ODataRequestContext.
     * @param dataSourceFactory The DataSourceFactory.
     */
    public ActionPostMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory, ProcessorConfiguration configuration) {
        super(requestContext, dataSourceFactory, configuration);
    }

    /**
     * Handles action call and returns result in case when action returns it.
     *
     * @param action The instance of action.
     * @return The result of calling action.
     * @throws ODataException in case of any error.
     */
    @Override
    public ProcessorResult handleWrite(Object action) throws ODataException {
        Operation operation;
        if (action instanceof Operation) {
            operation = (Operation) action;
            Object data = operation.doOperation(getODataRequestContext(), getDataSourceFactory());
            if (data == null) {
                return new ProcessorResult(ODataResponse.Status.NO_CONTENT);
            } else {
                return new ProcessorResult(ODataResponse.Status.CREATED, QueryResult.from(data));
            }
        } else {
            throw new ODataBadRequestException("Incorrect operation instance");
        }
    }
}
