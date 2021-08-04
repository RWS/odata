/**
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
package com.sdl.odata.unmarshaller.batch;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataNotImplementedException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.parser.ODataBatchRequestParser;
import com.sdl.odata.unmarshaller.AbstractParser;

import java.util.List;

/**
 * The OData Multipart Parser that is used to parse Batch request that contain multipart/mixed as the Content-Type.
 */
public class ODataBatchParser extends AbstractParser {

    public ODataBatchParser(ODataRequestContext request, ODataParser uriParser) {
        super(request, uriParser);
    }

    /**
     * Actually here processed not an entity but a multipart/mixed batch request body.
     * @param bodyText represents batch request body
     * @return parsed ODataBatchRequestContent
     * @throws ODataException If unable to parse entity
     */
    @Override
    protected Object processEntity(String bodyText) throws ODataException {
        return new ODataBatchRequestParser().parseBatch(bodyText);
    }

    /**
     * This method will not be used because batch body processing will go through 'processEntity' method.
     * @param bodyText The given batch request body
     * @return this method should not be invoked.
     * @throws com.sdl.odata.api.ODataException If unable to process entities
     */
    @Override
    protected List<?> processEntities(String bodyText) throws ODataException {
        throw new ODataNotImplementedException("Parsing the batch request for the entities is yet to be implemented.");
    }

}
