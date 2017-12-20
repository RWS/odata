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
package com.sdl.odata.test.util;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.CompoundKeyPredicate;
import com.sdl.odata.api.parser.ContextFragment;
import com.sdl.odata.api.parser.CountPath$;
import com.sdl.odata.api.parser.EntityCollectionPath;
import com.sdl.odata.api.parser.EntityPath;
import com.sdl.odata.api.parser.EntitySetPath;
import com.sdl.odata.api.parser.ExpandItem;
import com.sdl.odata.api.parser.ExpandOption;
import com.sdl.odata.api.parser.ExpandPathSegment;
import com.sdl.odata.api.parser.KeyPredicate;
import com.sdl.odata.api.parser.KeyPredicatePath;
import com.sdl.odata.api.parser.Literal;
import com.sdl.odata.api.parser.MetadataUri;
import com.sdl.odata.api.parser.NavigationPropertyExpandPathSegment;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.PathExpandItem;
import com.sdl.odata.api.parser.PathSegment;
import com.sdl.odata.api.parser.PropertyPath;
import com.sdl.odata.api.parser.PropertyPath$;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.api.parser.ServiceRootUri;
import com.sdl.odata.api.parser.SimpleKeyPredicate;
import com.sdl.odata.api.parser.StringLiteral;
import com.sdl.odata.api.parser.ValuePath$;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.test.model.AbstractComplexTypeSample;
import com.sdl.odata.test.model.AbstractEntityTypeSample;
import com.sdl.odata.test.model.ActionImportSample;
import com.sdl.odata.test.model.ActionSample;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.BankAccount;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.CollectionsSample;
import com.sdl.odata.test.model.ComplexKeySample;
import com.sdl.odata.test.model.ComplexTypeSample;
import com.sdl.odata.test.model.ComplexTypeSampleList;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.EntityTypeSample;
import com.sdl.odata.test.model.EnumSample;
import com.sdl.odata.test.model.ExpandedPropertiesSample;
import com.sdl.odata.test.model.FunctionImportSample;
import com.sdl.odata.test.model.FunctionSample;
import com.sdl.odata.test.model.IdNamePairComplex;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.PrimitiveTypesSample;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.SingletonSample;
import com.sdl.odata.test.model.UnboundActionSample;
import com.sdl.odata.test.model.UnboundFunctionSample;
import com.sdl.odata.test.model.complex.ODataDemoClassification;
import com.sdl.odata.test.model.complex.ODataDemoEntity;
import com.sdl.odata.test.model.complex.ODataDemoProperty;
import com.sdl.odata.test.model.complex.ODataDemoPropertyType;
import com.sdl.odata.test.model.complex.ODataDemoPropertyValue;
import com.sdl.odata.test.model.complex.ODataVersion;
import com.sdl.odata.test.model.complex.ODataVersionPart;
import scala.Option;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.api.parser.ODataUriUtil.asScalaList;
import static com.sdl.odata.api.parser.ODataUriUtil.asScalaMap;

/**
 * This class contains utility methods for testing only.
 */
public final class TestUtils {

    private static final int SIZE = 4096;

    private TestUtils() {
    }

    /**
     * Service root.
     */
    public static final String SERVICE_ROOT = "http://localhost:8080/odata.svc";

    public static List<Class<?>> getEdmEntityClasses() {
        return Arrays.asList(
                Customer.class,
                AbstractComplexTypeSample.class,
                ComplexTypeSample.class,
                ComplexTypeSampleList.class,
                AbstractEntityTypeSample.class,
                EntityTypeSample.class,
                Address.class,
                Order.class,
                Product.class,
                Category.class,
                BankAccount.class,
                PrimitiveTypesSample.class,
                CollectionsSample.class,
                EnumSample.class,
                IdNamePairSample.class,
                IdNamePairComplex.class,
                ExpandedPropertiesSample.class,
                ComplexKeySample.class,
                SingletonSample.class,
                FunctionSample.class,
                UnboundFunctionSample.class,
                FunctionImportSample.class,
                ActionSample.class,
                UnboundActionSample.class,
                ActionImportSample.class,
                //OData Entities to test complex models
                ODataDemoProperty.class,
                ODataDemoPropertyType.class,
                ODataDemoEntity.class,
                ODataDemoPropertyValue.class,
                ODataVersion.class,
                ODataVersionPart.class,
                ODataDemoClassification.class
        );
    }

    /**
     * Create an OData URI for a Service Document request with the given media types to put in the $format query
     * parameter.
     *
     * @param mediaTypes The media types to put in the @format query parameter.
     * @return The created OData URI.
     */
    public static ODataUri createODataUri(MediaType... mediaTypes) {
        return createODataUriForServiceDocument(SERVICE_ROOT, mediaTypes);
    }

    /**
     * Create a test OData URI specifying only the service root.
     *
     * @param serviceRoot The service root.
     * @param mediaTypes  The media types to put in the $format query parameter.
     */
    public static ODataUri createODataUri(String serviceRoot, MediaType... mediaTypes) {
        return createODataUriForServiceDocument(serviceRoot, mediaTypes);
    }

    /**
     * Create an OData URI for a $metadata request.
     *
     * @return The created OData URI.
     */
    public static ODataUri createODataUriForMetaData() {
        scala.Option<MediaType> format = scala.Option.apply(null);
        scala.Option<ContextFragment> context = scala.Option.apply(null);

        return new ODataUri(SERVICE_ROOT, new MetadataUri(format, context));
    }

    /**
     * Create an OData URI for a Service Document request
     * with the given media type to put in the $format query parameter.
     *
     * @param mediaTypes The media type to put in the $format query parameter.
     * @return The created OData URI.
     */
    public static ODataUri createODataUriForServiceDocument(MediaType... mediaTypes) {
        return createODataUriForServiceDocument(SERVICE_ROOT, mediaTypes);
    }

    /**
     * Create an OData URI for a Service Document request with the given Service Root, and media type to put in the
     * $format query parameter.
     *
     * @param serviceRoot The given Service Root.
     * @param mediaTypes  The media type to put in the $format query parameter.
     * @return The created OData URI.
     */
    public static ODataUri createODataUriForServiceDocument(String serviceRoot, MediaType... mediaTypes) {
        scala.Option<MediaType> format = scala.Option.apply(null);
        if (mediaTypes.length > 0) {
            format = scala.Option.apply(mediaTypes[0]);
        }
        return new ODataUri(serviceRoot, new ServiceRootUri(format));
    }

    /**
     * Create an OData URI with the given Entity Set name and a simple key predicate.
     *
     * @param entitySetName The given Entity Set name.
     * @return The created OData URI.ø
     */
    public static ODataUri createODataUriWithSimpleKeyPredicate(String entitySetName) {
        scala.Option<String> none = scala.Option.apply(null);
        scala.Option<EntityPath> noneSubPath = scala.Option.apply(null);

        KeyPredicatePath keyPredicatePath = new KeyPredicatePath(
                new SimpleKeyPredicate(new StringLiteral("1")), noneSubPath);
        EntityCollectionPath collectionPath = new EntityCollectionPath(none, Option.apply(keyPredicatePath));
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, Option.apply(collectionPath));
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(new ArrayList<>()));
        return new ODataUri(SERVICE_ROOT, resourcePathUri);
    }

    public static ODataRequest createODataRequest(ODataRequest.Method method, Map<String, String> headers)
            throws UnsupportedEncodingException {

        return new ODataRequest.Builder().setBodyText("test", "UTF-8")
                .setUri(SERVICE_ROOT)
                .setHeaders(headers)
                .setMethod(method).build();
    }

    /**
     * Create a test OData URI specifying the service root, entity set name and a list of entity key value pair(s).
     *
     * @param serviceRoot   The given service root.
     * @param entitySetName The given entity set name.
     * @param keyValuePairs The given list of entity key value pairs.
     * @return The created test OData URI.
     */
    public static ODataUri createODataUriEntityKeys(String serviceRoot, String entitySetName,
                                                    KeyValuePair... keyValuePairs) {

        scala.Option<EntityPath> noEntityPath = scala.Option.apply(null);

        KeyPredicate keyPredicate = createKeyPredicate(keyValuePairs);
        KeyPredicatePath keyPredicatePath = new KeyPredicatePath(keyPredicate, noEntityPath);
        scala.Option<String> noString = scala.Option.apply(null);
        scala.Option<PathSegment> keyPredicatePathOption = scala.Option.apply(keyPredicatePath);
        EntityCollectionPath entityCollectionPath = new EntityCollectionPath(noString, keyPredicatePathOption);
        scala.Option<EntityCollectionPath> entityCollectionPathOption = scala.Option.apply(entityCollectionPath);
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, entityCollectionPathOption);
        List<QueryOption> queryOptions = new ArrayList<>();
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }

    /**
     * Create a test OData URI for /$count path with a given service root and entity set name.
     *
     * @param serviceRoot   The given service root.
     * @param entitySetName The given entity set name.
     * @return The created test OData URI.
     */
    public static ODataUri createODataCountEntitiesUri(String serviceRoot, String entitySetName) {

        CountPath$ countPath = CountPath$.MODULE$;
        scala.Option<PathSegment> countPathOption = scala.Option.apply(countPath);

        scala.Option<String> noString = scala.Option.apply(null);
        EntityCollectionPath entityCollectionPath = new EntityCollectionPath(noString, countPathOption);
        scala.Option<EntityCollectionPath> entityCollectionPathOption = scala.Option.apply(entityCollectionPath);
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, entityCollectionPathOption);

        List<QueryOption> queryOptions = new ArrayList<>();
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }

    /**
     * Create a test OData URI for /$value path with a given service root, entity set name and property to read.
     *
     * @param serviceRoot   The given service root.
     * @param entitySetName The given entity set name.
     * @return The created test OData URI.
     */
    public static ODataUri createODataValueEntitiesUri(String serviceRoot, String entitySetName, String propertyName) {

        ValuePath$ valuePath = ValuePath$.MODULE$;
        scala.Option<PathSegment> valuePathOption = scala.Option.apply(valuePath);

        PropertyPath propertyPath = PropertyPath$.MODULE$.apply(propertyName, valuePathOption);
        scala.Option<PathSegment> propertyPathOption = scala.Option.apply(propertyPath);

        scala.Option<String> noString = scala.Option.apply(null);
        EntityCollectionPath entityCollectionPath = new EntityCollectionPath(noString, propertyPathOption);
        scala.Option<EntityCollectionPath> entityCollectionPathOption = scala.Option.apply(entityCollectionPath);
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, entityCollectionPathOption);

        List<QueryOption> queryOptions = new ArrayList<>();
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }

    private static KeyPredicate createKeyPredicate(KeyValuePair... keyValuePairs) {

        // Note: In case of a single key value pair, create a 'SimpleKeyPredicate'
        if (keyValuePairs.length == 1) {
            return new SimpleKeyPredicate(keyValuePairs[0].getValue());
        }

        Map<String, Literal> keyValues = new HashMap<>();
        for (KeyValuePair keyValuePair : keyValuePairs) {
            keyValues.put(keyValuePair.getKey(), keyValuePair.getValue());
        }

        return new CompoundKeyPredicate(asScalaMap(keyValues));
    }

    /**
     * Create a test OData URI specifying the service root, entity set name
     * and the list of named expand path segments ($expand operation).
     *
     * @param serviceRoot     The service root.
     * @param entitySetName   The entity set name.
     * @param expandPathNames The list of named expand path segments.
     * @return The created test OData URI.
     */
    public static ODataUri createODataUri(String serviceRoot, String entitySetName, String... expandPathNames) {

        scala.Option<String> none = scala.Option.apply(null);

        List<ExpandItem> expandItems = new ArrayList<>();
        for (String expandPathName : expandPathNames) {
            ExpandPathSegment path = new NavigationPropertyExpandPathSegment(expandPathName, none);
            expandItems.add(new PathExpandItem(none, path, scala.collection.immutable.List$.MODULE$.empty()));
        }

        ExpandOption expandOption = new ExpandOption(asScalaList(expandItems));

        List<QueryOption> queryOptions = new ArrayList<>();
        queryOptions.add(expandOption);

        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, null);
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }

    /**
     * Create a test OData URI specifying the service root and the entity set name.
     *
     * @param serviceRoot   The service root.
     * @param entitySetName The entity set name.
     * @return The created test OData URI.
     */
    public static ODataUri createODataUri(String serviceRoot, String entitySetName) {

        List<QueryOption> queryOptions = new ArrayList<>();
        EntitySetPath entitySetPath = new EntitySetPath(entitySetName, Option.<EntityCollectionPath>apply(null));
        ResourcePathUri resourcePathUri = new ResourcePathUri(entitySetPath, asScalaList(queryOptions));

        return new ODataUri(serviceRoot, resourcePathUri);
    }

    /**
     * Create an OData request with the given URI and HTTP method.
     *
     * @param uri    The given URI.
     * @param method The given HTTP method.
     * @return The built OData request.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequest createODataRequest(String uri, ODataRequest.Method method)
            throws UnsupportedEncodingException {
        return new ODataRequest.Builder().setBodyText("test", "UTF-8")
                .setUri(uri)
                .setMethod(method).build();
    }

    /**
     * Create an OData request with the given HTTP method, and list of media types in the 'Accept-header'.
     *
     * @param method     The given HTTP method.
     * @param mediaTypes The given list of media types.
     * @return The created OData request.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequest createODataRequest(ODataRequest.Method method, MediaType... mediaTypes)
            throws UnsupportedEncodingException {
        return createODataRequest( method, "test", mediaTypes);
    }

    /**
     * Create an OData request with the given HTTP method, and list of media types in the 'Accept-header'.
     *
     * @param method     The given HTTP method.
     * @param mediaTypes The given list of media types.
     * @return The created OData request.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequest createODataRequest(ODataRequest.Method method, String bodyText,
                                                  MediaType... mediaTypes)
            throws UnsupportedEncodingException {
        return new ODataRequest.Builder().setBodyText(bodyText, "UTF-8")
                .setUri(SERVICE_ROOT)
                .setAccept(mediaTypes)
                .setMethod(method).build();
    }

    /**
     * Create an OData request with the given HHTP method and defined content type.
     *
     * @param method      The given HTTP method.
     * @param contentType The given content type.
     * @return The created OData request.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequest createODataRequestWithContentType(ODataRequest.Method method, MediaType contentType)
            throws UnsupportedEncodingException {
        return new ODataRequest.Builder().setBodyText("test", "UTF-8")
                .setUri(SERVICE_ROOT)
                .setContentType(contentType)
                .setMethod(method).build();
    }


    /**
     * Create an OData request with the given HHTP method.
     *
     * @param method The given HTTP method.
     * @return The created OData request.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequest createSimpleODataRequest(ODataRequest.Method method)
            throws UnsupportedEncodingException {
        return new ODataRequest.Builder().setBodyText("test", "UTF-8")
                .setUri(SERVICE_ROOT)
                .setMethod(method).build();
    }

    /**
     * Create an OData Request Context with the given HTTP method.
     *
     * @param method          The given HTTP method.
     * @param entityDataModel The Entity Data Model.
     * @return The crated OData request context.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequestContext createODataRequestContext(ODataRequest.Method method,
                                                                EntityDataModel entityDataModel)
            throws UnsupportedEncodingException {
        return new ODataRequestContext(createODataRequest(method), createODataUri(), entityDataModel);
    }

    /**
     * Create an OData Request Context with the given HTTP method, OData URI and Entity Data Model.
     *
     * @param method          The given HTTP method.
     * @param oDataUri        The given OData URI.
     * @param entityDataModel The given Entity Data Model.
     * @return The created OData request context.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequestContext createODataRequestContext(ODataRequest.Method method, ODataUri oDataUri,
                                                                EntityDataModel entityDataModel)
            throws UnsupportedEncodingException {
        return new ODataRequestContext(createODataRequest(method), oDataUri, entityDataModel);
    }

    /**
     * Create an OData Request Context with the given HTTP method, OData URI and Entity Data Model.
     *
     * @param method          The given HTTP method.
     * @param oDataUri        The given OData URI.
     * @param entityDataModel The given Entity Data Model.
     * @return The created OData request context.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequestContext createODataRequestContext(ODataRequest.Method method, ODataUri oDataUri,
                                                                EntityDataModel entityDataModel, String bodyText)
            throws UnsupportedEncodingException {
        return new ODataRequestContext(createODataRequest(method, bodyText), oDataUri, entityDataModel);
    }

    /**
     * Create an OData Request Context with the given OData Request, OData URI and Entity Data Model.
     *
     * @param request         The given OData Request.
     * @param oDataUri        The given OData URI.
     * @param entityDataModel The given Entity Data Model.
     * @return The created OData Request Context.
     * @throws UnsupportedEncodingException
     */
    public static ODataRequestContext createODataRequestContext(ODataRequest request, ODataUri oDataUri,
                                                                EntityDataModel entityDataModel)
            throws UnsupportedEncodingException {
        return new ODataRequestContext(request, oDataUri, entityDataModel);
    }

    /**
     * Key Value Pair.
     */
    public static class KeyValuePair {

        private final String key;
        private final Literal value;

        public KeyValuePair(String key, Literal value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Literal getValue() {
            return value;
        }
    }

    /**
     * Read content from a given source.
     *
     * @param source The source.
     * @return The read content.
     * @throws java.io.IOException
     */
    public static String readContent(String source) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = TestUtils.class.getResourceAsStream(source)) {
            copy(inputStream, outputStream);
        }

        return outputStream.toString(Charset.forName("UTF-8").name());
    }

    private static long copy(InputStream is, OutputStream os)
            throws IOException {
        if (is == null) {
            throw new IllegalArgumentException();
        }

        if (os == null) {
            throw new IllegalArgumentException();
        }

        byte[] buf = new byte[SIZE];
        long total = 0;
        while (true) {
            int r = is.read(buf);
            if (r == -1) {
                break;
            }
            os.write(buf, 0, r);
            total += r;
        }
        return total;
    }
}
