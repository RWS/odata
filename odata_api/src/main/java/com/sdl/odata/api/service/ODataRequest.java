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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static java.util.Collections.unmodifiableMap;

/**
 * OData request.
 * <p>
 * This is an immutable class with a builder to create instances.
 * See for example http://www.javacodegeeks.com/2013/01/the-builder-pattern-in-practice.html
 */
public final class ODataRequest extends ODataRequestResponseBase {

    /**
     * Request method.
     */
    public enum Method {
        /**
         * GET request.
         */
        GET,
        /**
         * POST request.
         */
        POST,
        /**
         * PUT request.
         */
        PUT,
        /**
         * DELETE request.
         */
        DELETE,
        /**
         * PATCH request.
         */
        PATCH
    }

    /**
     * Builder for {@code ODataRequest} objects.
     */
    public static class Builder {
        private Method method;
        private String uri;
        private final Map<String, String> headersBuilder = new HashMap<>();
        private byte[] body;
        private Map<Class<?>, Object> additionalData = new HashMap<>();

        public Builder setMethod(Method builderMethod) {
            this.method = builderMethod;
            return this;
        }

        public Builder setUri(String builderUri) {
            this.uri = builderUri;
            return this;
        }

        public Builder setHeader(String name, String value) {
            this.headersBuilder.put(name, value);
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headersBuilder.putAll(headers);
            return this;
        }

        public Builder setAccept(MediaType... mediaTypes) {


            this.headersBuilder.put(
                    HeaderNames.ACCEPT,
                    Arrays.stream(mediaTypes).map(Object::toString).collect(Collectors.joining(", ")));
            return this;
        }

        public Builder setContentType(MediaType mediaType) {
            this.headersBuilder.put(HeaderNames.CONTENT_TYPE, mediaType.toString());
            return this;
        }

        public Builder setPrefer(String... prefers) {
            this.headersBuilder.put(HeaderNames.PREFER, Stream.of(prefers).collect(Collectors.joining(",")));
            return this;
        }

        public Builder setBody(byte[] builderBody) {
            this.body = builderBody;
            return this;
        }

        public Builder setBodyText(String bodyText, String charset) throws UnsupportedEncodingException {
            this.body = bodyText.getBytes(charset);
            return this;
        }

        public Builder addAdditionalData(Object data) {
            additionalData.put(data.getClass(), data);
            return this;
        }

        public ODataRequest build() {
            return new ODataRequest(this);
        }
    }

    private final Method method;
    private final String uri;
    private final Map<Class<?>, Object> additionalData;

    private ODataRequest(Builder builder) {
        super(unmodifiableMap(builder.headersBuilder), builder.body, null);

        if (builder.method == null) {
            throw new IllegalArgumentException("Method is required");
        }

        if (isNullOrEmpty(builder.uri)) {
            throw new IllegalArgumentException("URI is required");
        }

        this.method = builder.method;
        this.uri = builder.uri;
        this.additionalData = unmodifiableMap(builder.additionalData);
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public List<String> getPrefer() {
        String preferHeader = getHeader(HeaderNames.PREFER);
        if (isNullOrEmpty(preferHeader)) {
            return Collections.emptyList();
        }

        List<String> preferList = Arrays.stream(preferHeader.split(",")).map(String::trim).collect(Collectors.toList());

        return Collections.unmodifiableList(preferList);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getAdditionalData(Class<T> clazz) {
        if (additionalData.containsKey(clazz)) {
            return Optional.of((T) additionalData.get(clazz));
        }

        for (Map.Entry<Class<?>, Object> dataEntry : additionalData.entrySet()) {
            if (clazz.isAssignableFrom(dataEntry.getKey())) {
                return Optional.of((T) dataEntry.getValue());
            }
        }

        return Optional.empty();

    }

    @Override
    public String toString() {
        return method + " " + uri;
    }
}
