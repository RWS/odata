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
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.ODataRequest.Method.PUT;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * The PATCH Method Handler Test.
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
        return getPatchMethodHandler(entityDataModel, isEntitySetUri, getEntity());
    }

    private WriteMethodHandler getPatchMethodHandler(EntityDataModel entityDataModel, boolean isEntitySetUri,
                                                     Object entity)
            throws UnsupportedEncodingException {
        ODataRequestContext requestContext = createRequestContext(PUT, isEntitySetUri, entityDataModel);
        return new PatchMethodHandler(requestContext, dataSourceFactoryMock);
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
        ProcessorResult result = getPatchMethodHandler(entityDataModel, false, entity).handleWrite(entity);
        assertThat(result.getStatus(), is(OK));
        assertNull(result.getData());
        verify(dataSourceMock, times(1)).update(eq(entityOdataURI), any(), eq(entityDataModel));
    }

    @Test
    public void testWrite() throws Exception {
        // With ODataPerson
        doWrite(getEntity(), getEntityDataModel());

        // With ODataPersonNamedKey
        doWrite(getEntityForNamedKey(), getEntityDataModelForNamedKey());
    }

}
