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
 * OData schema.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 5
 *
 */
public interface Schema {

    /**
     * Returns the namespace of the schema. The namespace uniquely identifies the schema within an entity data model.
     *
     * @return The namespace of the schema.
     */
    String getNamespace();

    /**
     * Returns the alias of the schema.
     *
     * @return The alias of the schema or {@code null} if this schema does not have an alias.
     */
    String getAlias();

    /**
     * Returns the types defined in this schema.
     *
     * @return The types defined in this schema.
     */
    List<Type> getTypes();

    /**
     * Returns a type specified by its simple type name (not its fully-qualified name).
     *
     * @param simpleTypeName The simple type name (without namespace or alias) of the type.
     * @return The type identified by the specified simple type name or {@code null} if no matching type exists
     *      in this schema.
     */
    Type getType(String simpleTypeName);

    /**
     * Returns the type that corresponds to the specified Java class.
     *
     * @param javaType The Java class to find the corresponding type for.
     * @return The type that corresponds to the specified Java class or {@code null} if no corresponding type exists
     *      in this schema.
     */
    Type getType(Class<?> javaType);

    /**
     * Returns the Actions defined in this schema.
     *
     * @return The Actions defined in this schema.
     */
    List<Action> getActions();

    /**
     * Return the Action specified by the given action name.
     *
     * @param actionName The name of Action
     * @return The Action specified by the given action name.
     */
    Action getAction(String actionName);

    /**
     * Returns the functions defined in this schema.
     *
     * @return The functions defined in this schema.
     */
    List<Function> getFunctions();

    /**
     * Returns a function specified by its name.
     *
     * @param functionName The function name.
     * @return The function identified by it's name or {@code null} if no matching function exists in this schema.
     */
    Function getFunction(String functionName);
}
