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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.FunctionImport;
import com.sdl.odata.api.edm.model.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.EntityContainer}.
 *
 */
public final class EntityContainerImpl implements EntityContainer {
    /**
     * Entity Container Builder.
     */
    public static final class Builder {
        private String name;
        private String namespace;
        private String baseEntityContainerName;
        private final Map<String, EntitySet> entitySetsBuilder = new LinkedHashMap<>();
        private final Map<String, Singleton> singletonsBuilder = new LinkedHashMap<>();
        private final Map<String, FunctionImport> functionImportsBuilder = new LinkedHashMap<>();
        private final Map<String, ActionImport> actionImportsBuilder = new LinkedHashMap<>();

        public Builder setName(String builderName) {
            this.name = builderName;
            return this;
        }

        public Builder setNamespace(String builderNamespace) {
            this.namespace = builderNamespace;
            return this;
        }

        public Builder setBaseEntityContainerName(String containerName) {
            this.baseEntityContainerName = containerName;
            return this;
        }

        public Builder addEntitySet(EntitySet entitySet) {
            this.entitySetsBuilder.put(entitySet.getName(), entitySet);
            return this;
        }

        public Builder addEntitySets(Iterable<EntitySet> entitySets) {
            for (EntitySet entitySet : entitySets) {
                addEntitySet(entitySet);
            }
            return this;
        }

        public Builder addSingleton(Singleton singleton) {
            this.singletonsBuilder.put(singleton.getName(), singleton);
            return this;
        }

        public Builder addSingletons(Iterable<Singleton> singletons) {
            for (Singleton singleton : singletons) {
                addSingleton(singleton);
            }
            return this;
        }

        public Builder addFunctionImport(FunctionImport functionImport) {
            this.functionImportsBuilder.put(functionImport.getName(), functionImport);
            return this;
        }

        public Builder addFunctionImports(Iterable<FunctionImport> functionImports) {
            for (FunctionImport functionImport : functionImports) {
                addFunctionImport(functionImport);
            }
            return this;
        }

        public Builder addActionImport(ActionImport actionImport) {
            this.actionImportsBuilder.put(actionImport.getName(), actionImport);
            return this;
        }

        public Builder addActionImports(Iterable<ActionImport> actionImports) {
            for (ActionImport actionImport : actionImports) {
                addActionImport(actionImport);
            }
            return this;
        }

        public EntityContainerImpl build() {
            return new EntityContainerImpl(this);
        }
    }

    private final String name;
    private final String namespace;
    private final String baseEntityContainerName;
    private final Map<String, EntitySet> entitySets;
    private final Map<String, Singleton> singletons;
    private final Map<String, FunctionImport> functionImports;
    private final Map<String, ActionImport> actionImports;

    private EntityContainerImpl(Builder builder) {
        this.name = builder.name;
        this.namespace = builder.namespace;
        this.baseEntityContainerName = builder.baseEntityContainerName;
        this.entitySets = Collections.unmodifiableMap(builder.entitySetsBuilder);
        this.singletons = Collections.unmodifiableMap(builder.singletonsBuilder);
        this.functionImports = Collections.unmodifiableMap(builder.functionImportsBuilder);
        this.actionImports = Collections.unmodifiableMap(builder.actionImportsBuilder);
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
    public String getBaseEntityContainerName() {
        return baseEntityContainerName;
    }

    @Override
    public List<EntitySet> getEntitySets() {
        return new ArrayList<>(entitySets.values());
    }

    @Override
    public EntitySet getEntitySet(String entitySetName) {
        return entitySets.get(entitySetName);
    }

    @Override
    public List<Singleton> getSingletons() {
        return new ArrayList<>(singletons.values());
    }

    @Override
    public Singleton getSingleton(String singletonName) {
        return singletons.get(singletonName);
    }

    @Override
    public List<ActionImport> getActionImports() {
        return new ArrayList<>(actionImports.values());
    }

    @Override
    public ActionImport getActionImport(String actionImportName) {
        return actionImports.get(actionImportName);
    }

    @Override
    public List<FunctionImport> getFunctionImports() {
        return new ArrayList<>(functionImports.values());
    }

    @Override
    public FunctionImport getFunctionImport(String functionImportName) {
        return functionImports.get(functionImportName);
    }

    @Override
    public String toString() {
        return name;
    }
}
