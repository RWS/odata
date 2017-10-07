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
package com.sdl.odata.renderer.json;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractRenderer;
import com.sdl.odata.renderer.json.writer.JsonServiceDocumentWriter;
import com.sdl.odata.renderer.metadata.ServiceDocumentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.MediaType.JSON;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * JSON Service Root Renderer
 * <p>
 * It renders the service document according to Chapter 5 of reference.
 * Reference: <a href="http://docs.oasis-open.org/odata/odata-json-format/v4.0/os/odata-json-format-v4.0-os.html">
 * OData Atom Format Version 4.0 specification</a>
 * <p>
 * author: Stanislav Lozenko
 */
@Component
public class JsonServiceDocumentRenderer extends ServiceDocumentRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(JsonServiceDocumentRenderer.class);

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {

        int score = super.scoreServiceDocument(requestContext, JSON);
        LOG.debug("Score of JSON service document renderer is {}", score);

        return score;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {

        LOG.debug("Start rendering entity(es) for request: {}", requestContext);

        JsonServiceDocumentWriter writer = new JsonServiceDocumentWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        String json = writer.buildJson();

        try {
            responseBuilder
                    .setContentType(JSON)
                    .setHeader("OData-Version", AbstractRenderer.ODATA_VERSION_HEADER)
                    .setBodyText(json, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering entity(es) for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        ChunkedActionRenderResult renderResult = super.renderStart(requestContext, result, outputStream);
        renderResult.setContentType(JSON);
        renderResult.addHeader("OData-Version", AbstractRenderer.ODATA_VERSION_HEADER);

        return renderResult;
    }
}
