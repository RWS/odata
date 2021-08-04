/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.processor;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataNotImplementedException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.MetadataUri;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.RelativeUri;
import com.sdl.odata.api.parser.ServiceRootUri;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ODataQueryProcessor;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.ODataEntityNotFoundException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.ODataQuery;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Option;

import java.util.List;

import static com.sdl.odata.api.service.ODataResponse.Status.OK;

/**
 * Implementation of {@code ODataQueryProcessor}.
 */
@Component
public class ODataQueryProcessorImpl implements ODataQueryProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ODataQueryProcessorImpl.class);

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public ProcessorResult query(ODataRequestContext requestContext, Object data) throws ODataException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("ODataQueryProcessorImpl.query() for {}, data: {}", requestContext.getRequest(), data);
        }

        ODataUri oDataUri = requestContext.getUri();
        EntityDataModel entityDataModel = requestContext.getEntityDataModel();

        RelativeUri relativeUri = oDataUri.relativeUri();
        if (isMetadataUri(relativeUri) || isServiceRootUri(relativeUri)) {
            return new ProcessorResult(OK, QueryResult.from(entityDataModel));
        }

        Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(oDataUri, entityDataModel);
        if (!targetTypeOption.isDefined()) {
            throw new ODataBadRequestException("The target type could not be determined for this query: " +
                    requestContext.getRequest().getUri());
        }

        TargetType targetType = targetTypeOption.get();

        ODataQuery query = new QueryModelBuilder(requestContext.getEntityDataModel()).build(requestContext);
        LOG.trace("Query model: {}", query);

        QueryOperationStrategy strategy = dataSourceFactory.getStrategy(requestContext, query.operation(), targetType);
        if (strategy == null) {
            throw new ODataNotImplementedException("This query is not supported: " +
                    requestContext.getRequest().getUri());
        }

        QueryResult result;

        try {
            result = strategy.execute();
        } catch (Exception e) {
            LOG.error("Unexpected Exception when executing query " + query, e);
            throw e;
        }
        if (targetType.isCollection()) {
            if (result.getType() == QueryResult.ResultType.COLLECTION ||
                    result.getType() == QueryResult.ResultType.RAW_JSON) {
                return new ProcessorResult(OK, result);
            }
            throw new ODataDataSourceException("Expected a collection result, but found " +
                        result.getType().name() + " for this query: " +
                        result.getType().name(), requestContext.getRequest().getUri());
        } else {
            if (result.getType() != QueryResult.ResultType.COLLECTION) {
                return new ProcessorResult(OK, result);
            }
            List<?> list = (List<?>) result.getData();
            if (list.size() == 0) {
                throw new ODataEntityNotFoundException("Entity not found for this query: " +
                        requestContext.getRequest().getUri());
            } else if (list.size() > 1) {
                throw new ODataDataSourceException("Expected one result, but found multiple for this query: " +
                        requestContext.getRequest().getUri());
            }

            return new ProcessorResult(OK, QueryResult.from(list.get(0)));
        }
    }

    private boolean isMetadataUri(RelativeUri relativeUri) {
        return relativeUri instanceof MetadataUri;
    }

    private boolean isServiceRootUri(RelativeUri relativeUri) {
        return relativeUri instanceof ServiceRootUri;
    }
}
