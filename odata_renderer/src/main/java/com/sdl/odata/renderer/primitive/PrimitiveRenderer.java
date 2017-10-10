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
package com.sdl.odata.renderer.primitive;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractRenderer;
import com.sdl.odata.renderer.primitive.writer.PrimitiveWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.sdl.odata.api.service.MediaType.TEXT;

/**
 * Render primitive data: $count, $value.
 */
@Component
public class PrimitiveRenderer extends AbstractRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(PrimitiveRenderer.class);

    private static final int DEFAULT_PRIMITIVE_SCORE = 35;

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {
        int operationScore = DEFAULT_SCORE;

        if (ODataUriUtil.isValuePathUri(requestContext.getUri())
                || ODataUriUtil.isCountPathUri(requestContext.getUri())) {
            operationScore = DEFAULT_PRIMITIVE_SCORE;
        }

        LOG.debug("Renderer score is {}", operationScore);
        return operationScore;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {
        LOG.debug("Start value for request: {}", requestContext);

        PrimitiveWriter primitiveWriter = new PrimitiveWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        String response = primitiveWriter.getPropertyAsString(data.getData());

        LOG.debug("Response value is {}", response);

        try {
            responseBuilder
                    .setContentType(TEXT)
                    .setHeader("OData-Version", ODATA_VERSION_HEADER)
                    .setBodyText(response, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering property for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        PrimitiveWriter primitiveWriter = new PrimitiveWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        ChunkedActionRenderResult renderResult = primitiveWriter.getPropertyStartDocument(result.getData(),
                outputStream);
        renderResult.setContentType(TEXT);
        renderResult.addHeader("OData-Version", ODATA_VERSION_HEADER);

        return renderResult;
    }

    @Override
    public ChunkedActionRenderResult renderBody(ODataRequestContext requestContext, QueryResult result,
                                                ChunkedActionRenderResult previousResult) throws ODataException {
        PrimitiveWriter primitiveWriter = new PrimitiveWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        return primitiveWriter.getPropertyBodyDocument(result.getData(), previousResult);
    }

    @Override
    public void renderEnd(ODataRequestContext requestContext, QueryResult result,
                          ChunkedActionRenderResult previousResult) throws ODataException {
        PrimitiveWriter primitiveWriter = new PrimitiveWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        primitiveWriter.getPropertyEndDocument(result.getData(), previousResult);
    }
}
