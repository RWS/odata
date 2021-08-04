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
package com.sdl.odata.util;

import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * AnnotationUtilTest.
 */
public class AnnotationUtilTest {

    /**
     * Expected exception.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCheckAnnotationPresent() {
        //This should pass without exception
        assertThat(AnnotationsUtil.checkAnnotationPresent(AnnotatedClass.class, EdmEntity.class), notNullValue());
    }

    @Test
    public void testGetAnnotation() throws Exception {
        assertThat(AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmEntity.class), notNullValue());

        Field field = AnnotatedClass.class.getDeclaredField("annotatedField");
        assertThat(AnnotationsUtil.getAnnotation(field, EdmProperty.class), notNullValue());
    }

    @Test(expected = ODataSystemException.class)
    public void testGetAnnotationOnFieldError() throws Exception {
        Field field = AnnotatedClass.class.getDeclaredField("notAnnotatedField");
        AnnotationsUtil.getAnnotation(field, EdmProperty.class);
    }

    @Test(expected = ODataSystemException.class)
    public void testGetAnnotationOnClassError() throws Exception {
        AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmComplex.class);
    }

    @Test
    public void testGetAnnotationNotExistingError() throws Exception {
        expectedException.expect(ODataSystemException.class);
        expectedException.expectMessage("Could not load annotation: " + EdmComplex.class.toString()
                + " on source: " + AnnotatedClass.class.toString());

        AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmComplex.class);
    }

    @Test
    public void testGetAnnotationNotExistingCustomError() throws Exception {
        expectedException.expect(ODataSystemException.class);
        expectedException.expectMessage("My Special Error");

        AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmComplex.class, "My Special Error");
    }
}
