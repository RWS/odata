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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmFunction;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.test.model.FunctionSample;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link AnnotationFunctionFactory}.
 */
public class AnnotationFunctionFactoryTest {

    private AnnotationFunctionFactory factory;

    @Before
    public void setup() {
        factory = new AnnotationFunctionFactory();
    }

    @Test
    public void testFullyQualifiedFunctionName() {
        String fullyQualifiedFunctionName = AnnotationFunctionFactory.getFullyQualifiedFunctionName(
                FunctionSample.class.getAnnotation(EdmFunction.class), FunctionSample.class);

        assertEquals("ODataDemo.ODataDemoFunction", fullyQualifiedFunctionName);
    }

    @Test
    public void testFunctionAnnotation() {
        Function function = factory.build(FunctionSample.class);

        assertEquals("ODataDemoEntitySetPath", function.getEntitySetPath());
        assertEquals("ODataDemoFunction", function.getName());
        assertEquals("ODataDemo", function.getNamespace());
        assertEquals(2, function.getParameters().size());
        assertTrue(function.getParameters().stream().allMatch(parameter ->
                parameter.getName().equals("stringFunctionField") && parameter.getType().equals("String") ||
                        parameter.getName().equals("intFunctionField") && parameter.getType().equals("int")));

        assertEquals("Edm.String", function.getReturnType());
        assertTrue(function.isBound());
        assertTrue(function.isComposable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionWithoutEdmReturnType() {
        factory.build(WrongFunctionSample.class);
    }

    @Test
    public void testFullyQualifiedWrongFunctionName() {
        String fullyQualifiedFunctionName = AnnotationFunctionFactory.getFullyQualifiedFunctionName(
                WrongFunctionSample.class.getAnnotation(EdmFunction.class), WrongFunctionSample.class);

        assertEquals("com.sdl.odata.edm.factory.annotations.WrongFunctionSample", fullyQualifiedFunctionName);
    }

    /**
     * Sample function entity that has no {@link com.sdl.odata.api.edm.annotations.EdmReturnType} annotation.
     */
    @EdmFunction
    private class WrongFunctionSample {

        private String stringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }

}
