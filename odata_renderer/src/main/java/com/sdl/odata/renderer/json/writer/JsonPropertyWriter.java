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
package com.sdl.odata.renderer.json.writer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.renderer.AbstractPropertyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static com.sdl.odata.JsonConstants.CONTEXT;
import static com.sdl.odata.JsonConstants.VALUE;
import static com.sdl.odata.ODataRendererUtils.getContextURL;
import static com.sdl.odata.renderer.json.util.JsonWriterUtil.writePrimitiveValue;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Json Property Writer.
 */
public class JsonPropertyWriter extends AbstractPropertyWriter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonPropertyWriter.class);

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private JsonGenerator jsonGenerator;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public JsonPropertyWriter(ODataUri uri, EntityDataModel entityDataModel) throws ODataRenderException {
        super(uri, entityDataModel);
        try {
            jsonGenerator = JSON_FACTORY.createGenerator(outputStream, JsonEncoding.UTF8)
                    .setCodec(new JsonCodecMapper());
            jsonGenerator.writeStartObject();
        } catch (IOException e) {
            throw new ODataRenderException("Unable to render with following configuration");
        }
    }

    @Override
    protected String generateNullPropertyString() throws ODataRenderException {
        // A single-valued property that has the null value does not have a representation
        throw new ODataRenderException("Json Property Writer doesn't handle null property");
    }

    @Override
    protected String generatePrimitiveProperty(Object data, Type type) throws ODataRenderException {
        // It is not a problem to write an empty collection. Problem is to use its type for generating @odata.context
        try {
            if (isCollection(data)) {
                jsonGenerator.writeStringField(CONTEXT, getContextURL(getODataUri(), getEntityDataModel(), true));
                jsonGenerator.writeArrayFieldStart(VALUE);
                for (Object element : (List) data) {
                    jsonGenerator.writeObject(element);
                }
                jsonGenerator.writeEndArray();
                return closeStream(outputStream);

            } else {
                jsonGenerator.writeStringField(CONTEXT, getContextURL(getODataUri(), getEntityDataModel(), true));
                jsonGenerator.writeFieldName(VALUE);
                jsonGenerator.writeObject(data);
                return closeStream(outputStream);
            }
        } catch (IOException e) {
            throw new ODataRenderException("Unable to marshall primitive");
        }
    }

    @Override
    protected String generateComplexProperty(Object data, StructuredType type) throws ODataRenderException {
        return generateComplex(data, type, false);
    }

    private String generateComplex(Object data, StructuredType type, boolean isEmbedded) throws ODataRenderException {
        try {
            if (!isEmbedded) {
                jsonGenerator.writeStringField(CONTEXT, getContextURL(getODataUri(), getEntityDataModel()));
            }
            jsonGenerator.writeFieldName("value");
            processData(data, type, jsonGenerator);
            return closeStream(outputStream);
        } catch (ODataException | IOException | IllegalAccessException e) {
            throw new ODataRenderException("Unable to marshall complex");
        }
    }

    private void processData(Object data, StructuredType type, JsonGenerator generator)
            throws IllegalAccessException, IOException, ODataException {
        if (isCollection(data)) {
            LOG.trace("Given property is collection of complex values");
            generator.writeStartArray();
            for (Object obj : (List) data) {
                writeAllProperties(obj, type, generator);
            }
            generator.writeEndArray();
        } else {
            LOG.trace("Given property is single complex value");
            writeAllProperties(data, type, generator);
        }

    }

    private void writeAllProperties(final Object data, StructuredType type, final JsonGenerator generator)
            throws IOException, ODataRenderException {
        generator.writeStartObject();
        visitProperties(getEntityDataModel(), type, property -> {
            try {
                if (!(property instanceof NavigationProperty)) {
                    handleProperty(data, property, generator);
                }
            } catch (IllegalAccessException | IOException | ODataException e) {
                throw new ODataRenderException("Error while writing property: " + property.getName(), e);
            }
        });
        generator.writeEndObject();
    }

    private void handleProperty(Object data, StructuralProperty property, JsonGenerator generator)
            throws IllegalAccessException, IOException, ODataException {
        Field field = property.getJavaField();
        field.setAccessible(true);
        Object value = field.get(data);
        LOG.trace("Property name is '{}' and its value is '{}'", property.getName(), value);
        Type type = getType(value);
        if (type == null) {
            String msg = String.format("Field type %s is not found in entity data model", field.getType());
            LOG.error(msg);
            throw new ODataRenderException(msg);
        }

        switch (type.getMetaType()) {
            case PRIMITIVE:
                generator.writeFieldName(property.getName());
                writePrimitive(value, generator);
                break;
            case COMPLEX:
                generator.writeArrayFieldStart(property.getName());
                generateComplex(value, (StructuredType) type, true);
                generator.writeEndArray();
                break;
            default:
                defaultHandling(type);
        }
    }

    private void writePrimitive(Object element, JsonGenerator generator) throws IOException, ODataRenderException {
        if (isCollection(element)) {
            LOG.trace("element is collection {}", element);
            generator.writeStartArray();
            for (Object obj : (List<?>) element) {
                writePrimitiveValue(obj, generator);
            }
            generator.writeEndArray();
        } else if (element == null) {
            generateNullPropertyString();
        } else {
            writePrimitiveValue(element, generator);
        }
    }

    private String closeStream(ByteArrayOutputStream os) throws IOException {
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        return os.toString(UTF_8.name());
    }
}

