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
package com.sdl.odata.api.service;

/**
 * The Header Names.
 */
public final class HeaderNames {

    private HeaderNames() {
    }

    /**
     * Accept.
     */
    public static final String ACCEPT = "Accept";
    /**
     * Accept Charset.
     */
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    /**
     * Prefer.
     */
    public static final String PREFER = "Prefer";
    /**
     * Preference Applied.
     */
    public static final String PREFERENCE_APPLIED = "Preference-Applied";
    /**
     * Location.
     */
    public static final String LOCATION = "Location";
    /**
     * Content Type.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * Content Language.
     */
    public static final String CONTENT_LANGUAGE = "Content-Language";
    /**
     * Content Encoding.
     */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    /**
     * Content Length.
     */
    public static final String CONTENT_LENGTH = "Content-Length";
    /**
     * TE.
     * The transfer encodings the user agent is willing to accept.
     */
    public static final String TE = "TE";
    /**
     * X-Odata-TE.
     * The duplicate of {@link HeaderNames#TE} header. Represents the transfer encodings the user agent
     * is willing to accept.
     */
    public static final String X_ODATA_TE = "X-Odata-TE";
    /**
     * Transfer encoding.
     * The form of encoding used to safely transfer the entity to the user.
     */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    /**
     * OData chunked error message property name.
     */
    public static final String ODATA_CHUNKED_ERROR_MESSAGE_PROPERTY = "ODATA_CHUNKED_ERROR_MESSAGE";
    /**
     * ETag.
     */
    public static final String ETAG = "ETag";
    /**
     * OData Version.
     */
    public static final String ODATA_VERSION = "OData-Version";
    /**
     * OData Max Version.
     */
    public static final String ODATA_MAX_VERSION = "OData-MaxVersion";
    /**
     * OData Entity Id.
     */
    public static final String ODATA_ENTITY_ID = "OData-EntityId";
    /**
     * OData Isolation.
     */
    public static final String ODATA_ISOLATION = "OData-Isolation";
}
