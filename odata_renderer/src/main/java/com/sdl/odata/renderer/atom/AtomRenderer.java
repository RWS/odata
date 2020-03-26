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
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractAtomRenderer;
import com.sdl.odata.renderer.atom.writer.AtomWriter;
import com.sdl.odata.renderer.atom.writer.ODataV4AtomNSConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

import static com.sdl.odata.api.parser.ODataUriUtil.isActionCallUri;
import static com.sdl.odata.api.processor.query.QueryResult.ResultType.COLLECTION;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.ODataRequestContextUtil.isWriteOperation;

/**
 * Renderer which renders either an OData Atom XML feed or entry.
 * <p>
 * This renderer can generate an XML response body with either an &lt;atom:feed&gt; root element (to be used when the
 * result of a query consists of a collection of entities) or a &lt;atom:entry&gt; root element (to be used when the
 * result of a query consists of a single entity).
 * <p>
 * Reference:
 * http://docs.oasis-open.org/odata/odata-atom-format/v4.0/cs02/odata-atom-format-v4.0-cs02.html#_Toc372792738
 * OData Atom Format Version 4.0 specification
 */
@Component
public class AtomRenderer extends AbstractAtomRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(AtomRenderer.class);

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {

        // This renderer only handles entity queries
        if (!isEntityQuery(requestContext.getUri(), requestContext.getEntityDataModel())) {
            return DEFAULT_SCORE;
        }
        int returnScore = super.score(requestContext, data);
        LOG.debug("Renderer score is {}", returnScore);

        return returnScore;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult result, ODataResponse.Builder responseBuilder)
            throws ODataException {

        LOG.debug("Start rendering entity(es) for request: {} with result {}", requestContext, result);

        AtomWriter atomWriter = initAtomWriter(requestContext);

        atomWriter.startDocument();
        if (result.getType() == COLLECTION) {
            atomWriter.writeFeed((List<?>) result.getData(), buildContextURL(requestContext, result.getData()),
                    result.getMeta());
        } else {
            atomWriter.writeEntry(result.getData(), buildContextURL(requestContext, result.getData()));
        }
        atomWriter.endDocument();
        String renderedData = atomWriter.getXml();

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
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        LOG.debug("Start rendering response start content including OData specification metadata " +
                "for request: {} with result {}", requestContext, result);

        AtomWriter atomWriter = initAtomWriter(requestContext);
        atomWriter.startDocument(outputStream);

        if (result.getType() == COLLECTION) {
            atomWriter.writeStartFeed(buildContextURL(requestContext, result.getData()), result.getMeta());
        }
        ChunkedActionRenderResult renderResult = new ChunkedActionRenderResult(outputStream, atomWriter);
        renderResult.setContentType(ATOM_XML);
        renderResult.addHeader("OData-Version", ODATA_VERSION_HEADER);

        return renderResult;
    }

    @Override
    public ChunkedActionRenderResult renderBody(
            ODataRequestContext requestContext, QueryResult result, ChunkedActionRenderResult previousResult)
            throws ODataException {
        AtomWriter atomWriter = (AtomWriter) previousResult.getWriter();
        if (result.getType() == COLLECTION) {
            atomWriter.writeBodyFeed((List<?>) result.getData());
        } else {
            atomWriter.writeEntry(result.getData(), buildContextURL(requestContext, result.getData()));
        }

        return previousResult;
    }

    @Override
    public void renderEnd(ODataRequestContext requestContext, QueryResult result,
                          ChunkedActionRenderResult previousResult) throws ODataException {
        AtomWriter atomWriter = (AtomWriter) previousResult.getWriter();
        if (result.getType() == COLLECTION) {
            atomWriter.writeEndFeed();
        }
        atomWriter.endDocument(false);
    }

    protected AtomWriter initAtomWriter(ODataRequestContext requestContext) {
        return new AtomWriter(ZonedDateTime.now(), requestContext.getUri(),
                requestContext.getEntityDataModel(), new ODataV4AtomNSConfigurationProvider(),
                isWriteOperation(requestContext), isActionCallUri(requestContext.getUri()));
    }
}
