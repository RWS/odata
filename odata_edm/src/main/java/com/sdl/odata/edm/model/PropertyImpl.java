/*
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
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
import com.sdl.odata.api.edm.model.Property;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.Property}.
 *
 */
public final class PropertyImpl extends StructuralPropertyImpl implements Property {
    /**
     * Property Builder.
     */
    public static final class Builder extends StructuralPropertyImpl.Builder<Builder> {
        private String defaultValue;

        private long maxLength = Facets.MAX_LENGTH_UNSPECIFIED;
        private long precision = Facets.PRECISION_UNSPECIFIED;
        private long scale = Facets.SCALE_UNSPECIFIED;
        private long srid = Facets.SRID_UNSPECIFIED;
        private boolean isUnicode = true;

        public Builder setDefaultValue(String builderDefaultValue) {
            this.defaultValue = builderDefaultValue;
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

        public Builder setIsUnicode(boolean builderIsUnicode) {
            this.isUnicode = builderIsUnicode;
            return this;
        }

        public PropertyImpl build() {
            return new PropertyImpl(this);
        }
    }

    private final String defaultValue;

    private final long maxLength;
    private final long precision;
    private final long scale;
    private final long srid;
    private final boolean isUnicode;

    private PropertyImpl(Builder builder) {
        super(builder);
        this.defaultValue = builder.defaultValue;

        this.maxLength = builder.maxLength;
        this.precision = builder.precision;
        this.scale = builder.scale;
        this.srid = builder.srid;
        this.isUnicode = builder.isUnicode;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
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
