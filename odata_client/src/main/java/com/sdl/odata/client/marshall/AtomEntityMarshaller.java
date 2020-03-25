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
package com.sdl.odata.client.marshall;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.marshall.ODataEntityMarshaller;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.AbstractAtomRenderer;
import com.sdl.odata.renderer.atom.AtomRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;

/**
 * Atom marshaller implementation of {@link ODataEntityMarshaller}.
 */
public class AtomEntityMarshaller implements ODataEntityMarshaller {

    private static final Logger LOG = LoggerFactory.getLogger(AtomEntityMarshaller.class);

    private String url;
    private EntityDataModel entityDataModel;
    private AbstractAtomRenderer atomRenderer;

    public AtomEntityMarshaller(Iterable<Class<?>> edmEntityClasses, String url) {
        this(edmEntityClasses, url, new AtomRenderer());
    }

    protected AtomEntityMarshaller(Iterable<Class<?>> edmEntityClasses, String url, AbstractAtomRenderer atomRenderer) {
        this.url = url;
        this.atomRenderer = atomRenderer;
        try {
            LOG.debug("Building entity data model...");
            this.entityDataModel = buildEntityDataModel(edmEntityClasses);
        } catch (ODataEdmException | RuntimeException e) {
            throw new ODataClientRuntimeException("Cannot build OData entity model", e);
        }
    }

    @Override
    public String marshallEntity(Object oDataEntity, ODataClientQuery query) throws ODataClientException {
        String result;
        ODataResponse.Builder builder = new ODataResponse.Builder()
                .setStatus(OK);
        try {
            String encodedServiceQueryUrl = url + "/" +
                    URLEncoder.encode(query.getEdmEntityName(), StandardCharsets.UTF_8.name());
            ODataUri oDataServiceUri = new ODataParserImpl().parseUri(encodedServiceQueryUrl, entityDataModel);
            // marshall the entity Atom XML into the response
            atomRenderer.render(buildODataPostContext(oDataServiceUri), QueryResult.from(oDataEntity), builder);
            // return the text content of the response
            result = builder.build().getBodyText(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException | ODataException e) {
            throw new ODataClientException("Unable to marshall OData entity", e);
        }
        return result;
    }

    @Override
    public String marshallEntities(List<?> oDataEntities, ODataClientQuery query) throws ODataClientException {
        // use marshallEntity method implementation as AtomRenderer takes in count if the marshalling object is a List
        return marshallEntity(oDataEntities, query);
    }

    private ODataRequestContext buildODataPostContext(ODataUri serviceUri) throws
            UnsupportedEncodingException, ODataEdmException {
        ODataRequest request = new ODataRequest.Builder()
                .setUri(serviceUri.serviceRoot())
                .setBodyText("", StandardCharsets.UTF_8.name())
                .setAccept(ATOM_XML)
                .setMethod(POST)
                .build();
        return new ODataRequestContext(request, serviceUri, entityDataModel);
    }

    private EntityDataModel buildEntityDataModel(Iterable<Class<?>> edmEntityClasses) throws ODataEdmException {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        return factory.addClasses(edmEntityClasses).buildEntityDataModel();
    }

}
