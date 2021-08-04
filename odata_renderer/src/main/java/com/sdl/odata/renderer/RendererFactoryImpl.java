/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.renderer;

import com.sdl.odata.api.renderer.ODataRenderer;
import com.sdl.odata.api.renderer.RendererFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Spring based implementation of the renderer factory, it finds all available renderers in the spring context.
 */
@Component
public class RendererFactoryImpl implements RendererFactory {

    @Autowired
    private List<ODataRenderer> rendererList;

    @Override
    public List<ODataRenderer> getRenderers() {
        return rendererList;
    }
}
