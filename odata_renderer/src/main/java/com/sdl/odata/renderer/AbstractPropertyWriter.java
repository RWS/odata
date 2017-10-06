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
package com.sdl.odata.renderer;

import com.sdl.odata.api.ODataClientException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.renderer.PropertyStreamWriter.ChunkedStreamAction.BODY_DOCUMENT;
import static com.sdl.odata.renderer.PropertyStreamWriter.ChunkedStreamAction.END_DOCUMENT;
import static com.sdl.odata.renderer.PropertyStreamWriter.ChunkedStreamAction.START_DOCUMENT;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getAndCheckType;

/**
 * Handles property writing.
 */
public abstract class AbstractPropertyWriter implements PropertyStreamWriter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPropertyWriter.class);
    private final ODataUri oDataUri;
    private final EntityDataModel entityDataModel;
    private final TargetType targetType;

    public AbstractPropertyWriter(ODataUri oDataUri, EntityDataModel entityDataModel) throws ODataRenderException {
        this.oDataUri = checkNotNull(oDataUri);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.targetType = getTargetType();
    }

    /**
     * This is main method to get property as string.
     *
     * @param data represents simple primitive or complex value or collections of collection of these.
     * @return String that represents simple primitive or complex value
     * or collections of collection of these in the form of xml or json.
     * @throws ODataException if an error occurs.
     */
    public String getPropertyAsString(Object data) throws ODataException {
        LOG.debug("GetPropertyAsString invoked with {}", data);
        if (data != null) {
            return makePropertyString(data);
        } else {
            return generateNullPropertyString();
        }
    }

    @Override
    public ChunkedActionRenderResult getPropertyStartDocument(Object data, OutputStream outputStream)
            throws ODataException {
        LOG.debug("GetPropertyStartDocument invoked with {}", data);
        if (data == null) {
            // If null - return info in one piece within getPropertyBodyDocument() call and here just empty string
            return new ChunkedActionRenderResult(outputStream);
        } else {
            return makePropertyStringChunked(data, START_DOCUMENT, new ChunkedActionRenderResult(outputStream));
        }
    }

    @Override
    public ChunkedActionRenderResult getPropertyBodyDocument(Object data, ChunkedActionRenderResult previousResult)
            throws ODataException {
        LOG.debug("GetPropertyBodyDocument invoked with {}", data);
        if (data == null) {
            try {
                previousResult.getOutputStream().write(generateNullPropertyString().getBytes());
            } catch (IOException e) {
                throw new ODataRenderException("Unable to render property body.", e);
            }
            return previousResult;
        } else {
            return makePropertyStringChunked(data, BODY_DOCUMENT, previousResult);
        }
    }

    @Override
    public void getPropertyEndDocument(Object data, ChunkedActionRenderResult previousResult) throws ODataException {
        LOG.debug("GetPropertyEndDocument invoked with {}", data);
        if (data != null) {
            makePropertyStringChunked(data, END_DOCUMENT, previousResult);
        }
    }

    protected abstract ChunkedActionRenderResult getPrimitivePropertyChunked(
            Object data, Type type, ChunkedStreamAction action, ChunkedActionRenderResult previousResult)
            throws ODataException;

    protected abstract ChunkedActionRenderResult getComplexPropertyChunked(
            Object data, StructuredType type, ChunkedStreamAction action, ChunkedActionRenderResult previousResult)
            throws ODataException;

    /**
     * This abstract method this needs to be implemented in subclass. Purpose of this method is to
     * generate string (either json or xml ) if the given property is null. For example, atom null string
     * <p>
     * {@code
     * <pre>
     *  <metadata:value xmlns:metadata="metadata name space uri" metadata:context="some context" metadata:null="true" />
     * </pre>
     * }
     *
     * @return String that represents null property
     * @throws com.sdl.odata.api.renderer.ODataRenderException in case of any problems
     */
    protected abstract String generateNullPropertyString() throws ODataException;

    /**
     * This method handles simple primitive property and generates string based on property. For example following
     * atom xml generates in case of simple primitive in AtomPropertyWriter.
     * <p>
     * {@code
     * <pre>
     *     <value xmlns="metadata namespace uri" context="context">CEO</value>
     * </pre>
     * }
     *
     * @param data that represents primitive data. This will never be null.
     * @param type type of the property. This will never be null.
     * @return String that represents simple property
     * @throws com.sdl.odata.api.renderer.ODataRenderException in case of any problems
     */
    protected abstract String generatePrimitiveProperty(Object data, Type type) throws ODataException;

    /**
     * This method handles complex properties and generates string based on property. For example following
     * atom xml generates in case of complex property in AtomPropertyWriter.
     * <p>
     * {@code
     * <pre>
     * <metadata:value metadata:type="#Model.Address" metadata:context="context"
     * xmlns:metadata="metadata namespace uri"
     * xmlns="http://docs.oasis-open.org/odata/ns/data">
     * <Street>Obere Str. 57</Street>
     * <City>Berlin</City>
     * <Region metadata:null="true"/>
     * <PostalCode>D-12209</PostalCode>
     * </metadata:value>
     * </pre>
     * }
     *
     * @param data that represents complex property data. This will never be null.
     * @param type is StructuredType. This will never be null.
     * @return String that represents simple property
     * @throws com.sdl.odata.api.renderer.ODataRenderException in case of any problems
     */
    protected abstract String generateComplexProperty(Object data, StructuredType type) throws ODataException;

    private String makePropertyString(Object data) throws ODataException {
        String propertyXML = null;
        Type type = getTypeFromODataUri();
        validateRequest(type, data);
        switch (type.getMetaType()) {
            case PRIMITIVE:
                LOG.debug("Given property type is primitive");
                propertyXML = generatePrimitiveProperty(data, type);
                break;

            case COMPLEX:
                LOG.debug("Given property type is complex");
                propertyXML = generateComplexProperty(data, (StructuredType) type);
                break;

            default:
                defaultHandling(type);
        }
        return propertyXML;
    }

    private ChunkedActionRenderResult makePropertyStringChunked(Object data, ChunkedStreamAction action,
                                                                ChunkedActionRenderResult previousResult)
            throws ODataException {
        if (previousResult.getType() == null) {
            previousResult.setType(getTypeFromODataUri());
        }
        Type type = previousResult.getType();
        if (!previousResult.isTypeValidated()) {
            validateRequestChunk(type, data);
        }
        switch (type.getMetaType()) {
            case PRIMITIVE:
                LOG.debug("Given property type is primitive");
                return getPrimitivePropertyChunked(data, type, action, previousResult);
            case COMPLEX:
                LOG.debug("Given property type is complex");
                return getComplexPropertyChunked(data, (StructuredType) type, action, previousResult);
            default:
                defaultHandling(type);
        }
        return null;
    }

    private void validateRequest(Type type, Object data) throws ODataRenderException,
            ODataClientException, ODataEdmException {
        if (!areValidTypesToProceed(type, data)) {
            throw new ODataRenderException("ODataUri type is not matched with given 'data' type: " + type);
        }
    }

    private boolean areValidTypesToProceed(Type type, Object data) throws ODataRenderException, ODataEdmException {
        return isEmptyCollection(data) || !(isCollection(data) ^ targetType.isCollection())
                && getType(data).equals(type);
    }

    public void validateRequestChunk(Type type, Object data) throws ODataRenderException,
            ODataClientException, ODataEdmException {
        if (!areValidTypesToProceedChunk(type, data)) {
            throw new ODataRenderException("ODataUri type is not matched with given 'data' type: " + type);
        }
    }

    private boolean areValidTypesToProceedChunk(Type type, Object data) throws ODataRenderException, ODataEdmException {
        return isEmptyCollection(data) || getType(data).equals(type);
    }

    public Type getTypeFromODataUri() throws ODataRenderException {
        return entityDataModel.getType(targetType.typeName());
    }

    private TargetType getTargetType() throws ODataRenderException {
        Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(oDataUri, entityDataModel);
        if (targetTypeOption.isEmpty()) {
            throw new ODataRenderException("Target type should not be empty");
        }
        return targetTypeOption.get();
    }

    protected Type getType(Object data) throws ODataEdmException {
        Type type;
        if (isCollection(data)) {
            LOG.debug("Given property is collection");
            type = getAndCheckType(entityDataModel, ((List<?>) data).get(0).getClass());
        } else {
            type = getAndCheckType(entityDataModel, data.getClass());
        }
        return type;
    }

    protected boolean isEmptyCollection(Object data) {
        return isCollection(data) && ((List<?>) data).isEmpty();
    }

    protected boolean isCollection(Object data) {
        return data instanceof List;
    }

    protected ODataUri getODataUri() {
        return oDataUri;
    }

    protected EntityDataModel getEntityDataModel() {
        return entityDataModel;
    }

    protected void defaultHandling(Type type) throws ODataRenderException {
        String msg = String.format("Unhandled object type %s", type);
        LOG.warn(msg);
        throw new ODataRenderException(msg);
    }
}
