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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.AbstractType;
import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.EntityDataModel}.
 *
 */
public final class EntityDataModelImpl implements EntityDataModel {

    private static final Map<String, Type> NAME_TO_STANDARD_TYPE;

    static {
        Map<String, Type> nameToStandardTypeBuilder = new LinkedHashMap<>();

        for (PrimitiveType primitiveType : PrimitiveType.values()) {
            nameToStandardTypeBuilder.put(primitiveType.getFullyQualifiedName(), primitiveType);
        }

        for (AbstractType abstractType : AbstractType.values()) {
            nameToStandardTypeBuilder.put(abstractType.getFullyQualifiedName(), abstractType);
        }

        NAME_TO_STANDARD_TYPE = Collections.unmodifiableMap(nameToStandardTypeBuilder);
    }

    private static final TypeNameResolver PRIMITIVE_TYPE_NAME_RESOLVER = new PrimitiveTypeNameResolver();

    private final EntityContainer entityContainer;

    private final Map<String, Schema> schemasByNamespace;
    private final Map<String, Schema> schemasByAlias;

    public EntityDataModelImpl(EntityContainer entityContainer, Iterable<Schema> schemas) {
        this.entityContainer = entityContainer;

        Map<String, Schema> schemasByNamespaceBuilder = new LinkedHashMap<>();
        Map<String, Schema> schemasByAliasBuilder = new LinkedHashMap<>();

        for (Schema schema : schemas) {
            schemasByNamespaceBuilder.put(schema.getNamespace(), schema);

            String alias = schema.getAlias();
            if (!isNullOrEmpty(alias)) {
                schemasByAliasBuilder.put(alias, schema);
            }
        }

        this.schemasByNamespace = Collections.unmodifiableMap(schemasByNamespaceBuilder);
        this.schemasByAlias = Collections.unmodifiableMap(schemasByAliasBuilder);
    }

    @Override
    public EntityContainer getEntityContainer() {
        return entityContainer;
    }

    @Override
    public List<Schema> getSchemas() {
        return new ArrayList<>(schemasByNamespace.values());
    }

    @Override
    public Schema getSchema(String namespaceOrAlias) {
        Schema schema = schemasByNamespace.get(namespaceOrAlias);
        if (schema == null) {
            schema = schemasByAlias.get(namespaceOrAlias);
        }
        return schema;
    }

    @Override
    public Type getType(String fullyQualifiedTypeName) {
        // Check if it is one of the standard types
        Type standardType = NAME_TO_STANDARD_TYPE.get(fullyQualifiedTypeName);
        if (standardType != null) {
            return standardType;
        }

        int i = fullyQualifiedTypeName.lastIndexOf('.');
        if (i < 0) {
            throw new IllegalArgumentException(
                    "Name must be a fully-qualified type name, prefixed with a namespace or schema alias: "
                            + fullyQualifiedTypeName);
        }

        String namespaceOrAlias = fullyQualifiedTypeName.substring(0, i);
        String simpleName = fullyQualifiedTypeName.substring(i + 1);

        Schema schema = getSchema(namespaceOrAlias);
        return schema != null ? schema.getType(simpleName) : null;
    }

    @Override
    public Type getType(Class<?> javaType) {
        // First check if the Java type matches an OData primitive type
        String primitiveTypeName = PRIMITIVE_TYPE_NAME_RESOLVER.resolveTypeName(javaType);
        if (!isNullOrEmpty(primitiveTypeName)) {
            return PrimitiveType.forName(primitiveTypeName);
        }

        for (Schema schema : schemasByNamespace.values()) {
            Type type = schema.getType(javaType);
            if (type != null) {
                return type;
            }
        }

        return null;
    }
}
