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
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.metadata.ServiceDocumentRenderer;
import com.sdl.odata.renderer.xml.writer.XMLServiceDocumentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.sdl.odata.api.service.MediaType.XML;

/**
 * This triggers and service document generator and also gives correct score based input parameters.
 */
@Component
public class XMLServiceDocumentRenderer extends ServiceDocumentRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(XMLServiceDocumentRenderer.class);

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {

        int score = super.scoreServiceDocument(requestContext, XML);
        if (shouldBeDefaultToXML(requestContext.getUri(), score)) {
            score += 1;
        }
        LOG.debug("Score of XML service document renderer is {}", score);

        return score;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {
        LOG.debug("Start rendering service document for request: {}", requestContext);

        XMLServiceDocumentWriter writer = new XMLServiceDocumentWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        String serviceDocument = writer.buildServiceDocument();

        try {
            responseBuilder
                    .setContentType(XML)
                    .setHeader("OData-Version", ODATA_VERSION_HEADER)
                    .setBodyText(serviceDocument, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering service document for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        ChunkedActionRenderResult renderResult = super.renderStart(requestContext, result, outputStream);
        renderResult.setContentType(XML);
        renderResult.addHeader("OData-Version", ODATA_VERSION_HEADER);

        return renderResult;
    }

    private boolean shouldBeDefaultToXML(ODataUri uri, int score) {
        return isServiceDocument(uri) && (score == WILDCARD_MATCH_SCORE || score == DEFAULT_SCORE);
    }
}
