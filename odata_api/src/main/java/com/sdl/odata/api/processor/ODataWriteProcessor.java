/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.api.processor;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.service.ODataRequestContext;

/**
 * OData write processor interface. A write processor is used for write operations (creating, updating or deleting
 * entities).
 *
 */
public interface ODataWriteProcessor {

    /**
     * This method persists given edm entity. In case of any error it throws ODataException
     *
     * @param requestContext which contains necessary OData request, OData uri and entity model
     * @param data           edm entity to be persisted
     * @return The result of the write operation
     * @throws ODataException in case of any error
     */
    ProcessorResult write(ODataRequestContext requestContext, Object data) throws ODataException;

}
