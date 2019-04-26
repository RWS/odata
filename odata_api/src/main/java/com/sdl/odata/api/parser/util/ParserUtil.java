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
package com.sdl.odata.api.parser.util;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataNotImplementedException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EnumMember;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Common parser utility methods.
 */
public final class ParserUtil {

    private static final int PARSE_INDEX = 255;

    private ParserUtil() {
    }

    /**
     * Parses a primitive value into an instance of the specified primitive type.
     *
     * @param value The value as text.
     * @param primitiveType The expected primitive type.
     * @return An instance of the expected primitive type.
     * @throws ODataException If an error occurs so that the value cannot be parsed.
     */
    public static Object parsePrimitiveValue(String value, PrimitiveType primitiveType) throws ODataException {
        switch (primitiveType) {
            case BOOLEAN:
                return Boolean.valueOf(value);

            case BYTE:
                try {
                    final Integer v = Integer.valueOf(value);
                    if (v < 0 || v > PARSE_INDEX) {
                        throwParseException(value, primitiveType, null);
                    }
                    return v.byteValue();
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case DATE:
                try {
                    return LocalDate.parse(value);
                } catch (IllegalArgumentException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case DATE_TIME_OFFSET2:
                try {
                    return OffsetDateTime.parse(value);
                } catch (IllegalArgumentException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case DATE_TIME_OFFSET:
                try {
                    return ZonedDateTime.parse(value);
                } catch (IllegalArgumentException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case DURATION:
                try {
                    return Period.parse(value);
                } catch (IllegalArgumentException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case TIME_OF_DAY:
                try {
                    return LocalTime.parse(value);
                } catch (IllegalArgumentException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case DECIMAL:
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case DOUBLE:
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case SINGLE:
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case GUID:
                try {
                    return UUID.fromString(value);
                } catch (IllegalArgumentException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case INT16:
                try {
                    return Short.valueOf(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case INT32:
                try {
                    return Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case INT64:
                try {
                    return Long.valueOf(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            case STRING:
                return value;

            case SBYTE:
                try {
                    return Byte.valueOf(value);
                } catch (NumberFormatException e) {
                    throwParseException(value, primitiveType, e);
                }
                break;

            default:
                throw new ODataNotImplementedException("Unsupported primitive type: " + primitiveType +
                        " for value: " + value);
        }

        // Dummy, this will never be reached because throwParseException always throws an exception
        return null;
    }

    private static void throwParseException(String value, Type type, Throwable cause)
            throws ODataUnmarshallingException {
        throw new ODataUnmarshallingException("This is not a valid " + type + " value: " + value, cause);
    }

    public static Object parseEnumValue(String value, EnumType enumType) throws ODataException {
        EnumMember member = enumType.getMember(value);
        if (member == null) {
            member = enumType.getMember(Long.valueOf(value));
        }

        if (member == null) {
            throw new ODataUnmarshallingException("Enum member not found in entity data model, type: " + enumType +
                    " for value: " + value);
        }

        @SuppressWarnings("unchecked")
        Class<? extends Enum> javaType = (Class<? extends Enum>) enumType.getJavaType();

        try {
            return Enum.valueOf(javaType, member.getName());
        } catch (IllegalArgumentException e) {
            throw new ODataEdmException("Java enum type does not match what is registered in the entity data model", e);
        }
    }
}
