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
package com.sdl.odata.api.edm.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * OData structural property.
 */
public interface StructuralProperty {

    /**
     * Returns the name of the property.
     *
     * @return The name of the property.
     */
    String getName();

    /**
     * Returns the fully-qualified name of the type of the property. If the property is a collection
     * ({@link #isCollection()} returns {@code true}), the name will have the form "Collection(...)",
     * for example "Collection(Edm.String)".
     *
     * @return The fully-qualified name of the type of the property.
     */
    String getTypeName();

    /**
     * If the property is a collection ({@link #isCollection()} returns {@code true}), returns the fully-qualified
     * name of the element type of the collection. Otherwise (if the property is not a collection), returns
     * {@code null}.
     *
     * @return The fully-qualified name of the element type of the collection or {@code null} if the property
     *      is not a collection.
     */
    String getElementTypeName();

    /**
     * Returns {@code true} if the property is a collection, {@code false} otherwise.
     *
     * @return {@code true} if the property is a collection, {@code false} otherwise.
     */
    boolean isCollection();

    /**
     * Returns {@code true} if empty values are allowed for this property, {@code false} otherwise.
     *
     * @return {@code true} if empty values are allowed for this property, {@code false} otherwise.
     */
    boolean isNullable();

    /**
     * Returns the Java field which is associated with this property.
     *
     * @return The Java field which is associated with this property.
     */
    Field getJavaField();

    /**
     *
     *
     * @return The Java property descriptor.
     */
    PropertyDescriptor getPropertyDescriptor();
}
