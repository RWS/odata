/*
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

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.service.ODataRequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * The DataSource Factory Impl Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataSourceFactoryImplTest {

    @InjectMocks
    private DataSourceFactoryImpl dataSourceFactory;

    @Spy
    private List<DataSourceProvider> dataSourceProvidersSpy = new ArrayList<>();

    @Mock
    private DataSourceProvider dataSourceProviderMock;

    @Mock
    private DataSource dataSourceMock;

    @Mock
    private EntityDataModel entityDataModelMock;

    private ODataRequestContext oDataRequestContext;

    @Before
    public void setUp() throws ODataDataSourceException {

        dataSourceProvidersSpy.clear();
        dataSourceProvidersSpy.add(dataSourceProviderMock);
        when(dataSourceProviderMock.isSuitableFor(any(ODataRequestContext.class), eq("ODataDemo.Customer")
        )).thenReturn(true);
        when(dataSourceProviderMock.getDataSource(any(ODataRequestContext.class))).thenReturn(dataSourceMock);
        oDataRequestContext = new ODataRequestContext(null, null, entityDataModelMock);
    }

    @Test
    public void testGetDataSource() throws ODataDataSourceException {
        assertThat(dataSourceFactory.getDataSource(oDataRequestContext, "ODataDemo.Customer"), is(dataSourceMock));
    }

    @Test(expected = ODataDataSourceException.class)
    public void testGetDataSourceNotExisting() throws ODataDataSourceException {

        assertThat(dataSourceFactory.getDataSource(oDataRequestContext, "ODataDemo.Product"), is(dataSourceMock));
    }
}
