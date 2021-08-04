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
package com.sdl.odata.api.edm.annotations;

import com.sdl.odata.api.edm.model.PrimitiveType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the class is an enum type in the OData entity data model.
 * This must only be used on Java {@code enum} types.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 10
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmEnum {

    /**
     * The name of the enum type. If not specified, the name of the class is used.
     *
     * @return The name of the enum type.
     */
    String name() default "";

    /**
     * The namespace of the schema that the enum type is in. If not specified, the name of the package that contains
     * the enum class is used.
     *
     * @return The namespace of the schema that the enum type is in.
     */
    String namespace() default "";

    /**
     * The underlying type of the enum type.
     *
     * This must be one of the following OData primitive types: {@code Edm.Byte}, {@code Edm.SByte}, {@code Edm.Int16},
     * {@code Edm.Int32}, {@code Edm.Int64}.
     *
     * @return The underlying type of the enum type.
     */
    PrimitiveType underlyingType() default PrimitiveType.INT32;

    /**
     * Indicates if this enum type represents a set of flags.
     *
     * @return {@code true} if this enum type represents a set of flags, {@code false} otherwise.
     */
    boolean flags() default false;
}
