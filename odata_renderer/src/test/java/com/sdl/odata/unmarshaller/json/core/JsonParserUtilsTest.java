/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.unmarshaller.json.core;

import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test for JsonParserUtils.
 */
public class JsonParserUtilsTest {

    /**
     * Used for tests that expect exception when parsing field.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String BAD_VALUE = "BAD_VALUE";

    @Test
    public void testIncorrectEnumValue() throws Exception {
        thrown.expect(ODataUnmarshallingException.class);
        thrown.expectMessage(BAD_VALUE);
        JsonParserUtils.getAppropriateFieldValue(TestEnum.class, BAD_VALUE);
    }

    @Test
    public void testIncorrectIntegerValue() throws Exception {
        thrown.expect(ODataUnmarshallingException.class);
        thrown.expectMessage(BAD_VALUE);
        JsonParserUtils.getAppropriateFieldValue(Integer.class, BAD_VALUE);
    }

    /**
     * Test Enum to test invalid data format case.
     */
    enum TestEnum {
        TEST
    }
}
