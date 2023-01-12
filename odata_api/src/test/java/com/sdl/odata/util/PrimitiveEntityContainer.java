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
package com.sdl.odata.util;

import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.FunctionImport;
import com.sdl.odata.api.edm.model.Singleton;

import java.util.List;

public class PrimitiveEntityContainer implements EntityContainer {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getBaseEntityContainerName() {
        return null;
    }

    @Override
    public List<EntitySet> getEntitySets() {
        return null;
    }

    @Override
    public EntitySet getEntitySet(String name) {
        return null;
    }

    @Override
    public List<Singleton> getSingletons() {
        return null;
    }

    @Override
    public Singleton getSingleton(String name) {
        return null;
    }

    @Override
    public List<ActionImport> getActionImports() {
        return null;
    }

    @Override
    public ActionImport getActionImport(String name) {
        return null;
    }

    @Override
    public List<FunctionImport> getFunctionImports() {
        return null;
    }

    @Override
    public FunctionImport getFunctionImport(String functionImportName) {
        return null;
    }
}
