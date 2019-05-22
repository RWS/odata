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

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.link.ODataLink;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.processor.ProcessorConfiguration;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;

/**
 * Delete Method Handler is specific to 'DELETE' operation.
 *
 */
public class DeleteMethodHandler extends WriteMethodHandler {
    private static Logger log = LoggerFactory.getLogger(DeleteMethodHandler.class);

    public DeleteMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory, ProcessorConfiguration configuration) {
        super(requestContext, dataSourceFactory, configuration);
    }

    /**
     * This method delete entity.
     *
     * @param entity is null always. If it is not null then returns BAD_REQUEST status
     * @return status of request
     * @throws com.sdl.odata.api.processor.datasource.ODataDataSourceException in case of any error
     */
    @Override
    public ProcessorResult handleWrite(Object entity) throws ODataException {
        if (ODataUriUtil.isRefPathUri(getoDataUri())) {
            return processLink((ODataLink) entity);
        } else {
            if (entity != null) {
                throw new ODataBadRequestException("The body of a DELETE request must be empty.");
            }

            return processEntity();
        }
    }

    /**
     * This method finds correct data source based on target type and executes delete operation on data source.
     *
     * @return status of the action
     * @throws com.sdl.odata.api.processor.datasource.ODataDataSourceException in case of any errors
     */
    private ProcessorResult processEntity() throws ODataException {
        TargetType targetType = getTargetType();
        if (!targetType.isCollection()) {
            Option<String> singletonName = ODataUriUtil.getSingletonName(getoDataUri());
            if (singletonName.isDefined()) {
                throw new ODataBadRequestException("The URI refers to the singleton '" + singletonName.get() +
                        "'. Singletons cannot be deleted.");
            }

            Type type = getEntityDataModel().getType(targetType.typeName());
            DataSource dataSource = getDataSource(type.getFullyQualifiedName());

            log.debug("Data source found for entity type '{}'", type.getFullyQualifiedName());
            dataSource.delete(getoDataUri(), getEntityDataModel());
            return new ProcessorResult(NO_CONTENT);
        } else {
            throw new ODataBadRequestException("The URI for a DELETE request should refer to the single entity " +
                    "to be deleted, not to a collection of entities.");
        }
    }

    private ProcessorResult processLink(ODataLink link) throws ODataException {
        if (!link.fromNavigationProperty().isCollection() && !link.fromNavigationProperty().isNullable()) {
            throw new ODataBadRequestException("The link cannot be deleted, because the navigation property is " +
                    "not nullable: " + link.fromNavigationProperty() + " in the type: " + link.fromEntityType());
        }

        EntityType entityType = EntityDataModelUtil.getAndCheckEntityType(getEntityDataModel(),
                getTargetType().typeName());
        DataSource dataSource = getDataSource(entityType.getFullyQualifiedName());

        log.debug("Deleting link: {}", link);
        dataSource.deleteLink(getoDataUri(), link, getEntityDataModel());

        return new ProcessorResult(NO_CONTENT);
    }
}
