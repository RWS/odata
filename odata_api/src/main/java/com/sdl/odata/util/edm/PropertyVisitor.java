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
package com.sdl.odata.util.edm;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.StructuralProperty;

/**
 * Callback interface called by {@code EntityDataModelUtil.visitProperties}.
 * @param <E> OData Exception
 */
public interface PropertyVisitor<E extends ODataException> {
    void visit(StructuralProperty property) throws E;
}
