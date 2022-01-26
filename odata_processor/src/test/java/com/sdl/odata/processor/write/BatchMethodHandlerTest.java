/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.TransactionalDataSource;
import com.sdl.odata.api.service.ChangeSetEntity;
import com.sdl.odata.api.service.ODataRequestContext;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The Batch Method Handler Test.
 */
public class BatchMethodHandlerTest extends MethodHandlerTest {

    @Before
    public void setup() throws Exception {
        super.setup("Persons");
    }

    private BatchMethodHandler getPostMethodHandler(EntityDataModel entityDataModel, Object odataEntity)
            throws UnsupportedEncodingException {
        ODataRequestContext requestContext = super.createRequestContext(POST, true, entityDataModel);
        ChangeSetEntity entity = new ChangeSetEntity("1", requestContext, odataEntity);
        return new BatchMethodHandler(requestContext, dataSourceFactoryMock, Collections.singletonList(entity));
    }

    @Test(expected = ODataException.class)
    public void testFailWithDbError() throws Exception {
        stubForTesting(getEntity());
        TransactionalDataSource trxDataSourceMock = mock(TransactionalDataSource.class);
        when(trxDataSourceMock.create(any(ODataUri.class), any(), any(EntityDataModel.class)))
                .thenThrow(new ODataDataSourceException("something went wrong with db"));
        when(dataSourceMock.startTransaction()).thenReturn(trxDataSourceMock);

        EntityDataModel entityDataModel = getEntityDataModel();
        getPostMethodHandler(entityDataModel, getEntity()).handleWrite();
    }

    @Test
    public void testSuccess() throws Exception {
        stubForTesting(getEntity());
        TransactionalDataSource trxDataSourceMock = mock(TransactionalDataSource.class);
        when(trxDataSourceMock.create(any(ODataUri.class), any(), any(EntityDataModel.class))).thenReturn(getEntity());
        when(dataSourceMock.startTransaction()).thenReturn(trxDataSourceMock);

        EntityDataModel entityDataModel = getEntityDataModel();
        getPostMethodHandler(entityDataModel, getEntity()).handleWrite();
    }




}
