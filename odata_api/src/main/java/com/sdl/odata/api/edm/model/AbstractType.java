/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * OData abstract type.
 * <p>
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 4.5
 *
 */
public enum AbstractType implements Type {
    /**
     * Primitive Type.
     */
    PRIMITIVE_TYPE("PrimitiveType"),
    /**
     * Complex Type.
     */
    COMPLEX_TYPE("ComplexType"),
    /**
     * Entity Type.
     */
    ENTITY_TYPE("EntityType");

    private final String name;

    AbstractType(String name) {
        this.name = name;
    }

    /**
     * Returns the {@code AbstractType} for the specified name.
     *
     * @param name The name of the {@code AbstractType} to find.
     * @return The {@code AbstractType} for the specified name.
     * @throws java.lang.IllegalArgumentException If the name does not match an {@code AbstractType}.
     */
    public static AbstractType forName(String name) {
        String simpleName;
        if (name.startsWith(EntityDataModel.EDM_NAMESPACE)) {
            simpleName = name.substring(EntityDataModel.EDM_NAMESPACE.length() + 1);
        } else {
            simpleName = name;
        }

        for (AbstractType abstractType : AbstractType.values()) {
            if (abstractType.name.equals(simpleName)) {
                return abstractType;
            }
        }

        throw new IllegalArgumentException("Invalid abstract type name: " + name);
    }

    @Override
    public MetaType getMetaType() {
        return MetaType.ABSTRACT;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return EntityDataModel.EDM_NAMESPACE;
    }

    @Override
    public String getFullyQualifiedName() {
        return getNamespace() + "." + getName();
    }

    @Override
    public Class<?> getJavaType() {
        return null;
    }

    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}
