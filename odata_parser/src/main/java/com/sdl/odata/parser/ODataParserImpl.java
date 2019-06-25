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
package com.sdl.odata.parser;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriParseException;
import com.sdl.odata.api.parser.ResourcePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OData parser implementation.
 */
@Component
public class ODataParserImpl implements ODataParser {
    private static final Logger LOG = LoggerFactory.getLogger(ODataParserImpl.class);

    private ParserConfiguration parserConfiguration;

    public ODataParserImpl() {
        this.parserConfiguration = new ParserConfiguration();
    }

    @Autowired
    public ODataParserImpl(ParserConfiguration parserConfiguration) {
        this.parserConfiguration = parserConfiguration;
    }

    @Override
    public ODataUri parseUri(String uri, EntityDataModel entityDataModel) throws ODataUriParseException {
        LOG.debug("Parsing URI: {}", uri);
        ODataUri parsedUri = new ODataUriParser(entityDataModel, parserConfiguration.getBasePath()).parseUri(uri);
        LOG.debug("Parse result: {}", parsedUri);
        return parsedUri;
    }

    @Override
    public ResourcePath parseResourcePath(String resourcePath, EntityDataModel entityDataModel)
            throws ODataUriParseException {
        LOG.debug("Parsing resource path: {}", resourcePath);
        ResourcePath parsedResourcePath = new ODataUriParser(entityDataModel,
          parserConfiguration.getBasePath()).parseResourcePath(resourcePath);
        LOG.debug("Parse result: {}", parsedResourcePath);
        return parsedResourcePath;
    }
}
