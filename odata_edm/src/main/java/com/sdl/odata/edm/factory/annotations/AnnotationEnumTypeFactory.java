/**
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.EnumMember;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.edm.model.EnumMemberImpl;
import com.sdl.odata.edm.model.EnumTypeImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Enum Type Factory.
 */
final class AnnotationEnumTypeFactory {

    public EnumType build(Class<?> cls) {
        EdmEnum enumAnno = cls.getAnnotation(EdmEnum.class);

        boolean isFlags = enumAnno.flags();

        // NOTE: Values are generated automatically. If isFlags is false, then consecutive integer values starting
        // from 0 are used (0, 1, 2, ...). If isFlags is true, then consecutive bits are used (1, 2, 4, 8, ...).

        List<EnumMember> members = new ArrayList<>();
        long value = isFlags ? 1L : 0L;
        for (Field field : cls.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                members.add(new EnumMemberImpl(field.getName(), value));
                if (isFlags) {
                    value <<= 1;
                } else {
                    value++;
                }
            }
        }

        return new EnumTypeImpl.Builder()
                .setName(getTypeName(enumAnno, cls))
                .setNamespace(getNamespace(enumAnno, cls))
                .setJavaType(cls)
                .setUnderlyingType(enumAnno.underlyingType())
                .setIsFlags(isFlags)
                .addMembers(members)
                .build();
    }

    private static String getTypeName(EdmEnum enumAnno, Class<?> enumClass) {
        String name = enumAnno.name();
        if (isNullOrEmpty(name)) {
            // Use class name if name is not specified in EdmEnum annotation
            name = enumClass.getSimpleName();
        }
        return name;
    }

    private static String getNamespace(EdmEnum enumAnno, Class<?> enumClass) {
        String namespace = enumAnno.namespace();
        if (isNullOrEmpty(namespace)) {
            // Use package name if namespace is not specified in EdmEnum annotation
            namespace = enumClass.getPackage().getName();
        }
        return namespace;
    }

    public static String getFullyQualifiedTypeName(EdmEnum enumAnno, Class<?> enumClass) {
        String name = getTypeName(enumAnno, enumClass);
        String namespace = getNamespace(enumAnno, enumClass);
        return namespace + "." + name;
    }
}
