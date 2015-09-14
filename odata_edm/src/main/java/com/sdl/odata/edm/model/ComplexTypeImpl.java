/**
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

import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.MetaType;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.ComplexType}.
 *
 */
public final class ComplexTypeImpl extends StructuredTypeImpl implements ComplexType {
    /**
     * The Complex Type Builder.
     */
    public static final class Builder extends StructuredTypeImpl.Builder<Builder> {

        public ComplexTypeImpl build() {
            return new ComplexTypeImpl(this);
        }
    }

    private ComplexTypeImpl(Builder builder) {
        super(builder);
    }

    @Override
    public MetaType getMetaType() {
        return MetaType.COMPLEX;
    }
}
