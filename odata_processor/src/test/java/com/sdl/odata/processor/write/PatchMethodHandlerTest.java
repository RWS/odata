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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.processor.model.ODataPerson;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.ODataRequest.Method.PUT;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static com.sdl.odata.test.util.TestUtils.createODataRequestContext;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


/**
 * The PUT Method Handler Test.
 */
public class PatchMethodHandlerTest extends MethodHandlerTest {

    @Before
    public void setup() throws Exception {
        super.setup("Persons");
    }

    private void stubForTesting(Object entity, EntityDataModel entityDataModel) throws ODataException {
        super.stubForTesting(entity);
        when(dataSourceMock.update(entitySetOdataURI, entity, entityDataModel)).thenReturn(entity);
    }

    private WriteMethodHandler getPatchMethodHandler(EntityDataModel entityDataModel, boolean isEntitySetUri)
            throws UnsupportedEncodingException {
        ODataRequestContext requestContext = createRequestContext(PUT, isEntitySetUri, entityDataModel);
        return new PatchMethodHandler(requestContext, dataSourceFactoryMock, new ObjectMapper(), new ODataParserImpl());
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteWithNull() throws Exception {
        EntityDataModel entityDataModel = getEntityDataModel();
        stubForTesting(getEntity(), entityDataModel);
        getPatchMethodHandler(entityDataModel, false).handleWrite(null);
    }


    @Test(expected = ODataBadRequestException.class)
    public void testWriteEntitySet() throws Exception {
        EntityDataModel entityDataModel = getEntityDataModel();
        stubForTesting(getEntity(), entityDataModel);
        getPatchMethodHandler(entityDataModel, true).handleWrite(getEntity());
    }

    public void doWrite(Object entity, EntityDataModel entityDataModel) throws Exception {
        stubForTesting(entity, entityDataModel);
        ProcessorResult result = getPatchMethodHandler(entityDataModel, false).handleWrite(entity);
        assertThat(result.getStatus(), is(OK));
        assertNull(result.getData());
        verify(dataSourceMock, times(1)).update(eq(entityOdataURI), any(), eq(entityDataModel));
    }

    @Test
    public void testWrite() throws Exception {
        QueryOperationStrategy queryOperationStrategy = mock(QueryOperationStrategy.class);
        QueryResult queryResult1 = QueryResult.from(getEntity());
        QueryResult queryResult2 = QueryResult.from(getEntityForNamedKey());
        when(queryOperationStrategy.execute()).thenReturn(queryResult1).thenReturn(queryResult2);
        when(dataSourceFactoryMock.getStrategy(any(), any(), any())).thenReturn(queryOperationStrategy);

        // With ODataPerson
        doWrite(getEntity(), getEntityDataModel());

        // With ODataPersonNamedKey
        doWrite(getEntityForNamedKey(), getEntityDataModelForNamedKey());
    }

    @Override
    protected ODataRequestContext createRequestContext(ODataRequest.Method method, boolean isEntitySetUri,
                                                       EntityDataModel entityDataModel)
            throws UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (isEntitySetUri) {
                return createODataRequestContext(method,
                        entitySetOdataURI, entityDataModel, objectMapper.writeValueAsString(getEntity()));
            }
            return createODataRequestContext(method, entityOdataURI, entityDataModel, objectMapper.writeValueAsString(getEntity()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
