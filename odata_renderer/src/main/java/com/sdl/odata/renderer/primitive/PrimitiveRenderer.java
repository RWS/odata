package com.sdl.odata.renderer.primitive;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.AbstractRenderer;
import com.sdl.odata.renderer.primitive.writer.PrimitiveWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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
    public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder) throws ODataException {
        LOG.debug("Start value for request: {}", requestContext);

        PrimitiveWriter primitiveWriter = new PrimitiveWriter(requestContext.getUri(),
                requestContext.getEntityDataModel());
        String response = primitiveWriter.getPropertyAsString(data.getData());

        LOG.debug("Response value is {}", response);

        try {
            responseBuilder
                    .setContentType(MediaType.TEXT)
                    .setHeader("OData-Version", ODATA_VERSION_HEADER)
                    .setBodyText(response, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }

        LOG.debug("End rendering property for request: {}", requestContext);
    }
}
