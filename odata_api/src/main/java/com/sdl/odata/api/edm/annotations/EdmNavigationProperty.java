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
package com.sdl.odata.api.edm.annotations;

import com.sdl.odata.api.edm.model.OnDeleteAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this field is a navigation property in a complex type or an entity type.
 * <p>
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 7
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EdmNavigationProperty {

    /**
     * The name of the navigation property. If not specified, the name of the field is used.
     * <p>
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.1.1
     *
     * @return The name of the navigation property.
     */
    String name() default "";

    /**
     * The fully-qualified or alias-qualified name of the OData type of this navigation property.
     * <p>
     * To specify that this navigation property is a collection, use a name of the form "Collection(...)". For example,
     * if the navigation property is a list of strings, use: "Collection(Edm.String)".
     * <p>
     * If not specified, then the type of the navigation property is determined automatically from the type of the
     * field that this annotation is used on. This is done as follows:
     * <ul>
     * <li>If the field type is an array or a collection (it implements {@code java.util.Collection}), then the
     * navigation property type is a collection type.</li>
     * <li>If the field type corresponds to one of the OData primitive types, then the relevant OData primitive
     * type is used.</li>
     * <li>Otherwise the class that represents the Java field type is inspected. It should have one of the
     * annotations {@code EdmEntity}, {@code EdmComplex} or {@code EdmEnum}, which determines the corresponding
     * OData type.</li>
     * </ul>
     * <p>
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.1.2
     *
     * @return The name of the OData type of this property.
     */
    String type() default "";

    /**
     * Specifies whether this navigation property can be {@code null}. The default value is {@code true} as specified
     * by OData, but for properties of a Java primitive type the default value is {@code false}.
     * <p>
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.1.3
     *
     * @return {@code true} when {@code null} values are allowed for this property, {@code false} otherwise.
     */
    boolean nullable() default true;

    /**
     * For bidirectional relations, the name of the property in the target type that refers back to the type containing
     * this navigation property.
     *
     * @return The name of the property in the target type that refers back to the type containing this navigation
     * property.
     */
    String partner() default "";

    /**
     * Indicates if this navigation property is a containment navigation property.
     * <p>
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.1.5
     *
     * @return {@code true} if this navigation property is a containment navigation property, {@code false} otherwise.
     */
    boolean containsTarget() default false;

    EdmConstraint[] constraints() default { };

    OnDeleteAction[] onDelete() default { };
}
