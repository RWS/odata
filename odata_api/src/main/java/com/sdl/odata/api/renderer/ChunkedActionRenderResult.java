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

import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Chunked action result.
 */
public class ChunkedActionRenderResult {

    private String result;
    private ByteArrayOutputStream outputStream;
    private XMLStreamWriter writer;

    public ChunkedActionRenderResult(String result, XMLStreamWriter writer) {
        this.result = result;
        this.writer = writer;
    }

    public ChunkedActionRenderResult(String result, ByteArrayOutputStream outputStream, XMLStreamWriter writer) {
        this.result = result;
        this.outputStream = outputStream;
        this.writer = writer;
    }

    public ChunkedActionRenderResult(String result) {
        this(result, null);
    }

    public String getResult() {
        return result;
    }

    public XMLStreamWriter getWriter() {
        return writer;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public int getOutputStreamContentLength() throws UnsupportedEncodingException {
        return outputStream.toString(UTF_8.name()).length();
    }
}
