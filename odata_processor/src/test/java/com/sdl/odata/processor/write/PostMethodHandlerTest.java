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
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.processor.model.ODataPerson;
import org.junit.Before;
import org.junit.Test;

import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataResponse.Status.CREATED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The POST Method Handler Test.
 */
public class PostMethodHandlerTest extends MethodHandlerTest {
    private WriteMethodHandler writeMethodHandler;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ODataRequestContext requestContext = createContextWithEntitySet(POST);
        writeMethodHandler = new PostMethodHandler(requestContext, dataSourceFactoryMock);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteWithNull() throws Exception {
        stubForTesting();
        writeMethodHandler.handleWrite(null);
    }

    @Test
    public void testWrite() throws Exception {
        stubForTesting();
        ProcessorResult result = writeMethodHandler.handleWrite(entity);
        assertThat(result.getStatus(), is(CREATED));
        assertThat(result.getData(), is(entity));
        verify(dataSourceMock, times(1)).create(entitySetOdataURI, entity, entityDataModel);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteNonEntitySet() throws Exception {
        writeMethodHandler = new PostMethodHandler(createContextWithEntity(POST), dataSourceFactoryMock);
        stubForTesting();
        writeMethodHandler.handleWrite(entity);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteWithPrimitive() throws Exception {
        writeMethodHandler = new PostMethodHandler(createContextWithEntity(POST), dataSourceFactoryMock);
        stubForTesting();
        writeMethodHandler.handleWrite(entity);
    }

    @Test
    public void testValidateProperties() throws ODataException {
        writeMethodHandler.validateProperties(entity, entityDataModel);
        // Should pass without exceptions
    }

    @Test(expected = ODataBadRequestException.class)
    public void testValidatePropertiesMissingEntity() throws ODataException {
        ((ODataPerson) entity).setPrimaryPhone(null);
        writeMethodHandler.validateProperties(entity, entityDataModel);
        // Should fail with an exception because 'primaryPhone' is not nullable
    }

    @Test(expected = ODataBadRequestException.class)
    public void testValidatePropertiesMissingComplex() throws ODataException {
        ((ODataPerson) entity).setPrimaryAddress(null);
        writeMethodHandler.validateProperties(entity, entityDataModel);
        // Should fail with an exception because 'primaryAddress' is not nullable
    }
}
