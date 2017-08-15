package com.sdl.odata.api.service;

import com.sdl.odata.api.ODataException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OData content used for streaming content.
 * Used when we want to stream response in different smaller chunks.
 */
public interface ODataContent {

    /**
     * Write the available content into the given <code>OutputStream</code>.
     *
     * @param outputStream {@link OutputStream} in which the content is written.
     */
    void write(OutputStream outputStream) throws IOException, ODataException;
}
