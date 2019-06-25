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
package com.sdl.odata.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

import com.sdl.odata.api.ODataSystemException;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * This is a utility containing all useful utility functions for getting annotation information.
 */
public final class AnnotationsUtil {
    private AnnotationsUtil() {

    }

    /**
     * Check if the annotation is present and if not throws an exception,
     * this is just an overload for more clear naming.
     *
     * @param annotatedType   The source type to tcheck the annotation on
     * @param annotationClass The annotation to look for
     * @param <T>             The annotation subtype
     * @return The annotation that was requested
     * @throws ODataSystemException If unable to find the annotation or nullpointer in case null source was specified
     */
    public static <T extends Annotation> T checkAnnotationPresent(AnnotatedElement annotatedType,
                                                                  Class<T> annotationClass) {
        return getAnnotation(annotationClass, annotatedType);
    }

    /***
     * Var args version of {@link #checkAnnotationPresent(AnnotatedElement, Class)} where the first match is returned.
     * @param annotationClass
     * @param annotatedTypes
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T checkAnnotationPresent(Class<T> annotationClass,
                                                                  AnnotatedElement... annotatedTypes) {
        return getAnnotation(annotationClass, annotatedTypes);
    }

    /**
     * Small utility to easily get an annotation and will throw an exception if not provided.
     *
     * @param annotatedType   The source type to tcheck the annotation on
     * @param annotationClass The annotation to look for
     * @param <T>             The annotation subtype
     * @return The annotation that was requested
     * @throws ODataSystemException If unable to find the annotation or nullpointer in case null source was specified
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedType, Class<T> annotationClass) {
        return getAnnotation(annotatedType, annotationClass, null);
    }

    /***
     * Var args version of {@link #getAnnotation(AnnotatedElement, Class)} where the first match is returned.
     *
     * @param annotationClass
     * @param annotatedTypes
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, AnnotatedElement... annotatedTypes) {
        return getAnnotation(annotationClass, null, annotatedTypes);
    }

    /**
     * Small utility to easily get an annotation and will throw an exception if not provided.
     *
     * @param annotatedType   The source type to tcheck the annotation on
     * @param annotationClass The annotation to look for
     * @param error           The error if the annotaton is not present, can be null a default error will be returned
     * @param <T>             The annotation subtype
     * @return The annotation that was requested
     * @throws ODataSystemException If unable to find the annotation or nullpointer in case null source was specified
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedType, Class<T> annotationClass,
                                                         String error) {
        return getAnnotation(annotationClass, error, annotatedType);
    }

    /***
     *  Var args version of {@link #getAnnotation(AnnotatedElement, Class, String)} where the first match is returned.
     * @param annotationClass
     * @param error
     * @param annotatedTypes
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(Class<T> annotationClass,
                                                         String error,
                                                         AnnotatedElement... annotatedTypes)  {
        if (annotatedTypes == null || annotatedTypes.length == 0) {
            throw new IllegalArgumentException(error);
        }
        T result = null;
        for (AnnotatedElement annotatedType : annotatedTypes) {
            if (annotatedType.isAnnotationPresent(annotationClass)) {
                result = annotatedType.getAnnotation(annotationClass);
                break;
            }
        }
        if (result == null) {
            if (isNullOrEmpty(error)) {
                throw new ODataSystemException("Could not load annotation: " + annotationClass
                                               + " on source: " +
                                               (annotatedTypes.length == 1 ? annotatedTypes[0] :
                                                        Arrays.asList(annotatedTypes)));
            } else {
                throw new ODataSystemException(error);
            }
        }
        return result;
    }
}
