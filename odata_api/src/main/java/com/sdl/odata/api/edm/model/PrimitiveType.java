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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OData primitive type.
 * <p>
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 4.4
 *
 */
public enum PrimitiveType implements Type {
    /**
     * Binary.
     */
    BINARY("Binary", byte[].class),
    /**
     * Boolean.
     */
    BOOLEAN("Boolean", boolean.class),
    /**
     * Byte.
     */
    BYTE("Byte", byte.class),
    /**
     * Date.
     */
    DATE("Date", LocalDate.class),
    /**
     * Date Time Offset.
     */
    DATE_TIME_OFFSET("DateTimeOffset", ZonedDateTime.class),
    /**
     * Date Time Proper Offset (Zoned can have named zones).
     */
    DATE_TIME_OFFSET2("DateTimeOffset2", OffsetDateTime.class),
    /**
     * Duration.
     */
    DURATION("Duration", Period.class),
    /**
     * Time of the day.
     */
    TIME_OF_DAY("TimeOfDay", LocalTime.class),
    /**
     * Decimal.
     */
    DECIMAL("Decimal", BigDecimal.class),
    /**
     * Double.
     */
    DOUBLE("Double", double.class),
    /**
     * Single.
     */
    SINGLE("Single", float.class),
    /**
     * Geography Point.
     */
    GEOGRAPHY_POINT("GeographyPoint", null),
    /**
     * Geography Line String.
     */
    GEOGRAPHY_LINE_STRING("GeographyLineString", null),
    /**
     * Geography Polygon.
     */
    GEOGRAPHY_POLYGON("GeographyPolygon", null),
    /**
     * Geography Multi Point.
     */
    GEOGRAPHY_MULTI_POINT("GeographyMultiPoint", null),
    /**
     * Geography Multi Point.
     */
    GEOGRAPHY_MULTI_LINE_STRING("GeographyMultiLineString", null),
    /**
     * Geography Multi Polygon.
     */
    GEOGRAPHY_MULTI_POLYGON("GeographyMultiPolygon", null),
    /**
     * Geography Collection.
     */
    GEOGRAPHY_COLLECTION("GeographyCollection", null),
    /**
     * Geometry Point.
     */
    GEOMETRY_POINT("GeometryPoint", null),
    /**
     * Geometry Line String.
     */
    GEOMETRY_LINE_STRING("GeometryLineString", null),
    /**
     * Geometry Polygon.
     */
    GEOMETRY_POLYGON("GeometryPolygon", null),
    /**
     * Geometry Mukti Point.
     */
    GEOMETRY_MULTI_POINT("GeometryMultiPoint", null),
    /**
     * Geometry Multi Line String.
     */
    GEOMETRY_MULTI_LINE_STRING("GeometryMultiLineString", null),
    /**
     * Geometry Multi Polygon.
     */
    GEOMETRY_MULTI_POLYGON("GeometryMultiPolygon", null),
    /**
     * Geomtry Collection.
     */
    GEOMETRY_COLLECTION("GeometryCollection", null),
    /**
     * Guid.
     */
    GUID("Guid", UUID.class),
    /**
     * Int16.
     */
    INT16("Int16", short.class),
    /**
     * Int32.
     */
    INT32("Int32", int.class),
    /**
     * Int64.
     */
    INT64("Int64", long.class),
    /**
     * String.
     */
    STRING("String", String.class),
    /**
     * SByte.
     */
    SBYTE("SByte", byte.class);

    private static final Map<String, PrimitiveType> NAME_TO_PRIMITIVE_TYPE;

    static {
        Map<String, PrimitiveType> nameToPrimitiveTypeMap = new HashMap<>();
        for (PrimitiveType primitiveType : PrimitiveType.values()) {
            nameToPrimitiveTypeMap.put(primitiveType.name, primitiveType);
        }
        NAME_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(nameToPrimitiveTypeMap);
    }


    private final String name;
    private final Class<?> javaType;

    PrimitiveType(String name, Class<?> javaType) {
        this.name = name;
        this.javaType = javaType;
    }

    /**
     * Returns the {@code PrimitiveType} for the specified name.
     *
     * @param name The name of the {@code PrimitiveType} to find.
     * @return The {@code PrimitiveType} for the specified name.
     * @throws java.lang.IllegalArgumentException If the name does not match an {@code PrimitiveType}.
     */
    public static PrimitiveType forName(String name) {
        String simpleName;
        if (name.startsWith(EntityDataModel.EDM_NAMESPACE)) {
            simpleName = name.substring(EntityDataModel.EDM_NAMESPACE.length() + 1);
        } else {
            simpleName = name;
        }

        PrimitiveType primitiveType = NAME_TO_PRIMITIVE_TYPE.get(simpleName);
        if (primitiveType == null) {
            throw new IllegalArgumentException("Invalid primitive type name: " + name);
        }
        return primitiveType;
    }

    @Override
    public MetaType getMetaType() {
        return MetaType.PRIMITIVE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return EntityDataModel.EDM_NAMESPACE;
    }

    @Override
    public String getFullyQualifiedName() {
        return getNamespace() + "." + getName();
    }

    @Override
    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}
