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
 * OData function processor interface. A function processor is used for execution of function and function import
 * operations.
 */
public interface ODataFunctionProcessor {

    /**
     * Method to perform execution of function or function import.
     * @param requestContext    request context
     * @return                  processor result
     * @throws ODataException   exception thrown if function cannot be executed
     */
    ProcessorResult doFunction(ODataRequestContext requestContext) throws ODataException;
}
