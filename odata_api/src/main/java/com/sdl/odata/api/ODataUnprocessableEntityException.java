/**
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
package com.sdl.odata.api;

/**
 * Exception thrown when trying to insert or update an entity
 * when mandatory field is missing and unsuitable for processing.
 */
public class ODataUnprocessableEntityException extends ODataClientException {

  public ODataUnprocessableEntityException(String message) {
    super(ODataErrorCode.PROCESSOR_ERROR, message);
  }

  public ODataUnprocessableEntityException(String message, String target) {
    super(ODataErrorCode.PROCESSOR_ERROR, message, target);
  }

  public ODataUnprocessableEntityException(String message, Throwable cause) {
    super(ODataErrorCode.PROCESSOR_ERROR, message, cause);
  }

  public ODataUnprocessableEntityException(String message, String target, Throwable cause) {
    super(ODataErrorCode.PROCESSOR_ERROR, message, target, cause);
  }

}
