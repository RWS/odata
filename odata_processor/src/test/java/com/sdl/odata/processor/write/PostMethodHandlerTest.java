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
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.processor.model.ODataPerson;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataResponse.Status.CREATED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The POST Method Handler Test.
 */
public class PostMethodHandlerTest extends MethodHandlerTest {

    @Before
    public void setup() throws Exception {
        super.setup("Persons");
    }

    private void stubForTesting(Object entity, EntityDataModel entityDataModel) throws ODataException {
        super.stubForTesting(entity);
        when(dataSourceMock.create(entitySetOdataURI, entity, entityDataModel)).thenReturn(entity);
    }

    private WriteMethodHandler getPostMethodHandler(EntityDataModel entityDataModel, boolean isEntitySetUri)
            throws UnsupportedEncodingException {
        ODataRequestContext requestContext = super.createRequestContext(POST, isEntitySetUri, entityDataModel);
        return new PostMethodHandler(requestContext, dataSourceFactoryMock, processorConfiguration);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteWithNull() throws Exception {
        EntityDataModel entityDataModel = getEntityDataModel();
        stubForTesting(getEntity(), entityDataModel);
        getPostMethodHandler(entityDataModel, true).handleWrite(null);
    }


    public void doWrite(Object entity, EntityDataModel entityDataModel) throws Exception {
        stubForTesting(entity,  entityDataModel);
        ProcessorResult result = getPostMethodHandler(entityDataModel, true).handleWrite(entity);
        assertThat(result.getStatus(), is(CREATED));
        assertThat(result.getData(), is(entity));
        verify(dataSourceMock, times(1)).create(entitySetOdataURI, entity,  entityDataModel);
    }

    @Test
    public void testWrite() throws Exception {
        // With ODataPerson
        doWrite(getEntity(), getEntityDataModel());

        // With ODataPersonNamedKey
        doWrite(getEntityForNamedKey(), getEntityDataModelForNamedKey());
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteNonEntitySet() throws Exception {
        stubForTesting(getEntity(), getEntityDataModel());
        WriteMethodHandler writeMethodHandler = getPostMethodHandler(getEntityDataModel(), false);
        writeMethodHandler.handleWrite(getEntity());
    }

    @Test
    public void testValidateProperties() throws Exception {
        // With ODataPerson
        WriteMethodHandler writeMethodHandler = getPostMethodHandler(getEntityDataModel(), true);
        writeMethodHandler.validateProperties(getEntity(), getEntityDataModel());

        // With ODataPersonNamedKey
        writeMethodHandler = getPostMethodHandler(getEntityDataModelForNamedKey(), true);
        writeMethodHandler.validateProperties(getEntityForNamedKey(), getEntityDataModelForNamedKey());
    }

    @Test(expected = ODataBadRequestException.class)
    public void testValidatePropertiesMissingEntity() throws Exception {
        Object entity = getEntity();
        ((ODataPerson) entity).setPrimaryPhone(null);
        WriteMethodHandler writeMethodHandler = getPostMethodHandler(getEntityDataModel(), true);
        writeMethodHandler.validateProperties(entity, getEntityDataModel());
        // Should fail with an exception because 'primaryPhone' is not nullable
    }

    @Test(expected = ODataBadRequestException.class)
    public void testValidatePropertiesMissingComplex() throws Exception {
        Object entity = getEntity();
        ((ODataPerson) entity).setPrimaryAddress(null);
        WriteMethodHandler writeMethodHandler = getPostMethodHandler(getEntityDataModel(), true);
        writeMethodHandler.validateProperties(entity, getEntityDataModel());
        // Should fail with an exception because 'primaryAddress' is not nullable
    }



}
