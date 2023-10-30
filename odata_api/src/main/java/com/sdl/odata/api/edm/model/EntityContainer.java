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

import java.util.List;

/**
 * OData entity container.
 * <p>
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13
 *
 */
public interface EntityContainer {

    /**
     * Returns the name of the entity container.
     *
     * @return The name of the entity container.
     */
    String getName();

    /**
     * Returns the namespace of the schema under which this entity container is placed in the metadata document.
     *
     * @return The namespace of the schema under which this entity container is placed in the metadata document.
     */
    String getNamespace();

    /**
     * Returns the name of the base entity container or {@code null} if this entity container does not have a base
     * entity container.
     *
     * @return The name of the base entity container or {@code null} if this entity container does not have a base
     * entity container.
     */
    String getBaseEntityContainerName();

    /**
     * Returns the entity sets in this entity container.
     *
     * @return The entity sets in this entity container.
     */
    List<EntitySet> getEntitySets();

    /**
     * Returns the entity set with the specified name or {@code null} if there is no entity set with the specified name.
     *
     * @param name The name of the entity set.
     * @return The entity set with the specified name or {@code null} if there is no entity set with the specified name.
     */
    EntitySet getEntitySet(String name);

    /**
     * Returns the singletons in this entity container.
     *
     * @return The singletons in this entity container.
     */
    List<Singleton> getSingletons();

    /**
     * Returns the singleton with the specified name or {@code null} if there is no singleton with the specified name.
     *
     * @param name The name of the singleton.
     * @return The singleton with the specified name or {@code null} if there is no singleton with the specified name.
     */
    Singleton getSingleton(String name);

    /**
     * Returns the ActionImports in this entity container.
     *
     * @return The ActionImports in this entity container.
     */
    List<ActionImport> getActionImports();


    /**
     * Returns the action import with the specified name or {@code null} if there is no action import with the
     * specified name.
     *
     * @param name The name of the action import.
     * @return The action import with the specified name or {@code null} if there is no action import with the
     * specified name.
     */
    ActionImport getActionImport(String name);

    /**
     * Returns the function imports in this entity container.
     *
     * @return The function imports in this entity container.
     */
    List<FunctionImport> getFunctionImports();

    /**
     * Returns the function import with the specified name or {@code null} if there is no function import with the
     * specified name.
     *
     * @param functionImportName The name of the function import.
     * @return The function import with the specified name or {@code null} if there is no function import with the
     * specified name.
     */
    FunctionImport getFunctionImport(String functionImportName);
}
