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

/**
 * OData property facets.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2
 *
 */
public interface Facets {

    /**
     * Special value to indicate that {@code maxLength} is unspecified.
     */
    long MAX_LENGTH_UNSPECIFIED = Long.MIN_VALUE;

    /**
     * Special value to indicate that {@code maxLength} has the value "max".
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.2
     */
    long MAX_LENGTH_MAX = -1L;

    /**
     * Special value to indicate that {@code precision} is unspecified.
     */
    long PRECISION_UNSPECIFIED = Long.MIN_VALUE;

    /**
     * Special value to indicate that {@code scale} is unspecified.
     */
    long SCALE_UNSPECIFIED = Long.MIN_VALUE;

    /**
     * Special value to indicate that {@code scale} has the value "variable".
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.4
     */
    long SCALE_VARIABLE = -1L;

    /**
     * Special value to indicate that {@code srid} is unspecified.
     */
    long SRID_UNSPECIFIED = Long.MIN_VALUE;

    /**
     * Special value to indicate that {@code srid} has the value "variable".
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.6
     */
    long SRID_VARIABLE = -1L;

    /**
     * Returns the maximum length of the value of the property. This is only relevant for binary, string and stream
     * properties. The maximum length is a positive integer or one of the special values {@link #MAX_LENGTH_UNSPECIFIED}
     * or {@link #MAX_LENGTH_MAX}.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.2
     *
     * @return The maximum length of the value of the property.
     */
    long getMaxLength();

    /**
     * Returns the precision of the value of the property. This is only relevant for properties of the following
     * primitive types: {@code Edm.DateTimeOffset}, {@code Edm.Decimal}, {@code Edm.Duration}, {@code Edm.TimeOfDay}.
     *
     * For {@code Edm.Decimal} properties the precision is a positive integer or the special value
     * {@link #PRECISION_UNSPECIFIED}.
     *
     * For temporal properties the precision is an integer between zero and twelve or the special value
     * {@link #PRECISION_UNSPECIFIED}.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.3
     *
     * @return The precision of the value of the property.
     */
    long getPrecision();

    /**
     * Returns the scale of the value of the property. This is only relevant for properties of the primitive type
     * {@code Edm.Decimal}. The scale is a non-negative integer that is less than or equal to the precision, or one of
     * the special values {@link #SCALE_UNSPECIFIED} or {@link #SCALE_VARIABLE}.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 6.2.4
     *
     * @return The scale of the value of the property.
     */
    long getScale();

    /**
     * Returns the spatial reference system identifier of the property. This is only relevant for geography and
     * geometry properties. The SRID is a non-negative integer or one of the special values {@link #SRID_UNSPECIFIED}
     * or {@link #SRID_VARIABLE}.
     *
     * @return The spatial reference system identifier of the property.
     */
    long getSRID();

    /**
     * Returns {@code true} if the property is encoded with Unicode, {@code false} if it is encoded with ASCII. This
     * is only relevant for {@code Edm.String} properties.
     *
     * @return {@code true} if the property is encoded with Unicode, {@code false} if it is encoded with ASCII.
     */
    boolean isUnicode();
}
