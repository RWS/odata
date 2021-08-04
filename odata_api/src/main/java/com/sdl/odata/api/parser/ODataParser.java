/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.parser;

import com.sdl.odata.api.edm.model.EntityDataModel;

/**
 * OData parser interface.
 */
public interface ODataParser {

    /**
     * Parse an OData URI into an {@code ODataUri} object, which is the root of an abstract syntax tree that describes
     * the URI.
     *
     * @param uri             The URI to parse.
     * @param entityDataModel The entity data model.
     * @return An {@code ODataUri} object.
     * @throws ODataUriParseException If an error occurs while parsing.
     */
    ODataUri parseUri(String uri, EntityDataModel entityDataModel) throws ODataUriParseException;

    /**
     * Parse a given 'resource path' into a {@code ResourcePath} object.
     *
     * @param resourcePath    The given 'resource path' to parse.
     * @param entityDataModel The entity data model.
     * @return The parsed {@code ResourcePath} object.
     * @throws ODataUriParseException If an error occurs while parsing.
     */
    ResourcePath parseResourcePath(String resourcePath, EntityDataModel entityDataModel) throws ODataUriParseException;
}
