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
package com.sdl.odata;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.unmarshaller.NoEntityUnmarshaller;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * No Entity Unmarshaller Test.
 */
public class NoEntityUnmarshallerTest extends UnmarshallerTest {

    @Test
    public void testTheLowestScore() throws UnsupportedEncodingException, ODataException {
        ODataRequest.Builder builder = new ODataRequest.Builder()
                .setBodyText("test", "UTF-8").setUri("mockURI").setMethod(ODataRequest.Method.GET);
        ODataRequestContext context = new ODataRequestContext(builder.build(), odataUri, entityDataModel);
        NoEntityUnmarshaller noEntityUnmarshaller = new NoEntityUnmarshaller();
        assertThat(noEntityUnmarshaller.score(context), is(1));
        assertThat(noEntityUnmarshaller.unmarshall(context), is(nullValue()));
    }
}
