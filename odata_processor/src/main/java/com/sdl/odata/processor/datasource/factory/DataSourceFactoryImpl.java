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
package com.sdl.odata.processor.datasource.factory;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.Singleton;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link com.sdl.odata.api.processor.datasource.factory.DataSourceFactory}.
 */
@Component
public class DataSourceFactoryImpl implements DataSourceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceFactoryImpl.class);

    @Autowired(required = false)
    private List<DataSourceProvider> dataSourceProviders = new ArrayList<>();

    @Override
    public DataSource getDataSource(ODataRequestContext requestContext,
                                    final String entityType)
            throws ODataDataSourceException {

        for (DataSourceProvider dataSourceProvider : dataSourceProviders) {
            // Note: The first suitable data source provider wins
            if (dataSourceProvider.isSuitableFor(requestContext, entityType)) {
                return dataSourceProvider.getDataSource(requestContext);
            }
        }

        LOG.error("No data source provider found for entity type '{}'", entityType);
        throw new ODataDataSourceException("No data source provider found for entity type '" + entityType + "'");
    }

    @Override
    public QueryOperationStrategy getStrategy(ODataRequestContext requestContext, QueryOperation operation,
                                              TargetType expectedODataEntityType) throws ODataException {

        String entityTypeName = getEntityTypeName(operation, requestContext.getEntityDataModel());

        if (entityTypeName != null) {
            for (DataSourceProvider dataSourceProvider : dataSourceProviders) {
                if (dataSourceProvider.isSuitableFor(requestContext, entityTypeName)) {
                    QueryOperationStrategy strategy = dataSourceProvider.getStrategy(requestContext, operation,
                            expectedODataEntityType);
                    if (strategy != null) {
                        return strategy;
                    }
                }
            }
        }

        LOG.error("No strategy found for operation: {}", operation);
        return null;
    }

    private String getEntityTypeName(QueryOperation operation, EntityDataModel entityDataModel) {
        EntitySet entitySet = entityDataModel.getEntityContainer().getEntitySet(operation.entitySetName());

        // If the supplied entity is an EntitySet, return entity set type. Else check for Singleton
        if (entitySet != null) {
            return entitySet.getTypeName();
        }
        Singleton singleton = entityDataModel.getEntityContainer().getSingleton(operation.entitySetName());

        if (singleton != null) {
            return singleton.getTypeName();
        }
        return null;
    }
}
