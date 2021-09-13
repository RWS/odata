/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
import com.sdl.odata.api.ODataException;
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
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AbstractRenderer}.
 */
public class AbstractRendererTest {

    private static final ODataUri ODATA_URI = new ODataUri("http://localhost:8080/odata.svc",
            new ResourcePathUri(
                    new EntitySetPath("Customers", Option.<EntityCollectionPath>apply(null)),
                    List$.MODULE$.<QueryOption>empty()));

    private EntityDataModel entityDataModel;

    @Before
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
        public void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
                throws ODataException {
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
        assertThat(renderer.scoreByMediaType(Arrays.asList(
                new MediaType("text", "html"),
                new MediaType("application", "xml", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.1"))
        ), XML), is(MAXIMUM_HEADER_SCORE - 2));

        assertThat(renderer.scoreByMediaType(Arrays.asList(
                new MediaType("text", "html"),
                new MediaType("application", "json", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.8"))
        ), XML), CoreMatchers.is(AbstractRenderer.WILDCARD_MATCH_SCORE));

        assertThat(renderer.scoreByMediaType(Arrays.asList(
                new MediaType("text", "html"),
                new MediaType("application", "xml", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.8"))
        ), JSON), CoreMatchers.is(AbstractRenderer.WILDCARD_MATCH_SCORE));

        assertThat(renderer.scoreByMediaType(
                Collections.singletonList(ATOM_XML), ATOM_XML), is(MAXIMUM_HEADER_SCORE));

        assertThat(renderer.scoreByMediaType(
                Collections.<MediaType>emptyList(), JSON), is(DEFAULT_SCORE));
    }

    @Test
    public void testScoreForAtom() throws UnsupportedEncodingException {
        AbstractRenderer atomRenderer = new AtomRenderer();
        int score = atomRenderer.score(buildODataRequest(ODATA_URI, ATOM_XML), QueryResult.from(Lists.newArrayList()));
        assertThat(score, is(MAXIMUM_HEADER_SCORE));
        score = atomRenderer.score(buildODataRequest(ODATA_URI, XML), QueryResult.from(Lists.newArrayList()));
        assertThat(score, is(MAXIMUM_HEADER_SCORE));
    }

    @Test
    public void testScoreForJsonMarshaller() throws Exception {
        AbstractRenderer atomRenderer = new JsonRenderer();
        int score = atomRenderer.score(buildODataRequest(ODATA_URI, JSON), QueryResult.from(Lists.newArrayList()));
        assertThat(score, is(MAXIMUM_HEADER_SCORE));
    }

    @Test
    public void testScoreForNoAcceptHeader() throws Exception {
        AbstractRenderer atomRenderer = new AtomRenderer();
        MediaType[] mediaTypes = {new MediaType("text", "html"),
                new MediaType("application", "test", ImmutableMap.of("q", "0.8")),
                new MediaType("*", "*", ImmutableMap.of("q", "0.1"))};

        int atomXMLScore = atomRenderer.score(buildODataRequest(ODATA_URI, mediaTypes),
                QueryResult.from(Lists.newArrayList()));
        assertThat(atomXMLScore, CoreMatchers.is(AbstractRenderer.WILDCARD_MATCH_SCORE + 1));

        int jsonScore = new JsonRenderer().score(buildODataRequest(ODATA_URI, mediaTypes),
                QueryResult.from(Lists.newArrayList()));
        assertTrue("Score of XML marshaller should have higher value in case of wild card matching ",
                atomXMLScore > jsonScore);
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
            assertThat(renderer.scoreByFormat(formatOption, ATOM_SVC_XML), is(DEFAULT_SCORE));
            assertThat(renderer.scoreByFormat(formatOption, ATOM_XML), is(DEFAULT_SCORE));
            assertThat(renderer.scoreByFormat(formatOption, XML), is(DEFAULT_SCORE));
            assertThat(renderer.scoreByFormat(formatOption, JSON), is(DEFAULT_SCORE));
        }
    }

    private void assertScoreByFormatMatchMediaType(Option<FormatOption> formatOption, MediaType mediaType) {
        if (formatOption.get().mediaType().equals(mediaType)) {
            assertThat(renderer.scoreByFormat(formatOption, mediaType), is(MAXIMUM_FORMAT_SCORE));
        } else {
            assertThat(renderer.scoreByFormat(formatOption, mediaType), is(DEFAULT_SCORE));
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
