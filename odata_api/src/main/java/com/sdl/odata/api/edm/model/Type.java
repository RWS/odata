/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * OData nominal type.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 4.1
 *
 */
public interface Type {

    /**
     * Returns the meta type of this type. The meta type describes what kind of type this is.
     *
     * @return The meta type of this type.
     */
    MetaType getMetaType();

    /**
     * Returns the name of this type.
     *
     * @return The name of this type.
     */
    String getName();

    /**
     * Returns the namespace of this type.
     *
     * @return The namespace of this type.
     */
    String getNamespace();

    /**
     * Returns the fully-qualified name of this type.
     *
     * @return The fully-qualified name of this type.
     */
    String getFullyQualifiedName();

    /**
     * Returns the Java type associated with this type.
     *
     * @return The Java type associated with this type.
     */
    Class<?> getJavaType();
}
