/*
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

import com.sdl.odata.api.edm.model.PropertyRef;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.PropertyRef}.
 *
 */
public final class PropertyRefImpl implements PropertyRef {

    private final String path;
    private final String alias;

    public PropertyRefImpl(String path, String alias) {
        this.path = path;
        this.alias = alias;
    }

    public PropertyRefImpl(String path) {
        this(path, null);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
