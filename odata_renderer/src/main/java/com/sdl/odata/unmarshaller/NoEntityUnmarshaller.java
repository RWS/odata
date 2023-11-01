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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshaller;
import org.springframework.stereotype.Component;

/**
 * Unmarshaller which doesn't unmarshall anything, and always returns null.
 */
@Component
public class NoEntityUnmarshaller implements ODataUnmarshaller {

    @Override
    public int score(ODataRequestContext requestContext) {
        // Return the lowest possible score; we want this unmarshaller to be selected only
        // if there is no other unmarshaller available.
        return 1;
    }

    @Override
    public Object unmarshall(ODataRequestContext requestContext)
            throws ODataException {
        return null;
    }
}
