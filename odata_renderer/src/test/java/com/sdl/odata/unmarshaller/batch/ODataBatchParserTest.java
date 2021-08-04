/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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

import com.google.common.collect.Lists;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.parser.BatchRequestComponent;
import com.sdl.odata.parser.ODataBatchRequestContent;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.parser.ODataRequestComponent;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.Test;
import scala.collection.JavaConverters;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Batch Request Parser Test.
 */
public class ODataBatchParserTest extends UnmarshallerTest {

    private static final String REQUEST_ENTITY_PATH = "/batch/BatchReq.txt";

    @Test
    public void testSimpleBatch() throws Exception {
        requestBuilder.setUri(odataUri.serviceRoot() + "/$batch").setMethod(ODataRequest.Method.POST);
        preparePostRequestContext(REQUEST_ENTITY_PATH);

        ODataBatchParser parser = new ODataBatchParser(context, new ODataParserImpl());
        ODataBatchRequestContent batchRequestContent = (ODataBatchRequestContent) parser.processEntity(
                context.getRequest().getBodyText(StandardCharsets.UTF_8.name()));

        // check batch request components
        List<ODataRequestComponent> batchRequestComponents = Lists.newArrayList(
                JavaConverters.asJavaCollection(batchRequestContent.requestComponents()));
        assertFalse(batchRequestComponents.isEmpty());
        assertEquals(1, batchRequestComponents.size());
        assertTrue(batchRequestComponents.get(0) instanceof BatchRequestComponent);
        BatchRequestComponent batchRequestComponent = (BatchRequestComponent) batchRequestComponents.get(0);

        // check batch request component headers
        Map<String, String> batchRequestComponentHeaders = JavaConverters.mapAsJavaMap(
                batchRequestComponent.getHeaders().headers());
        assertEquals(2, batchRequestComponentHeaders.size());
        assertEquals("localhost", batchRequestComponentHeaders.get("Host"));
        assertEquals("binary", batchRequestComponentHeaders.get("Content-Transfer-Encoding"));

        // check batch request component details
        Map<String, String> batchRequestComponentDetails = JavaConverters.mapAsJavaMap(
                batchRequestComponent.getRequestDetails());
        assertEquals(4, batchRequestComponentDetails.size());
        assertEquals("GET", batchRequestComponentDetails.get("RequestType"));
        assertEquals("Customers('ALFKI')", batchRequestComponentDetails.get("RequestEntity"));
        assertEquals("/service/", batchRequestComponentDetails.get("RelativePath"));
        assertEquals("", batchRequestComponentDetails.get("RequestBody"));
    }

}
