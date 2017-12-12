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

import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.JSON;

import com.sdl.odata.client.api.exception.ODataClientException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * OData Client for Convergence purposes.
 */
public class ODataClientConv extends DefaultODataClient {

  @Override
  public Object createEntity(Map<String, String> requestProperties, Object entity) {
    BasicODataClientQuery query = buildQueryForEntity(entity);
    String entitySetName = query.getEdmEntityName();
    try {
      // encoding can be an issue due to tomcat's validation
      URL endpointUrl = new URL(super.getUrlToCall(entitySetName, false, null));
      String marshalledEntity = componentsProvider.getMarshaller().marshallEntity(entity, query);
      String createdEntity = componentsProvider.getEndpointCaller()
          .doPostEntity(requestProperties, endpointUrl, marshalledEntity, JSON, ATOM_XML);
      return componentsProvider.getUnmarshaller().unmarshallEntity(createdEntity, query);
    } catch (ODataClientException e) {
      throw formFailedRequestException(e, entitySetName);
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw formFailedUrlFormingException(e, entitySetName);
    }
  }

  @Override
  protected BasicODataClientQuery buildQueryForEntity(Object entity) {
    return new BasicODataClientQuery.Builder()
        .withEntityType(entity.getClass())
        .withExpandParameters("*")
        .build();
  }


}
