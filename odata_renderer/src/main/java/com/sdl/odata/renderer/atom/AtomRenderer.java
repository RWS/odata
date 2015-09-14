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
package com.sdl.odata.renderer.atom;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractAtomRenderer;
import com.sdl.odata.renderer.atom.writer.AtomWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

import static com.sdl.odata.api.parser.ODataUriUtil.isActionCallUri;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.ODataRequestContextUtil.isWriteOperation;

/**
 * Renderer which renders either an OData Atom XML feed or entry.
 *
 * This renderer can generate an XML response body with either an &lt;atom:feed&gt; root element (to be used when the
 * result of a query consists of a collection of entities) or a &lt;atom:entry&gt; root element (to be used when the
 * result of a query consists of a single entity).
 *
 * Reference:
 * http://docs.oasis-open.org/odata/odata-atom-format/v4.0/cs02/odata-atom-format-v4.0-cs02.html#_Toc372792738
 * OData Atom Format Version 4.0 specification
 */
@Component
public final class AtomRenderer extends AbstractAtomRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(AtomRenderer.class);
    private String renderedData;

    @Override
    public int score(ODataRequestContext requestContext, Object data) {

        // This renderer only handles entity queries
        if (!isEntityQuery(requestContext.getUri(), requestContext.getEntityDataModel())) {
            return DEFAULT_SCORE;
        }
        int returnScore = super.score(requestContext, data);
        LOG.debug("Renderer score is {}", returnScore);

        return returnScore;
    }

    @Override
    public void render(ODataRequestContext requestContext, Object data, ODataResponse.Builder responseBuilder)
            throws ODataException {

        LOG.debug("Start rendering entity(es) for request: {} with data {}", requestContext, data);

        ZonedDateTime dateTime = ZonedDateTime.now();
        AtomWriter atomWriter = new AtomWriter(dateTime, requestContext.getUri(), requestContext.getEntityDataModel(),
                                    isWriteOperation(requestContext), isActionCallUri(requestContext.getUri()));

        atomWriter.startDocument();
        if (data instanceof List) {
            atomWriter.writeFeed((List<?>) data, buildContextURL(requestContext, data));
        } else {
            atomWriter.writeEntry(data, buildContextURL(requestContext, data));
        }
        atomWriter.endDocument();
        renderedData = atomWriter.getXml();

        if (responseBuilder != null) {
            try {
                responseBuilder
                        .setContentType(ATOM_XML)
                        .setHeader("OData-Version", ODATA_VERSION_HEADER)
                        .setBodyText(renderedData, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new ODataSystemException(e);
            }
        }

        LOG.debug("End rendering entity(es) for request: {}", requestContext);
    }

    @Override
    public String getRenderedData() {
        return renderedData;
    }

}
