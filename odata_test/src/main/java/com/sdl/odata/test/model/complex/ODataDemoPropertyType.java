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
package com.sdl.odata.test.model.complex;

import com.sdl.odata.api.edm.annotations.EdmEnum;


/**
 * This enum class mirrors ODataCarPropertyType, but with extra Edm annotations.
 */
@EdmEnum(namespace = "ODataDemo")
public enum ODataDemoPropertyType {
    /**
     * Classification property type.
     */
    CLASSIFICATION,
    /**
     * Boolean property type.
     */
    BOOLEAN,

    /**
     * Integer property type.
     */
    INTEGER,

    /**
     * Float property type.
     */
    FLOAT,

    /**
     * Set of strings property type.
     */
    SET,

    /**
     * String property type.
     */
    STRING,

    /**
     * Version property type, e.g. 1.0.4.
     */
    VERSION


}

