package com.sdl.odata.renderer.primitive.writer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.renderer.AbstractPropertyWriter;

import java.util.List;

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
