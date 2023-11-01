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
package com.sdl.odata.processor.write;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.service.ODataRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.ODataRequest.Method.DELETE;
import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * The DELETE Method Handler Test.
 */
public class DeleteMethodHandlerTest extends MethodHandlerTest {

    @BeforeEach
    public void setup() {
        super.setup("Persons");
    }

    private WriteMethodHandler getDeleteMethodHandler(EntityDataModel entityDataModel, boolean isEntitySetUri)
            throws UnsupportedEncodingException {
        ODataRequestContext requestContext = super.createRequestContext(DELETE, isEntitySetUri, entityDataModel);
        return new DeleteMethodHandler(requestContext, dataSourceFactoryMock);
    }

    @Test
    public void testWriteWithNotNull() throws Exception {
        stubForTesting(getEntity());
        assertThrows(ODataBadRequestException.class, () ->
                getDeleteMethodHandler(getEntityDataModel(), false).handleWrite(getEntity())
        );
    }


    @Test
    public void testWriteEntitySet() throws Exception {
        stubForTesting(getEntity());
        assertThrows(ODataBadRequestException.class, () ->
                getDeleteMethodHandler(getEntityDataModel(), true).handleWrite(getEntity())
        );
    }

    public void doWrite(Object entity, EntityDataModel entityDataModel) throws Exception {
        stubForTesting(entity);
        ProcessorResult result = getDeleteMethodHandler(entityDataModel, false).handleWrite(null);
        assertEquals(NO_CONTENT, result.getStatus());
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
