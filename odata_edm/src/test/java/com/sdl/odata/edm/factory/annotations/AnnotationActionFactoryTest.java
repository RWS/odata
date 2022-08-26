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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.test.model.ActionSample;
import com.sdl.odata.test.model.DefaultActionSample;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AnnotationActionFactory test.
 */
public class AnnotationActionFactoryTest {

    private final AnnotationActionFactory factory = new AnnotationActionFactory();

    @Test
    public void testAnnotationActionFactory() {
        Action action = factory.build(ActionSample.class);

        assertEquals("ODataDemoAction", action.getName());
        assertEquals("ODataDemo", action.getNamespace());
        assertEquals("ODataDemoEntitySetPath", action.getEntitySetPath());
        assertTrue(action.isBound());
        assertEquals(5, action.getParameters().size());
        assertTrue(action.getParameters().stream().allMatch(parameter -> parameter.getName().equals("StringParameter")
                || parameter.getName().equals("NumberParameter")
                || parameter.getName().equals("parametersMap")
                || parameter.getName().equals("parametersMapList")
                || parameter.getName().equals("intNumber")));
        assertEquals("Customers", action.getReturnType());
    }

    @Test
    public void testDefaultAction() {
        Action action = factory.build(DefaultActionSample.class);

        assertEquals("DefaultActionSample", action.getName());
        assertEquals("com.sdl.odata.test.model", action.getNamespace());

        assertFalse(action.isBound());
        assertEquals("", action.getEntitySetPath());
        assertEquals(1, action.getParameters().size());
        assertTrue(action.getParameters().stream().allMatch(parameter -> parameter.getName().equals("someParameter")));
        assertEquals("BankAccounts", action.getReturnType());
    }

}
