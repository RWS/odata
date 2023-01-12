/*
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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.Schema}.
 *
 */
public final class SchemaImpl implements Schema {
    /**
     * Schema Builder.
     */
    public static final class Builder {
        private String namespace;
        private String alias;

        private final Map<String, Type> typesBuilder = new LinkedHashMap<>();
        private final Map<String, Function> functionsBuilder = new LinkedHashMap<>();
        private final Map<String, Action> actionsBuilder = new LinkedHashMap<>();

        public Builder setNamespace(String builderNamespace) {
            this.namespace = builderNamespace;
            return this;
        }

        public Builder setAlias(String builderAlias) {
            this.alias = builderAlias;
            return this;
        }

        public Builder addType(Type type) {
            this.typesBuilder.put(type.getName(), type);
            return this;
        }

        public Builder addTypes(Iterable<Type> types) {
            for (Type type : types) {
                addType(type);
            }
            return this;
        }

        public Builder addFunction(Function function) {
            this.functionsBuilder.put(function.getName(), function);
            return this;
        }

        public Builder addFunctions(Iterable<Function> functions) {
            for (Function function : functions) {
                addFunction(function);
            }
            return this;
        }

        public Builder addAction(Action action) {
            this.actionsBuilder.put(action.getName(), action);
            return this;
        }

        public Builder addActions(Iterable<Action> actions) {
            for (Action action : actions) {
                addAction(action);
            }
            return this;
        }

        public SchemaImpl build() {
            return new SchemaImpl(this);
        }
    }

    private final String namespace;
    private final String alias;

    private final Map<String, Type> types;
    private final Map<String, Function> functions;
    private final Map<String, Action> actions;

    private SchemaImpl(Builder builder) {
        this.namespace = builder.namespace;
        this.alias = builder.alias;
        this.types = Collections.unmodifiableMap(builder.typesBuilder);
        this.functions = Collections.unmodifiableMap(builder.functionsBuilder);
        this.actions = Collections.unmodifiableMap(builder.actionsBuilder);
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public List<Type> getTypes() {
        return new ArrayList<>(types.values());
    }

    @Override
    public Type getType(String simpleTypeName) {
        return types.get(simpleTypeName);
    }

    @Override
    public Type getType(Class<?> javaType) {
        // NOTE: Only entity types, complex types and enum types defined in this schema are searched here.
        // Primitive types are not handled here. Type definitions are not searched; they map to their corresponding
        // underlying primitive type, and there can potentially be multiple type definitions with the same underlying
        // primitive type, therefore it is not useful to find a type definition corresponding to a particular primitive
        // Java type.

        for (Type type : types.values()) {
            if (javaType.equals(type.getJavaType())) {
                return type;
            }
        }

        return null;
    }

    @Override
    public List<Action> getActions() {
        return new ArrayList<>(actions.values());
    }

    @Override
    public Action getAction(String actionName) {
        return actions.get(actionName);
    }

    @Override
    public List<Function> getFunctions() {
        return new ArrayList<>(functions.values());
    }

    @Override
    public Function getFunction(String functionName) {
        return functions.get(functionName);
    }

    @Override
    public String toString() {
        return namespace;
    }
}
