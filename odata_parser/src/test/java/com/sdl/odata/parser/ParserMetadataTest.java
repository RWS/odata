/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.MetadataUri;
import com.sdl.odata.api.parser.ODataUriParseException;
import com.sdl.odata.api.parser.RelativeUri;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Parser Metadata Test.
 *
 */
public class ParserMetadataTest extends ParserTestSuite {

    @Test
    public void testEmptyMetadataFormat() throws ODataException {
        uri = parser.parseUri(SERVICE_ROOT + "$metadata", model);
        RelativeUri relative = uri.relativeUri();
        assertTrue(relative instanceof MetadataUri);

        MetadataUri metadata = (MetadataUri) relative;
        assertTrue(metadata.format().isEmpty());
        assertTrue(metadata.context().isEmpty());
    }

    @Test
    public void testXmlAndJsonFormat() throws ODataException {
        // xml
        uri = parser.parseUri(SERVICE_ROOT + "$metadata?$format=xml", model);
        MetadataUri metadata = (MetadataUri) uri.relativeUri();
        assertEquals("application", metadata.format().get().getType());
        assertEquals("xml", metadata.format().get().getSubType());
        // json
        uri = parser.parseUri(SERVICE_ROOT + "$metadata?$format=json", model);
        metadata = (MetadataUri) uri.relativeUri();
        assertEquals("application", metadata.format().get().getType());
        assertEquals("json", metadata.format().get().getSubType());
    }

    @Test
    public void testNonExistentFormat() {
        assertThrows(ODataUriParseException.class, () ->
                uri = parser.parseUri(SERVICE_ROOT + "$metadata?$format=superman", model)
        );
    }
}
