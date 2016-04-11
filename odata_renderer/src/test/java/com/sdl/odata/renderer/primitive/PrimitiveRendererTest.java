package com.sdl.odata.renderer.primitive;

import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.renderer.RendererTest;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.util.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.test.util.TestUtils.SERVICE_ROOT;
import static com.sdl.odata.test.util.TestUtils.createODataRequest;
import static org.junit.Assert.assertTrue;

/**
 * Unit test to covering {@link PrimitiveRenderer}.
 */
public class PrimitiveRendererTest extends RendererTest {

    private PrimitiveRenderer primitiveRenderer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        primitiveRenderer = new PrimitiveRenderer();
    }

    @Test
    public void testCountPathScore() throws UnsupportedEncodingException {
        ODataUri countPathODataUri = TestUtils.createODataCountEntitiesUri(SERVICE_ROOT, "Customer");
        ODataRequestContext oDataRequestContext = TestUtils.createODataRequestContext(
                createODataRequest(ODataRequest.Method.GET), countPathODataUri, entityDataModel);
        int score = primitiveRenderer.score(oDataRequestContext, QueryResult.from(5L));
        assertTrue(score > 0);
    }

    @Test
    public void testValuePathScore() throws UnsupportedEncodingException {
        ODataUri countPathODataUri = TestUtils.createODataValueEntitiesUri(SERVICE_ROOT, "Customer", Customer.PHONE);
        ODataRequestContext oDataRequestContext = TestUtils.createODataRequestContext(
                createODataRequest(ODataRequest.Method.GET), countPathODataUri, entityDataModel);
        int score = primitiveRenderer.score(oDataRequestContext, QueryResult.from(5L));
        assertTrue(score > 0);
    }
}
