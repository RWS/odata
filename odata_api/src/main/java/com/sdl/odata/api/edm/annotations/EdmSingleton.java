/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * Defines a singleton for an entity type. This annotation must only be used on classes that also have an
 * {@code EdmEntity} annotation.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 13.3
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmSingleton {

    /**
     * The name of the singleton. If the name of the singleton is not specified using either this attribute or the
     * {@code value} attribute, the name of the entity is used.
     *
     * @return The name of the singleton.
     */
    String name() default "";

    /**
     * Convenience attribute for the name of the entity set.
     *
     * @return The name of the singleton.
     */
    String value() default "";

}
