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

import com.sdl.odata.api.edm.model.Facets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the Return Type of Action or Function.
 *
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793972">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.3</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmReturnType {

    /**
     * The Type attribute specifies the type of the result returned by the function or action.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793973">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.3.1</a>
     *
     * @return The name of the OData type.
     */
    String type();

    /**
     * Specifies whether this property can be {@code null}. The default value is {@code true} as specified by OData.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793974">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.3.2</a>
     *
     * @return {@code true} if return data can be null
     */
    boolean nullable() default true;

    /**
     * The maximum length of this return type. The following special values can be used:
     * <ul>
     * <li>{@code Facets.MAX_LENGTH_UNSPECIFIED} to indicate that the maximum length is not specified.</li>
     * <li>{@code Facets.MAX_LENGTH_MAX} to indicate that the maximum length is whatever the service supports.</li>
     * </ul>
     * <p>
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793912">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.2</a>
     *
     * @return The maximum length of this return type.
     */
    long maxLength() default Facets.MAX_LENGTH_UNSPECIFIED;

    /**
     * The precision of this return type. The following special values can be used:
     * <ul>
     * <li>{@code Facets.PRECISION_UNSPECIFIED} to indicate that the precision is not specified.</li>
     * </ul>
     * <p>
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793913">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.3</a>
     *
     * @return The precision of this return type.
     */
    long precision() default Facets.PRECISION_UNSPECIFIED;

    /**
     * The scale of this return type. The following special values can be used:
     * <ul>
     * <li>{@code Facets.SCALE_UNSPECIFIED} to indicate that the scale is not specified.</li>
     * <li>{@code Facets.SCALE_VARIABLE} to indicate that the scale is variable.</li>
     * </ul>
     * <p>
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793914">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.4</a>
     *
     * @return The scale of this return type.
     */
    long scale() default Facets.SCALE_UNSPECIFIED;

    /**
     * The spatial reference system identifier of this return type. The following special values can be used:
     * <ul>
     * <li>{@code Facets.SRID_UNSPECIFIED} to indicate that the SRID is not specified.</li>
     * <li>{@code Facets.SRID_VARIABLE} to indicate that the SRID is variable.</li>
     * </ul>
     * <p>
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793916">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.6</a>
     *
     * @return The spatial reference system identifier of this return type.
     */
    long srid() default Facets.SRID_UNSPECIFIED;

}
