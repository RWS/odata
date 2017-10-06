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
package com.sdl.odata.renderer.xml;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractAtomRenderer;
import com.sdl.odata.renderer.xml.writer.XMLPropertyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.sdl.odata.api.service.MediaType.XML;

/**
 * Renderer which renders an OData XML value.
 * <p>
 * This renderer generates an XML response body with an &lt;metadata:value&gt; root element. This should for example be
 * used when the result of a query consists of a single primitive or complex value, or a collection of primitive or
 * complex values.
 * <p>
 * Reference: OData Atom Format Version 4.0 specification
 */
@Component
public final class XMLValueRenderer extends AbstractAtomRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(XMLValueRenderer.class);

    private static final int DEFAULT_OPERATION_SCORE = 50;

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {
        // This renderer only handles non-entity queries
        if (!isNonEntityQuery(requestContext.getUri(), requestContext.getEntityDataModel())) {
            return DEFAULT_SCORE;
        }

        int operationScore = DEFAULT_SCORE;
        if (ODataUriUtil.isFunctionCallUri(requestContext.getUri())
                || ODataUriUtil.isActionCallUri(requestContext.getUri())) {
            operationScore = DEFAULT_OPERATION_SCORE;
        }

        int returnScore = Math.max(super.score(requestContext, data), operationScore);
        LOG.debug("Renderer score is {}", returnScore);

        return returnScore;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {

        LOG.debug("Start rendering property for request: {}", requestContext);

        // Root element must be <metadata:value>
        XMLPropertyWriter propertyWriter = new XMLPropertyWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        String response = propertyWriter.getPropertyAsString(data.getData());
        LOG.debug("Response property xml is {}", response);
        try {
            responseBuilder
                    .setContentType(XML)
                    .setBodyText(response, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering property for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        LOG.debug("Start rendering start property for request: {}", requestContext);
        XMLPropertyWriter propertyWriter = new XMLPropertyWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        Type type = propertyWriter.getTypeFromODataUri();
        propertyWriter.validateRequestChunk(type, result.getData());

        ChunkedActionRenderResult renderResult = propertyWriter.getPropertyStartDocument(result.getData(),
                outputStream);
        renderResult.setType(type);
        renderResult.setTypeValidated(true);
        renderResult.setWriter(propertyWriter);
        renderResult.setContentType(XML);
        return renderResult;
    }

    @Override
    public ChunkedActionRenderResult renderBody(ODataRequestContext requestContext, QueryResult result,
                                                ChunkedActionRenderResult previousResult) throws ODataException {
        LOG.debug("Start rendering body property for request: {}", requestContext);
        return ((XMLPropertyWriter) previousResult.getWriter())
                .getPropertyBodyDocument(result.getData(), previousResult);
    }

    @Override
    public void renderEnd(ODataRequestContext requestContext, QueryResult result,
                          ChunkedActionRenderResult previousResult) throws ODataException {
        LOG.debug("Start rendering end property for request: {}", requestContext);
        ((XMLPropertyWriter) previousResult.getWriter()).getPropertyEndDocument(result.getData(), previousResult);
    }
}
