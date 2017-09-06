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

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * OData service content streamer. Streams content into {@link OutputStream}.
 */
public class ODataContentStreamer implements ODataContent {

    private ODataRenderer oDataRenderer;
    private ODataRequestContext oDataRequestContext;
    private QueryResult queryResult;

    public ODataContentStreamer(ODataRenderer oDataRenderer, ODataRequestContext oDataRequestContext,
                                QueryResult queryResult) {
        this.oDataRenderer = oDataRenderer;
        this.oDataRequestContext = oDataRequestContext;
        this.queryResult = queryResult;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, ODataException {
        ChunkedActionRenderResult startRenderResult = oDataRenderer.renderStart(oDataRequestContext, queryResult);
        writeWithFlush(outputStream, startRenderResult.getResult());

        Iterator resultDataIterator = ((Stream) queryResult.getData()).iterator();
        ChunkedActionRenderResult bodyRenderResult = null;
        while (resultDataIterator.hasNext()) {
            Object nextResultChunk = resultDataIterator.next();
            bodyRenderResult = oDataRenderer.renderBody(
                    oDataRequestContext, QueryResult.from(nextResultChunk), startRenderResult);
            writeWithFlush(outputStream, bodyRenderResult.getResult());
        }

        writeWithFlush(outputStream, oDataRenderer.renderEnd(oDataRequestContext, queryResult,
                bodyRenderResult == null ? startRenderResult : bodyRenderResult));
    }

    private void writeWithFlush(OutputStream outputStream, String content) throws IOException {
        outputStream.write(content.getBytes());
        outputStream.flush();
    }
}
