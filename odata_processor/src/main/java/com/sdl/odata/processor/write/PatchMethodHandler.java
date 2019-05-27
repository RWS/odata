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
import com.sdl.odata.api.edm.model.*;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.model.ReferencableEntity;
import com.sdl.odata.processor.ProcessorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getPropertyValue;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * Patch Method Handler is specific to 'PATCH' operation.
 */
public class PatchMethodHandler extends WriteMethodHandler {
    private static Logger log = LoggerFactory.getLogger(PatchMethodHandler.class);

    public PatchMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory, ProcessorConfiguration configuration) {
        super(requestContext, dataSourceFactory, configuration);
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
            validateProperties(entity, getEntityDataModel());

            DataSource dataSource = getDataSource(type.getFullyQualifiedName());
            log.debug("Data source found for type '{}'", type.getFullyQualifiedName());

            validateTargetType(entity);
            validateKeys(entity, (EntityType) type);
            Object updatedEntity = dataSource.update(getoDataUri(), entity, getEntityDataModel(), true);
            // Location header needs to be determined after due to ID generation
            Map<String, String> headers = getResponseHeaders(updatedEntity);
            if (isMinimalReturnPreferred()) {
                return new ProcessorResult(NO_CONTENT, headers);
            }
            return new ProcessorResult(OK, QueryResult.from(updatedEntity), headers);
        } else {
            throw new ODataBadRequestException("The URI for a PATCH request should refer to the single entity " +
                    "to be updated, not to a collection of entities.");
        }
    }

    /**
     * Checks if no inline properties are present (11.4.3 Update an Entity)
     *
     * @param entity The entity to check.
     * @throws ODataBadRequestException If any of the inline (non-reference) properties of the entity are present.
     */
    protected void validateProperties(final Object entity, final EntityDataModel edm)
            throws ODataException {
        final Type type = edm.getType(entity.getClass());
        // No validation needed if it is not a structured type
        if (!(type instanceof StructuredType)) {
            return;
        }

        visitProperties(edm, (StructuredType) type, property -> {
            Object value = getPropertyValue(property, entity);
            if (property instanceof NavigationProperty) {
                boolean isReference = true;
                if(value != null) {
                    isReference = property.isCollection() ? ((Collection) value).stream().allMatch(e -> isReference(e)) : isReference(
                            value);
                }
                if(!isReference)
                {
                    throw new ODataBadRequestException("The property '" + property.getName() +
                                                       "' is required to be can either be null or reference");
                }
            }
        });
    }

    public boolean isReference(Object o)
    {
        if(o instanceof ReferencableEntity)
            return ((ReferencableEntity)o).isReference();
        else
            return true;
    }
}
