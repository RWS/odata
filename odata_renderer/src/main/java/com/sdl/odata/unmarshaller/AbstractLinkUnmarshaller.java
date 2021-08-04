/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.FromEntity;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUriParseException;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.link.ODataLink;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import scala.Option;

import static com.sdl.odata.renderer.AbstractRenderer.DEFAULT_SCORE;

/**
 * The Abstract Link Unmarshaller.
 */
public abstract class AbstractLinkUnmarshaller extends AbstractUnmarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLinkUnmarshaller.class);

    @Autowired
    private ODataParser uriParser;

    protected abstract MediaType[] supportedMediaTypes();

    @Override
    public int score(ODataRequestContext requestContext) {
        // NOTE: Only POST and PUT are allowed for creating and updating links
        // See OData specification part 1, paragraph 11.4.6
        ODataRequest.Method method = requestContext.getRequest().getMethod();
        if ((isPostMethod(method) || isPutMethod(method)) &&
                ODataUriUtil.isRefPathUri(requestContext.getUri())) {
            MediaType contentType = requestContext.getRequest().getContentType();
            int score = super.score(contentType, supportedMediaTypes());
            LOG.debug("Match for {}: {} with score: {}", this.getClass().getSimpleName(),
                    requestContext.getRequest(), score);
            return score;
        }

        return DEFAULT_SCORE;
    }

    protected abstract String getToEntityId(ODataRequestContext requestContext) throws ODataUnmarshallingException;

    @Override
    public Object unmarshall(ODataRequestContext requestContext) throws ODataException {
        EntityDataModel entityDataModel = requestContext.getEntityDataModel();

        Option<FromEntity> fromEntityOption = ODataUriUtil.getFromEntity(requestContext.getUri(), entityDataModel);
        if (fromEntityOption.isEmpty()) {
            throw new ODataBadRequestException("The URI of a " + requestContext.getRequest().getMethod() +
                    " request to create a link must refer to a navigation property of a specific entity." +
                    " This information could not be determined from the URI: " + requestContext.getRequest().getUri());
        }

        FromEntity fromEntity = fromEntityOption.get();
        LOG.debug("fromEntity={}", fromEntity);

        String toEntityId = getToEntityId(requestContext);
        LOG.debug("toEntityId={}");

        scala.collection.immutable.Map<String, Object> toEntityKey;
        try {
            // Try to parse the id as an absolute URI
            toEntityKey = ODataUriUtil.getEntityKeyMap(uriParser.parseUri(toEntityId, entityDataModel),
                    entityDataModel);
        } catch (ODataUriParseException e1) {
            try {
                // Try to parse the id as a relative URI (resource path)
                toEntityKey = ODataUriUtil.getEntityKeyMap(
                        uriParser.parseResourcePath(toEntityId, entityDataModel), entityDataModel);
            } catch (ODataUriParseException e2) {
                throw new ODataBadRequestException("The id to the entity that is being linked to could not be " +
                        "determined from the request data: " + toEntityId);
            }
        }

        return new ODataLink(fromEntity.entityType(), fromEntity.navigationProperty(), fromEntity.entityKey(),
                toEntityKey);
    }
}

