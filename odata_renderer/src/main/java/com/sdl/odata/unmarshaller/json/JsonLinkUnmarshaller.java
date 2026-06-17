/**
 * Copyright (c) 2014-2026 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.unmarshaller.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import com.sdl.odata.JsonConstants;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.unmarshaller.AbstractLinkUnmarshaller;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Unmarshaller for POST and PUT requests where the URI is reference (it ends in ".../$ref"). The body of such a
 * request is expected to contain an entity reference in JSON format. This unmarshaller returns an ODataLink object
 * containing information about the link to be created.
 * <p>
 * See OData v4 specification part 1, paragraph 11.4.6 Modifying Relationships between Entities
 * See OData v4 JSON format specification, chapter 13 Entity Reference
 */
@Component
public class JsonLinkUnmarshaller extends AbstractLinkUnmarshaller {
    private static final MediaType[] SUPPORTED_MEDIA_TYPES = {MediaType.JSON};

    @Override
    protected MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

    @Override
    protected String getToEntityId(ODataRequestContext requestContext) throws ODataUnmarshallingException {
        // The body is expected to contain a single entity reference
        // See OData JSON specification chapter 13

        String bodyText;
        try {
            bodyText = requestContext.getRequest().getBodyText(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException("UTF-8 is not supported", e);
        }

        String idValue = null;
        try {
            JsonParser parser = JsonFactory.builder().build().createParser(bodyText);
            while (idValue == null && !parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) {
                    break;
                }

                if (token.equals(JsonToken.PROPERTY_NAME) && parser.currentName().equals(JsonConstants.ID)) {
                    token = parser.nextToken();
                    if (token.equals(JsonToken.VALUE_STRING)) {
                        idValue = parser.getString();
                    }
                }
            }
        } catch (JacksonException e) {
            throw new ODataUnmarshallingException("Error while parsing JSON data", e);
        }

        if (isNullOrEmpty(idValue)) {
            throw new ODataUnmarshallingException("The JSON object in the body has no '@odata.id' value," +
                    " or the value is empty. The JSON object in the body must have an '@odata.id' value" +
                    " that refers to the entity to link to.");
        }

        return idValue;
    }
}
