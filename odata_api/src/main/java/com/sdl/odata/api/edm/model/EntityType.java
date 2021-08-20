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
package com.sdl.odata.api.edm.model;

/**
 * OData entity type. An entity type is a complex type (it consists of properties and navigation properties) and it
 * has a key that uniquely identifies it.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 8
 *
 */
public interface EntityType extends StructuredType {

    /**
     * Returns the key of the entity type.
     *
     * @return The key of the entity type.
     */
    Key getKey();

    /**
     * Returns {@code true} if the entity type is read-only, {@code false} otherwise.
     *
     * @return {@code true} if the entity type is read-only, {@code false} otherwise.
     */
    boolean isReadOnly();

    /**
     * Returns {@code true} if the entity type has a stream, {@code false} otherwise.
     *
     * @return {@code true} if the entity type has a stream, {@code false} otherwise.
     */
    boolean hasStream();
}
