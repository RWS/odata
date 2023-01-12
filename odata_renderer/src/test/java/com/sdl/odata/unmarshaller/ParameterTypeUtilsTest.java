/*
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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.parser.util.ParameterTypeUtil;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.test.model.ActionSample;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test for parameter types util class.
 */
public class ParameterTypeUtilsTest {

    @Test
    public void testSettingWrappedParameter() throws NoSuchFieldException, ODataUnmarshallingException {
        ActionSample actionSample = new ActionSample();
        Field field = actionSample.getClass().getDeclaredField("number");
        ParameterTypeUtil.setParameter(actionSample, field, "24");
        assertEquals(24L, actionSample.getNumber());
    }

    @Test
    public void testSettingPrimitive() throws NoSuchFieldException, ODataUnmarshallingException {
        ActionSample actionSample = new ActionSample();
        Field field = actionSample.getClass().getDeclaredField("intNumber");
        ParameterTypeUtil.setParameter(actionSample, field, "42");
        assertEquals(42, actionSample.getIntNumber());
    }

    @Test
    public void testSettingString() throws NoSuchFieldException, ODataUnmarshallingException {
        ActionSample actionSample = new ActionSample();
        Field field = actionSample.getClass().getDeclaredField("stringParameter");
        ParameterTypeUtil.setParameter(actionSample, field, "someText");
        assertEquals("someText", actionSample.getStringParameter());
    }

    @Test
    public void testSettingNull() throws NoSuchFieldException, ODataUnmarshallingException {
        ActionSample actionSample = new ActionSample();
        Field field = actionSample.getClass().getDeclaredField("stringParameter");
        ParameterTypeUtil.setParameter(actionSample, field, null);
        assertNull(actionSample.getStringParameter());
    }
}
