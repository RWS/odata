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
import java.util.UUID;

/**
 * Batch Method Handler is specific to batch operations.
 */
public class BatchMethodHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BatchMethodHandler.class);

    private final List<ChangeSetEntity> changeSetEntities;
    private final EntityDataModel entityDataModel;
    private final DataSourceFactory dataSourceFactory;
    private boolean isTransactionStarted = false;
    private String transactionID = UUID.randomUUID().toString();
    private int requestsProcessedCount = 0;

    public BatchMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory,
                              List<ChangeSetEntity> changeSetEntries) {
        this.changeSetEntities = changeSetEntries;
        this.entityDataModel = requestContext.getEntityDataModel();
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Handles transactional operations for each parsed odata request.
     *
     * @return processor results
     */
    public List<ProcessorResult> handleWrite() throws ODataException {
        LOG.info("Handling transactional operations per each odata request.");
        List<ProcessorResult> resultList = new ArrayList<>();
        for (ChangeSetEntity changeSetEntity : changeSetEntities) {
            ODataRequestContext requestContext = changeSetEntity.getRequestContext();
            ODataUri requestUri = requestContext.getUri();
            ODataRequest.Method method = requestContext.getRequest().getMethod();

            ProcessorResult result = null;
            if (method == ODataRequest.Method.POST) {
                result = handlePOST(requestContext, requestUri, changeSetEntity);
            } else if (method == ODataRequest.Method.PUT || method == ODataRequest.Method.PATCH) {
                result = handlePutAndPatch(requestContext, requestUri, changeSetEntity);
            } else if (method == ODataRequest.Method.DELETE) {
                result = handleDelete(requestContext, requestUri, changeSetEntity);
            }
            resultList.add(result);
            // If the processed request fails - do not continue. Transaction in this case was already closed
            // in handle method
            if (result.getStatus() == ODataResponse.Status.BAD_REQUEST) {
                break;
            }
        }
        return resultList;
    }

    private ProcessorResult handlePOST(ODataRequestContext requestContext, ODataUri oDataUri,
                                       ChangeSetEntity changeSetEntity) {
        LOG.info("Handling POST operation");
        Object entityData = changeSetEntity.getOdataEntity();
        Map<String, String> headers = new HashMap<>();
        ODataRequest odataRequest = requestContext.getRequest();
        headers.putAll(odataRequest.getHeaders());
        headers.put("changeSetId", changeSetEntity.getChangeSetId());

        TransactionalDataSource dataSource = null;
        try {
            dataSource = (TransactionalDataSource) getDataSourceFromTargetType(requestContext,
                    getRequestType(odataRequest, oDataUri), entityData);
            headers.putAll(WriteMethodUtil.getResponseHeaders(entityData, oDataUri, entityDataModel));
            starTransactionIfNeeded(dataSource);
            Object createdEntity = dataSource.create(oDataUri, entityData, entityDataModel, transactionID);
            increaseProcessedAndCloseTransactionIfNeeded(dataSource);

            if (WriteMethodUtil.isMinimalReturnPreferred(odataRequest)) {
                return new ProcessorResult(ODataResponse.Status.NO_CONTENT, headers);
            }
            return new ProcessorResult(ODataResponse.Status.CREATED, createdEntity, headers, requestContext);

        } catch (ODataException ex) {
            // If POST operation fails - end transaction and do not continue farther
            return prepareFailedResult(dataSource, ex.getMessage(), headers, requestContext);
        }
    }

    private ProcessorResult handleDelete(ODataRequestContext requestContext, ODataUri odataUri,
                                         ChangeSetEntity changeSetEntity) {
        LOG.debug("Handling DELETE operation");

        Map<String, String> headers = new HashMap<>();
        ODataRequest oDataRequest = requestContext.getRequest();
        headers.putAll(oDataRequest.getHeaders());
        headers.put("changeSetId", changeSetEntity.getChangeSetId());

        TransactionalDataSource dataSource = null;
        try {
            Option<String> singletonName = ODataUriUtil.getSingletonName(odataUri);
            dataSource = (TransactionalDataSource) getDataSourceFromTargetName(
                    requestContext, getRequestType(oDataRequest, odataUri).getFullyQualifiedName());
            starTransactionIfNeeded(dataSource);
            if (singletonName.isDefined()) {
                throw new ODataBadRequestException("The URI refers to the singleton '" + singletonName.get() +
                        "'. Singletons cannot be deleted.");
            }
            dataSource.delete(odataUri, entityDataModel, transactionID);
            increaseProcessedAndCloseTransactionIfNeeded(dataSource);
            return new ProcessorResult(ODataResponse.Status.NO_CONTENT, null, headers, requestContext);
        } catch (ODataException e) {
            return prepareFailedResult(dataSource, e.getMessage(), headers, requestContext);
        }
    }

    private ProcessorResult handlePutAndPatch(ODataRequestContext requestContext, ODataUri requestUri,
                                              ChangeSetEntity changeSetEntity) {
        LOG.debug("Handling PUT or PATCH operation");
        Object entityData = changeSetEntity.getOdataEntity();

        Map<String, String> headers = new HashMap<>();
        headers.put("changeSetId", changeSetEntity.getChangeSetId());
        TransactionalDataSource dataSource = null;
        try {
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

            dataSource = (TransactionalDataSource) getDataSourceFromTargetType(
                    requestContext, type, entityData);
            starTransactionIfNeeded(dataSource);
            Object updatedEntity = dataSource.update(requestUri, entityData, entityDataModel, transactionID);
            increaseProcessedAndCloseTransactionIfNeeded(dataSource);
            // add additional headers

            headers.putAll(oDataRequest.getHeaders());
            if (WriteMethodUtil.isMinimalReturnPreferred(oDataRequest)) {
                return new ProcessorResult(ODataResponse.Status.NO_CONTENT, headers);
            }
            return new ProcessorResult(ODataResponse.Status.OK, updatedEntity, headers, requestContext);
        } catch (ODataException ex) {
            return prepareFailedResult(dataSource, ex.getMessage(), headers, requestContext);
        }
    }

    private ProcessorResult prepareFailedResult(TransactionalDataSource dataSource, String message, Map<String,
            String> headers, ODataRequestContext requestContext) {
        if (dataSource != null) {
            dataSource.endTransaction(transactionID, false);
        }
        return new ProcessorResult(ODataResponse.Status.BAD_REQUEST, message, headers, requestContext);
    }

    /**
     * Returns request type.
     * @param oDataRequest
     * @param oDataUri
     * @return
     * @throws ODataTargetTypeException
     */
    private Type getRequestType(ODataRequest oDataRequest, ODataUri oDataUri) throws ODataTargetTypeException {
        TargetType targetType = WriteMethodUtil.getTargetType(oDataRequest, entityDataModel, oDataUri);
        return entityDataModel.getType(targetType.typeName());
    }

    /**
     * If it's the first batch request call - start transaction.
     * @param dataSource
     */
    private void starTransactionIfNeeded(TransactionalDataSource dataSource) {
        if (!isTransactionStarted) {
            dataSource.startTransaction(transactionID);
            isTransactionStarted = true;
        }
    }

    /**
     * Increase processed change set requests count and check if it is the last one: if it is - end transaction
     * successfully, otherwise  - continue processing of change sets.
     * @param dataSource
     */
    private void increaseProcessedAndCloseTransactionIfNeeded(TransactionalDataSource dataSource) {
        // Increase processed request entities and if it was the last one - close transaction
        requestsProcessedCount++;
        if (requestsProcessedCount == changeSetEntities.size()) {
            LOG.info("Ending the transaction with id: {} ", transactionID);
            dataSource.endTransaction(transactionID, true);
        }
    }

    private DataSource getDataSourceFromTargetType(ODataRequestContext requestContext,
                                                   Type type, Object entityData) throws ODataException {
        if (!MetaType.ENTITY.equals(type.getMetaType())) {
            throw new ODataBadRequestException("The body of the write request must contain a valid entity.");
        }
        WriteMethodUtil.validateProperties(entityData, entityDataModel);

        return WriteMethodUtil.getDataSource(requestContext, type.getFullyQualifiedName(), dataSourceFactory);
    }

    private DataSource getDataSourceFromTargetName(ODataRequestContext requestContext,
                                                   String entityType) throws ODataException {
        return dataSourceFactory.getDataSource(requestContext, entityType);
    }
}
