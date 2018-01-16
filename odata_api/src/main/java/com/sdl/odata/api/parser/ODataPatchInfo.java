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
package com.sdl.odata.api.parser;

import java.util.Map;

/**
 * DTO that contains data for partial entity update (PATCH).
 */
public class ODataPatchInfo {
  private final Map<String, Object> fields;
  private final Map<String, String> odataValues;
  private final Map<String, Object> links;

  public ODataPatchInfo(Map<String, Object> fields,
      Map<String, String> odataValues, Map<String, Object> links) {
    this.fields = fields;
    this.odataValues = odataValues;
    this.links = links;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public Map<String, String> getOdataValues() {
    return odataValues;
  }

  public Map<String, Object> getLinks() {
    return links;
  }
}
