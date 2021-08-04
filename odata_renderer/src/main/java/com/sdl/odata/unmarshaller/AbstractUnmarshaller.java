/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.unmarshaller.ODataUnmarshaller;

import static com.sdl.odata.renderer.AbstractRenderer.DEFAULT_SCORE;
import static com.sdl.odata.renderer.AbstractRenderer.MAXIMUM_FORMAT_SCORE;

/**
 * Any type of unmarshaller (atom or json) should extend from this class.
 * So that common functionality can be abstracted.
 *
 */
public abstract class AbstractUnmarshaller implements ODataUnmarshaller {
    /**
     * This method checks request method is PUT or POST or PATCH.
     *
     * @param request is ODataRequest
     * @return true if requested method is PUT or POST or PATCH otherwise false
     */
    protected boolean isRightMethodForUnmarshall(ODataRequest request) {
        ODataRequest.Method method = request.getMethod();
        return isPostMethod(method) || isPatchMethod(method) || isPutMethod(method);
    }

    /**
     * This method specifies requested method is POST or not.
     *
     * @param method is ODataRequest.Method
     * @return true if requested method is POST
     */
    protected boolean isPostMethod(ODataRequest.Method method) {
        return ODataRequest.Method.POST.equals(method);
    }

    /**
     * This method specifies requested method is PUT or not.
     *
     * @param method is ODataRequest.Method
     * @return true if requested method is PUT
     */
    protected boolean isPutMethod(ODataRequest.Method method) {
        return ODataRequest.Method.PUT.equals(method);
    }

    /**
     * This method specifies requested method is PATCH or not.
     *
     * @param method is ODataRequest.Method
     * @return true if requested method is PATCH
     */
    protected boolean isPatchMethod(ODataRequest.Method method) {
        return ODataRequest.Method.PATCH.equals(method);
    }

    protected boolean isDeleteMethod(ODataRequest.Method method) {
        return ODataRequest.Method.DELETE.equals(method);
    }

    /**
     * Calculates score based on given media type.
     *
     * @param contentTypeFromRequest content type from the ODatRequest
     * @param expectedTypes          is variable arguments
     * @return integer value which represent the score
     */
    protected int score(MediaType contentTypeFromRequest, MediaType... expectedTypes) {
        if (contentTypeFromRequest == null) {
            return DEFAULT_SCORE;
        }
        for (MediaType expected : expectedTypes) {
            if (contentTypeFromRequest.matches(expected)) {
                return MAXIMUM_FORMAT_SCORE;
            }
        }
        return DEFAULT_SCORE;
    }
}
