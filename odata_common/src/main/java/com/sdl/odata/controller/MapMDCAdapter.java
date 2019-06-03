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
package com.sdl.odata.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.spi.MDCAdapter;

public class MapMDCAdapter implements MDCAdapter
{
    private Map<String, String> contextMap;
    public MapMDCAdapter(Map<String, String> contextMap)
    {
        setContextMap(contextMap);
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return new HashMap<>(contextMap);
    }

    @Override public String get(String key) {
        return contextMap.get(key);
    }

    @Override public void clear() {
        contextMap.clear();
    }

    @Override public void put(String key, String val) {
        contextMap.put(key, val);
    }

    @Override public void remove(String key) {
        contextMap.remove(key);
    }

    @Override public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap == null ? new HashMap<>() : contextMap;
    }
}
