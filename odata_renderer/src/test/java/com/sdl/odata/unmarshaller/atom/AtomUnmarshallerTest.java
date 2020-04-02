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
package com.sdl.odata.unmarshaller.atom;


import com.google.common.collect.ImmutableMap;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.sdl.odata.api.service.ODataRequest.Method.DELETE;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataRequest.Method.PUT;
import static com.sdl.odata.test.util.TestUtils.createODataRequest;
import static com.sdl.odata.test.util.TestUtils.createODataRequestContext;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Atom Unmarshaller Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AtomUnmarshallerTest extends UnmarshallerTest {

    private static final Map<String, String> CONTENT_TYPE = ImmutableMap.of("Content-type", "application/atom+xml");

    @InjectMocks
    private AtomUnmarshaller atomUnmarshaller = new AtomUnmarshaller();

    @Spy
    private ODataParser uriParser = new ODataParserImpl();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testAtomNullScore() throws UnsupportedEncodingException, ODataException {
        assertThat(atomUnmarshaller.score(errorContext), is(0));
        assertThat(atomUnmarshaller.unmarshall(errorContext), is(nullValue()));
    }

    @Test
    public void testScoreForPOST() throws UnsupportedEncodingException {
        assertTrue(atomUnmarshaller.score(createODataRequestContext(
                createODataRequest(POST, CONTENT_TYPE), odataUri, entityDataModel)) > 0);
    }

    @Test
    public void testScoreForGET() throws UnsupportedEncodingException {
        assertThat(atomUnmarshaller.score(createODataRequestContext(
                createODataRequest(GET, CONTENT_TYPE), odataUri, entityDataModel)), is(0));
    }

    @Test
    public void testScoreForPUT() throws UnsupportedEncodingException {
        assertTrue(atomUnmarshaller.score(createODataRequestContext(
                createODataRequest(PUT, CONTENT_TYPE), odataUri, entityDataModel)) > 0);
    }

    @Test
    public void testScoreForDelete() throws UnsupportedEncodingException {
        assertThat(atomUnmarshaller.score(createODataRequestContext(DELETE, entityDataModel)), is(0));
    }
}
