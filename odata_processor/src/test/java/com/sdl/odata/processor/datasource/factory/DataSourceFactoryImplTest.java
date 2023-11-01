/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.service.ODataRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The DataSource Factory Impl Test.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DataSourceFactoryImplTest {

    @InjectMocks
    private DataSourceFactoryImpl dataSourceFactory;

    private final List<DataSourceProvider> dataSourceProvidersSpy = new ArrayList<>();

    @Mock
    private DataSourceProvider dataSourceProviderMock;

    @Mock
    private DataSource dataSourceMock;

    @Mock
    private EntityDataModel entityDataModelMock;

    private ODataRequestContext oDataRequestContext;

    @BeforeEach
    public void setUp() throws ODataDataSourceException {
        dataSourceProvidersSpy.clear();
        dataSourceProvidersSpy.add(dataSourceProviderMock);
        dataSourceFactory.setDataSourceProviders(dataSourceProvidersSpy);
        oDataRequestContext = new ODataRequestContext(null, null, entityDataModelMock);

        when(dataSourceProviderMock.getDataSource(any(ODataRequestContext.class))).thenReturn(dataSourceMock);
        when(dataSourceProviderMock.isSuitableFor(oDataRequestContext, "ODataDemo.Customer"))
                .thenReturn(true);
    }

    @Test
    public void testGetDataSource() throws ODataDataSourceException {
        assertEquals(dataSourceMock, dataSourceFactory.getDataSource(oDataRequestContext,
                "ODataDemo.Customer"));
    }

    @Test
    public void testGetDataSourceNotExisting() {
        assertThrows(ODataDataSourceException.class, () ->
                dataSourceFactory.getDataSource(oDataRequestContext, "ODataDemo.Product")
        );
    }
}
