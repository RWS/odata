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
package com.sdl.odata.renderer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;

import java.io.OutputStream;

/**
 * Property stream writer. Writer API needed for chunked requests handling.
 */
public interface PropertyStreamWriter {

    /**
     * Get property start document. Includes heading info like meta.
     *
     * @param data         data to serialize
     * @param outputStream {@link OutputStream} to write response to
     * @return heading info
     * @throws ODataException OData exception
     */
    ChunkedActionRenderResult getPropertyStartDocument(Object data, OutputStream outputStream) throws ODataException;

    /**
     * Get property body document. Includes serialized entities.
     *
     * @param data           data to serialize
     * @param previousResult previous result
     * @return serialized entities
     * @throws ODataException OData exception
     */
    ChunkedActionRenderResult getPropertyBodyDocument(Object data, ChunkedActionRenderResult previousResult)
            throws ODataException;

    /**
     * Get property body document. Includes closing info to form full proper response.
     *
     * @param data           data to serialize
     * @param previousResult previous result
     * @throws ODataException OData exception
     */
    void getPropertyEndDocument(Object data, ChunkedActionRenderResult previousResult) throws ODataException;

    /**
     * Action for chunked stream requests. Defines whether this is a start of document, body or end.
     */
    enum ChunkedStreamAction {
        START_DOCUMENT,
        BODY_DOCUMENT,
        END_DOCUMENT
    }
}
