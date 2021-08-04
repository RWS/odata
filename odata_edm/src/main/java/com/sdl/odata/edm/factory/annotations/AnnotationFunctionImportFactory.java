/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmFunctionImport;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.FunctionImport;
import com.sdl.odata.edm.model.FunctionImportImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Function Import Factory.
 */
public class AnnotationFunctionImportFactory {

    private final List<FunctionImportImpl.Builder> functionImportBuilders = new ArrayList<>();

    /**
     * Add function import to builder of this factory by specified class.
     * @param cls    function import class
     */
    public void addFunctionImport(Class<?> cls) {
        EdmFunctionImport functionImportAnnotation = cls.getAnnotation(EdmFunctionImport.class);
        FunctionImportImpl.Builder functionImportBuilder = new FunctionImportImpl.Builder()
                .setEntitySetName(functionImportAnnotation.entitySet())
                .setFunctionName(functionImportAnnotation.namespace() + "." + functionImportAnnotation.function())
                .setIncludeInServiceDocument(functionImportAnnotation.includeInServiceDocument())
                .setName(functionImportAnnotation.name())
                .setJavaClass(cls);

        functionImportBuilders.add(functionImportBuilder);
    }

    /**
     * Returns built function imports using passed lookup for searching entity sets and functions for appropriate
     * function import.
     * @param lookup    lookup to search entity sets and functions for appropriate function import
     * @return          built function imports
     */
    public Iterable<FunctionImport> build(FactoryLookup lookup) {
        List<FunctionImport> builder = new ArrayList<>();
        for (FunctionImportImpl.Builder functionImportBuilder : functionImportBuilders) {
            EntitySet entitySet = lookup.getEntitySet(functionImportBuilder.getEntitySetName());
            Function function = lookup.getFunction(functionImportBuilder.getFunctionName());
            if (entitySet == null && function.isBound()) {
                throw new IllegalArgumentException("Could not find EntitySet with name: " +
                        functionImportBuilder.getEntitySetName());
            }
            functionImportBuilder.setEntitySet(entitySet);
            functionImportBuilder.setFunction(
                    function);
            builder.add(functionImportBuilder.build());
        }

        return Collections.unmodifiableList(builder);
    }

    private static String getTypeName(EdmFunctionImport functionImportAnnotation,
                                      Class<?> functionImportClass) {
        String name = functionImportAnnotation.name();
        if (isNullOrEmpty(name)) {
            // Use class name if name is not specified in EdmFunction annotation
            name = functionImportClass.getSimpleName();
        }
        return name;
    }

    private static String getNamespace(EdmFunctionImport functionImportAnnotation,
                                       Class<?> functionImportClass) {
        String namespace = functionImportAnnotation.namespace();
        if (isNullOrEmpty(namespace)) {
            // Use package name if namespace is not specified in EdmFunction annotation
            namespace = functionImportClass.getPackage().getName();
        }
        return namespace;
    }

    /**
     * Returns fully qualified function import name by function import annotation and class.
     * @param functionImportAnnotation    function import annotation
     * @param functionImportClass         function import class
     * @return                            fully qualified function import name
     */
    public static String getFullyQualifiedFunctionImportName(EdmFunctionImport functionImportAnnotation,
                                                             Class<?> functionImportClass) {
        String name = getTypeName(functionImportAnnotation, functionImportClass);
        String namespace = getNamespace(functionImportAnnotation, functionImportClass);
        return namespace + "." + name;
    }
}
