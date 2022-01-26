/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.sdl.odata.ErrorRendererConstants.CODE;
import static com.sdl.odata.ErrorRendererConstants.ERROR;
import static com.sdl.odata.ErrorRendererConstants.MESSAGE;
import static com.sdl.odata.ErrorRendererConstants.TARGET;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;

/**
 * The Json Error Response Writer.
 *
 */
public class JsonErrorResponseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonErrorResponseWriter.class);

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    /**
     * Gets the json error output according to ODataException.
     *
     * @param exception ODataException
     * @return errorJsonResponse
     * @throws ODataRenderException If unable to render the json error message
     */
    public String getJsonError(ODataException exception) throws ODataRenderException {
        checkNotNull(exception);

        LOG.debug("Start building Json error document");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            JsonGenerator jsonGenerator = JSON_FACTORY.createGenerator(outputStream, JsonEncoding.UTF8);
            jsonGenerator.writeStartObject();

            jsonGenerator.writeObjectFieldStart(ERROR);

            jsonGenerator.writeStringField(CODE, String.valueOf(exception.getCode().getCode()));
            jsonGenerator.writeStringField(MESSAGE, String.valueOf(exception.getMessage()));
            // optional
            if (exception.getTarget() != null) {
                jsonGenerator.writeStringField(TARGET, String.valueOf(exception.getTarget()).replace("\"", "'"));
            }
            jsonGenerator.writeEndObject();
            jsonGenerator.close();

            return outputStream.toString();
        } catch (IOException e) {
            LOG.error("Not possible to write error JSON.");
            throw new ODataRenderException("Not possible to write error JSON: ", e);
        }
    }

}
