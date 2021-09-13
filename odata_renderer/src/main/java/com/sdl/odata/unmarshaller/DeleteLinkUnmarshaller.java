/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
import com.sdl.odata.api.parser.IdOption;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.link.ODataLink;
import com.sdl.odata.api.service.ODataRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Option;

import java.util.Collections;

import static com.sdl.odata.renderer.AbstractRenderer.DEFAULT_SCORE;
import static com.sdl.odata.renderer.AbstractRenderer.DELETE_LINK_SCORE;

/**
 * Unmarshaller for DELETE requests where the URI is reference (it ends in ".../$ref"). This unmarshaller returns an
 * ODataLink object containing information about the link to be deleted. Note: All necessary information is extracted
 * from the URI, the body is empty for these kinds of requests.
 * <p>
 * See OData v4 specification part 1, paragraph 11.4.6 Modifying Relationships between Entities
 */
@Component
public class DeleteLinkUnmarshaller extends AbstractUnmarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteLinkUnmarshaller.class);

    @Autowired
    private ODataParser uriParser;

    @Override
    public int score(ODataRequestContext requestContext) {
        // Method must be DELETE and the URI must be an entity reference URI
        if (isDeleteMethod(requestContext.getRequest().getMethod()) &&
                ODataUriUtil.isRefPathUri(requestContext.getUri())) {
            LOG.debug("Match for DeleteLinkUnmarshaller: {}", requestContext.getRequest());
            return DELETE_LINK_SCORE;
        }

        return DEFAULT_SCORE;
    }

    @Override
    public Object unmarshall(ODataRequestContext requestContext) throws ODataException {
        EntityDataModel entityDataModel = requestContext.getEntityDataModel();

        Option<FromEntity> fromEntityOption = ODataUriUtil.getFromEntity(requestContext.getUri(), entityDataModel);
        if (fromEntityOption.isEmpty()) {
            throw new ODataBadRequestException("The URI of a DELETE request to delete a link must refer to a " +
                    "navigation property of a specific entity. " +
                    "This information could not be determined from the URI: " +
                    requestContext.getRequest().getUri());
        }

        FromEntity fromEntity = fromEntityOption.get();

        Option<IdOption> idOption = ODataUriUtil.getIdOption(requestContext.getUri());

        if (fromEntity.navigationProperty().isCollection()) {
            // There must be an '$id' option for a collection navigation property
            // See OData specification part 1, paragraph 11.4.6.2 Remove a Reference to an Entity
            if (idOption.isEmpty()) {
                throw new ODataBadRequestException("The URI of a DELETE request to delete a link for a collection " +
                        "navigation property must have an '$id' option that identifies the entity to remove from the " +
                        "collection.");
            }

            ODataUri idUri = uriParser.parseUri(idOption.get().value(), entityDataModel);

            return new ODataLink(fromEntity.entityType(), fromEntity.navigationProperty(), fromEntity.entityKey(),
                    ODataUriUtil.getEntityKeyMap(idUri, entityDataModel));
        } else {
            // There must not be an '$id' option for a single value navigation property
            // See OData specification part 1, paragraph 11.4.6.2 Remove a Reference to an Entity
            if (idOption.isDefined()) {
                throw new ODataBadRequestException("The URI of a DELETE request to delete a link for a single value " +
                        "navigation property must not have an '$id' option.");
            }

            return new ODataLink(fromEntity.entityType(), fromEntity.navigationProperty(), fromEntity.entityKey(),
                    ODataUriUtil.asScalaMap(Collections.<String, Object>emptyMap()));
        }
    }
}
