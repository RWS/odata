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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.FunctionImport;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.FunctionImport}.
 */
public final class FunctionImportImpl implements FunctionImport {

    private String name;
    private Function function;
    private EntitySet entitySet;
    private boolean includeInServiceDocument;
    private Class<?> javaClass;

    private FunctionImportImpl(Builder builder) {
        this.name = builder.name;
        this.function = builder.function;
        this.entitySet = builder.entitySet;
        this.includeInServiceDocument = builder.includeInServiceDocument;
        this.javaClass = builder.javaClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Function getFunction() {
        return function;
    }

    @Override
    public EntitySet getEntitySet() {
        return entitySet;
    }

    @Override
    public boolean isIncludeInServiceDocument() {
        return includeInServiceDocument;
    }

    @Override
    public Class<?> getJavaClass() {
        return javaClass;
    }

    /**
     * Builder to build {@link FunctionImportImpl}.
     */
    public static final class Builder {

        private String name;
        private Function function;
        private EntitySet entitySet;
        private String functionName;
        private String entitySetName;
        private boolean includeInServiceDocument;
        private Class<?> javaClass;

        public Builder setName(String newName) {
            this.name = newName;
            return this;
        }

        public Builder setFunction(Function newFunction) {
            this.function = newFunction;
            return this;
        }

        public Builder setEntitySet(EntitySet newEntitySet) {
            this.entitySet = newEntitySet;
            return this;
        }

        public Builder setIncludeInServiceDocument(boolean newIncludeInServiceDocument) {
            this.includeInServiceDocument = newIncludeInServiceDocument;
            return this;
        }

        public String getFunctionName() {
            return functionName;
        }

        public Builder setFunctionName(String newFunctionName) {
            this.functionName = newFunctionName;
            return this;
        }

        public String getEntitySetName() {
            return entitySetName;
        }

        public Builder setEntitySetName(String newEntitySetName) {
            this.entitySetName = newEntitySetName;
            return this;
        }

        public Builder setJavaClass(Class<?> newJavaClass) {
            this.javaClass = newJavaClass;
            return this;
        }

        public FunctionImportImpl build() {
            return new FunctionImportImpl(this);
        }
    }
}
