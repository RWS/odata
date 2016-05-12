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
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.renderer.AbstractPropertyWriter;

import java.util.List;

/**
 * Primitive writer for simple text responses,
 * like /Entity/$count, /Entity/prop/$value.
 */
public class PrimitiveWriter extends AbstractPropertyWriter {

    public PrimitiveWriter(ODataUri oDataUri, EntityDataModel entityDataModel) throws ODataRenderException {
        super(oDataUri, entityDataModel);
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
