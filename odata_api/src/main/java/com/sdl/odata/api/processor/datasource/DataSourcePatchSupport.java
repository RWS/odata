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
package com.sdl.odata.api.processor.datasource;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataPatchInfo;
import com.sdl.odata.api.parser.ODataUri;

/**
 * DataSource that support partial entity update (PATCH).
 */
public interface DataSourcePatchSupport extends DataSource {

  /**
   * Patches an entity in the data storage.
   *
   * @param uri The OData URI.
   * @param patchInfo Patch data to update the entity.
   * @param entityDataModel The entity data model.
   * @return The updated entity.
   * @throws ODataException If the operation fails.
   */
  Object patch(ODataUri uri, ODataPatchInfo patchInfo, EntityDataModel entityDataModel)
      throws ODataException;
}
