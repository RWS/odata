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
package com.sdl.odata.renderer.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.parser.ODataBatchException;
import com.sdl.odata.api.parser.ODataBatchRendererException;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractRenderer;
import com.sdl.odata.renderer.atom.AtomRenderer;
import com.sdl.odata.renderer.json.JsonRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.api.service.HeaderNames.CONTENT_LENGTH;
import static com.sdl.odata.api.service.HeaderNames.CONTENT_TYPE;
import static com.sdl.odata.api.service.HeaderNames.LOCATION;
import static com.sdl.odata.api.service.MediaType.MULTIPART;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static java.lang.Math.max;

/**
 * OData Batch Request Renderer.
 * The main class for creating batch response. Includes the batch error processing.
 */
@Component
public class ODataBatchRequestRenderer extends AbstractRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(ODataBatchRequestRenderer.class);

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String COLON = ": ";
    private static final String CONTENT_TYPE_HTTP = CONTENT_TYPE + COLON + MediaType.HTTP.getSubType();
    private static final String CT_ENCODING_BINARY = "Content-Transfer-Encoding: binary";

    private static final String CONTENT_ID = "Content-ID";
    private static final String NEW_LINE = System.lineSeparator();
    private static final String FORMAT = "format";
    private static final String BODY = "body";

    private String contentLength;

    /**
     * Batch score mechanism exists not only for simple rendering, but also
     * for computing batch error scores.
     *
     * @param requestContext The request context.
     * @param data           The data to render.
     * @return batch score
     */
    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {
        if (data.getType() == QueryResult.ResultType.NOTHING || data.getType() != QueryResult.ResultType.EXCEPTION) {
            return DEFAULT_SCORE;
        }

        if (data.getData() instanceof ODataBatchException) {
            return MAXIMUM_FORMAT_SCORE;
        }

        List<MediaType> accept = requestContext.getRequest().getAccept();
        int batchAcceptScore = scoreByMediaType(accept, MediaType.MULTIPART);
        int contentTypeScore = scoreByContentType(requestContext, MULTIPART);
        int resultScore = max(batchAcceptScore, contentTypeScore);

        return resultScore > 0 ? (resultScore + ERROR_EXTRA_SCORE) : DEFAULT_SCORE;
    }

    @Override
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException {
        LOG.debug("Starting rendering batch request entities for request: {} with data {}", requestContext, data);
        checkNotNull(data);
        checkNotNull(data.getData());

        StringBuilder sb = new StringBuilder();

        contentLength = requestContext.getRequest().getHeader(CONTENT_LENGTH.toLowerCase());

        if (contentLength == null) {
            contentLength = requestContext.getRequest().getHeader(CONTENT_LENGTH);
        }
        // building batchId (changeSetId are already provided in result headers
        String batchId = buildBatchId(requestContext);

        // the start of batch
        sb.append(batchId).append(NEW_LINE);
        buildHTTPandBinary(sb);
        sb.append(NEW_LINE);

        int changeSetCount = 0;
        if (data.getType() == QueryResult.ResultType.COLLECTION) {
            List<ProcessorResult> results = (List<ProcessorResult>) data.getData();
            int numberOfChangeSets = getNumberOfChangeSetsInResult(results);
            for (ProcessorResult result : results) {

                Map<String, String> renderMap = buildRenderedData(result);

                boolean isGET = result.getRequestContext().getRequest().getMethod().equals(ODataRequest.Method.GET);

                // only batch can handle GET request
                if (isGET) {
                    if (!renderMap.isEmpty()) {
                        buildObjectData(sb, result, renderMap);
                    } else {
                        buildException(new ODataBatchRendererException("Unable to render batch data"), sb, result);
                    }
                } else {
                    changeSetCount++;
                    String changeSetId = result.getHeaders().get("changeSetId");

                    if (changeSetCount == 1) {
                        sb.append(batchId).append(NEW_LINE);
                        sb.append(CONTENT_TYPE + COLON).append("multipart/mixed;boundary=")
                                .append(changeSetId).append(NEW_LINE);
                        sb.append(NEW_LINE);
                    }

                    sb.append("--").append(changeSetId).append(NEW_LINE);
                    buildHTTPandBinary(sb);
                    if (result.getHeaders().get(CONTENT_ID) != null) {
                        sb.append(CONTENT_ID + COLON).append(result.getHeaders().get(CONTENT_ID)).append(NEW_LINE);
                    } else {
                        // not such a good implementation, but we should somehow provide the Content-ID
                        sb.append(CONTENT_ID + COLON).append(changeSetCount).append(NEW_LINE);
                    }

                    sb.append(NEW_LINE);

                    if (!renderMap.isEmpty()) {
                        buildObjectData(sb, result, renderMap);
                    } else {
                        buildException(new ODataBatchRendererException("Unable to render changeset data"), sb, result);
                    }

                    if (changeSetCount == numberOfChangeSets) {
                        sb.append("--").append(changeSetId).append("--").append(NEW_LINE);
                    }
                }
            }

        } else if (data.getType() == QueryResult.ResultType.EXCEPTION) {
            buildException((ODataException) data.getData(), sb, null);
        }

        // the end of batch
        sb.append(batchId).append("--").append(NEW_LINE);

        try {
            responseBuilder
                    .setStatus(ODataResponse.Status.OK)
                    .setContentType(MediaType.MULTIPART)
                    .setHeader("OData-Version", ODATA_VERSION_HEADER)
                    .setBodyText(sb.toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("Finishing rendering batch request entities for request: {}", requestContext);
    }

    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        ChunkedActionRenderResult renderResult = super.renderStart(requestContext, result, outputStream);
        renderResult.setContentType(MediaType.MULTIPART);
        renderResult.addHeader("OData-Version", ODATA_VERSION_HEADER);

        return renderResult;
    }

    private void buildHTTPandBinary(StringBuilder sb) {
        sb.append(CONTENT_TYPE_HTTP).append(NEW_LINE).append(CT_ENCODING_BINARY).append(NEW_LINE);
    }

    private void buildObjectData(StringBuilder sb, ProcessorResult result, Map<String, String> renderMap) {
        String location = result.getHeaders().get(LOCATION);

        sb.append(HTTP_VERSION + " ").append(result.getStatus().toString().replace("_", " ")).append(NEW_LINE);
        sb.append(CONTENT_TYPE).append(COLON).append(renderMap.get(FORMAT)).append(NEW_LINE);
        if (location != null) {
            sb.append(LOCATION).append(COLON).append(location).append(NEW_LINE);
        }
        sb.append(CONTENT_LENGTH).append(COLON).append(contentLength).append(NEW_LINE);
        sb.append(NEW_LINE);

        if (renderMap.get(BODY) != null) {
            sb.append(renderMap.get(BODY)).append(NEW_LINE).append(NEW_LINE);
        } else {
            // DELETE shouldn't contain message inside the batch body
            sb.append(NEW_LINE);
        }
    }

    private void buildException(ODataException ex, StringBuilder sb, ProcessorResult result) {
        LOG.debug("{} was found. Start to create an error batch request");
        if (result != null) {
            sb.append(HTTP_VERSION + " ").append(result.getStatus().toString().replace("_", " ")).append(NEW_LINE);
        } else if (ex.getCode() != null) {
            if (ex.getCode().toString().equals("ENTITY_NOT_FOUND_ERROR")) {
                sb.append(HTTP_VERSION + " ")
                        .append(ODataResponse.Status.NOT_FOUND.toString().replace("_", " ")).append(NEW_LINE);

            } else {
                sb.append(HTTP_VERSION + " ")
                        .append(ODataResponse.Status.BAD_REQUEST.toString().replace("_", " ")).append(NEW_LINE);
            }
        }
        sb.append(CONTENT_TYPE_HTTP).append(NEW_LINE);
        sb.append(CONTENT_LENGTH).append(COLON).append(contentLength).append(NEW_LINE).append(NEW_LINE);
        sb.append(ex.getMessage()).append(NEW_LINE);
    }

    private Map<String, String> buildRenderedData(ProcessorResult result) throws ODataException {
        LOG.debug("Start to render the data");
        Map<String, String> resultMap = new HashMap<>();

        if (result.getStatus().getCode() < ODataResponse.Status.NO_CONTENT.getCode()) {
            String contentType = result.getHeaders().get(CONTENT_TYPE);
            if (!result.getHeaders().containsKey(CONTENT_TYPE) ||
                    contentType.startsWith(MediaType.ATOM_XML.toString())) {
                // render XML
                resultMap.put(FORMAT, MediaType.ATOM_XML.toString());
                resultMap.put(BODY, getRenderedXML(result));

            } else if (contentType.startsWith(MediaType.JSON.toString())) {
                // render JSON
                resultMap.put(FORMAT, MediaType.JSON.toString());
                resultMap.put(BODY, getRenderedJSON(result));
            }
        } else {
            resultMap.put(FORMAT, MediaType.HTTP.getSubType());
            resultMap.put(BODY, (String) result.getData());
        }

        return resultMap;
    }

    private String getRenderedJSON(ProcessorResult result) throws ODataException {
        LOG.debug("JSON found as the content type. JSON Renderer will be used to render the result data");
        JsonRenderer renderer = new JsonRenderer();

        ODataResponse.Builder builder = new ODataResponse.Builder()
                .setStatus(result.getStatus());
        renderer.render(result.getRequestContext(), result.getQueryResult(), builder);

        try {
            // pretty print
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(builder.build().getBodyText(StandardCharsets.UTF_8.name()),
                    Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (IOException ex) {
            throw new ODataBatchRendererException("Unable to pretty print following json data");
        }
    }

    private String getRenderedXML(ProcessorResult result) throws ODataException {
        LOG.debug("Content Type not specified. Atom Renderer will be used to render the result data");
        AbstractRenderer atomRenderer = new AtomRenderer();
        ODataResponse.Builder builder = new ODataResponse.Builder()
                .setStatus(result.getStatus());

        atomRenderer.render(result.getRequestContext(), result.getQueryResult(), builder);

        try {
            return builder.build().getBodyText(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataRenderException("Unsupported encoding", e.getMessage());
        }
    }

    private int getNumberOfChangeSetsInResult(List<ProcessorResult> result) {
        int changeSets = 0;

        for (ProcessorResult processorResult : result) {
            if (!processorResult.getRequestContext().getRequest().getMethod().equals(ODataRequest.Method.GET)) {
                changeSets++;
            }
        }

        return changeSets;
    }

    private String buildBatchId(ODataRequestContext requestContext) throws ODataBatchRendererException {
        StringBuilder sb = new StringBuilder();
        String contentType = requestContext.getRequest().getHeaders().get(CONTENT_TYPE.toLowerCase());

        if (contentType == null) {
            contentType = requestContext.getRequest().getHeader(CONTENT_TYPE);
        }

        if (isNullOrEmpty(contentType)) {
            throw new ODataBatchRendererException("Request Context Content-Type is missing");
        }

        sb.append("--");
        // get the batch UUID
        sb.append(contentType.substring(contentType.indexOf("=") + 1));
        // substring existing batch id after "batch_" charset
        return sb.toString();
    }
}
