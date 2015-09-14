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
package com.sdl.odata.api.edm.model;

/**
 * OData derivable type. Entity types and complex types are derivable types; they can inherit properties from a
 * base type.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 8.1.2 and 9.1.2
 *
 */
public interface DerivableType extends Type {

    /**
     * Returns the fully-qualified name of the base type of this type, or {@code null} if this type does not derive
     * from a base type.
     *
     * @return The fully-qualified name of the base type of this type or {@code null}.
     */
    String getBaseTypeName();

    /**
     * Returns {@code true} if this type is abstract, {@code false} otherwise.
     *
     * @return {@code true} if this type is abstract, {@code false} otherwise.
     */
    boolean isAbstract();
}
