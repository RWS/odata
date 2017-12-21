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
package com.sdl.odata.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.ODataWriteProcessor;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.processor.write.ActionPostMethodHandler;
import com.sdl.odata.processor.write.DeleteMethodHandler;
import com.sdl.odata.processor.write.PatchMethodHandler;
import com.sdl.odata.processor.write.PostMethodHandler;
import com.sdl.odata.processor.write.PutMethodHandler;
import com.sdl.odata.processor.write.WriteMethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sdl.odata.api.service.ODataResponse.Status.METHOD_NOT_ALLOWED;


/**
 * Implementation of {@code ODataWriteProcessor}.
 */
@Component
public class ODataWriteProcessorImpl implements ODataWriteProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ODataWriteProcessorImpl.class);

    @Autowired
    private DataSourceFactory dataSourceFactory;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ODataParser uriParser;

    @Override
    public ProcessorResult write(ODataRequestContext requestContext, Object entity) throws ODataException {
        try {
            WriteMethodHandler methodHandler = getHandler(requestContext);
            if (methodHandler == null) {
                return new ProcessorResult(METHOD_NOT_ALLOWED);
            }
            return methodHandler.handleWrite(entity);
        } catch (ODataDataSourceException e) {
            LOG.error("Couldn't persist or delete given entity '" + entity + "'", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Unexpected Exception when persisting or deleting an entity.", e);
            throw e;
        }
    }

    private WriteMethodHandler getHandler(ODataRequestContext requestContext) {
        ODataRequest.Method method = requestContext.getRequest().getMethod();
        LOG.debug("Requested method is {}", method);
        switch (method) {
            case POST:
                if (ODataUriUtil.isActionCallUri(requestContext.getUri())) {
                    LOG.debug("Invoking Action POST method handler");
                    return new ActionPostMethodHandler(requestContext, dataSourceFactory);
                } else {
                    return new PostMethodHandler(requestContext, dataSourceFactory);
                }
            case PUT:
                return new PutMethodHandler(requestContext, dataSourceFactory);
            case PATCH:
                return new PatchMethodHandler(requestContext, dataSourceFactory, objectMapper, uriParser);
            case DELETE:
                return new DeleteMethodHandler(requestContext, dataSourceFactory);
            default:
                LOG.error("Invalid HTTP method: {}", method);
        }
        return null;
    }
}
