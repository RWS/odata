/*
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
package com.sdl.odata.api.edm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A reference to a property that is part of the key of an entity type.
 * <p>
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 8.3
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ })
public @interface EdmPropertyRef {

    /**
     * The path to the property.
     * <p>
     * This is a path expression that resolves to a primitive property of the entity type itself or a primitive
     * property of a complex property (recursively) of the entity type. The names of the properties in the path are
     * joined together by forward slashes.
     *
     * @return The path to the property.
     */
    String path();

    /**
     * The alias for the property reference.
     * <p>
     * An alias is required if the {@code path} attribute refers to a property that is a member of a complex type.
     * If the key property is not a member of a complex type, then an alias must not be set.
     *
     * @return The alias.
     */
    String alias() default "";
}
