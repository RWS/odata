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
package com.sdl.odata.renderer.json.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.sdl.odata.util.PrimitiveUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Class with utility methods to write JSON properties.
 */
public final class JsonWriterUtil {

    private JsonWriterUtil() {
    }

    /**
     * Write the given primitive value to the JSON stream by using the given JSON generator.
     *
     * @param primitiveValue The given primitive value to write.
     * @param jsonGenerator  The given JSON generator.
     * @throws IOException If unable to write to the json output stream
     */
    public static void writePrimitiveValue(Object primitiveValue, JsonGenerator jsonGenerator) throws IOException {

        Class<?> primitiveClass = PrimitiveUtil.wrap(primitiveValue.getClass());
        if (String.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeString(String.valueOf(primitiveValue));
        } else if (Byte.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeObject(primitiveValue);
        } else if (Short.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeNumber((short) primitiveValue);
        } else if (Integer.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeNumber((int) primitiveValue);
        } else if (Float.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeNumber((float) primitiveValue);
        } else if (Double.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeNumber((double) primitiveValue);
        } else if (Long.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeNumber((long) primitiveValue);
        } else if (Boolean.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeBoolean((boolean) primitiveValue);
        } else if (UUID.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeString(primitiveValue.toString());
        } else if (BigDecimal.class.isAssignableFrom(primitiveClass)) {
            jsonGenerator.writeNumber((BigDecimal) primitiveValue);
        } else {
            jsonGenerator.writeObject(primitiveValue.toString());
        }
    }

    public static String escapeQuotes(String source) {
        return source.replaceAll("\"", "\\\\\"");
    }
}
