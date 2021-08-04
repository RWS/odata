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
package com.sdl.odata.renderer;

import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.sdl.odata.api.parser.ODataUriUtil.getFormatOption;
import static com.sdl.odata.api.service.MediaType.JSON;

/**
 * Abstract superclass with common functionality for JSON renderers.
 */
public abstract class AbstractJsonRenderer extends AbstractRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonRenderer.class);

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {

        // Try scoring against the $format query parameter
        int formatScore = scoreByFormat(getFormatOption(requestContext.getUri()), JSON);

        // Try the types that should be allowed according to the OData specification
        // See: OData Atom Format Version 4.0, chapter 3: Requesting the Atom Format
        List<MediaType> accept = requestContext.getRequest().getAccept();
        int acceptScore = scoreByMediaType(accept, JSON);
        int totalScore = Math.max(formatScore, acceptScore);
        if (isRequestedContentTypeSupported(requestContext)) {
            totalScore = Math.max(totalScore, scoreByContentType(requestContext, JSON));
        }

        LOG.debug("Renderer score is {}", totalScore);

        return totalScore > 0 ? totalScore : DEFAULT_SCORE;
    }
}
