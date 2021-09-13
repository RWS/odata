/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Primitive Util - Guava.Primitive twin.
 */
public final class PrimitiveUtil {

    /**
     * A map from primitive types to their corresponding wrapper types.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAP;
    private static final Map<Class<?>, Class<?>> WRAP_TO_PRIMITIVE;

    private PrimitiveUtil() {
    }

    static {
        Map<Class<?>, Class<?>> primitiveMap = new HashMap<>();
        Map<Class<?>, Class<?>> wrapToPrim = new HashMap<>();

        add(primitiveMap, wrapToPrim, boolean.class, Boolean.class);
        add(primitiveMap, wrapToPrim, byte.class, Byte.class);
        add(primitiveMap, wrapToPrim, char.class, Character.class);
        add(primitiveMap, wrapToPrim, double.class, Double.class);
        add(primitiveMap, wrapToPrim, float.class, Float.class);
        add(primitiveMap, wrapToPrim, int.class, Integer.class);
        add(primitiveMap, wrapToPrim, long.class, Long.class);
        add(primitiveMap, wrapToPrim, short.class, Short.class);
        add(primitiveMap, wrapToPrim, void.class, Void.class);


        PRIMITIVE_TO_WRAP = Collections.unmodifiableMap(primitiveMap);
        WRAP_TO_PRIMITIVE = Collections.unmodifiableMap(wrapToPrim);
    }

    private static void add(Map<Class<?>, Class<?>> forward,
                            Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    /**
     * Guava wrap alternative.
     *
     * @param type type
     * @return wrapped result
     */
    public static <T> Class<T> wrap(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }

        Class<T> wrapped = (Class<T>) PRIMITIVE_TO_WRAP.get(type);
        return (wrapped == null) ? type : wrapped;
    }

    /**
     * Guava unwrap alternative.
     *
     * @param type type
     * @return unwrapped result
     */
    public static <T> Class<T> unwrap(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }

        Class<T> unwrapped = (Class<T>) WRAP_TO_PRIMITIVE.get(type);
        return (unwrapped == null) ? type : unwrapped;
    }

}
