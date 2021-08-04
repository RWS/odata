/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.api.edm.util;

import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.lang.reflect.Field;

/**
 * Collection of Edm entities utilities.
 */
public final class EdmUtil {

    private EdmUtil() { }

    public static Object getEdmPropertyValue(Object entity, String propertyName) throws IllegalAccessException {
        for (Field fld : entity.getClass().getDeclaredFields()) {
            EdmProperty ann = fld.getAnnotation(EdmProperty.class);
            if (ann != null && propertyName.equals(ann.name())) {
                fld.setAccessible(true);
                return fld.get(entity);
            }
        }
        throw new IllegalAccessException("No property " + propertyName +
                " in object of type " + entity.getClass().getName());
    }
}
