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
package com.sdl.odata.api.service;

/**
 * The Change Set Entity.
 */
public class ChangeSetEntity {

    private final Object odataEntity;
    private final ODataRequestContext requestContext;
    private String changeSetId;

    /**
     * Constructor used to initialize each change set item within a batch request.
     *
     * @param requestContext Corresponding individual request context.
     * @param odataEntity    Entity data that contains OData Entity Information. Empty String for Delete operation.
     * @param changeSetId    Changeset Id including changeset prefix
     */
    public ChangeSetEntity(String changeSetId, ODataRequestContext requestContext, Object odataEntity) {
        this.requestContext = requestContext;
        this.odataEntity = odataEntity;
        this.changeSetId = changeSetId;
    }

    public Object getOdataEntity() {
        return odataEntity;
    }

    public ODataRequestContext getRequestContext() {
        return requestContext;
    }

    public String getChangeSetId() {
        return changeSetId;
    }

}
