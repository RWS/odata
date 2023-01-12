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
import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.api.edm.model.EntitySet;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.ActionImport}.
 */
public final class ActionImportImpl implements ActionImport {
    private String name;
    private Action action;
    private EntitySet entitySet;
    private Class<?> javaClass;

    private ActionImportImpl(Builder builder) {
        this.name = builder.name;
        this.action = builder.action;
        this.entitySet = builder.entitySet;
        this.javaClass = builder.javaClass;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public EntitySet getEntitySet() {
        return this.entitySet;
    }

    @Override
    public Class<?> getJavaClass() {
        return this.javaClass;
    }

    /**
     * Builder to build {@link FunctionImportImpl}.
     */
    public static final class Builder {

        private String name;
        private Action action;
        private String actionName;
        private EntitySet entitySet;
        private String entitySetName;
        private Class<?> javaClass;

        public Builder setName(String newName) {
            this.name = newName;
            return this;
        }

        public Builder setAction(Action newAction) {
            this.action = newAction;
            return this;
        }

        public Builder setActionName(String newActionName) {
            this.actionName = newActionName;
            return this;
        }

        public String getActionName() {
            return this.actionName;
        }

        public Builder setEntitySet(EntitySet newEntitySet) {
            this.entitySet = newEntitySet;
            return this;
        }

        public Builder setEntitySetName(String newEntitySetName) {
            this.entitySetName = newEntitySetName;
            return this;
        }

        public Builder setJavaClass(Class<?> newJavaClass) {
            this.javaClass = newJavaClass;
            return this;
        }

        public String getEntitySetName() {
            return this.entitySetName;
        }

        public ActionImportImpl build() {
            return new ActionImportImpl(this);
        }
    }

}
