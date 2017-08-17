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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Superclass for {@code ODataRequest} and {@code ODataResponse} which contains common functionality for requests
 * and responses.
 */
public abstract class ODataRequestResponseBase {

    private Map<String, String> headers;
    private final byte[] body;
    private ODataContent streamingContent;

    protected ODataRequestResponseBase(Map<String, String> headers, byte[] body, ODataContent streamingContent) {
        this.headers = headers;
        this.body = body;
        this.streamingContent = streamingContent;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String name) {
        // NOTE: HTTP header names are case-insensitive; search through the map with a case-insensitive search
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public List<MediaType> getAccept() {
        String acceptHeader = getHeader(HeaderNames.ACCEPT);
        if (isNullOrEmpty(acceptHeader)) {
            return Collections.emptyList();
        }

        List<MediaType> mediaTypesBuilder = new ArrayList<>();
        for (String part : acceptHeader.split(",")) {
            mediaTypesBuilder.add(MediaType.fromString(part.trim()));
        }

        return Collections.unmodifiableList(mediaTypesBuilder);
    }


    public MediaType getContentType() {
        String contentTypeHeader = getHeader(HeaderNames.CONTENT_TYPE);
        return (isNullOrEmpty(contentTypeHeader)) ?
                null : MediaType.fromString(contentTypeHeader);
    }

    public byte[] getBody() {
        return body;
    }

    public ODataContent getStreamingContent() {
        return streamingContent;
    }

    public String getBodyText(String charset) throws UnsupportedEncodingException {
        return new String(body, charset);
    }
}
