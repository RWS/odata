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
package com.sdl.odata.util;

import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * AnnotationUtilTest.
 */
public class AnnotationUtilTest {

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

    @Test
    public void testGetAnnotationOnFieldError() throws Exception {
        Field field = AnnotatedClass.class.getDeclaredField("notAnnotatedField");
        assertThrows(ODataSystemException.class, () ->
                AnnotationsUtil.getAnnotation(field, EdmProperty.class)
        );
    }

    @Test
    public void testGetAnnotationOnClassError() {
        assertThrows(ODataSystemException.class, () ->
                AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmComplex.class)
        );
    }

    @Test
    public void testGetAnnotationNotExistingError() {
        assertThrows(ODataSystemException.class, () ->
                AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmComplex.class),
                "Could not load annotation: " + EdmComplex.class
                        + " on source: " + AnnotatedClass.class
        );
    }

    @Test
    public void testGetAnnotationNotExistingCustomError() {
        assertThrows(ODataSystemException.class, () ->
                AnnotationsUtil.getAnnotation(AnnotatedClass.class, EdmComplex.class, "My Special Error"),
                "My Special Error"
        );
    }
}
