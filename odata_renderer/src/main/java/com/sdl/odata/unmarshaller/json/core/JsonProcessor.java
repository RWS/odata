/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.unmarshaller.json.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.sdl.odata.JsonConstants;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * OData JSON Processor.
 * <p>
 * It is able to parse all values JSON has. Complex values -&gt; Arrays -&gt; Primitive values. All 3 phase has its own
 * parsing algorithm.
 *
 */
public class JsonProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(JsonProcessor.class);

    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    /**
     * OData.
     */
    public static final String ODATA = "@odata";
    /**
     * OData Bind.
     */
    public static final String ODATA_BIND = "@odata.bind";
    /**
     * SVC Extension.
     */
    public static final String SVC_EXTENSION = ".svc/";
    private final String inputJson;

    private Map<String, String> odataValues = new HashMap<>();
    private Map<String, Object> values = new HashMap<>();
    private Map<String, Object> links = new TreeMap<>();

    public JsonProcessor(String bodyText) {
        if (bodyText == null) {
            throw new IllegalArgumentException();
        }
        this.inputJson = bodyText;
    }

    /**
     * Initialize processor, automatically scanning the input JSON.
     * @throws ODataUnmarshallingException If unable to initialize
     */
    public void initialize() throws ODataUnmarshallingException {
        LOG.info("Parser is initializing");
        try {
            JsonParser jsonParser = JSON_FACTORY.createParser(inputJson);

            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String token = jsonParser.getCurrentName();
                if (token != null) {
                    if (token.startsWith(ODATA)) {
                        processSpecialTags(jsonParser);
                    } else if (token.endsWith(ODATA_BIND)) {
                        processLinks(jsonParser);
                    } else {
                        process(jsonParser);
                    }
                }
            }
        } catch (IOException e) {
            throw new ODataUnmarshallingException("It is unable to unmarshall", e);
        }
    }

    /**
     * Process all things that do not contain special ODataTags.
     *
     * @param jsonParser the parser
     * @throws ODataUnmarshallingException If unable to unmarshall
     * @throws IOException If unable to read input parser
     */
    private void process(JsonParser jsonParser) throws IOException, ODataUnmarshallingException {
        if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
            LOG.info("Starting to parse {} token", jsonParser.getCurrentName());
            String key = jsonParser.getCurrentName();
            jsonParser.nextToken();

            JsonToken token = jsonParser.getCurrentToken();
            if (token == JsonToken.START_ARRAY) {
                if (JsonConstants.VALUE.equals(key)) {
                    throw new ODataUnmarshallingException("Feed is not supported");
                }
                values.put(key, getCollectionValue(jsonParser));
            } else if (token == JsonToken.START_OBJECT) {
                values.put(key, getEmbeddedObject(jsonParser));
            } else {
                if (token.equals(JsonToken.VALUE_NULL)) {
                    values.put(key, null);
                } else {
                    values.put(key, jsonParser.getText());
                }
            }
        }
    }

    /**
     * Parse the complex values.
     *
     * @param jsonParser the parser
     * @return list of parsed result objects
     * @throws IOException If unable to read input parser
     */
    private List<Object> getCollectionValue(JsonParser jsonParser) throws IOException {
        LOG.info("Start parsing {} array", jsonParser.getCurrentName());
        List<Object> list = new ArrayList<>();
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                Object embedded = getEmbeddedObject(jsonParser);
                list.add(embedded);
            }
            if (!"}".equals(jsonParser.getText())) {
                list.add(jsonParser.getText());
            } else {
                LOG.info("Array is over.");
            }
        }
        return list;
    }

    /**
     * Process an embedded object.
     *
     * @param jsonParser the parser
     * @return map with embedded object key:values
     * @throws IOException If unable to read input parser
     */
    private Object getEmbeddedObject(JsonParser jsonParser) throws IOException {
        LOG.info("Start parsing an embedded object.");
        Map<String, Object> embeddedMap = new HashMap<>();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getText();
            jsonParser.nextToken();
            JsonToken token = jsonParser.getCurrentToken();
            if (token == JsonToken.START_ARRAY) {
                Object embeddedArray = getCollectionValue(jsonParser);
                embeddedMap.put(key, embeddedArray);
            } else if (token == JsonToken.START_OBJECT) {
                Object embeddedObject = getEmbeddedObject(jsonParser);
                embeddedMap.put(key, embeddedObject);
            } else {
                if (token.equals(JsonToken.VALUE_NULL)) {
                    embeddedMap.put(key, null);
                } else {
                    embeddedMap.put(key, jsonParser.getText());
                }
            }
        }
        return embeddedMap;
    }

    /**
     * This method fills odataMap that contains special tags "odata" with key:value When the key token is over, you need
     * to execute nextToken() in order to get value.
     *
     * @param jsonParser the parser
     * @throws IOException If unable to read input parser
     */
    private void processSpecialTags(JsonParser jsonParser) throws IOException {
        LOG.info("@odata tags found - start parsing");
        String key = jsonParser.getCurrentName();
        jsonParser.nextToken();
        String value = jsonParser.getText();
        odataValues.put(key, value);
    }

    /**
     * Process OData links.
     *
     * @param jsonParser the parser
     * @throws IOException If unable to read input parser
     */
    private void processLinks(JsonParser jsonParser) throws IOException {

        LOG.info("@odata.bind tag found - start parsing");

        final String fullLinkFieldName = jsonParser.getText();
        final String key = fullLinkFieldName.substring(0, fullLinkFieldName.indexOf(ODATA_BIND));
        JsonToken token = jsonParser.nextToken();
        if (token != JsonToken.START_ARRAY) {
            // Single link
            links.put(key, processLink(jsonParser));
        } else {
            // Array of links
            final List<String> linksList = new ArrayList<>();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                linksList.add(processLink(jsonParser));
            }
            this.links.put(key, linksList);

        }
    }

    /**
     * Process OData link.
     *
     * @param jsonParser the parser
     * @throws IOException If unable to read input parser
     * @return the link
     */
    private String processLink(JsonParser jsonParser) throws IOException {
        final String link = jsonParser.getText();
        if (link.contains(SVC_EXTENSION)) {
            return link.substring(link.indexOf(SVC_EXTENSION) + SVC_EXTENSION.length());
        }
        return link;
    }


    /**
     * Get the map with all types of values [array,collection,primitive] Except the odata.
     *
     * @return a map of values
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Get the map of OData values.
     *
     * @return a map of OData values
     */
    public Map<String, String> getODataValues() {
        return odataValues;
    }

    /**
     * Get the links.
     *
     * @return the links
     */
    public Map<String, Object> getLinks() {
        return links;
    }
}
