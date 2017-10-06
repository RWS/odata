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
package com.sdl.odata.api.renderer;

import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.service.HeaderNames;
import com.sdl.odata.api.service.MediaType;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Chunked action result. Used in renderer to bind together result chunks.
 */
public class ChunkedActionRenderResult {

    private OutputStream outputStream;
    private Object writer;
    private Type type;
    private boolean typeValidated;
    private Map<String, String> headers = new HashMap<>();

    public ChunkedActionRenderResult() {
    }

    public ChunkedActionRenderResult(OutputStream outputStream) {
        this(outputStream, null);
    }

    public ChunkedActionRenderResult(OutputStream outputStream, Object writer) {
        this.outputStream = outputStream;
        this.writer = writer;
    }

    public Object getWriter() throws ODataRenderException {
        return writer;
    }

    public void setWriter(Object writer) {
        this.writer = writer;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setContentType(MediaType mediaType) {
        headers.put(HeaderNames.CONTENT_TYPE, mediaType.toString());
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isTypeValidated() {
        return typeValidated;
    }

    public void setTypeValidated(boolean typeValidated) {
        this.typeValidated = typeValidated;
    }
}
