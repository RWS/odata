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
package com.sdl.odata.api.edm.model;

/**
 * OData structural property.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 6
 *
 */
public interface Property extends StructuralProperty, Facets {

    /**
     * Returns the default value of this property or {@code null} if this property has no default value.
     *
     * @return The default value of this property or {@code null} if this property has no default value.
     */
    String getDefaultValue();
}
