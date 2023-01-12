/*
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.client;

import com.sdl.odata.client.marshall.AtomEntityMarshaller;
import com.sdl.odata.client.marshall.AtomEntityUnmarshaller;
import org.slf4j.Logger;

import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * Extends {@link AbstractODataClientComponentsProvider} for getting ODataV4 Client components.
 * Use this implementation for BasicODataClient initialization passing edm entity classes and client properties.
 */
public class ODataV4ClientComponentsProvider extends AbstractODataClientComponentsProvider {

    private static final Logger LOG = getLogger(ODataV4ClientComponentsProvider.class);

    public ODataV4ClientComponentsProvider(Iterable<String> edmEntityClasses, Properties properties) {
        super(edmEntityClasses, properties);
   }

    protected void initComponentsProvider(Iterable<String> edmEntityClasses) {
        setEntityUnmarshaller(new AtomEntityUnmarshaller(
                getClassesForNames(edmEntityClasses), getWebServiceUrl().toString()));
        setEntityMarshaller(new AtomEntityMarshaller(getClassesForNames(edmEntityClasses),
                getWebServiceUrl().toString()));
    }
}
