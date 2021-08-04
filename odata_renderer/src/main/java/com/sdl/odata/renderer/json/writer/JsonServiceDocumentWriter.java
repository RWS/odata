/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.Singleton;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.sdl.odata.JsonConstants.CONTEXT;
import static com.sdl.odata.JsonConstants.ENTITY_SET;
import static com.sdl.odata.JsonConstants.KIND;
import static com.sdl.odata.JsonConstants.NAME;
import static com.sdl.odata.JsonConstants.SINGLETON;
import static com.sdl.odata.JsonConstants.URL;
import static com.sdl.odata.JsonConstants.VALUE;
import static com.sdl.odata.ODataRendererUtils.getContextURL;

/**
 * Json Service Root Writer.
 * It generates the content for Json ServiceRoot Renderer.
 *
 */
public class JsonServiceDocumentWriter {
    private static final Logger LOG = LoggerFactory.getLogger(JsonServiceDocumentWriter.class);

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private final ODataUri uri;
    private final EntityDataModel entityDataModel;

    public JsonServiceDocumentWriter(ODataUri uri, EntityDataModel entityDataModel) {
        this.uri = uri;
        this.entityDataModel = entityDataModel;
    }

    /**
     * The main method for Writer.
     * It builds the service root document according to spec.
     *
     * @return output in json
     * @throws ODataRenderException If unable to render the json service document
     */
    public String buildJson() throws ODataRenderException {
        LOG.debug("Start building Json service root document");
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            JsonGenerator jsonGenerator = JSON_FACTORY.createGenerator(stream, JsonEncoding.UTF8);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(CONTEXT, getContextURL(uri, entityDataModel));
            jsonGenerator.writeArrayFieldStart(VALUE);


            List<EntitySet> entities = entityDataModel.getEntityContainer().getEntitySets();
            for (EntitySet entity : entities) {
                if (entity.isIncludedInServiceDocument()) {
                    writeObject(jsonGenerator, entity);
                }
            }

            List<Singleton> singletons = entityDataModel.getEntityContainer().getSingletons();
            for (Singleton singleton : singletons) {
                writeObject(jsonGenerator, singleton);
            }

            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            return stream.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new ODataRenderException("It is unable to render service document", e);
        }
    }

    /**
     * Build an embedded json object that will have key-value attributes like
     * 'name' and 'url' (they are MUST), 'title' and 'kind'.
     *
     * @param jsonGenerator jsonGenerator
     * @param entity        entitySet or singleton
     * @throws IOException
     */
    private void writeObject(JsonGenerator jsonGenerator, Object entity) throws IOException {
        jsonGenerator.writeStartObject();

        writeName(jsonGenerator, entity);
        writeKind(jsonGenerator, entity);
        writeURL(jsonGenerator, entity);

        jsonGenerator.writeEndObject();
    }

    /**
     * Writes the name of the entity
     * It is a MUST element.
     *
     * @param jsonGenerator jsonGenerator
     * @param entity        entity from the container
     */
    private void writeName(JsonGenerator jsonGenerator, Object entity) throws IOException {
        jsonGenerator.writeFieldName(NAME);
        if (entity instanceof EntitySet) {
            jsonGenerator.writeObject(((EntitySet) entity).getName());
        } else {
            jsonGenerator.writeObject(((Singleton) entity).getName());
        }
    }

    /**
     * Writes the kind of the entity.
     *
     * @param jsonGenerator jsonGenerator
     * @param entity        entity from the container
     */
    private void writeKind(JsonGenerator jsonGenerator, Object entity) throws IOException {
        jsonGenerator.writeFieldName(KIND);
        if (entity instanceof EntitySet) {
            jsonGenerator.writeObject(ENTITY_SET);
        } else {
            jsonGenerator.writeObject(SINGLETON);
        }
    }

    /**
     * Writes the url of the entity
     * It is a MUST element.
     *
     * @param jsonGenerator jsonGenerator
     * @param entity        entity from the container
     */
    private void writeURL(JsonGenerator jsonGenerator, Object entity) throws IOException {
        // It is exactly the same as the 'name' property.
        jsonGenerator.writeFieldName(URL);
        if (entity instanceof EntitySet) {
            jsonGenerator.writeObject(((EntitySet) entity).getName());
        } else {
            jsonGenerator.writeObject(((Singleton) entity).getName());
        }
    }
}
