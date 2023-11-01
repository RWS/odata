/**
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.annotations.EdmAction;
import com.sdl.odata.api.edm.annotations.EdmActionImport;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.annotations.EdmFunction;
import com.sdl.odata.api.edm.annotations.EdmFunctionImport;
import com.sdl.odata.api.edm.factory.EntityDataModelFactory;
import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.edm.model.TypeDefinition;
import com.sdl.odata.edm.model.EntityContainerImpl;
import com.sdl.odata.edm.model.EntityDataModelImpl;
import com.sdl.odata.edm.model.PrimitiveTypeNameResolver;
import com.sdl.odata.edm.model.SchemaImpl;
import com.sdl.odata.edm.model.TypeNameResolver;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * An {@code EntityDataModelFactory} which builds the entity data model from a set of classes annotated with
 * annotations defined in package {@code com.sdl.odata.api.edm.annotations}.
 *
 */
public class AnnotationEntityDataModelFactory implements EntityDataModelFactory {

    private static final TypeNameResolver PRIMITIVE_TYPE_NAME_RESOLVER = new PrimitiveTypeNameResolver();

    private static final TypeNameResolver TYPE_NAME_RESOLVER = javaType -> {
        // First check if the Java type matches an OData primitive type
        String primitiveTypeName = PRIMITIVE_TYPE_NAME_RESOLVER.resolveTypeName(javaType);
        if (!isNullOrEmpty(primitiveTypeName)) {
            return primitiveTypeName;
        }

        EdmEntity entityAnno = javaType.getAnnotation(EdmEntity.class);
        if (entityAnno != null) {
            return AnnotationEntityTypeFactory.getFullyQualifiedTypeName(entityAnno, javaType);
        }

        EdmComplex complexAnno = javaType.getAnnotation(EdmComplex.class);
        if (complexAnno != null) {
            return AnnotationComplexTypeFactory.getFullyQualifiedTypeName(complexAnno, javaType);
        }

        EdmEnum enumAnno = javaType.getAnnotation(EdmEnum.class);
        if (enumAnno != null) {
            return AnnotationEnumTypeFactory.getFullyQualifiedTypeName(enumAnno, javaType);
        }

        return null;
    };

    private final List<Class<?>> entityClasses = new ArrayList<>();
    private final List<Class<?>> complexClasses = new ArrayList<>();
    private final List<Class<?>> functionClasses = new ArrayList<>();
    private final List<Class<?>> functionImportClasses = new ArrayList<>();
    private final List<Class<?>> actionClasses = new ArrayList<>();
    private final List<Class<?>> actionImportClasses = new ArrayList<>();
    private final List<Class<?>> enumClasses = new ArrayList<>();
    private final List<TypeDefinition> typeDefinitions = new ArrayList<>();

    private final Map<String, SchemaImpl.Builder> schemaBuilders = new LinkedHashMap<>();

    public AnnotationEntityDataModelFactory() {
    }

    /**
     * Sets the alias for the schema identified by the specified namespace.
     *
     * @param namespace The namespace of the schema.
     * @param alias     The alias of the schema.
     */
    public void setSchemaAlias(String namespace, String alias) {
        getSchemaBuilder(namespace).setAlias(alias);
    }

    private SchemaImpl.Builder getSchemaBuilder(String namespace) {
        SchemaImpl.Builder builder = schemaBuilders.get(namespace);
        if (builder == null) {
            builder = new SchemaImpl.Builder().setNamespace(namespace);
            schemaBuilders.put(namespace, builder);
        }
        return builder;
    }

    /**
     * Adds a class to be included when building the entity data model. This class must be annoted with one of the
     * annotations {@code EdmEntity}, {@code EdmComplex} or {@code EdmEnum}.
     *
     * @param cls The class to be included in the entity data model.
     */
    public AnnotationEntityDataModelFactory addClass(Class<?> cls) {
        EdmEntity entityAnnotation = cls.getAnnotation(EdmEntity.class);
        EdmComplex complexAnnotation = cls.getAnnotation(EdmComplex.class);
        EdmFunction functionAnnotation = cls.getAnnotation(EdmFunction.class);
        EdmFunctionImport functionImportAnnotation = cls.getAnnotation(EdmFunctionImport.class);
        EdmAction actionAnnotation = cls.getAnnotation(EdmAction.class);
        EdmActionImport actionImportAnnotation = cls.getAnnotation(EdmActionImport.class);
        EdmEnum enumAnnotation = cls.getAnnotation(EdmEnum.class);

        Set<Annotation> annotations = new HashSet<>();

        annotations.add(entityAnnotation);
        annotations.add(complexAnnotation);
        annotations.add(functionAnnotation);
        annotations.add(functionImportAnnotation);
        annotations.add(actionAnnotation);
        annotations.add(actionImportAnnotation);
        annotations.add(enumAnnotation);

        checkClassPrecondition(annotations, cls);

        if (entityAnnotation != null) {
            entityClasses.add(cls);
        } else if (complexAnnotation != null) {
            complexClasses.add(cls);
        } else if (functionAnnotation != null) {
            functionClasses.add(cls);
        } else if (functionImportAnnotation != null) {
            functionImportClasses.add(cls);
        } else if (actionAnnotation != null) {
            actionClasses.add(cls);
        } else if (actionImportAnnotation != null) {
            actionImportClasses.add(cls);
        } else if (enumAnnotation != null) {
            if (!cls.isEnum()) {
                throw new IllegalArgumentException("The EdmEnum annotation must only be used on enum classes: " +
                        cls.getName());
            }
            enumClasses.add(cls);
        }
        return this;
    }

    public AnnotationEntityDataModelFactory addClasses(Iterable<Class<?>> classes) {
        for (Class<?> cls : classes) {
            addClass(cls);
        }
        return this;
    }

    private void checkClassPrecondition(Set<Annotation> annotations, Class<?> cls) {
        // Check for size is exactly 2: one element for null value, one for present annotation on class
        if (annotations.size() != 2) {
            throw new IllegalArgumentException(
                    "The class must have exactly one of the annotations EdmEntity, EdmComplex, EdmEnum, EdmFunction," +
                            " EdmFunctionImport, EdmAction, EdmActionImport: "
                            + cls.getName());
        }
    }

    /**
     * Adds a type definition.
     *
     * @param typeDefinition The type definition.
     */
    public void addTypeDefinition(TypeDefinition typeDefinition) {
        typeDefinitions.add(typeDefinition);
    }

    @Override
    public EntityDataModel buildEntityDataModel() throws ODataEdmException {
        EntityContainerImpl.Builder entityContainerBuilder = new EntityContainerImpl.Builder();

        final AnnotationEntitySetFactory entitySetFactory = new AnnotationEntitySetFactory();
        final AnnotationSingletonFactory singletonFactory = new AnnotationSingletonFactory();

        // Entity types
        String entityContainerName = buildEntityTypes(entitySetFactory, singletonFactory);

        buildComplexTypes();
        buildFunctions();
        buildActions();

        // Add function imports without preset EntitySets and Function fields
        AnnotationFunctionImportFactory functionImportFactory = new AnnotationFunctionImportFactory();
        addFunctionImports(functionImportFactory);

        // Add function imports without preset EntitySets and Function fields
        AnnotationActionImportFactory actionImportFactory = new AnnotationActionImportFactory();
        addActionImports(actionImportFactory);

        buildEnumTypes();
        buildTypeDefinitions();

        // Schemas
        final Map<String, Schema> schemas = new LinkedHashMap<>();
        buildSchemas(schemas);

        final List<EntitySet> entitySets = new ArrayList<>();

        // FactoryLookup which is used to look up information in the not-yet-built entity data model
        FactoryLookup lookup = new FactoryLookup() {
            @Override
            public StructuredType getStructuredType(String fullyQualifiedTypeName) {
                int i = fullyQualifiedTypeName.lastIndexOf('.');
                String namespace = fullyQualifiedTypeName.substring(0, i);
                String simpleTypeName = fullyQualifiedTypeName.substring(i + 1);

                Schema schema = schemas.get(namespace);
                if (schema != null) {
                    Type type = schema.getType(simpleTypeName);
                    return type instanceof StructuredType ? (StructuredType) type : null;
                } else {
                    return null;
                }
            }

            @Override
            public String getEntitySetOrSingletonName(String entityTypeName) {
                String result = entitySetFactory.getEntitySetName(entityTypeName);
                if (isNullOrEmpty(result)) {
                    result = singletonFactory.getSingletonName(entityTypeName);
                }
                return result;
            }

            @Override
            public Function getFunction(String functionName) {
                int i = functionName.lastIndexOf('.');
                String namespace = functionName.substring(0, i);
                String simpleFunctionName = functionName.substring(i + 1);
                Schema schema = schemas.get(namespace);
                if (schema == null) {
                    throw new IllegalArgumentException("Could not find schema with namespace: " + namespace);
                }
                Function function = schema.getFunction(simpleFunctionName);
                if (function == null) {
                    throw new IllegalArgumentException("Could not find function: " + simpleFunctionName +
                            " in schema with namespace: " + namespace);
                }
                return function;
            }

            @Override
            public Action getAction(String actionName) {
                int i = actionName.lastIndexOf('.');
                String namespace = actionName.substring(0, i);
                String simpleActionName = actionName.substring(i + 1);
                Schema schema = schemas.get(namespace);
                if (schema == null) {
                    throw new IllegalArgumentException("There are no such schema with namespace: " + namespace);
                }
                Action action = schema.getAction(simpleActionName);
                if (action == null) {
                    throw new IllegalArgumentException("Can't find action with name: " + simpleActionName);
                }
                return action;
            }

            @Override
            public EntitySet getEntitySet(final String entitySetName) {
                List<EntitySet> internalEntityList = new LinkedList<>();

                Predicate<EntitySet> predicate = entitySet -> entitySet.getName().equals(entitySetName);
                entitySets.stream().filter(predicate).forEach(internalEntityList::add);

                return internalEntityList.isEmpty() ? null : internalEntityList.get(0);
            }
        };

        entitySets.addAll(entitySetFactory.build(lookup));

        // Build entity sets and singletons and add them to the entity container builder
        entityContainerBuilder
                .addEntitySets(entitySets)
                .addSingletons(singletonFactory.build(lookup))
                .addFunctionImports(functionImportFactory.build(lookup))
                .addActionImports(actionImportFactory.build(lookup));


        if (!schemas.isEmpty()) {
            // NOTE: Namespace of entity container is set automatically from information of the first schema
            String firstSchemaNamespace = schemas.values().iterator().next().getNamespace();

            // fallback in case @EdmEntity does not have valid containerName attribute set,
            // it will be extracted from the schema
            if (isNullOrEmpty(entityContainerName)) {
                if (!firstSchemaNamespace.contains(".")) {
                    entityContainerName = firstSchemaNamespace;
                } else {
                    String[] namespaces = firstSchemaNamespace.split("\\.");
                    StringJoiner joiner = new StringJoiner("");
                    for (String namespace : namespaces) {
                        joiner.add(namespace);
                    }
                    entityContainerName = joiner.toString();
                }
            }

            entityContainerBuilder.setName(entityContainerName);
            entityContainerBuilder.setNamespace(firstSchemaNamespace);
        }

        return new EntityDataModelImpl(entityContainerBuilder.build(), schemas.values());
    }

    private String buildEntityTypes(AnnotationEntitySetFactory entitySetFactory,
                                    AnnotationSingletonFactory singletonFactory) {
        AnnotationEntityTypeFactory entityTypeFactory = new AnnotationEntityTypeFactory(TYPE_NAME_RESOLVER);
        String entityContainerName = null;
        for (Class<?> entityClass : entityClasses) {
            EntityType entityType = entityTypeFactory.build(entityClass);
            getSchemaBuilder(entityType.getNamespace()).addType(entityType);

            if (isNullOrEmpty(entityContainerName)) {
                EdmEntity edmEntity = entityClass.getAnnotation(EdmEntity.class);
                if (edmEntity != null) {
                    entityContainerName = edmEntity.containerName();
                }
            }

            // Create and add EntitySet if an @EdmEntitySet annotation is present on the entity class
            entitySetFactory.addEntityType(entityClass, entityType);

            // Create and add Singleton if an @EdmSingleton annotation is present on the entity class
            singletonFactory.addEntityType(entityClass, entityType);
        }
        return entityContainerName;
    }

    private void addActionImports(AnnotationActionImportFactory actionImportFactory) {
        actionImportClasses.forEach(actionImportFactory::addActionImport);
    }

    private void addFunctionImports(AnnotationFunctionImportFactory functionImportFactory) {
        functionImportClasses.forEach(functionImportFactory::addFunctionImport);
    }

    private void buildSchemas(Map<String, Schema> schemas) {
        for (SchemaImpl.Builder builder : schemaBuilders.values()) {
            Schema schema = builder.build();
            schemas.put(schema.getNamespace(), schema);
        }
    }

    private void buildTypeDefinitions() {
        for (TypeDefinition typeDefinition : typeDefinitions) {
            getSchemaBuilder(typeDefinition.getNamespace()).addType(typeDefinition);
        }
    }

    private void buildEnumTypes() {
        AnnotationEnumTypeFactory enumTypeFactory = new AnnotationEnumTypeFactory();
        for (Class<?> enumClass : enumClasses) {
            EnumType enumType = enumTypeFactory.build(enumClass);
            getSchemaBuilder(enumType.getNamespace()).addType(enumType);
        }
    }

    private void buildActions() {
        AnnotationActionFactory actionFactory = new AnnotationActionFactory();
        for (Class<?> actionClass : actionClasses) {
            Action action = actionFactory.build(actionClass);
            getSchemaBuilder(action.getNamespace()).addAction(action);
        }
    }

    private void buildFunctions() {
        AnnotationFunctionFactory functionFactory = new AnnotationFunctionFactory();
        for (Class<?> functionClass : functionClasses) {
            Function function = functionFactory.build(functionClass);
            getSchemaBuilder(function.getNamespace()).addFunction(function);
        }
    }

    private void buildComplexTypes() {
        AnnotationComplexTypeFactory complexTypeFactory = new AnnotationComplexTypeFactory(TYPE_NAME_RESOLVER);
        for (Class<?> complexClass : complexClasses) {
            ComplexType complexType = complexTypeFactory.build(complexClass);
            getSchemaBuilder(complexType.getNamespace()).addType(complexType);
        }
    }
}
