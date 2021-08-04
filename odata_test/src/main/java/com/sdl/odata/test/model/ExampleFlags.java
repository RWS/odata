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
package com.sdl.odata.test.model;

import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;

/**
 * Example of a flags enum for testing.
 */
@EdmEnum(name = "MyFlags", underlyingType = PrimitiveType.INT16, flags = true)
public enum ExampleFlags {
    /**
     * Has name value.
     */
    HAS_NAME,
    /**
     * Has the description value.
     */
    HAS_DESCRIPTION,
    /**
     * Is special value.
     */
    IS_SPECIAL
}
