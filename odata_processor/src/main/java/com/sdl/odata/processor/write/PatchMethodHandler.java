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
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;

/**
 * Patch Method Handler is specific to 'PATCH' operation.
 */
public class PatchMethodHandler extends WriteMethodHandler {
    private static Logger log = LoggerFactory.getLogger(PatchMethodHandler.class);

    public PatchMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory) {
        super(requestContext, dataSourceFactory);
    }

    @Override
    public ProcessorResult handleWrite(Object entity) throws ODataException {
        if (ODataUriUtil.isRefPathUri(getoDataUri())) {
            throw new ODataBadRequestException("The URI of a PATCH request must not be an entity reference URI.");
        }

        if (entity == null) {
            throw new ODataBadRequestException("The body of a PATCH request must contain a valid entity.");
        }

        return processRequest(entity);
    }

    private ProcessorResult processRequest(Object entity) throws ODataException {

        TargetType targetType = getTargetType();
        if (!targetType.isCollection()) {
            Type type = getEntityDataModel().getType(targetType.typeName());
            if (!MetaType.ENTITY.equals(type.getMetaType())) {
                throw new ODataBadRequestException("The body of a PATCH request must contain a valid entity.");
            }
            //Patch method supports partial updates, therefore all properties are nullable
            //validateProperties(entity, getEntityDataModel());

            DataSource dataSource = getDataSource(type.getFullyQualifiedName());
            log.debug("Data source found for type '{}'", type.getFullyQualifiedName());

            // Get the location header before trying to create the entity
            Map<String, String> headers = getResponseHeaders(entity);

            validateTargetType(entity);
            validateKeys(entity, (EntityType) type);
            Object updatedEntity = dataSource.update(getoDataUri(), entity, getEntityDataModel());
            if (isMinimalReturnPreferred()) {
                return new ProcessorResult(NO_CONTENT, headers);
            }
            return new ProcessorResult(OK, QueryResult.from(updatedEntity), headers);
        } else {
            throw new ODataBadRequestException("The URI for a PATCH request should refer to the single entity " +
                    "to be updated, not to a collection of entities.");
        }
    }
}
