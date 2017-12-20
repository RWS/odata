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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.*;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataEntityNotFoundException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.SelectByKeyOperation;
import com.sdl.odata.api.processor.query.SelectOperation;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.client.BasicODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.marshall.JsonEntityMarshaller;
import com.sdl.odata.processor.parser.ProcessorODataJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.odata.api.parser.ODataUriUtil.getEntityKeyMap;
import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;

/**
 * Patch Method Handler is specific to 'PATCH' operation.
 */
public class PatchMethodHandler extends WriteMethodHandler {
    private static Logger log = LoggerFactory.getLogger(PatchMethodHandler.class);
    private final ObjectMapper objectMapper;
    private final ProcessorODataJsonParser parser;

    public PatchMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory,
                              ObjectMapper objectMapper, ODataParser uriParser) {
        super(requestContext, dataSourceFactory);
        this.objectMapper = objectMapper;
        this.parser = new ProcessorODataJsonParser(requestContext, uriParser);
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
            try {
                final String bodyText = this.getRequest().getBodyText("UTF-8");
                Map<String, Object> bodyFromJson = convertToMap(bodyText);
                final QueryOperationStrategy strategy = this.getDataSourceFactory()
                        .getStrategy(this.getODataRequestContext(),
                                new SelectByKeyOperation(new SelectOperation(getEntitySetNameFromUri(),
                                        true), getEntityKeyMap(getoDataUri(), getEntityDataModel())),
                                targetType);
                final QueryResult queryResult = strategy.execute();
                Object data = queryResult.getData();
                Map<String, Object> bodyFromDB = convertObjectToMap(data);
                mergeJsonToDB(bodyFromJson, bodyFromDB);
                entity = toOdataEntity(bodyFromDB);
            } catch (IOException e) {
                throw new ODataSystemException(e);
            }


            validateProperties(entity, getEntityDataModel());

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

    private String getEntitySetNameFromUri() {
        return ((EntitySetPath)((ResourcePathUri)
                this.getODataRequestContext().getUri().relativeUri()).resourcePath()).entitySetName();
    }

    private Object toOdataEntity(Map<String, Object> bodyFromDB) throws ODataException {
        try {
            return parser.processEntity(objectMapper.writeValueAsString(bodyFromDB));
        } catch (IOException e) {
            throw new ODataSystemException(e);
        }
    }

    private void mergeJsonToDB(Map<String, Object> bodyFromJson, Map<String, Object> bodyFromDB) {
        for (Map.Entry<String, Object> bodyEntry : bodyFromJson.entrySet()) {
            bodyFromDB.put(bodyEntry.getKey(), bodyEntry.getValue());
        }
    }

    private Map<String, Object> convertObjectToMap(Object data) throws ODataException {
        try {
            JsonEntityMarshaller marshaller = new JsonEntityMarshaller(getEntityDataModel(),
                    getoDataUri().serviceRoot());
            return convertToMap(marshaller.marshallEntity(data,
                    new BasicODataClientQuery(new BasicODataClientQuery.Builder().withEntityType(data.getClass()))));
        } catch (ODataClientException e) {
            throw new ODataSystemException(e);
        }
    }

    private Map<String, Object> convertToMap(String bodyText) throws ODataException {
        try {
            return objectMapper.readValue(bodyText, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            throw new ODataBadRequestException(e.getMessage());
        }
    }
}
