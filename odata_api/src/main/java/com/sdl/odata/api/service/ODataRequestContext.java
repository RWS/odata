/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.service;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;

/**
 * The OData Request Context.
 */
public final class ODataRequestContext {

    private final ODataRequest request;
    private final ODataUri uri;
    private final EntityDataModel entityDataModel;

    public ODataRequestContext(ODataRequest request, ODataUri uri, EntityDataModel entityDataModel) {
        this.request = request;
        this.uri = uri;
        this.entityDataModel = entityDataModel;
    }

    public ODataRequestContext(ODataRequest request, EntityDataModel entityDataModel) {
        this(request, null, entityDataModel);
    }

    public ODataRequestContext withUri(ODataUri oDataUri) {
        return new ODataRequestContext(this.request, oDataUri, this.entityDataModel);
    }

    public ODataRequest getRequest() {
        return request;
    }

    public ODataUri getUri() {
        return uri;
    }

    public EntityDataModel getEntityDataModel() {
        return entityDataModel;
    }

    @Override
    public String toString() {
        return request.toString();
    }
}
