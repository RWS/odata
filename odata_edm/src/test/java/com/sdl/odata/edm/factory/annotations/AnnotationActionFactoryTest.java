/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * AnnotationActionFactory test.
 */
public class AnnotationActionFactoryTest {
    private AnnotationActionFactory factory = new AnnotationActionFactory();

    @Test
    public void testAnnotationActionFactory() {
        Action action = factory.build(ActionSample.class);

        assertThat(action.getName(), is("ODataDemoAction"));
        assertThat(action.getNamespace(), is("ODataDemo"));
        assertThat(action.isBound(), is(true));
        assertThat(action.getEntitySetPath(), is("ODataDemoEntitySetPath"));
        assertThat(action.getParameters().size(), is(5));
        assertTrue(action.getParameters().stream().allMatch(parameter -> parameter.getName().equals("StringParameter")
                || parameter.getName().equals("NumberParameter")
                || parameter.getName().equals("parametersMap")
                || parameter.getName().equals("parametersMapList")
                || parameter.getName().equals("intNumber")));
        assertThat(action.getReturnType(), is("Customers"));
    }

    @Test
    public void testDefaultAction() {
        Action action = factory.build(DefaultActionSample.class);

        assertThat(action.getName(), is("DefaultActionSample"));
        assertThat(action.getNamespace(), is("com.sdl.odata.test.model"));
        assertThat(action.isBound(), is(false));
        assertThat(action.getEntitySetPath(), is(""));
        assertThat(action.getParameters().size(), is(1));
        assertTrue(action.getParameters().stream().allMatch(parameter -> parameter.getName().equals("someParameter")));
        assertThat(action.getReturnType(), is("BankAccounts"));
    }

}
