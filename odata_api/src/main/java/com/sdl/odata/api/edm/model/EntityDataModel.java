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
package com.sdl.odata.api.edm.model;

import java.util.List;

/**
 * An OData entity data model.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL)
 *
 */
public interface EntityDataModel {

    /**
     * The Edm Namespace.
     */
    String EDM_NAMESPACE = "Edm";

    /**
     * Returns the entity container of this entity data model.
     *
     * @return The entity container of this entity data model.
     */
    EntityContainer getEntityContainer();

    /**
     * Returns all schemas in this entity data model.
     *
     * @return All schemas in this entity data model.
     */
    List<Schema> getSchemas();

    /**
     * Returns the schema with the specified namespace or alias.
     *
     * @param namespaceOrAlias A namespace or alias that identifies a schema.
     * @return The schema with the specified namespace or alias or {@code null} if no matching schema was found.
     */
    Schema getSchema(String namespaceOrAlias);

    /**
     * Get a type by name. The name must be a fully-qualified name, prefixed by a namespace or a schema alias.
     *
     * @param fullyQualifiedTypeName The fully-qualified name of the type.
     * @return The type or {@code null} if no type with the specified name was found.
     */
    Type getType(String fullyQualifiedTypeName);

    /**
     * Get a type by its corresponding Java type.
     *
     * @param javaType The Java type.
     * @return The type or {@code null} if no type that corresponds with the Java type was found.
     */
    Type getType(Class<?> javaType);
}
