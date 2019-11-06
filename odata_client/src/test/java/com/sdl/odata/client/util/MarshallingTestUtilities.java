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
package com.sdl.odata.client.util;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.EntityCollectionPath;
import com.sdl.odata.api.parser.EntitySetPath;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.atom.AtomRenderer;
import com.sdl.odata.renderer.xml.XMLValueRenderer;
import com.sdl.odata.unmarshaller.atom.ODataAtomParser;
import scala.Option;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utilities to aid Marshalling tests.
 */
public final class MarshallingTestUtilities {

    private MarshallingTestUtilities() {
    }

    /**
     * Returns an  Atom XML file which contains a entry node containing
     * an OData marshalled form of the entity (DTO) object.
     *
     * @param entityDto  the entity to marshall
     * @param serviceUri the service Uri
     * @return an Atom formatted XML file which contains a entry node which
     * contains a marshalled form of the entity (DTO) object
     * @throws java.io.UnsupportedEncodingException
     * @throws com.sdl.odata.api.ODataException
     */
    public static String atomMarshall(Object entityDto, ODataUri serviceUri)
            throws ODataException, UnsupportedEncodingException {
        // build a dummy OData response
        ODataResponse.Builder builder = new ODataResponse.Builder().setStatus(OK);

        // marshall the entity Atom XML into the response
        new AtomRenderer().render(buildODataContext("", ATOM_XML, serviceUri), QueryResult.from(entityDto), builder);

        // return the text content of the response
        return builder.build().getBodyText(UTF_8.name());
    }

    /**
     * Returns Collection of primitives or primitive in XML format.
     *
     * @param objectToMarshall object to marshall
     * @param serviceUri       service Uri
     * @return marshalled XML object
     * @throws UnsupportedEncodingException
     * @throws ODataException
     */
    public static String marshalPrimitives(Object objectToMarshall, ODataUri serviceUri)
            throws UnsupportedEncodingException, ODataException {
        ODataResponse.Builder responseBuilder = new ODataResponse.Builder().setStatus(OK);
        new XMLValueRenderer().render(buildODataContext("", XML, serviceUri),
                QueryResult.from(objectToMarshall), responseBuilder);

        return responseBuilder.build().getBodyText(UTF_8.name());
    }

    /**
     * Returns an  Atom XML file which contains a entry node containing
     * an OData marshalled form of the entity (DTO) object.
     *
     * @param entityDto the entity to marshall
     * @return an Atom formatted XML file which contains a entry node which
     * contains a marshalled form of the entity (DTO) object
     * @throws java.io.UnsupportedEncodingException
     * @throws com.sdl.odata.api.ODataException
     */
    public static String atomMarshall(Object entityDto)
            throws ODataException, UnsupportedEncodingException {
        // return the text content of the response
        return atomMarshall(entityDto, getDefaultTestUri());
    }

    /**
     * Unmarshalls an Atom XML form of on OData entity into the actual entity (DTO) object.
     *
     * @param oDataEntityXml the Atom XML form of on OData entity
     * @return an entity (DTO) object
     * @throws java.io.UnsupportedEncodingException
     * @throws com.sdl.odata.api.ODataException
     */
    public static Object atomUnMarshall(String oDataEntityXml) throws UnsupportedEncodingException, ODataException {
        return atomUnmarshall(oDataEntityXml, getDefaultTestUri());
    }

    /**
     * Unmarshalls an Atom XML form of on OData entity into the actual entity (DTO) object.
     *
     * @param oDataEntityXml the Atom XML form of on OData entity
     * @param serviceUri     the service uri
     * @return an entity (DTO) object
     * @throws java.io.UnsupportedEncodingException
     * @throws com.sdl.odata.api.ODataException
     */
    public static Object atomUnmarshall(String oDataEntityXml, ODataUri serviceUri)
            throws UnsupportedEncodingException, ODataException {
        EntityDataModel entityDataModel = buildEntityDataModel();

        // build a dummy request context which contains the Xml
        ODataRequest request = buildODataRequestFromString(oDataEntityXml, serviceUri);
        ODataRequestContext requestContext = new ODataRequestContext(request, serviceUri, entityDataModel);

        // unmarshall the OData request context into an entity
        return new ODataAtomParser(requestContext, new ODataParserImpl()).getODataEntity();
    }

    private static ODataRequest buildODataRequestFromString(String content, ODataUri serviceUri)
            throws UnsupportedEncodingException {
        ODataRequest.Builder requestBuilder = new ODataRequest.Builder();
        requestBuilder.setBodyText(content, UTF_8.name());
        requestBuilder.setUri(serviceUri.serviceRoot());
        requestBuilder.setMethod(ODataRequest.Method.GET);
        return requestBuilder.build();
    }

    public static EntityDataModel buildEntityDataModel() throws ODataEdmException {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        return factory.addClasses(getEdmEntityClasses()).buildEntityDataModel();
    }

    private static ODataRequestContext buildODataContext(
            String body, MediaType mediaType, ODataUri serviceUri)
            throws UnsupportedEncodingException, ODataEdmException {
        ODataRequest request = new ODataRequest.Builder().setBodyText(body, "UTF-8")
                .setUri(serviceUri.serviceRoot())
                .setAccept(mediaType)
                .setMethod(GET).build();

        return new ODataRequestContext(request, serviceUri, buildEntityDataModel());
    }

    private static ODataUri getDefaultTestUri() {
        return getProductsUri();
    }

    private static ODataUri getProductsUri() {
        return createODataUri("http://localhost:8080/odata.svc", "Products");
    }

    /**
     * Create an ODataUri, e.g. http://localhost:8080/odata.svc/ContextVocabularies?$expand=aspectDefinitions.
     *
     * @param urlString the endpoint url
     * @return an ODataUri
     * @throws com.sdl.odata.api.ODataException
     */
    public static ODataUri createODataUri(String urlString) throws ODataException {
        return new ODataParserImpl().parseUri(urlString, buildEntityDataModel());
    }

    private static ODataUri createODataUri(String serviceRoot, String entitySetName) {

        List<QueryOption> queryOptions = new ArrayList<>();
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, Option.<EntityCollectionPath>apply(null));
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, ODataUriUtil.asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }

    public static EntityType getEntityType(EntityDataModel entityDataModel, Object entity) throws ODataEdmException {
        final Type type = entityDataModel.getType(entity.getClass());
        if (type == null) {
            String msg = String.format("Given entity %s is not found in entity data model", entity);
            throw new ODataEdmException(msg);
        }
        return (EntityType) type;
    }
}
