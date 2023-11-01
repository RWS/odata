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
package com.sdl.odata.util;

import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.Type;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;

/**
 * Dummy collection type for testing.
 */
public class PrimitiveEntityDataModel extends AbstractCollection implements EntityDataModel  {
    @Override
    public EntityContainer getEntityContainer() {
        return new PrimitiveEntityContainer();
    }

    @Override
    public List<Schema> getSchemas() {
        return null;
    }

    @Override
    public Schema getSchema(String namespaceOrAlias) {
        return null;
    }

    @Override
    public Type getType(String fullyQualifiedTypeName) {
        return null;
    }

    @Override
    public Type getType(Class<?> javaType) {
        return null;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
