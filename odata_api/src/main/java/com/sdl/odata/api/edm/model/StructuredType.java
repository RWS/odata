/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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

import java.util.List;

/**
 * OData structured type. Entity types and complex types are structured types; they consist of a number of properties
 * and navigation properties.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 4.2
 *
 */
public interface StructuredType extends DerivableType {

    /**
     * Returns the structural properties ("normal" properties and navigation properties) of this structured type.
     *
     * @return The structural properties of this structured type.
     */
    List<StructuralProperty> getStructuralProperties();

    /**
     * Returns the structural property identified by the specified name.
     *
     * @param name The name of the property.
     * @return The structural property identified by the specified name or {@code null} if no property with the
     *      specified name exists in the structured type.
     */
    StructuralProperty getStructuralProperty(String name);

    /**
     * Returns {@code true} if this an open type, {@code false} otherwise.
     *
     * @return {@code true} if this an open type, {@code false} otherwise.
     */
    boolean isOpen();
}
