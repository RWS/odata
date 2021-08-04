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
package com.sdl.odata.unmarshaller.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.unmarshaller.AbstractActionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Parser for Action request body in JSON format. It returns action or actionImport instance based on input parameters.
 */
public class ODataJsonActionParser extends AbstractActionParser {
    private static final Logger LOG = LoggerFactory.getLogger(ODataJsonActionParser.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public ODataJsonActionParser(ODataRequestContext newRequestContext) {
        super(newRequestContext);
    }

    @Override
    public Map<String, Object> parseRequestBody(String body) throws IOException {
        // The very primitive parser to parse simple key-value parameters
        if (isNullOrEmpty(body)) {
            return Collections.emptyMap();
        }

        Map<String, Object> parsedMap = OBJECT_MAPPER.readValue(body, new TypeReference<HashMap<String, Object>>() {
        });
        LOG.debug("The request body is parsed to map: {}", parsedMap);
        return parsedMap;
    }
}
