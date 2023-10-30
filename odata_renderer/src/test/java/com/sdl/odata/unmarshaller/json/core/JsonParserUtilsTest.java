/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for JsonParserUtils.
 */
public class JsonParserUtilsTest {

    private static final String BAD_VALUE = "BAD_VALUE";

    @Test
    public void testIncorrectEnumValue() {
        assertThrows(ODataUnmarshallingException.class, () ->
                JsonParserUtils.getAppropriateFieldValue(TestEnum.class, BAD_VALUE),
                BAD_VALUE
        );
    }

    @Test
    public void testIncorrectIntegerValue() {
        assertThrows(ODataUnmarshallingException.class, () ->
                JsonParserUtils.getAppropriateFieldValue(Integer.class, BAD_VALUE),
                BAD_VALUE
        );
    }

    /**
     * Test Enum to test invalid data format case.
     */
    enum TestEnum {
        TEST
    }
}
