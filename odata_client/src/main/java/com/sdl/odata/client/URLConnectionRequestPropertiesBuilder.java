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
package com.sdl.odata.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to build URLConnection request properties.
 */
public class URLConnectionRequestPropertiesBuilder {

    private static final String COOKIES_SEPARATOR = "; ";

    private Map<String, String> requestProperties = new HashMap<>();

    /**
     * Add provided cookie to 'Cookie' request property.
     * @param cookieName The cookie name
     * @param cookieValue The ccokie value
     * @return this
     */
    public URLConnectionRequestPropertiesBuilder withCookie(String cookieName, String cookieValue) {
        if (requestProperties.containsKey("Cookie")) {
            // there are existing cookies so just append the new cookie at the end
            final String cookies = requestProperties.get("Cookie");
            requestProperties.put("Cookie", cookies + COOKIES_SEPARATOR + buildCookie(cookieName, cookieValue));
        } else {
            // this is the first cookie to be added
            requestProperties.put("Cookie", buildCookie(cookieName, cookieValue));
        }
        return this;
    }

    private String buildCookie(String cookieName, String cookieValue) {
        return cookieName + "=" + cookieValue;
    }

    /**
     * Add access toke as 'Authorization' request property.
     * @param accessToken The access token to be added
     * @return this
     */
    public URLConnectionRequestPropertiesBuilder withAccessToken(String accessToken) {
        requestProperties.put("Authorization", "Bearer " + accessToken);
        return this;
    }

    public Map<String, String> build() {
        return requestProperties;
    }

}
