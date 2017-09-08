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

import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.service.HeaderNames;
import com.sdl.odata.api.service.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Chunked action result.
 */
public class ChunkedActionRenderResult {

    private String result;
    private ByteArrayOutputStream outputStream;
    private Object writer;
    private Map<String, String> headers = new HashMap<>();

    public ChunkedActionRenderResult() {
    }

    public ChunkedActionRenderResult(String result, ByteArrayOutputStream outputStream, Object writer) {
        this.result = result;
        this.outputStream = outputStream;
        this.writer = writer;
    }

    public ChunkedActionRenderResult(String result) {
        this(result, null, null);
    }

    public String getResult() {
        return result;
    }

    public Object getWriter() throws ODataRenderException {
        return writer;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public int getOutputStreamContentLength() {
        try {
            return outputStream == null ? 0 : outputStream.toString(UTF_8.name()).length();
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }
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
}
