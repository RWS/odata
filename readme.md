# Tridion OData v4 Framework 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sdl/odata/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sdl/odata)

This is the Tridion Open Data Framework based on the [OData standard](http://www.odata.org/) fully implemented in Java. The framework provides a Java implementation of the OData Service and Java Client Libraries, aligned to the OData v4 OASIS standard.

# Starting the Service

The OData service comes with a set of prepared scripts to run and install the service:
- **Linux/macOS:** `./bin/start.sh` / `./bin/stop.sh`
- **Windows:** `.\bin\start.bat`

Make sure shell scripts are executable first: `chmod +x bin/start.sh bin/stop.sh`

This can be done from a prebuilt distribution or using the output artifacts in the `odata_assembly` Maven module.

[Full Documentation](odata_assembly/src/main/resources/readme.md)

## Once Started
The following URLs are available by default:
- http://localhost:8080/odata.svc — OData root collections
- http://localhost:8080/odata.svc/$metadata — OData EDM model

The standard framework ships with no entities, so results will be empty until a DataSource is configured.

## HTTPS
HTTPS is disabled by default and can be enabled via command-line arguments:
```
--https.enabled=true
--https.port=8084
--https.keystore-path=config/keystore
--https.key-alias=tomcat
--https.keystore-passwd=changeit
--https.truststore-passwd=changeit
```

# Building your own Service
Provide data models and datasources for the framework to pick up. See the demo repository for a full walkthrough: https://github.com/sdl/odata-example

# DataSources

The OData framework is built around a well-defined EDM (Entity Data Model) — a registry of entities, their properties, and relations. To serve data, a datasource must be configured. The framework allows any datasource to be plugged in.

## JPA DataSource

A JPA DataSource extension is available for using JPA-annotated entities with minimal effort. See: https://github.com/sdl/odata-jpa-datasource

# Building the OData Framework
Prerequisites:
* Maven 3.x or higher
* JDK 25 or higher
* Scala SDK 2.12.x (downloaded automatically by the Scala Maven plugin if missing)

```
mvn clean install
```

# Maven Artifacts
```xml
<dependencies>
    <dependency>
        <groupId>com.sdl</groupId>
        <artifactId>odata_service</artifactId>
        <version>2.14</version>
    </dependency>
    <dependency>
        <groupId>com.sdl</groupId>
        <artifactId>odata_common</artifactId>
        <version>2.14</version>
    </dependency>
</dependencies>
```

## Components

| Module | Description |
|---|---|
| `odata_api` | Framework APIs |
| `odata_assembly` | Assembly structure for standalone distribution |
| `odata_checkstyle` | Checkstyle configuration |
| `odata_client` | OData Java Client library |
| `odata_client_api` | OData Java Client API |
| `odata_common` | Common packages and utilities |
| `odata_controller` | Spring Boot REST controller |
| `odata_edm` | OData EDM metadata (Entity Data Model) |
| `odata_parser` | OData URI parser |
| `odata_processor` | Handlers for processing requests |
| `odata_renderer` | Renderers for Atom and JSON output |
| `odata_service` | Core OData service and Pekko-based processing engine |
| `odata_test` | Test components |
| `odata_war` | OData WAR distribution artifact |
| `odata_webservice` | Spring Boot based OData HTTP webservice container |

# License
Copyright (c) 2014-2026 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
