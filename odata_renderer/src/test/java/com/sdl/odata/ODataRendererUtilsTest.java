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
package com.sdl.odata;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriParseException;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.parser.ODataParserImpl;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static com.sdl.odata.ODataRendererUtils.buildContextUrlFromOperationCall;
import static com.sdl.odata.ODataRendererUtils.isForceExpandParamSet;
import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link com.sdl.odata.ODataRendererUtils}.
 */
public class ODataRendererUtilsTest {

    protected EntityDataModel entityDataModel;
    private ODataParser uriParser = new ODataParserImpl();

    @Before
    public void setUp() throws Exception {
        this.entityDataModel = buildEntityDataModel();
    }

    @Test
    public void testBuildContextUrlForActions() throws ODataUriParseException {
        String uri = "http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction";
        ODataUri odataUri = uriParser.parseUri(uri, entityDataModel);

        String contextUrl = buildContextUrlFromOperationCall(odataUri, entityDataModel, false);

        TestCase.assertEquals("http://some.com/xyz.svc/$metadata#Customers/$entity", contextUrl);
    }

    @Test
    public void testBuildContextUrlForFunctions() throws ODataRenderException, ODataUriParseException {
        String uri = "http://some.com/xyz.svc/Customers(1)/ODataDemo.ODataDemoFunction(par1=5,par2=10,par3='foo')";
        ODataUri odataUri = uriParser.parseUri(uri, entityDataModel);

        String contextUrl = buildContextUrlFromOperationCall(odataUri, entityDataModel, true);

        TestCase.assertEquals("http://some.com/xyz.svc/$metadata#Edm.String", contextUrl);
    }

    @Test
    public void testForceExpandOnFunctions() throws Exception {

        ODataUri odataUri = new ODataParserImpl()
                .parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoFunction(par1=1)",
                        entityDataModel);

        assertFalse(isForceExpandParamSet(odataUri));

        odataUri = new ODataParserImpl()
                .parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoFunction(expand=true)",
                        entityDataModel);

        assertTrue(isForceExpandParamSet(odataUri));

        odataUri = new ODataParserImpl()
                .parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoFunction(expand='',par2='bar')",
                        entityDataModel);

        assertFalse(isForceExpandParamSet(odataUri));
    }

    private EntityDataModel buildEntityDataModel() throws ODataEdmException {
        final AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        return factory.addClasses(getEdmEntityClasses()).buildEntityDataModel();
    }
}
