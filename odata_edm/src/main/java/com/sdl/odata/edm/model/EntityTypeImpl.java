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

import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.Key;
import com.sdl.odata.api.edm.model.MetaType;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.EntityType}.
 *
 */
public final class EntityTypeImpl extends StructuredTypeImpl implements EntityType {
    /**
     * The Entity Type Impl Builder.
     */
    public static final class Builder extends StructuredTypeImpl.Builder<Builder> {
        private Key key;
        private boolean isReadOnly;
        private boolean hasStream;

        public Builder setKey(Key builderKey) {
            this.key = builderKey;
            return this;
        }

        public Builder setIsReadOnly(boolean readOnly) {
            this.isReadOnly = readOnly;
            return this;
        }

        public Builder setHasStream(boolean hasStreamForEntityType) {
            this.hasStream = hasStreamForEntityType;
            return this;
        }

        public EntityTypeImpl build() {
            return new EntityTypeImpl(this);
        }
    }

    private final Key key;
    private final boolean isReadOnly;
    private final boolean hasStream;

    private EntityTypeImpl(Builder builder) {
        super(builder);
        this.key = builder.key;
        this.isReadOnly = builder.isReadOnly;
        this.hasStream = builder.hasStream;
    }

    @Override
    public MetaType getMetaType() {
        return MetaType.ENTITY;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public boolean hasStream() {
        return hasStream;
    }
}
