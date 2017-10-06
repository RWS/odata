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
package com.sdl.odata.renderer.primitive.writer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.renderer.AbstractPropertyWriter;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static java.text.MessageFormat.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Primitive writer for simple text responses,
 * like /Entity/$count, /Entity/prop/$value.
 */
public class PrimitiveWriter extends AbstractPropertyWriter {
    private static final Logger LOG = getLogger(PrimitiveWriter.class);

    public PrimitiveWriter(ODataUri oDataUri, EntityDataModel entityDataModel) throws ODataRenderException {
        super(oDataUri, entityDataModel);
    }

    @Override
    protected ChunkedActionRenderResult getPrimitivePropertyChunked(
            Object data, Type type, ChunkedStreamAction action, ChunkedActionRenderResult previousResult)
            throws ODataException {
        switch (action) {
            case START_DOCUMENT:
                return previousResult;
            case BODY_DOCUMENT:
                try {
                    previousResult.getOutputStream().write(generatePrimitiveProperty(data, type).getBytes());
                } catch (IOException e) {
                    throw new ODataRenderException("Unable to render body for primitive property", e);
                }
                return new ChunkedActionRenderResult();
            case END_DOCUMENT:
                return previousResult;
            default:
                throw new ODataRenderException(format(
                        "Unable to render primitive type value because of wrong ChunkedStreamAction: {0}",
                        action));
        }
    }

    @Override
    protected ChunkedActionRenderResult getComplexPropertyChunked(
            Object data, StructuredType type, ChunkedStreamAction action, ChunkedActionRenderResult previousResult)
            throws ODataException {
        try {
            previousResult.getOutputStream().write(generateComplexProperty(data, type).getBytes());
        } catch (IOException e) {
            throw new ODataRenderException("Unable to render Complex property", e);
        }
        return previousResult;
    }

    @Override
    protected String generateNullPropertyString() throws ODataException {
        return "";
    }

    @Override
    protected String generatePrimitiveProperty(Object data, Type type) throws ODataException {
        if (isCollection(data)) {
            if (!((List) data).isEmpty()) {
                return ((List) data).get(0).toString();
            }
            return "";
        }

        return data.toString();
    }

    @Override
    protected String generateComplexProperty(Object data, StructuredType type) throws ODataException {
        throw new ODataRenderException("Complex property is not supported by primitive renderer");
    }
}
