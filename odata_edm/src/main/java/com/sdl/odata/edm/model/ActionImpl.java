/**
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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.Parameter;

import java.util.Set;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.Action}.
 */
public final class ActionImpl implements Action {
    private String name;
    private String namespace;
    private boolean bound;
    private String entitySetPath;
    private String returnType;
    private Set<Parameter> parameters;
    private Class<?> javaClass;

    private ActionImpl(Builder builder) {
        this.name = builder.name;
        this.namespace = builder.namespace;
        this.bound = builder.bound;
        this.entitySetPath = builder.entitySetPath;
        this.returnType = builder.returnType;
        this.parameters = builder.parameters;
        this.javaClass = builder.javaClass;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public boolean isBound() {
        return this.bound;
    }

    @Override
    public String getEntitySetPath() {
        return this.entitySetPath;
    }

    @Override
    public String getReturnType() {
        return this.returnType;
    }

    @Override
    public Set<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public Class<?> getJavaClass() {
        return this.javaClass;
    }


    /**
     * Builder to build {@link ActionImpl}.
     */
    public static final class Builder {

        private String name;
        private String namespace;
        private boolean bound;
        private String entitySetPath;
        private String returnType;
        private Set<Parameter> parameters;
        private Class<?> javaClass;

        public Builder setName(String newName) {
            this.name = newName;
            return this;
        }

        public Builder setNamespace(String newNamespace) {
            this.namespace = newNamespace;
            return this;
        }

        public Builder setBound(boolean newBound) {
            this.bound = newBound;
            return this;
        }

        public Builder setEntitySetPath(String newEntitySetPath) {
            this.entitySetPath = newEntitySetPath;
            return this;
        }

        public Builder setReturnType(String newReturnType) {
            this.returnType = newReturnType;
            return this;
        }

        public Builder setParameters(Set<Parameter> newParameters) {
            this.parameters = newParameters;
            return this;
        }

        public Builder setJavaClass(Class<?> newJavaClass) {
            this.javaClass = newJavaClass;
            return this;
        }

        public ActionImpl build() {
            return new ActionImpl(this);
        }
    }

}
