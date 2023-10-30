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

import com.sdl.odata.api.edm.model.Facets;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.TypeDefinition;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.TypeDefinition}.
 *
 */
public final class TypeDefinitionImpl extends TypeImpl implements TypeDefinition {
    /**
     * Type Definition Builder.
     */
    public static final class Builder extends TypeImpl.Builder<Builder> {
        private PrimitiveType underlyingType;

        private long maxLength = Facets.MAX_LENGTH_UNSPECIFIED;
        private long precision = Facets.PRECISION_UNSPECIFIED;
        private long scale = Facets.SCALE_UNSPECIFIED;
        private long srid = Facets.SRID_UNSPECIFIED;
        private boolean isUnicode = true;

        public Builder setUnderlyingType(PrimitiveType builderUnderlyingType) {
            this.underlyingType = builderUnderlyingType;
            return this;
        }

        public Builder setMaxLength(long builderMaxLength) {
            this.maxLength = builderMaxLength;
            return this;
        }

        public Builder setPrecision(long builderPrecision) {
            this.precision = builderPrecision;
            return this;
        }

        public Builder setScale(long builderScale) {
            this.scale = builderScale;
            return this;
        }

        public Builder setSRID(long builderSRID) {
            this.srid = builderSRID;
            return this;
        }

        public Builder setIsUnicode(boolean isUnicodeForType) {
            this.isUnicode = isUnicodeForType;
            return this;
        }

        public TypeDefinitionImpl build() {
            return new TypeDefinitionImpl(this);
        }
    }

    private final PrimitiveType underlyingType;

    private final long maxLength;
    private final long precision;
    private final long scale;
    private final long srid;
    private final boolean isUnicode;

    private TypeDefinitionImpl(Builder builder) {
        super(builder.setJavaType(builder.underlyingType.getJavaType()));
        this.underlyingType = builder.underlyingType;

        this.maxLength = builder.maxLength;
        this.precision = builder.precision;
        this.scale = builder.scale;
        this.srid = builder.srid;
        this.isUnicode = builder.isUnicode;
    }

    @Override
    public MetaType getMetaType() {
        return MetaType.TYPE_DEFINITION;
    }

    @Override
    public PrimitiveType getUnderlyingType() {
        return underlyingType;
    }

    @Override
    public long getMaxLength() {
        return maxLength;
    }

    @Override
    public long getPrecision() {
        return precision;
    }

    @Override
    public long getScale() {
        return scale;
    }

    @Override
    public long getSRID() {
        return srid;
    }

    @Override
    public boolean isUnicode() {
        return isUnicode;
    }
}
