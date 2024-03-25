/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.edm.model.Parameter;

import java.lang.reflect.Field;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.Parameter}.
 */
public final class ParameterImpl implements Parameter {

    private String name;
    private String type;
    private boolean nullable;
    private long maxLength;
    private long precision;
    private long scale;
    private long srid;
    private boolean unicode;
    private Field javaField;

    private ParameterImpl(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.nullable = builder.nullable;
        this.maxLength = builder.maxLength;
        this.precision = builder.precision;
        this.scale = builder.scale;
        this.srid = builder.srid;
        this.unicode = builder.unicode;
        this.javaField = builder.javaField;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isNullable() {
        return nullable;
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
        return unicode;
    }

    @Override
    public Field getJavaField() {
        return javaField;
    }

    /**
     * Builder class to build {@link ParameterImpl}.
     */
    public static final class Builder {

        private String name;
        private String type;
        private boolean nullable;
        private long maxLength;
        private long precision;
        private long scale;
        private long srid;
        private boolean unicode;
        private Field javaField;

        public Builder setName(String newName) {
            this.name = newName;
            return this;
        }

        public Builder setType(String newType) {
            this.type = newType;
            return this;
        }

        public Builder setNullable(boolean newNullable) {
            this.nullable = newNullable;
            return this;
        }

        public Builder setMaxLength(long newMaxLength) {
            this.maxLength = newMaxLength;
            return this;
        }

        public Builder setPrecision(long newPrecision) {
            this.precision = newPrecision;
            return this;
        }

        public Builder setScale(long newScale) {
            this.scale = newScale;
            return this;
        }

        public Builder setSRID(long newSrid) {
            this.srid = newSrid;
            return this;
        }

        public Builder setUnicode(boolean newUnicode) {
            this.unicode = newUnicode;
            return this;
        }

        public Builder setJavaField(Field newJavaField) {
            this.javaField = newJavaField;
            return this;
        }

        public ParameterImpl build() {
            return new ParameterImpl(this);
        }
    }
}
