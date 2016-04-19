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
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataTargetTypeException;
import com.sdl.odata.api.processor.datasource.TransactionalDataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ChangeSetEntity;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.processor.write.util.WriteMethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.api.processor.query.QueryResult.from;

/**
 * Batch Method Handler is specific to batch operations.
 */
public class BatchMethodHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BatchMethodHandler.class);

    private final List<ChangeSetEntity> changeSetEntities;
    private final EntityDataModel entityDataModel;
    private final DataSourceFactory dataSourceFactory;
    private final ODataRequestContext requestContext;
    private final ODataRequest odataRequest;

    private final Map<String, TransactionalDataSource> dataSourceMap = new HashMap<>();

    public BatchMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory,
                              List<ChangeSetEntity> changeSetEntries) {
        this.changeSetEntities = changeSetEntries;
        this.entityDataModel = requestContext.getEntityDataModel();
        this.dataSourceFactory = dataSourceFactory;
        this.requestContext = requestContext;
        this.odataRequest = requestContext.getRequest();
    }

    /**
     * Handles transactional operations for each parsed odata request.
     *
     * @return processor results
     */
    public List<ProcessorResult> handleWrite() throws ODataException {
        LOG.info("Handling transactional operations per each odata request.");
        List<ProcessorResult> resultList = new ArrayList<>();

        try {
            try {
                for (ChangeSetEntity changeSetEntity : changeSetEntities) {
                    ODataUri requestUri = requestContext.getUri();
                    ODataRequest.Method method = requestContext.getRequest().getMethod();

                    ProcessorResult result = null;
                    if (method == ODataRequest.Method.POST) {
                        result = handlePOST(requestUri, changeSetEntity);
                    } else if (method == ODataRequest.Method.PUT || method == ODataRequest.Method.PATCH) {
                        result = handlePutAndPatch(requestUri, changeSetEntity);
                    } else if (method == ODataRequest.Method.DELETE) {
                        result = handleDelete(requestUri, changeSetEntity);
                    }
                    resultList.add(result);
                }
            } finally {
                commitTransactions();
            }
        } catch (ODataException e) {
            LOG.error("Transaction could not be processed, rolling back");
            rollbackTransactions();
        }
        return resultList;
    }

    private ProcessorResult handlePOST(ODataUri oDataUri, ChangeSetEntity changeSetEntity) throws ODataException {
        LOG.debug("Handling POST operation");
        Object entityData = changeSetEntity.getOdataEntity();
        Map<String, String> headers = buildDefaultEntityHeaders(changeSetEntity);

        validateEntityData(oDataUri, entityData);
        DataSource dataSource = getTransactionalDataSource(getRequestType(oDataUri));
        headers.putAll(WriteMethodUtil.getResponseHeaders(entityData, oDataUri, entityDataModel));
        Object createdEntity = dataSource.create(oDataUri, entityData, entityDataModel);

        if (WriteMethodUtil.isMinimalReturnPreferred(odataRequest)) {
            return new ProcessorResult(ODataResponse.Status.NO_CONTENT, headers);
        }
        return new ProcessorResult(ODataResponse.Status.CREATED, from(createdEntity), headers, requestContext);
    }

    private ProcessorResult handleDelete(ODataUri odataUri, ChangeSetEntity changeSetEntity) throws ODataException {
        LOG.debug("Handling DELETE operation");
        Map<String, String> headers = buildDefaultEntityHeaders(changeSetEntity);

        Option<String> singletonName = ODataUriUtil.getSingletonName(odataUri);
        DataSource dataSource = getTransactionalDataSource(getRequestType(odataUri));
        if (singletonName.isDefined()) {
            throw new ODataBadRequestException("The URI refers to the singleton '" + singletonName.get() +
                    "'. Singletons cannot be deleted.");
        }
        dataSource.delete(odataUri, entityDataModel);
        return new ProcessorResult(ODataResponse.Status.NO_CONTENT, null, headers, requestContext);
    }

    private ProcessorResult handlePutAndPatch(ODataUri requestUri,
                                              ChangeSetEntity changeSetEntity) throws ODataException {
        LOG.debug("Handling PUT or PATCH operation");
        Object entityData = changeSetEntity.getOdataEntity();

        Map<String, String> headers = new HashMap<>();
        headers.put("changeSetId", changeSetEntity.getChangeSetId());
        validateEntityData(requestUri, entityData);

        ODataRequest oDataRequest = requestContext.getRequest();
        TargetType targetType = WriteMethodUtil.getTargetType(oDataRequest, entityDataModel, requestUri);

        if (targetType.isCollection()) {
            throw new ODataBadRequestException("The URI for a PATCH request should refer to the single entity " +
                    "to be updated, not to a collection of entities.");
        }
        // Get the location header before trying to create the entity
        headers.putAll(WriteMethodUtil.getResponseHeaders(entityData, requestUri, entityDataModel));

        WriteMethodUtil.validateTargetType(entityData, oDataRequest, entityDataModel, requestUri);
        Type type = entityDataModel.getType(targetType.typeName());
        WriteMethodUtil.validateKeys(entityData, (EntityType) type, requestUri, entityDataModel);

        DataSource dataSource = getTransactionalDataSource(type);
        Object updatedEntity = dataSource.update(requestUri, entityData, entityDataModel);

        // add additional headers
        headers.putAll(oDataRequest.getHeaders());
        if (WriteMethodUtil.isMinimalReturnPreferred(oDataRequest)) {
            return new ProcessorResult(ODataResponse.Status.NO_CONTENT, headers);
        }
        return new ProcessorResult(ODataResponse.Status.OK, from(updatedEntity), headers, requestContext);
    }

    private Map<String, String> buildDefaultEntityHeaders(ChangeSetEntity changeSetEntity) {
        Map<String, String> headers = new HashMap<>();
        ODataRequest oDataRequest = requestContext.getRequest();
        headers.putAll(oDataRequest.getHeaders());
        headers.put("changeSetId", changeSetEntity.getChangeSetId());

        return headers;
    }

    private void commitTransactions() {
        LOG.info("Committing batch transactions");
        dataSourceMap.values().forEach(TransactionalDataSource::commit);
    }

    private void rollbackTransactions() {
        LOG.info("Rolling back batch transactions");
        dataSourceMap.values().forEach(TransactionalDataSource::rollback);
    }

    /**
     * Returns request type for the given odata uri.
     * @param oDataUri the odata uri
     * @return the request type
     * @throws ODataTargetTypeException if unable to determine request type
     */
    private Type getRequestType(ODataUri oDataUri) throws ODataTargetTypeException {
        TargetType targetType = WriteMethodUtil.getTargetType(odataRequest, entityDataModel, oDataUri);
        return entityDataModel.getType(targetType.typeName());
    }

    /**
     * If it's the first batch request call - start transaction.
     * @param type The type of the entity to request a datasource for
     */
    private TransactionalDataSource getTransactionalDataSource(Type type) throws ODataException {
        DataSource dataSource = dataSourceFactory.getDataSource(requestContext, type.getFullyQualifiedName());
        String dataSourceKey = dataSource.getClass().toString();
        if (dataSourceMap.containsKey(dataSourceKey)) {
            return dataSourceMap.get(dataSourceKey);
        } else {
            TransactionalDataSource transactionalDataSource = dataSource.startTransaction();
            dataSourceMap.put(dataSourceKey, transactionalDataSource);
            return transactionalDataSource;
        }
    }

    private void validateEntityData(ODataUri oDataUri, Object entityData) throws ODataException {
        Type targetType = getRequestType(oDataUri);
        if (!MetaType.ENTITY.equals(targetType.getMetaType())) {
            throw new ODataBadRequestException("The body of the write request must contain a valid entity.");
        }
        WriteMethodUtil.validateProperties(entityData, entityDataModel);
    }
}
