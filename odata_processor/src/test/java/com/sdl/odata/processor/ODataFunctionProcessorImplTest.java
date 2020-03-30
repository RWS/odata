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
package com.sdl.odata.processor;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.annotations.EdmFunction;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.test.model.FunctionImportSample;
import com.sdl.odata.test.model.FunctionNotNullableImportSample;
import com.sdl.odata.test.model.FunctionNotNullableSample;
import com.sdl.odata.test.model.FunctionSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.UnboundFunctionSample;
import com.sdl.odata.test.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test for {@link ODataFunctionProcessorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ODataFunctionProcessorImplTest {

    private EntityDataModel entityDataModel;

    @Mock
    private DataSourceFactory dataSourceFactory;

    @InjectMocks
    private ODataFunctionProcessorImpl functionProcessor;

    @Before
    public void setup() throws ODataEdmException {
        entityDataModel = new AnnotationEntityDataModelFactory()
                .addClass(Order.class)
                .addClass(FunctionSample.class)
                .addClass(FunctionNotNullableSample.class)
                .addClass(UnboundFunctionSample.class)
                .addClass(FunctionImportSample.class)
                .addClass(FunctionNotNullableImportSample.class)
                .addClass(FakeFunctionSample.class)
                .addClass(NullResultFunctionSample.class)
                .addClass(NoInitFunctionSample.class)
                .buildEntityDataModel();
        initMocks(ODataFunctionProcessorImpl.class);
    }

    @Test
    public void testDoFunctionUsingFunction() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)/ODataDemo.ODataDemoFunction(" +
                        "stringFunctionField='myString',intFunctionField=10)", entityDataModel), entityDataModel);
        ProcessorResult processorResult = functionProcessor.doFunction(requestContext);
        assertEquals(ODataResponse.Status.OK, processorResult.getStatus());
        assertEquals("myString10", processorResult.getData());
    }

    @Test
    public void testDoFunctionWithDefaultTransportHeaders() throws ODataException, UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("te", "chunked");

        ODataUri uri = new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)/ODataDemo.ODataDemoFunction(" +
                        "stringFunctionField='myString',intFunctionField=12)", entityDataModel);

        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, uri, entityDataModel, headers);

        ProcessorResult processorResult = functionProcessor.doFunction(requestContext);
        assertEquals(ODataResponse.Status.OK, processorResult.getStatus());
        assertTrue(Stream.class.isAssignableFrom(processorResult.getData().getClass()));
    }

    @Test
    public void testDoFunctionWithCustomTransportHeaders() throws ODataException, UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odata-te", "chunked");

        ODataUri uri = new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)/ODataDemo.ODataDemoFunction(" +
                        "stringFunctionField='myString',intFunctionField=24)", entityDataModel);

        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, uri, entityDataModel, headers);

        ProcessorResult processorResult = functionProcessor.doFunction(requestContext);
        assertEquals(ODataResponse.Status.OK, processorResult.getStatus());
        assertTrue(Stream.class.isAssignableFrom(processorResult.getData().getClass()));
    }

    @Test
    public void testDoFunctionUsingFunctionImport() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/ODataDemoFunctionImport(" +
                        "stringFunctionField='myString2',intFunctionField=50)", entityDataModel), entityDataModel);
        ProcessorResult processorResult = functionProcessor.doFunction(requestContext);
        assertEquals(ODataResponse.Status.OK, processorResult.getStatus());
        assertEquals("myString250", processorResult.getData());
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testAllNullParametersUsingFunctionImport() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/ODataDemoFunctionNotNullableImport", entityDataModel), entityDataModel);
        functionProcessor.doFunction(requestContext);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testNullParameterUsingFunctionImport() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/ODataDemoFunctionNotNullableImport(" +
                        "stringFunctionField='myString')", entityDataModel), entityDataModel);
        functionProcessor.doFunction(requestContext);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testDoFunctionUsingEntitySet() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)", entityDataModel), entityDataModel);
        functionProcessor.doFunction(requestContext);
    }

    @Test(expected = ODataEdmException.class)
    public void testDoFunctionUsingFakeFunction() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)/ODataDemo.ODataDemoFakeFunction", entityDataModel),
                entityDataModel);
        functionProcessor.doFunction(requestContext);
    }

    @Test(expected = ODataEdmException.class)
    public void testDoFunctionUsingWrongInitFunction() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)/ODataDemo.ODataDemoNoInitFunction", entityDataModel),
                entityDataModel);
        functionProcessor.doFunction(requestContext);
    }

    @Test
    public void testDoFunctionUsingNullResultFunction() throws ODataException, UnsupportedEncodingException {
        ODataRequestContext requestContext = TestUtils.createODataRequestContext(GET, new ODataParserImpl().parseUri(
                "http://localhost/odata.svc/Orders(1)/ODataDemo.ODataDemoNullFunction", entityDataModel),
                entityDataModel);
        ProcessorResult processorResult = functionProcessor.doFunction(requestContext);
        assertEquals(ODataResponse.Status.NO_CONTENT, processorResult.getStatus());
    }

    /**
     * Fake function not implementing {@link com.sdl.odata.api.edm.model.Operation} interface that should fail.
     */
    @EdmFunction(namespace = "ODataDemo", name = "ODataDemoFakeFunction", entitySetPath = "ODataDemoEntitySetPath",
            isBound = true, isComposable = true)
    @EdmReturnType(type = "Orders")
    static class FakeFunctionSample {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }
    }

    /**
     * No result function to cover NO_CONTENT result status.
     */
    @EdmFunction(namespace = "ODataDemo", name = "ODataDemoNullFunction", entitySetPath = "ODataDemoEntitySetPath",
            isBound = true, isComposable = true)
    @EdmReturnType(type = "Orders")
    static class NullResultFunctionSample implements Operation {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

        @Override
        public Object doOperation(ODataRequestContext requestContext,
                                  DataSourceFactory dataSourceFactory) {
            return someField;
        }
    }

    /**
     * Function that cannot be initialized because of private modifier and non static.
     */
    @EdmFunction(namespace = "ODataDemo", name = "ODataDemoNoInitFunction", entitySetPath = "ODataDemoEntitySetPath",
            isBound = true, isComposable = true)
    @EdmReturnType(type = "Orders")
    private class NoInitFunctionSample {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }
    }
}
