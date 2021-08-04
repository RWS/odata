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
package com.sdl.odata.api.edm.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the class is a Function of an entity data model.
 * <p>
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793966">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 12.2</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmFunction {

    /**
     * The name of the Function. If not specified, the name of the class is used.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793967">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 12.2.1</a>
     *
     * @return The name of the Function.
     */
    String name() default "";

    /**
     * The IsBound attribute of Function.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793969">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 12.2.2</a>
     *
     * @return {@code true} if the Function is bound
     */
    boolean isBound() default false;

    /**
     * Indicates that Function is composable.
     * A composable function can be invoked with additional path segments or system query options appended to the path
     * that identifies the composable function as appropriate for the type returned by the composable function.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793970">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 12.2.3</a>
     *
     * @return {@code true} if the Function is composable
     */
    boolean isComposable() default false;

    /**
     * The entity set path. The first segment of the entity set path is the name of the binding parameter.
     * The remaining segments of the entity set path represent navigation segments or type casts.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793971">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 12.2.4</a>
     *
     * @return the entity set path
     */
    String entitySetPath() default "";

    /**
     * The namespace of the schema that the function is in. If not specified, the name of the package that contains
     * the function class is used.
     *
     * @return The namespace of the schema that the function is in.
     */
    String namespace() default "";
}
