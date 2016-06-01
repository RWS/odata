# SDL OData v4 Framework [![Build Status](https://travis-ci.org/sdl/odata.svg?branch=master)](https://travis-ci.org/sdl/odata) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sdl/odata/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sdl/odata)

This is the SDL Open Data Framework based on OData standard (http://www.odata.org/) fully implemented in Java. The SDL OData framework offers a Java
implementation of the OData Service and also provides Java Client Libraries. The SDL OData framework is aligned to the v4 version of the OData OASIS standard.

# Starting the Service

The OData service comes with a set of prepared shell scripts to allow running and install the OData service. In order to start the OData service
simply run `bin/start.sh`for Linux/OSX or `.\bin\start.ps1` for Windows. 
 
This can be done from a prebuild distribution or using the output artefacts in the odata_assembly maven module.

[Full Documentation](odata_assembly/src/main/resources/readme.md)

## Once Started
When the OData service has started the following URL's by default should be available:
http://localhost:8080/odata.svc (Gives the OData root collections)           
http://localhost:8080/odata.svc/$metadata (Gives the OData EDM model)                          

The standard framework has no entities delivered in the service, so this will be empty then. See the section about DataSources for clarification.

# Building your own Service
In order to build your own service you need to provide some data models and datasources for the framework to pick up. We have written an extensive demo on how to do this which is available in this Git repository: https://github.com/sdl/odata-example

# DataSources

The OData standard is based around resources that are well defined and modelled in something called the EDM. The SDL OData framework is build around the principle of having a well defined EDM. The EDM is a registry that contains which entities are present in the model and which properties and relations they have.

Based on the EDM the OData framework parses the input that is being sent or requested. Most important is that in order to return a result or show requested data there needs to be a datasource. That is why in the OData framework this is the most critical component and we offer the flexibility to plug in any datasource you want.

## JPA DataSource

There is a JPA Datasource available that allows you to use JPA annotated entities in your OData framework with minimal effort. Just simply configure the JPA Datasource to point to your JPA model and where the database is and you are good to go. You can find more information and the JPA datasource extension itself here: https://github.com/sdl/odata-jpa-datasource

# Building the OData Framework
In order to build and run the OData framework on your pc the following is required:
* Maven 3.x or higher
* JDK 8 or higher
* Scala SDK 2.10.x

If above pre-requisites are met building is as simple as running the following command `mvn clean install`

# Maven Artefacts
```
<depedencies>
   <dependency>
      <groupId>com.sdl</groupId>
      <artifactId>odata_service</artifactId>
      <version>2.0.4</version>
   </dependency>
   <dependency>
      <groupId>com.sdl</groupId>
      <artifactId>odata_common</artifactId>
      <version>2.0.4</version>
   </dependency>
<dependencies>
```

## Components

The SDL OData v4 Framework consists of the following Architecture components, each represented by their own Maven module:

- `odata_api` - Framework APIs
- `odata_assembly` - Assembly structure for standalone distribution
- `odata_checkstyle` - Checkstyle configuration
- `odata_client` - OData Java Client library
- `odata_common` - Common packages and utilities
- `odata_controller` - Spring Boot REST controller
- `odata_edm` - The OData EDM metadata (Entity Data Model)
- `odata_parser` - OData URI parser
- `odata_processor` - Handlers for processing requests
- `odata_renderer` - Renderers for Atom and JSON output
- `odata_service` - The core OData service and Akka based processing engine
- `odata_test` - Test components
- `odata_war` - OData WAR distribution artifact
- `odata_webservice` - Spring Boot based OData HTTP webservice container

# License
Copyright (c) 2014 All Rights Reserved by the SDL Group.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
