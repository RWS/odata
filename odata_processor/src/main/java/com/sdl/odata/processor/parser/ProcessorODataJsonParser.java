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
package com.sdl.odata.processor.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.unmarshaller.json.ODataJsonParser;

public class ProcessorODataJsonParser extends ODataJsonParser {

    public ProcessorODataJsonParser(ODataRequestContext request, ODataParser uriParser) {
        super(request, uriParser);
    }

    @Override
    public Object processEntity(String bodyText) throws ODataException {
        return super.processEntity(bodyText);
    }
}
