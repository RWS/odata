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
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractRenderer;
import com.sdl.odata.renderer.json.writer.JsonErrorResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.sdl.odata.api.processor.query.QueryResult.ResultType.EXCEPTION;
import static com.sdl.odata.api.processor.query.QueryResult.ResultType.NOTHING;
import static com.sdl.odata.api.service.HeaderNames.CONTENT_LANGUAGE;
import static com.sdl.odata.api.service.MediaType.JSON;
import static java.lang.Math.max;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;

/**
 * The Json Error Response Renderer
 * <p>
 * Renderer an error in response.
 * Reference: OData JSON Format Version 4.0. Paragraph 11
 */
@Component
public class JsonErrorResponseRenderer extends AbstractRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(JsonErrorResponseRenderer.class);


    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {

        if (data == null || data.getType() == NOTHING || data.getType() != EXCEPTION) {
            return DEFAULT_SCORE;
        }

        // Try the types that should be allowed according to the OData specification
        // See: OData JSON Format Version 4.0, chapter 19: Error Response
        List<MediaType> accept = requestContext.getRequest().getAccept();
        int jsonAcceptScore = scoreByMediaType(accept, JSON);
        int contentTypeScore = scoreByContentType(requestContext, JSON);
        int resultScore = max(jsonAcceptScore, contentTypeScore);

        return resultScore > 0 ? (resultScore + ERROR_EXTRA_SCORE) : DEFAULT_SCORE;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {
        LOG.debug("Start rendering error response for request: {}", requestContext);

        JsonErrorResponseWriter writer = new JsonErrorResponseWriter();

        try {
            responseBuilder.setContentType(JSON)
                    .setHeader(CONTENT_LANGUAGE, ENGLISH.getLanguage())
                    .setHeader("OData-Version", ODATA_VERSION_HEADER)
                    .setBodyText(writer.getJsonError((ODataException) data.getData()), UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering error response for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        ChunkedActionRenderResult renderResult = super.renderStart(requestContext, result, outputStream);
        renderResult.addHeader(CONTENT_LANGUAGE, ENGLISH.getLanguage());
        renderResult.addHeader("OData-Version", ODATA_VERSION_HEADER);

        return renderResult;
    }
}
