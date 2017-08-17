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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OData response.
 *
 * This is an immutable class with a builder to create instances.
 * See for example http://www.javacodegeeks.com/2013/01/the-builder-pattern-in-practice.html
 */
public final class ODataResponse extends ODataRequestResponseBase {

    /**
     * Response status value.
     */
    public enum Status {
        /**
         * OK Status.
         */
        OK(200),
        /**
         * Created Status.
         */
        CREATED(201),
        /**
         * Accepted Status.
         */
        ACCEPTED(202),
        /**
         * No content Status.
         */
        NO_CONTENT(204),
        /**
         * Not modified Status.
         */
        NOT_MODIFIED(304),
        /**
         * Bad Request Status.
         */
        BAD_REQUEST(400),
        /**
         * Unauthorized Status.
         */
        UNAUTHORIZED(401),
        /**
         * Forbidden Status.
         */
        FORBIDDEN(403),
        /**
         * Not Found Status.
         */
        NOT_FOUND(404),
        /**
         * Method Not Allowed Status.
         */
        METHOD_NOT_ALLOWED(405),
        /**
         * Not Acceptable Status.
         */
        NOT_ACCEPTABLE(406),
        /**
         * Gone Status.
         */
        GONE(410),
        /**
         * Request Entity Too Large Status.
         */
        REQUEST_ENTITY_TOO_LARGE(413),
        /**
         * Unsupported Media Type Status.
         */
        UNSUPPORTED_MEDIA_TYPE(415),
        /**
         * Internal Server Error Status.
         */
        INTERNAL_SERVER_ERROR(500),
        /**
         * Not Implemented Status.
         */
        NOT_IMPLEMENTED(501),
        /**
         * Service Unavailable Status.
         */
        SERVICE_UNAVAILABLE(503);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        /**
         * Gets the code for this status value.
         *
         * @return The code for this status value.
         */
        public int getCode() {
            return code;
        }

        /**
         * Gets the status value for the given code.
         *
         * @param code The code to get the status value for.
         * @return The status value corresponding to the specified code.
         * @throws java.lang.IllegalArgumentException If there is no status value corresponding to the specified code.
         */
        public static Status forCode(int code) {
            for (Status status : Status.values()) {
                if (status.code == code) {
                    return status;
                }
            }

            throw new IllegalArgumentException("Invalid status code: " + code);
        }

        @Override
        public String toString() {
            return code + " " + name();
        }
    }

    /**
     * Builder for {@code ODataResponse} objects.
     */
    public static class Builder {
        private Status status;
        private final Map<String, String> headersMap = new HashMap<>();

        private byte[] body;
        private ODataContent oDataContent;

        public Builder setStatus(Status builderStatus) {
            this.status = builderStatus;
            return this;
        }

        public Builder setHeader(String name, String value) {
            this.headersMap.put(name, value);
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headersMap.putAll(headers);
            return this;
        }

        public Builder setContentType(MediaType contentType) {
            this.headersMap.put(HeaderNames.CONTENT_TYPE, contentType.toString());
            return this;
        }

        public Builder setBody(byte[] builderBody) {
            this.body = builderBody;
            return this;
        }

        public Builder setODataContent(ODataContent builderODataContent) {
            this.oDataContent = builderODataContent;
            return this;
        }

        public Builder setBodyText(String bodyText, String charset) throws UnsupportedEncodingException {
            this.body = bodyText.getBytes(charset);
            return this;
        }

        public ODataResponse build() {
            return new ODataResponse(this);
        }
    }

    private final Status status;

    private ODataResponse(Builder builder) {
        super(Collections.unmodifiableMap(builder.headersMap), builder.body, builder.oDataContent);

        if (builder.status == null) {
            throw new IllegalArgumentException("Status is required");
        }


        this.status = builder.status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status.toString();
    }
}
