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
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.parser.MetadataUri;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.sdl.odata.api.service.MediaType.XML;

/**
 * <p>Renderer which renders the OData metadata document.</p>
 * <p>The metadata document is always in XML and conforms to the CSDL schema.</p>
 * <p>Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL).</p>
 */
@Component
public final class MetadataDocumentRenderer extends AbstractRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentRenderer.class);
    /**
     * Max Renderer Score.
     */
    public static final int MAX = 100;
    private static final String ODATA_VERSION_HEADER = "4.0";

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {
        ODataUri uri = requestContext.getUri();
        return uri != null && uri.relativeUri() instanceof MetadataUri ? MAX : 0;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {

        LOG.debug("Start rendering $metadata document for request: {}", requestContext);

        MetadataDocumentWriter writer = new MetadataDocumentWriter(requestContext.getEntityDataModel());
        writer.startDocument();
        writer.writeMetadataDocument();
        writer.endDocument();

        try {
            responseBuilder
                    .setStatus(ODataResponse.Status.OK)
                    .setContentType(XML)
                    .setHeader("OData-Version", ODATA_VERSION_HEADER)
                    .setBodyText(writer.getXml(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering $metadata document for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        ChunkedActionRenderResult renderResult = super.renderStart(requestContext, result, outputStream);
        renderResult.setContentType(XML);
        renderResult.addHeader("OData-Version", ODATA_VERSION_HEADER);

        return renderResult;
    }
}
