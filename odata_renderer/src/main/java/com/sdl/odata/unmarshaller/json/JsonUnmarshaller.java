/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.unmarshaller.AbstractUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sdl.odata.renderer.AbstractRenderer.DEFAULT_SCORE;

/**
 * The OData Json Unmarshaller.
 *
 */
@Component
public class JsonUnmarshaller extends AbstractUnmarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(JsonUnmarshaller.class);

    @Autowired
    private ODataParser uriParser;

    @Override
    public int score(ODataRequestContext requestContext) {
        if (isRightMethodForUnmarshall(requestContext.getRequest()) &&
                !ODataUriUtil.isRefPathUri(requestContext.getUri())) {
            MediaType contentType = requestContext.getRequest().getContentType();
            int score = super.score(contentType, MediaType.JSON);
            LOG.debug("Match for JsonUnmarshaller: {} with score: {}", requestContext.getRequest(), score);
            return score;
        }

        return DEFAULT_SCORE;
    }

    @Override
    public Object unmarshall(ODataRequestContext requestContext) throws ODataException {
        LOG.info("Json Unmarshaller invoked with {}", requestContext.getRequest());
        return new ODataJsonParser(requestContext, uriParser).getODataEntity();
    }
}
