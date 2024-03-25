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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.NavigationPropertyBinding;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.NavigationPropertyBinding}.
 *
 */
public final class NavigationPropertyBindingImpl implements NavigationPropertyBinding {

    private final String path;
    private final String target;

    public NavigationPropertyBindingImpl(String path, String target) {
        this.path = path;
        this.target = target;
    }

    public String getPath() {
        return path;
    }

    public String getTarget() {
        return target;
    }
}
