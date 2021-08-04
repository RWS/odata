/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.api.edm.annotations;

import com.sdl.odata.api.edm.model.Facets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this field is a property in a complex type or an entity type.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 6
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EdmProperty {

    /**
     * The name of the property. If not specified, the name of the field is used.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.1.1
     *
     * @return The name of the property.
     */
    String name() default "";

    /**
     * The fully-qualified or alias-qualified name of the OData type of this property.
     *
     * To specify that this property is a collection, use a name of the form "Collection(...)". For example, if the
     * property is a list of strings, use: "Collection(Edm.String)".
     *
     * If not specified, then the type of the property is determined automatically from the type of the field that this
     * annotation is used on. This is done as follows:
     * <ul>
     *     <li>If the field type is an array or a collection (it implements {@code java.util.Collection}), then the
     *     property type is a collection type.</li>
     *     <li>If the field type corresponds to one of the OData primitive types, then the relevant OData primitive
     *     type is used.</li>
     *     <li>Otherwise the class that represents the Java field type is inspected. It should have one of the
     *     annotations {@code EdmEntity}, {@code EdmComplex} or {@code EdmEnum}, which determines the corresponding
     *     OData type.</li>
     * </ul>
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.1.2
     *
     * @return The name of the OData type of this property.
     */
    String type() default "";

    /**
     * Specifies whether this property can be {@code null}. The default value is {@code true} as specified by OData,
     * but for properties of a Java primitive type the default value is {@code false}.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.1
     *
     * @return {@code true} when {@code null} values are allowed for this property, {@code false} otherwise.
     */
    boolean nullable() default true;

    /**
     * The default value of this property as a string.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.7
     *
     * @return The default value of this property as a string.
     */
    String defaultValue() default "";

    /**
     * The maximum length of this property. The following special values can be used:
     * <ul>
     *     <li>{@code Facets.MAX_LENGTH_UNSPECIFIED} to indicate that the maximum length is not specified.</li>
     *     <li>{@code Facets.MAX_LENGTH_MAX} to indicate that the maximum length is whatever the service supports.</li>
     * </ul>
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.2
     *
     * @return The maximum length of this property.
     */
    long maxLength() default Facets.MAX_LENGTH_UNSPECIFIED;

    /**
     * The precision of this property. The following special values can be used:
     * <ul>
     *     <li>{@code Facets.PRECISION_UNSPECIFIED} to indicate that the precision is not specified.</li>
     * </ul>
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.3
     *
     * @return The precision of this property.
     */
    long precision() default Facets.PRECISION_UNSPECIFIED;

    /**
     * The scale of this property. The following special values can be used:
     * <ul>
     *     <li>{@code Facets.SCALE_UNSPECIFIED} to indicate that the scale is not specified.</li>
     *     <li>{@code Facets.SCALE_VARIABLE} to indicate that the scale is variable.</li>
     * </ul>
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.4
     *
     * @return The scale of this property.
     */
    long scale() default Facets.SCALE_UNSPECIFIED;

    /**
     * The spatial reference system identifier of this property. The following special values can be used:
     * <ul>
     *     <li>{@code Facets.SRID_UNSPECIFIED} to indicate that the SRID is not specified.</li>
     *     <li>{@code Facets.SRID_VARIABLE} to indicate that the SRID is variable.</li>
     * </ul>
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.6
     *
     * @return The spatial reference system identifier of this property.
     */
    long srid() default Facets.SRID_UNSPECIFIED;

    /**
     * Indicates if the value of this property is Unicode encoded. If {@code false}, the value of this property
     * is ASCII encoded. This attribute is only relevant for properties of type {@code Edm.String}.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.5
     *
     * @return {@code true} if the value of this property is Unicode encoded, {@code false} of it is ASCII encoded.
     */
    boolean unicode() default true;
}
