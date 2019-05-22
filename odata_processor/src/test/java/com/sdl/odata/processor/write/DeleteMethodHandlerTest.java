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
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.service.ODataRequestContext;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.ODataRequest.Method.DELETE;
import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * The DELETE Method Handler Test.
 */
public class DeleteMethodHandlerTest extends MethodHandlerTest {

    @Before
    public void setup() throws Exception {
        super.setup("Persons");
    }

    private WriteMethodHandler getDeleteMethodHandler(EntityDataModel entityDataModel, boolean isEntitySetUri)
            throws UnsupportedEncodingException {
        ODataRequestContext requestContext = super.createRequestContext(DELETE, isEntitySetUri, entityDataModel);
        return new DeleteMethodHandler(requestContext, dataSourceFactoryMock, processorConfiguration);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testWriteWithNotNull() throws Exception {
        stubForTesting(getEntity());
        getDeleteMethodHandler(getEntityDataModel(), false).handleWrite(getEntity());
    }


    @Test(expected = ODataBadRequestException.class)
    public void testWriteEntitySet() throws Exception {
        stubForTesting(getEntity());
        getDeleteMethodHandler(getEntityDataModel(), true).handleWrite(getEntity());
    }

    public void doWrite(Object entity, EntityDataModel entityDataModel) throws Exception {
        stubForTesting(entity);
        ProcessorResult result = getDeleteMethodHandler(entityDataModel, false).handleWrite(null);
        assertThat(result.getStatus(), is(NO_CONTENT));
        assertNull(result.getData());
        verify(dataSourceMock, times(1)).delete(entityOdataURI, entityDataModel);
    }

    @Test
    public void testWrite() throws Exception {
        // With ODataPerson
        doWrite(getEntity(), getEntityDataModel());

        // With ODataPersonNamedKey
        doWrite(getEntityForNamedKey(), getEntityDataModelForNamedKey());
    }

}
