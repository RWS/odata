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
package com.sdl.odata.renderer;

import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;

import java.util.List;

import static com.sdl.odata.api.parser.ODataUriUtil.getFormatOption;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.XML;
import static java.lang.Math.max;

/**
 * Abstract superclass with common functionality for Atom renderers.
 */
public abstract class AbstractAtomRenderer extends AbstractRenderer {
    private static final int DEFAULT_XML_RENDER_SCORE = 31;

    @Override
    public int score(ODataRequestContext requestContext, QueryResult data) {

        // Try scoring against the $format query parameter
        int atomXmlFormatScore = scoreByFormat(getFormatOption(requestContext.getUri()), ATOM_XML);
        int xmlFormatScore = scoreByFormat(getFormatOption(requestContext.getUri()), XML);

        // Try the types that should be allowed according to the OData specification
        // See: OData Atom Format Version 4.0, chapter 3: Requesting the Atom Format
        List<MediaType> accept = requestContext.getRequest().getAccept();
        int atomXmlAcceptScore = scoreByMediaType(accept, ATOM_XML);
        int xmlAcceptScore = scoreByMediaType(accept, XML);
        int totalScore = max(atomXmlFormatScore, max(xmlFormatScore, max(atomXmlAcceptScore, xmlAcceptScore)));

        // accept header or format option not specified and content type is specified then
        // response should be rendered based on content type (either json or xml).
        if (isRequestedContentTypeSupported(requestContext)) {
            totalScore = max(totalScore, scoreByContentType(requestContext, ATOM_XML));
        } else {
            if (totalScore == WILDCARD_MATCH_SCORE || totalScore == DEFAULT_SCORE) {
                return DEFAULT_XML_RENDER_SCORE;
            }
        }

        return totalScore > 0 ? totalScore : DEFAULT_SCORE;
    }
}
