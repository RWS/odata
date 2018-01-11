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

import static com.sdl.odata.api.parser.ODataUriUtil.asScalaList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;
import static org.slf4j.LoggerFactory.getLogger;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.EntityCollectionPath;
import com.sdl.odata.api.parser.EntitySetPath;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientParserException;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.marshall.ODataEntityUnmarshaller;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.unmarshaller.json.ODataJsonParser;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import scala.Option;

/**
 * A class which can unmarshall an Odata Service Atom Response back to it's original Entity object.
 */
public class JsonEntityUnmarshaller implements ODataEntityUnmarshaller {

    private static final Logger LOG = getLogger(JsonEntityUnmarshaller.class);
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final Pattern PRIMITIVE_VALUE_RESPONSE_PATTERN = Pattern.compile(
            "<metadata:value[^>]+>(.*)</metadata:value>", Pattern.DOTALL);
    private static final Pattern COLLECTION_OF_PRIMITIVE_VALUE_RESPONSE_PATTERN = Pattern.compile(
            "<metadata:element>(.+?)</metadata:element>", Pattern.DOTALL);
    /**
     * Types that should be unmarshalled as primitive values.
     */
    public static final Set<Class> PRIMITIVE_CLASSES = Stream
            .of(String.class, Integer.class, Long.class, Boolean.class, Double.class, Float.class)
            .collect(Collectors.toSet());

    static {
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
    }

    private String url;
    private EntityDataModel entityDataModel;

    public JsonEntityUnmarshaller(Iterable<Class<?>> edmEntityClasses, String url) {
        this.url = url;
        try {
            LOG.debug("Building entity data model...");
            this.entityDataModel = buildEntityDataModel(edmEntityClasses);
        } catch (ODataEdmException | RuntimeException e) {
            throw new ODataClientRuntimeException(
                    format("Caught exception {0}: {1} when building OData entity model",
                            e.getClass().getSimpleName(), e.getMessage()),
                    e);
        }
    }

    @Override
    public Object unmarshallEntity(String odataServiceResponse, ODataClientQuery query) throws ODataClientException {
        LOG.debug("Unmarshalling entity for query: {}", query);
        try {
            if (List.class.getSimpleName().equals(query.getEntityType().getSimpleName())) {
                return unmarshallCollectionOfPrimitives(odataServiceResponse);
            } else if (PRIMITIVE_CLASSES.contains(query.getEntityType())) {
                return unmarshallPrimitives(odataServiceResponse);
            } else {
                return jsonUnmarshall(odataServiceResponse, odataServiceResponse, query);
            }
        } catch (UnsupportedEncodingException e) {
            throw new ODataClientException(e);
        }
    }

    private Object unmarshallPrimitives(String odataServiceResponse) {
        Matcher matcher = PRIMITIVE_VALUE_RESPONSE_PATTERN.matcher(odataServiceResponse);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Object unmarshallCollectionOfPrimitives(String odataServiceResponse) {
        List<Object> list = new ArrayList<>();
        Matcher matcher = COLLECTION_OF_PRIMITIVE_VALUE_RESPONSE_PATTERN.matcher(odataServiceResponse);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    @Override
    public List<?> unmarshall(String odataServiceResponse, ODataClientQuery query) throws ODataClientException {
        LOG.debug("Unmarshalling entities for query: {}", query);
        try {
            ODataRequest request = buildODataRequestFromString(odataServiceResponse, query);
            ODataUri oDataUri = createODataUri(url, query.getEdmEntityName());
            ODataRequestContext requestContext = new ODataRequestContext(request, oDataUri, entityDataModel);
            return getODataJsonParser(requestContext).getODataEntities();
        } catch (UnsupportedEncodingException | ODataException e) {
            throw new ODataClientException(e);
        }
    }

    protected ODataJsonParser getODataJsonParser(ODataRequestContext requestContext) {
        return new ODataJsonParser(requestContext, new ODataParserImpl());
    }

    /**
     * Unmarshalls an Atom XML form of on OData entity into the actual entity (DTO) object.
     *
     * @param oDataEntityJson the Atom XML form of on OData entity
     * @return an entity (DTO) object
     * @throws UnsupportedEncodingException
     * @throws ODataClientException
     */
    public Object jsonUnmarshall(String oDataEntityJson, String fullResponse, ODataClientQuery query)
            throws UnsupportedEncodingException, ODataClientException {
        Object unmarshalledEntity;
        // build a dummy request context which contains the Xml
        ODataRequest request = buildODataRequestFromString(oDataEntityJson, query);
        ODataRequestContext requestContext =
                new ODataRequestContext(request, createODataUri(url, query.getEdmEntityName()), entityDataModel);

        // unmarshall the OData request context into an entity
        try {
            unmarshalledEntity = getODataJsonParser(requestContext).getODataEntity();
        } catch (ODataException | RuntimeException e) {
            throw new ODataClientParserException(
                    format("Caught exception {0}: {1} when parsing response received from OData service",
                            e.getClass().getSimpleName(), e.getMessage()),
                    e, oDataEntityJson, fullResponse);
        }
        return unmarshalledEntity;
    }

    private ODataRequest buildODataRequestFromString(String content, ODataClientQuery query)
            throws UnsupportedEncodingException {
        ODataRequest.Builder requestBuilder = new ODataRequest.Builder();
        requestBuilder.setBodyText(content, UTF_8.name());
        requestBuilder.setUri(getTestServiceRoot(url, query.getEdmEntityName()));
        requestBuilder.setMethod(ODataRequest.Method.GET);
        return requestBuilder.build();
    }

    public EntityDataModel buildEntityDataModel(Iterable<Class<?>> edmEntityClasses) throws ODataEdmException {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        return factory.addClasses(edmEntityClasses).buildEntityDataModel();
    }

    private String getTestServiceRoot(String serviceRoot, String entitySetName) {
        return createODataUri(serviceRoot, entitySetName).serviceRoot();
    }

    private ODataUri createODataUri(String serviceRoot, String entitySetName) {
        List<QueryOption> queryOptions = new ArrayList<>();
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, Option.<EntityCollectionPath>apply(null));
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }
}
