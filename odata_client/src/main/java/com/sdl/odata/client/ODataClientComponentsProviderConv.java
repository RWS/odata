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
package com.sdl.odata.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.sdl.odata.client.marshall.JsonEntityMarshaller;
import com.sdl.odata.client.marshall.JsonEntityUnmarshaller;
import java.util.Properties;
import org.slf4j.Logger;


/**
 * Extends {@link AbstractODataClientComponentsProvider} for getting ODataV4 Client components.
 * Use this implementation for BasicODataClient initialization passing edm entity classes and client properties.
 */
public class ODataClientComponentsProviderConv extends AbstractODataClientComponentsProvider {

    private static final Logger LOG = getLogger(ODataClientComponentsProviderConv.class);

    public ODataClientComponentsProviderConv(Iterable<String> edmEntityClasses, Properties properties) {
        super(edmEntityClasses, properties);
   }

    protected void initComponentsProvider(Iterable<String> edmEntityClasses) {
        setEntityUnmarshaller(new JsonEntityUnmarshaller(
                getClassesForNames(edmEntityClasses), getWebServiceUrl().toString()));
        setEntityMarshaller(new JsonEntityMarshaller(getClassesForNames(edmEntityClasses),
                getWebServiceUrl().toString()));
    }
}
