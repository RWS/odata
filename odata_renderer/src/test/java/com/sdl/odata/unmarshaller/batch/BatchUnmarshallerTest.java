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
package com.sdl.odata.unmarshaller.batch;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.parser.ODataBatchRequestContent;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test to cover {@link BatchUnmarshaller}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BatchUnmarshallerTest extends UnmarshallerTest {

    private static final String REQUEST_ENTITY_PATH = "/batch/BatchReq.txt";

    @Spy
    private ODataParser oDataParser = new ODataParserImpl();

    @InjectMocks
    private BatchUnmarshaller batchUnmarshaller;

    @Before
    public void setup() throws IOException, ODataUnmarshallingException {
        odataUri = new ODataUriParser(entityDataModel).parseUri(odataUri.serviceRoot() + "/$batch");
        requestBuilder.setUri(odataUri.serviceRoot()).setMethod(ODataRequest.Method.POST);
        preparePostRequestContext(REQUEST_ENTITY_PATH);

    }

    @Test
    public void testUnmarshallerScore() {
        assertTrue(batchUnmarshaller.score(context) > 0);
    }

    @Test
    public void testUnmarshaller() throws ODataException {
        ODataBatchRequestContent unmarshalledBatchRequest = (ODataBatchRequestContent) batchUnmarshaller.
                unmarshall(context);
        assertNotNull(unmarshalledBatchRequest);
    }
}
