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
package com.sdl.odata.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.EntityCollectionPath;
import com.sdl.odata.api.parser.EntitySetPath;
import com.sdl.odata.api.parser.FormatOption;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.renderer.atom.AtomRenderer;
import com.sdl.odata.renderer.json.JsonRenderer;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.CollectionsSample;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.EnumSample;
import com.sdl.odata.test.model.ExpandedPropertiesSample;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.PrimitiveTypesSample;
import com.sdl.odata.test.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Option;
import scala.collection.immutable.List$;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;

import static com.sdl.odata.api.service.MediaType.ATOM_SVC_XML;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.renderer.AbstractRenderer.DEFAULT_SCORE;
import static com.sdl.odata.renderer.AbstractRenderer.MAXIMUM_FORMAT_SCORE;
import static com.sdl.odata.renderer.AbstractRenderer.MAXIMUM_HEADER_SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AbstractRenderer}.
 */
public class AbstractRendererTest {

    private static final ODataUri ODATA_URI = new ODataUri("http://localhost:8080/odata.svc",
            new ResourcePathUri(
                    new EntitySetPath("Customers", Option.<EntityCollectionPath>apply(null)),
                    List$.MODULE$.<QueryOption>empty()));

    private EntityDataModel entityDataModel;

    @BeforeEach
    public void setUp() throws Exception {

        final AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();

        factory.addClass(Customer.class);
        factory.addClass(Address.class);
        factory.addClass(Order.class);
        factory.addClass(Product.class);
        factory.addClass(Category.class);
        factory.addClass(PrimitiveTypesSample.class);
        factory.addClass(CollectionsSample.class);
        factory.addClass(EnumSample.class);
        factory.addClass(IdNamePairSample.class);
        factory.addClass(ExpandedPropertiesSample.class);

        this.entityDataModel = factory.buildEntityDataModel();
    }

    private final AbstractRenderer renderer = new AbstractRenderer() {
        @Override
        public int score(ODataRequestContext requestContext, QueryResult data) {
            return 0;
        }

        @Override
        public void render(ODataRequestContext requestContext, QueryResult data,
                           ODataResponse.Builder responseBuilder) {
        }
    };

    @Test
    public void testByFormat() {
        assertScoreByFormat(createFormatOption(null));
        assertScoreByFormat(createFormatOption(ATOM_SVC_XML));
        assertScoreByFormat(createFormatOption(ATOM_XML));
        assertScoreByFormat(createFormatOption(XML));
        assertScoreByFormat(createFormatOption(JSON));
    }

    @Test
    public void testScoreByMediaType() {
        assertEquals(MAXIMUM_HEADER_SCORE - 2, renderer.scoreByMediaType(Arrays.asList(
                new MediaType("text", "html"),
                new MediaType("application", "xml", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.1"))
        ), XML));

        assertEquals(AbstractRenderer.WILDCARD_MATCH_SCORE, renderer.scoreByMediaType(Arrays.asList(
                new MediaType("text", "html"),
                new MediaType("application", "json", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.8"))
        ), XML));

        assertEquals(AbstractRenderer.WILDCARD_MATCH_SCORE, renderer.scoreByMediaType(Arrays.asList(
                new MediaType("text", "html"),
                new MediaType("application", "xml", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.8"))
        ), JSON));

        assertEquals(MAXIMUM_HEADER_SCORE, renderer.scoreByMediaType(
                Collections.singletonList(ATOM_XML), ATOM_XML));

        assertEquals(DEFAULT_SCORE, renderer.scoreByMediaType(
                Collections.emptyList(), JSON));
    }

    @Test
    public void testScoreForAtom() throws UnsupportedEncodingException {
        AbstractRenderer atomRenderer = new AtomRenderer();
        int score = atomRenderer.score(buildODataRequest(ODATA_URI, ATOM_XML), QueryResult.from(Lists.newArrayList()));
        assertEquals(MAXIMUM_HEADER_SCORE, score);
        score = atomRenderer.score(buildODataRequest(ODATA_URI, XML), QueryResult.from(Lists.newArrayList()));
        assertEquals(MAXIMUM_HEADER_SCORE, score);
    }

    @Test
    public void testScoreForJsonMarshaller() throws Exception {
        AbstractRenderer atomRenderer = new JsonRenderer();
        int score = atomRenderer.score(buildODataRequest(ODATA_URI, JSON), QueryResult.from(Lists.newArrayList()));
        assertEquals(MAXIMUM_HEADER_SCORE, score);
    }

    @Test
    public void testScoreForNoAcceptHeader() throws Exception {
        AbstractRenderer atomRenderer = new AtomRenderer();
        MediaType[] mediaTypes = {new MediaType("text", "html"),
                new MediaType("application", "test", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.1"))};

        int atomXMLScore = atomRenderer.score(buildODataRequest(ODATA_URI, mediaTypes),
                QueryResult.from(Lists.newArrayList()));
        assertEquals(AbstractRenderer.WILDCARD_MATCH_SCORE + 1, atomXMLScore);

        int jsonScore = new JsonRenderer().score(buildODataRequest(ODATA_URI, mediaTypes),
                QueryResult.from(Lists.newArrayList()));
        assertTrue(atomXMLScore > jsonScore,
                "Score of XML marshaller should have higher value in case of wild card matching ");
    }

    private Option<FormatOption> createFormatOption(MediaType mediaType) {
        if (mediaType == null) {
            return scala.Option.apply(null);
        } else {
            return scala.Option.apply(new FormatOption(mediaType));
        }
    }

    private void assertScoreByFormat(Option<FormatOption> formatOption) {
        if (formatOption.isDefined()) {
            assertScoreByFormatMatchMediaType(formatOption, ATOM_SVC_XML);
            assertScoreByFormatMatchMediaType(formatOption, ATOM_XML);
            assertScoreByFormatMatchMediaType(formatOption, XML);
            assertScoreByFormatMatchMediaType(formatOption, JSON);
        } else {
            assertEquals(DEFAULT_SCORE, renderer.scoreByFormat(formatOption, ATOM_SVC_XML));
            assertEquals(DEFAULT_SCORE, renderer.scoreByFormat(formatOption, ATOM_XML));
            assertEquals(DEFAULT_SCORE, renderer.scoreByFormat(formatOption, XML));
            assertEquals(DEFAULT_SCORE, renderer.scoreByFormat(formatOption, JSON));
        }
    }

    private void assertScoreByFormatMatchMediaType(Option<FormatOption> formatOption, MediaType mediaType) {
        if (formatOption.get().mediaType().equals(mediaType)) {
            assertEquals(MAXIMUM_FORMAT_SCORE, renderer.scoreByFormat(formatOption, mediaType));
        } else {
            assertEquals(DEFAULT_SCORE, renderer.scoreByFormat(formatOption, mediaType));
        }
    }

    private ODataRequestContext buildODataRequest(ODataUri odataURi, MediaType... mediatype)
            throws UnsupportedEncodingException {
        ODataRequest.Builder builder = new ODataRequest.Builder().setBodyText("test", "UTF-8")
                .setUri("mockURI").setMethod(ODataRequest.Method.GET);
        if (mediatype.length > 0) {
            builder.setAccept(mediatype);
        }
        return new ODataRequestContext(builder.build(), odataURi, entityDataModel);
    }
}
