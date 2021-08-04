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
package com.sdl.odata.api;

/**
 * The OData Error Codes.
 */
public enum ODataErrorCode {
    /**
     * Uri Parse Error.
     */
    URI_PARSE_ERROR(1000),
    /**
     * Batch Parse Error.
     */
    BATCH_PARSE_ERROR(1200),
    /**
     * Batch Render Error.
     */
    BATCH_RENDER_ERROR(1300),
    /**
     * Bad Request Error.
     */
    BAD_REQUEST_ERROR(1500),
    /**
     * Unsupported Media Type Error.
     */
    UNSUPPORTED_MEDIA_TYPE_ERROR(1700),
    /**
     * Not Implemented Error.
     */
    NOT_IMPLEMENTED_ERROR(1800),
    /**
     * Unmarshaller Error.
     */
    UNMARSHALLER_ERROR(2000),
    /**
     * Duplicate Key Error.
     */
    DUPLICATE_KEY_ERROR(3000),
    /**
     * Entity Already Linked Error.
     */
    ENTITY_ALREADY_LINKED_ERROR(3400),
    /**
     * Entity Not Found Error.
     */
    ENTITY_NOT_FOUND_ERROR(3500),
    /**
     * Target Type Error.
     */
    TARGET_TYPE_ERROR(3600),
    /**
     * Renderer Error.
     */
    RENDERER_ERROR(4000),
    /**
     * EDM Error.
     */
    EDM_ERROR(6000),
    /**
     * Data Source Error.
     */
    DATA_SOURCE_ERROR(7000),
    /**
     * Query Retrieval Error.
     */
    QUERY_RETRIEVAL_ERROR(7500),
    /**
     * Processor Error.
     */
    PROCESSOR_ERROR(8000),
    /**
     * Unknown Error.
     */
    UNKNOWN_ERROR(9000);

    private final int code;

    ODataErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
