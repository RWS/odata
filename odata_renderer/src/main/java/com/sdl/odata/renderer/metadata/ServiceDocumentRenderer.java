/*
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
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ServiceRootUri;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.renderer.AbstractRenderer;

import static com.sdl.odata.api.parser.ODataUriUtil.getFormatOption;
import static java.lang.Math.max;

/**
 * Renderer which renders the OData service document.
 * The service document can either be in XML or JSON.
 */
public abstract class ServiceDocumentRenderer extends AbstractRenderer {

    /**
     * Check if the given OData URI describes a request to a 'Service Document'.
     *
     * @param uri THe given OData URI.
     * @return {@code true} if the OData URI describes a request to a 'Service Document'.
     */
    protected boolean isServiceDocument(ODataUri uri) {
        return uri != null && uri.relativeUri() instanceof ServiceRootUri;
    }

    /**
     * Calculate a score for a 'Service Document Renderer' based on
     * a given OData request context and required media type.
     *
     * @param requestContext    The given OData request context.
     * @param requiredMediaType The required media type.
     * @return The calculated score.
     */
    protected int scoreServiceDocument(ODataRequestContext requestContext, MediaType requiredMediaType) {
        if (isServiceDocument(requestContext.getUri())) {
            int scoreByFormat = scoreByFormat(getFormatOption(requestContext.getUri()), requiredMediaType);
            int scoreByMediaType = scoreByMediaType(requestContext.getRequest().getAccept(), requiredMediaType);
            return max(scoreByFormat, scoreByMediaType);
        } else {
            return DEFAULT_SCORE;
        }
    }
}
