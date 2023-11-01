/**
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
package com.sdl.odata.renderer.json.util;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintJson;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link JsonWriterUtilTest}.
 */
public class JsonWriterUtilTest {

    private static final String EXPECTED_PRIMITIVE_VALUES_PATH = "/json/PrimitiveValues.json";

    @Test
    public void testWritePrimitiveValues() throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        appendPrimitiveValue("MyString", "Some text", jsonGenerator);
        appendPrimitiveValue("MyByteProperty", Byte.MAX_VALUE, jsonGenerator);
        appendPrimitiveValue("MyShortProperty", (short) 1, jsonGenerator);
        appendPrimitiveValue("MyIntegerProperty", 2, jsonGenerator);
        appendPrimitiveValue("MyFloatProperty", 3.0f, jsonGenerator);
        appendPrimitiveValue("MyDoubleProperty", 4.0d, jsonGenerator);
        appendPrimitiveValue("MyLongProperty", (long) 5, jsonGenerator);
        appendPrimitiveValue("MyBooleanProperty", true, jsonGenerator);
        appendPrimitiveValue("MyUUIDProperty", UUID.fromString("23492a5b-c4f1-4a50-b7a5-d8ebd6067902"), jsonGenerator);
        appendPrimitiveValue("DecimalValueProperty", BigDecimal.valueOf(21), jsonGenerator);

        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        assertEquals(prettyPrintJson(readContent(EXPECTED_PRIMITIVE_VALUES_PATH)), prettyPrintJson(stream.toString()));
    }

    private void appendPrimitiveValue(String fieldName, Object primitiveValue, JsonGenerator jsonGenerator)
            throws IOException {

        jsonGenerator.writeFieldName(fieldName);
        JsonWriterUtil.writePrimitiveValue(primitiveValue, jsonGenerator);
    }
}
