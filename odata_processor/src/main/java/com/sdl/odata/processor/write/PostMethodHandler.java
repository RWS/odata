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
package com.sdl.odata.processor.write;

import java.util.Map;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.link.ODataLink;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.processor.ProcessorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sdl.odata.api.service.ODataResponse.Status.CREATED;
import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;

/**
 * Post Method Handler is specific to 'POST' operation.
 */
public class PostMethodHandler extends WriteMethodHandler {
    private static Logger log = LoggerFactory.getLogger(PostMethodHandler.class);

    public PostMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory,
                             ProcessorConfiguration configuration) {
        super(requestContext, dataSourceFactory, configuration);
    }

    @Override
    public ProcessorResult handleWrite(Object entity) throws ODataException {
        if (entity instanceof ODataLink) {
            return processLink((ODataLink) entity);
        } else {
            if (entity == null) {
                throw new ODataBadRequestException("The body of a POST request must contain a valid entity.");
            }

            return processEntity(entity);
        }
    }

    private ProcessorResult processEntity(Object entity) throws ODataException {
        TargetType targetType = getTargetType();
        if (targetType.isCollection()) {
            Type type = getEntityDataModel().getType(targetType.typeName());
            if (!MetaType.ENTITY.equals(type.getMetaType())) {
                throw new ODataBadRequestException("The body of a POST request must contain a valid entity.");
            }
            validateProperties(entity, getEntityDataModel());

            DataSource dataSource = getDataSource(type.getFullyQualifiedName());
            log.debug("Data source found for type '{}'", type.getFullyQualifiedName());

            validateTargetType(entity);
            Object createdEntity = dataSource.create(getoDataUri(), entity, getEntityDataModel());
            // Location header needs to be determined after due to ID generation
            Map<String, String> headers = getResponseHeaders(createdEntity);
            if (isMinimalReturnPreferred()) {
                return new ProcessorResult(NO_CONTENT, headers);
            }
            return new ProcessorResult(CREATED, QueryResult.from(createdEntity), headers);
        } else {
            throw new ODataBadRequestException("The URI for a POST request should refer to a collection in which " +
                    "to create the entity, not to a single entity.");
        }
    }

    private ProcessorResult processLink(ODataLink link) throws ODataException {
        if (!link.getFromNavigationProperty().isCollection()) {
            throw new ODataBadRequestException("For a POST request to store an entity link, " +
                    "the referenced navigation " +
                    "property must be a collection navigation property. To set a link to a single value navigation " +
                    "property, use a PUT request instead. " +
                    "The navigation property: " + link.getFromNavigationProperty().getName() +
                    " in type: " + link.getFromEntityType().getFullyQualifiedName() + " is a single value.");
        }

        DataSource dataSource = getDataSource(link.fromEntityType().getFullyQualifiedName());
        dataSource.createLink(getoDataUri(), link, getEntityDataModel());

        return new ProcessorResult(NO_CONTENT);
    }
}
