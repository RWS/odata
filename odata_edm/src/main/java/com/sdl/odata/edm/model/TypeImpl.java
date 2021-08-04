/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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

import com.sdl.odata.api.edm.model.Type;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.Type}.
 *
 */
public abstract class TypeImpl implements Type {
    /**
     * Type Builder.
     * @param <B> builder
     */
    public abstract static class Builder<B extends Builder> {
        private final B self;

        private String name;
        private String namespace;
        private Class<?> javaType;

        protected Builder() {
            this.self = (B) this;
        }

        public B setName(String builderName) {
            this.name = builderName;
            return self;
        }

        public B setNamespace(String builderNamespace) {
            this.namespace = builderNamespace;
            return self;
        }

        public B setJavaType(Class<?> builderJavaType) {
            this.javaType = builderJavaType;
            return self;
        }
    }

    private final String name;
    private final String namespace;
    private final Class<?> javaType;

    protected TypeImpl(Builder builder) {
        this.name = builder.name;
        this.namespace = builder.namespace;
        this.javaType = builder.javaType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getFullyQualifiedName() {
        return getNamespace() + "." + getName();
    }

    @Override
    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}
