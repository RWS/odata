package com.sdl.odata.api.service;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.renderer.ODataRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.stream.Stream;

import static com.sdl.odata.api.service.ODataResponse.Status.OK;

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
        Iterator resultDataIterator = ((Stream) queryResult.getData()).iterator();
        while (resultDataIterator.hasNext()) {
            Object nextResultChunk = resultDataIterator.next();
            ODataResponse.Builder responseBuilder = new ODataResponse.Builder().setStatus(OK);
            oDataRenderer.render(oDataRequestContext, QueryResult.from(nextResultChunk), responseBuilder);
            byte[] resultToGo = responseBuilder.build().getBody();
            // TODO: most likely we should cut off some OData strings to contain only entities info
            // and append once per request at the beginning and at the end.
            outputStream.write(resultToGo);
            outputStream.flush();
        }
    }
}
