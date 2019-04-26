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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.sdl.odata.api.parser.util.ParserUtil.parsePrimitiveValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Parser Util Test.
 */
public class ParserUtilTest {

    @Test
    public void testParseBoolean() throws ODataException {
        assertTrue((boolean) parsePrimitiveValue("true", PrimitiveType.BOOLEAN));
        assertFalse((boolean) parsePrimitiveValue("false", PrimitiveType.BOOLEAN));
    }

    @Test
    public void testParseNumericTypes() throws ODataException {
        assertThat(parsePrimitiveValue("36", PrimitiveType.BYTE), is((byte) 36));
        assertThat(parsePrimitiveValue("234", PrimitiveType.BYTE), is((byte) 234));

        assertThat(parsePrimitiveValue("36", PrimitiveType.SBYTE), is((byte) 36));
        assertThat(parsePrimitiveValue("-120", PrimitiveType.SBYTE), is((byte) -120));

        assertThat(parsePrimitiveValue("3476", PrimitiveType.INT16), is((short) 3476));
        assertThat(parsePrimitiveValue("2343771", PrimitiveType.INT32), is(2343771));
        assertThat(parsePrimitiveValue("84723648623", PrimitiveType.INT64), is(84723648623L));

        assertThat(parsePrimitiveValue("6432477478236420.234807", PrimitiveType.DECIMAL),
                is(new BigDecimal("6432477478236420.234807")));

        assertThat(parsePrimitiveValue("123.625", PrimitiveType.DOUBLE), is(123.625));
        assertThat(parsePrimitiveValue("8.75", PrimitiveType.SINGLE), is(8.75f));
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseByteInvalid1() throws ODataException {
        parsePrimitiveValue("-1", PrimitiveType.BYTE);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseByteInvalid2() throws ODataException {
        parsePrimitiveValue("256", PrimitiveType.BYTE);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseSByteInvalid1() throws ODataException {
        parsePrimitiveValue("-129", PrimitiveType.SBYTE);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseSByteInvalid2() throws ODataException {
        parsePrimitiveValue("128", PrimitiveType.SBYTE);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseInt16Invalid1() throws ODataException {
        parsePrimitiveValue("-32769", PrimitiveType.INT16);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseInt16Invalid2() throws ODataException {
        parsePrimitiveValue("32768", PrimitiveType.INT16);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseInt32Invalid1() throws ODataException {
        parsePrimitiveValue("-2147483649", PrimitiveType.INT32);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseInt32Invalid2() throws ODataException {
        parsePrimitiveValue("2147483648", PrimitiveType.INT32);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseInt64Invalid1() throws ODataException {
        parsePrimitiveValue("-9223372036854775809", PrimitiveType.INT64);
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testParseInt64Invalid2() throws ODataException {
        parsePrimitiveValue("9223372036854775808", PrimitiveType.INT64);
    }

    @Test
    public void testParseDateTimeTypes() throws ODataException {
        assertThat(parsePrimitiveValue("2014-07-31", PrimitiveType.DATE),
                is(LocalDate.parse("2014-07-31")));

        assertThat(parsePrimitiveValue("2014-07-31T12:34:03.023Z", PrimitiveType.DATE_TIME_OFFSET),
                   is(ZonedDateTime.parse("2014-07-31T12:34:03.023Z")));

        assertThat(parsePrimitiveValue("2014-07-31T12:34:03.023Z", PrimitiveType.DATE_TIME_OFFSET2),
                   is(OffsetDateTime.parse("2014-07-31T12:34:03.023Z")));

        assertThat(parsePrimitiveValue("P3Y30M30D", PrimitiveType.DURATION),
                is(Period.parse("P3Y30M30D")));

        assertThat(parsePrimitiveValue("09:56:18.821", PrimitiveType.TIME_OF_DAY),
                is(LocalTime.parse("09:56:18.821")));
    }

    @Test
    public void testParseGuid() throws ODataException {
        assertThat(parsePrimitiveValue("a6a8e5be-6d8e-40ce-bb8d-3836d63aef0e", PrimitiveType.GUID),
                is(UUID.fromString("a6a8e5be-6d8e-40ce-bb8d-3836d63aef0e")));
    }
}
