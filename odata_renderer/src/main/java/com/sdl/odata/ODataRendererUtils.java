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
package com.sdl.odata;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.renderer.ODataRenderException;
import scala.Option;
import scala.collection.JavaConverters;

import java.util.Map;

import static com.sdl.odata.JsonConstants.METADATA;
import static com.sdl.odata.api.parser.ODataUriUtil.getContextUrl;
import static com.sdl.odata.api.parser.ODataUriUtil.getFunctionCallParameters;
import static com.sdl.odata.api.parser.ODataUriUtil.isFunctionCallUri;

/**
 * This class contains render utility classes.
 */
public final class ODataRendererUtils {
    private static final String FORCE_EXPAND_PARAM = "expand";

    private ODataRendererUtils() {
    }


    /**
     * This method returns odata context based on oDataUri.
     * Throws ODataRenderException in case context is not defined.
     *
     * @param entityDataModel The entity data model.
     * @param oDataUri        is object which is the root of an abstract syntax tree that describes
     * @return string that represents context
     * @throws ODataRenderException if unable to get context from url
     */
    public static String getContextURL(ODataUri oDataUri, EntityDataModel entityDataModel) throws ODataRenderException {
        return getContextURL(oDataUri, entityDataModel, false);
    }

    /**
     * This method returns odata context based on oDataUri.
     * Throws ODataRenderException in case context is not defined.
     *
     * @param entityDataModel The entity data model.
     * @param oDataUri        is object which is the root of an abstract syntax tree that describes
     * @param isPrimitive     True if the context URL is for primitive.
     * @return string that represents context
     * @throws ODataRenderException if unable to get context from url
     */
    public static String getContextURL(ODataUri oDataUri, EntityDataModel entityDataModel, boolean isPrimitive)
            throws ODataRenderException {
        if (ODataUriUtil.isActionCallUri(oDataUri) ||
                ODataUriUtil.isFunctionCallUri(oDataUri)) {
            return buildContextUrlFromOperationCall(oDataUri, entityDataModel, isPrimitive);
        }

        Option<String> contextOption = getContextUrl(oDataUri);
        if (contextOption.isEmpty()) {
            throw new ODataRenderException("Could not construct context");
        }
        return contextOption.get();
    }

    /**
     * Builds the 'Context URL' when the request is an action or function call.
     *
     * @param oDataUri        The odata uri
     * @param entityDataModel The entity data model.
     * @param isPrimitive     True if the context URL is for primitive.
     * @return The built 'Context URL'.
     * @see <a href=
     * "http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793671">
     * OData Version 4.0 Part 1: Protocol, paragraph 10.16</a>
     */
    public static String buildContextUrlFromOperationCall(ODataUri oDataUri, EntityDataModel entityDataModel,
                                                          boolean isPrimitive) {
        String serviceRoot = oDataUri.serviceRoot();
        String returnType = ODataUriUtil.getOperationReturnType(oDataUri, entityDataModel);
        return serviceRoot + "/" + METADATA + "#" + returnType + (isPrimitive ? "" : "/$entity");
    }

    /**
     * Check if the reference is not null.
     * This differs from Guava that throws Illegal Argument Exception
     *
     * @param reference reference
     * @param <T>       type
     * @return reference or exception
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }
        return reference;
    }

    /**
     * Check if the reference is not null.
     * This differs from Guava that throws Illegal Argument Exception + message.
     *
     * @param reference reference
     * @param message   error message
     * @param args      arguments
     * @param <T>       type
     * @return reference or exception
     */
    public static <T> T checkNotNull(T reference, String message, Object... args) {
        if (reference == null) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return reference;
    }

    /**
     * Checks if we are trying to force expand all Nav properties for function calls by looking at expand parameter.
     *
     * @param oDataUri The OData URI
     * @return boolean if force expand parameter is set
     */
    public static boolean isForceExpandParamSet(ODataUri oDataUri) {
        if (isFunctionCallUri(oDataUri)) {
            // Check if we have expand param set to true
            Option<scala.collection.immutable.Map<String, String>> params = getFunctionCallParameters(oDataUri);

            if (params.isDefined() && !params.get().isEmpty()) {
                Map<String, String> parametersMap = JavaConverters.mapAsJavaMap(params.get());
                if (parametersMap.containsKey(FORCE_EXPAND_PARAM)) {
                    return Boolean.parseBoolean(parametersMap.get(FORCE_EXPAND_PARAM));
                }
            }
        }
        return false;
    }

}
