/*
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
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
package com.sdl.odata.client.api.exception;

/**
 * An exception which records the details of an OData client parser exception.
 */
public class ODataClientParserException extends ODataClientException {

    private final String responseFragment;
    private final String fullResponse;

    public ODataClientParserException(String message, Throwable e, String responseFragment, String fullResponse) {
        super(message, e);
        this.responseFragment = responseFragment;
        this.fullResponse = fullResponse;
    }

    public ODataClientParserException(String message, Throwable e, String fullResponse) {
        super(message, e);
        this.responseFragment = null;
        this.fullResponse = fullResponse;
    }

    public String getResponseFragment() {
        return responseFragment;
    }

    public String getFullResponse() {
        return fullResponse;
    }
}
