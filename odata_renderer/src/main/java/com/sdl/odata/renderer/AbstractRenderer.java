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
package com.sdl.odata.renderer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.FormatOption;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.api.renderer.ODataRenderer;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.sdl.odata.ODataRendererUtils.buildContextUrlFromOperationCall;
import static com.sdl.odata.api.parser.ODataUriUtil.getContextUrl;
import static com.sdl.odata.api.parser.ODataUriUtil.getContextUrlWriteOperation;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.api.service.ODataRequest.Method;
import static com.sdl.odata.api.service.ODataRequestContextUtil.isWriteOperation;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static java.text.MessageFormat.format;

/**
 * Abstract superclass with common functionality for renderers.
 */
public abstract class AbstractRenderer implements ODataRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRenderer.class);
    /**
     * Wildcard Match Score.
     */
    public static final int WILDCARD_MATCH_SCORE = 30;
    /**
     * Default Score.
     */
    public static final int DEFAULT_SCORE = 0;
    /**
     * Content Type Header.
     */
    public static final int CONTENT_TYPE_HEADER = 35;
    /**
     * Maximum Format Score.
     */
    public static final int MAXIMUM_FORMAT_SCORE = 130;
    /**
     * Maximum Header Score.
     */
    public static final int MAXIMUM_HEADER_SCORE = 100;
    /**
     * Delete Link Score.
     */
    public static final int DELETE_LINK_SCORE = 100;
    /**
     * Error Extra Score.
     */
    public static final int ERROR_EXTRA_SCORE = 100;
    /**
     * Priority Score.
     */
    public static final int PRIORITY_SCORE = 1;
    /**
     * OData Protocol Version.
     */
    protected static final String ODATA_VERSION_HEADER = "4.0";

    protected boolean isGetRequest(ODataRequest request) {
        return request.getMethod() == Method.GET;
    }

    /**
     * Check if the parsed OData URI is a query and it results in an entity or a collection of entities.
     *
     * @param uri             The parsed OData URI.
     * @param entityDataModel The Entity Data Model.
     * @return {@code true} if it is about an entity query.
     */
    protected boolean isEntityQuery(ODataUri uri, EntityDataModel entityDataModel) {
        return getTargetType(uri, entityDataModel).map(t -> t.getMetaType() == MetaType.ENTITY).orElse(false);
    }

    /**
     * Check if the parsed OData URI is a query and it results in something that is not an entity or a collection of
     * entities; for example a primitive value, complex object, enum value or a collection of any of those.
     *
     * @param uri             The parsed OData URI.
     * @param entityDataModel The entity data model
     * @return {@code true} if it is about an entity query.
     */
    protected boolean isNonEntityQuery(ODataUri uri, EntityDataModel entityDataModel) {
        return getTargetType(uri, entityDataModel).map(t -> t.getMetaType() != MetaType.ENTITY).orElse(false);
    }

    private Optional<Type> getTargetType(ODataUri uri, EntityDataModel entityDataModel) {
        final Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(uri, entityDataModel);
        if (!targetTypeOption.isEmpty()) {
            TargetType targetType = targetTypeOption.get();
            LOG.debug("Target type is {} and is it collection {}", targetType.typeName(), targetType.isCollection());
            return Optional.ofNullable(entityDataModel.getType(targetType.typeName()));
        }
        return Optional.empty();
    }

    /**
     * Computes a score by checking the value of the '$format' parameter (if present) against a required media type.
     *
     * @param formatOption      The option containing the '$format' parameter.
     * @param requiredMediaType The required media type.
     * @return A score that indicates if the media type present in the '$format' parameter
     * matches the required media type.
     */
    protected int scoreByFormat(Option<FormatOption> formatOption, MediaType requiredMediaType) {
        if (!formatOption.isDefined()) {
            return DEFAULT_SCORE;
        }
        if (formatOption.get().mediaType().matches(requiredMediaType)) {
            return MAXIMUM_FORMAT_SCORE;
        }
        return DEFAULT_SCORE;
    }

    /**
     * Computes a score by examining a list of media types (typically from the 'Accept' header) against
     * a required media type.
     *
     * @param mediaTypes        The list of media types to examine.
     * @param requiredMediaType The required media type.
     * @return A score that indicates if one of the media types in the list matches the required media type.
     */
    protected int scoreByMediaType(List<MediaType> mediaTypes, MediaType requiredMediaType) {
        int score = MAXIMUM_HEADER_SCORE;
        boolean match = false;
        boolean matchWildCard = false;
        for (MediaType mediaType : mediaTypes) {
            if (mediaType.matches(requiredMediaType)) {
                if (mediaType.isWildCardMediaType()) {
                    matchWildCard = true;
                }
                match = true;
                break;
            }

            // Lower the score for each subsequent possible match
            score -= 2;
        }
        return match && !matchWildCard ? score : matchWildCard ? WILDCARD_MATCH_SCORE : DEFAULT_SCORE;
    }

    protected int scoreByContentType(ODataRequestContext oDataRequestContext, MediaType expected) {
        if (checkForContentType(oDataRequestContext, expected)) {
            return CONTENT_TYPE_HEADER;
        }
        return DEFAULT_SCORE;
    }

    private boolean checkForContentType(ODataRequestContext oDataRequestContext, MediaType expected) {
        MediaType requestContentType = oDataRequestContext.getRequest().getContentType();
        return requestContentType != null && requestContentType.matches(expected);
    }

    protected boolean isRequestedContentTypeSupported(ODataRequestContext oDataRequestContext) {
        return checkForContentType(oDataRequestContext, ATOM_XML) || checkForContentType(oDataRequestContext, JSON);
    }

    protected boolean isListOrStream(Object data) {
        return data instanceof List || data instanceof Stream;
    }

    /**
     * Build the 'Context URL' from a given OData request context.
     *
     * @param requestContext The given OData request context
     * @param data           Result data
     * @return The built 'Context URL'
     * @throws ODataRenderException If unable to build context url
     */
    protected String buildContextURL(ODataRequestContext requestContext, Object data) throws ODataRenderException {
        ODataUri oDataUri = requestContext.getUri();
        if (ODataUriUtil.isActionCallUri(oDataUri) || ODataUriUtil.isFunctionCallUri(oDataUri)) {
            return buildContextUrlFromOperationCall(oDataUri, requestContext.getEntityDataModel(),
                    isListOrStream(data));
        }

        Option<String> contextURL;
        if (isWriteOperation(requestContext)) {
            contextURL = getContextUrlWriteOperation(oDataUri);
        } else {
            contextURL = getContextUrl(oDataUri);
        }
        checkContextURL(requestContext, contextURL);
        return contextURL.get();
    }

    /**
     * Check whether the given 'Context URL' is defined.
     *
     * @param requestContext The 'Request Context' used to build the given 'Context URL'.
     * @param contextURL     The given 'Context URL'.
     * @throws ODataRenderException if the 'Context URL' is not defined
     */
    protected void checkContextURL(ODataRequestContext requestContext, Option<String> contextURL)
            throws ODataRenderException {

        if (!contextURL.isDefined()) {
            throw new ODataRenderException(
                    String.format("Not possible to create context URL for request %s", requestContext));
        }
    }

    /**
     * Default implementation. Returns empty string, the real content goes when triggering renderBody.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                                 OutputStream outputStream) throws ODataException {
        // Do nothing for default implementation
        return new ChunkedActionRenderResult(outputStream);
    }

    /**
     * Default implementation. Return the whole rendered data within this method call.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public ChunkedActionRenderResult renderBody(ODataRequestContext requestContext, QueryResult result,
                                                ChunkedActionRenderResult previousResult) throws ODataException {
        LOG.debug("Start rendering property for request body: {}", requestContext);
        ODataResponse.Builder responseBuilder = new ODataResponse.Builder().setStatus(OK);
        render(requestContext, result, responseBuilder);
        try {
            previousResult.getOutputStream().write(responseBuilder.build().getBody());
            return previousResult;
        } catch (java.io.IOException e) {
            throw new ODataRenderException(format("Unable to render result: {0} for request: {1}",
                    result, requestContext.getRequest()), e);
        }
    }

    /**
     * Default implementation. Return empty string as we get all rendered data by triggering renderBody.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void renderEnd(ODataRequestContext requestContext, QueryResult result,
                          ChunkedActionRenderResult previousResult) throws ODataException {
        // Do nothing for default implementation
    }
}
