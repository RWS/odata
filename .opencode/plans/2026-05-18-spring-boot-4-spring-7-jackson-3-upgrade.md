# Spring Boot 4 / Spring Framework 7 / Jackson 3 Upgrade Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Upgrade all framework dependencies from Spring Boot 3.5.11 / Spring 6.2.17 / Jackson 2.17.3 to Spring Boot 4.0.6 / Spring Framework 7.0.7 / Jackson 3.1.3.

**Architecture:** All version properties are centralized in the root `pom.xml`. Jackson usage is entirely contained within the `odata_renderer` module and uses the streaming API heavily, requiring method-level rename changes in addition to package changes. The Spring upgrade is mostly a property version bump plus a starter rename (`spring-boot-starter-web` → `spring-boot-starter-webmvc`).

**Tech Stack:** Java 25, Maven multi-module (15 modules), Spring Boot 4.0.6, Spring Framework 7.0.7, Jackson 3.1.3, Scala 2.12, Apache Pekko 1.4.

---

## Affected Files

| File | Change |
|---|---|
| `pom.xml` (root) | Version property bumps, Jackson group ID/artifact changes, BOM swap, starter rename |
| `odata_renderer/pom.xml` | Jackson dependency group ID changes |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonWriter.java` | Package rename + method renames |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonErrorResponseWriter.java` | Package rename + method renames |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonServiceDocumentWriter.java` | Package rename + method renames |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonCodecMapper.java` | Package rename, `ObjectMapper` → `JsonMapper` |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonPropertyWriter.java` | Package rename + method renames |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonWriterUtil.java` | Package rename |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/json/util/JsonWriterUtil.java` | Package rename |
| `odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/core/JsonProcessor.java` | Package rename + method renames (`getCurrentName()` → `currentName()`, `getText()` → `getString()`, `FIELD_NAME` → `PROPERTY_NAME`) |
| `odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/JsonLinkUnmarshaller.java` | Package rename + method renames |
| `odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/ODataJsonActionParser.java` | Package rename, `ObjectMapper` → `JsonMapper`, `TypeReference` package change |
| `odata_renderer/src/main/java/com/sdl/odata/renderer/batch/ODataBatchRequestRenderer.java` | Package rename, `ObjectMapper` → `JsonMapper` |
| `odata_renderer/src/test/java/com/sdl/odata/renderer/util/PrettyPrinter.java` | Package rename, `ObjectMapper` → `JsonMapper` |
| `odata_renderer/src/test/java/com/sdl/odata/renderer/json/writer/JsonPropertyWriterTest.java` | Package rename + method renames |
| `odata_renderer/src/test/java/com/sdl/odata/renderer/json/util/JsonWriterUtilTest.java` | Package rename |

---

## Jackson 3 API Migration Reference

Use this table when making method-level changes in Tasks 3–5:

| Jackson 2 | Jackson 3 | Notes |
|---|---|---|
| `com.fasterxml.jackson.core.*` | `tools.jackson.core.*` | All streaming classes |
| `com.fasterxml.jackson.databind.*` | `tools.jackson.databind.*` | ObjectMapper etc. |
| `com.fasterxml.jackson.core.type.TypeReference` | `tools.jackson.core.type.TypeReference` | Package: `tools.jackson.core.type` |
| `com.fasterxml.jackson.annotation.*` | **UNCHANGED** (still `com.fasterxml.jackson.annotation`) | Annotations artifact is 2.x |
| `JsonToken.FIELD_NAME` | `JsonToken.PROPERTY_NAME` | Enum constant renamed |
| `jsonParser.getCurrentName()` | `jsonParser.currentName()` | Parser method |
| `jsonParser.getText()` | `jsonParser.getString()` | Parser method |
| `jsonParser.getCurrentLocation()` | `jsonParser.currentLocation()` | Parser method |
| `jsonParser.getTokenLocation()` | `jsonParser.currentTokenLocation()` | Parser method |
| `jsonGenerator.writeFieldName(x)` | `jsonGenerator.writePropertyName(x)` | Generator method |
| `jsonGenerator.writeObject(x)` | `jsonGenerator.writePOJO(x)` | Generator method |
| `jsonGenerator.writeStringField(k,v)` | `jsonGenerator.writeStringProperty(k,v)` | Generator method |
| `jsonGenerator.writeNumberField(k,v)` | `jsonGenerator.writeNumberProperty(k,v)` | Generator method |
| `jsonGenerator.writeArrayFieldStart(k)` | `jsonGenerator.writeArrayPropertyStart(k)` | Generator method |
| `jsonGenerator.writeObjectFieldStart(k)` | `jsonGenerator.writeObjectPropertyStart(k)` | Generator method |
| `jsonGenerator.writeNullField(k)` | `jsonGenerator.writeNullProperty(k)` | Generator method |
| `new ObjectMapper()` | `JsonMapper.builder().build()` or `new JsonMapper()` | Use `tools.jackson.databind.json.JsonMapper` |
| `objectMapper.writeValue(gen, obj)` | `objectMapper.writeValue(gen, obj)` | No change in signature |
| `ObjectMapper extends ObjectMapper` (subclass) | `JsonCodecMapper extends JsonMapper` | `JsonCodecMapper` must extend `JsonMapper` |
| `new JsonFactory()` | `JsonFactory.builder().build()` | Factory now immutable/builder-based |
| `JsonProcessingException` | `JacksonException` (RuntimeException) | Exception hierarchy change |

---

## Task 1: Bump version properties in root `pom.xml`

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: Update Spring Boot and Spring Framework version properties**

In `pom.xml`, locate the `<properties>` section and change:

```xml
<!-- FROM -->
<spring.version>6.2.17</spring.version>
<spring-boot.version>3.5.11</spring-boot.version>

<!-- TO -->
<spring.version>7.0.7</spring.version>
<spring-boot.version>4.0.6</spring-boot.version>
```

- [ ] **Step 2: Update Jackson version property and group IDs**

In `pom.xml` `<properties>`:
```xml
<!-- FROM -->
<jackson.version>2.17.3</jackson.version>

<!-- TO -->
<jackson.version>3.1.3</jackson.version>
```

In `pom.xml` `<dependencyManagement>`, find all Jackson dependencies and update their group IDs:

```xml
<!-- jackson-core and jackson-databind: com.fasterxml.jackson.core -> tools.jackson.core -->
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>${jackson.version}</version>
</dependency>
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
</dependency>
<!-- jackson-annotations stays on com.fasterxml; Jackson 3 ships it at 2.20.x -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.20.0</version>
</dependency>
```

Note: `jackson-annotations` remains under `com.fasterxml.jackson.core` because Jackson 3 still ships that artifact under the old group.

- [ ] **Step 3: Update spring-boot-starter-parent BOM import if present**

In `pom.xml` `<dependencyManagement>`, verify that the Spring Boot BOM import (`spring-boot-dependencies` or `spring-boot-starter-parent`) has its version set via `${spring-boot.version}`. It should already be — no additional change needed beyond Step 1's property bump.

- [ ] **Step 4: Rename spring-boot-starter-web to spring-boot-starter-webmvc**

In `pom.xml` `<dependencyManagement>` and in child modules, rename:

```xml
<!-- FROM -->
<artifactId>spring-boot-starter-web</artifactId>

<!-- TO -->
<artifactId>spring-boot-starter-webmvc</artifactId>
```

Check these child module poms:
- `odata_common/pom.xml` — uses `spring-boot-starter-web`
- `odata_controller/pom.xml` — uses `spring-boot-starter-web`

Update both to `spring-boot-starter-webmvc`.

- [ ] **Step 5: Verify the build resolves dependencies**

```bash
mvn dependency:resolve -q
```

Expected: BUILD SUCCESS with no unresolved artifacts. If Jackson or Spring artifacts fail to resolve, check that the correct group IDs are in `<dependencyManagement>`.

- [ ] **Step 6: Commit**

```bash
git add pom.xml odata_common/pom.xml odata_controller/pom.xml
git commit -m "chore: bump Spring Boot 4.0.6, Spring 7.0.7, Jackson 3.1.3 versions in POM"
```

---

## Task 2: Update `odata_renderer/pom.xml` Jackson dependency declarations

**Files:**
- Modify: `odata_renderer/pom.xml`

- [ ] **Step 1: Update Jackson group IDs in renderer module POM**

In `odata_renderer/pom.xml`, find the Jackson dependencies and update group IDs:

```xml
<!-- FROM -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>

<!-- TO -->
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
</dependency>
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

- [ ] **Step 2: Commit**

```bash
git add odata_renderer/pom.xml
git commit -m "chore: update odata_renderer POM to Jackson 3 group IDs"
```

---

## Task 3: Migrate Jackson streaming API in `JsonProcessor.java` and `JsonLinkUnmarshaller.java`

**Files:**
- Modify: `odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/core/JsonProcessor.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/JsonLinkUnmarshaller.java`

- [ ] **Step 1: Update imports in `JsonProcessor.java`**

Replace:
```java
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
```
With:
```java
import tools.jackson.core.JsonFactory;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
```

- [ ] **Step 2: Update method calls in `JsonProcessor.java`**

Apply these changes:

1. Factory initialization (line ~43):
```java
// FROM
private static final JsonFactory JSON_FACTORY = new JsonFactory();
// TO
private static final JsonFactory JSON_FACTORY = JsonFactory.builder().build();
```

2. Token constant:
```java
// FROM (all occurrences)
JsonToken.FIELD_NAME
// TO
JsonToken.PROPERTY_NAME
```

3. Parser methods:
```java
// FROM
jsonParser.getCurrentName()
// TO
jsonParser.currentName()

// FROM
jsonParser.getText()
// TO
jsonParser.getString()
```

4. If any `catch (JsonProcessingException e)` exists, change to:
```java
import tools.jackson.core.JacksonException;
// ...
catch (JacksonException e)
```

- [ ] **Step 3: Update imports in `JsonLinkUnmarshaller.java`**

Replace:
```java
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
```
With:
```java
import tools.jackson.core.JsonFactory;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
```

- [ ] **Step 4: Update method calls in `JsonLinkUnmarshaller.java`**

Apply:
```java
// FROM
new JsonFactory().createParser(bodyText)
// TO
JsonFactory.builder().build().createParser(bodyText)

// FROM
JsonToken.FIELD_NAME
// TO
JsonToken.PROPERTY_NAME

// FROM
jsonParser.getText()
// TO
jsonParser.getString()
```

- [ ] **Step 5: Compile the renderer module only**

```bash
mvn compile -pl odata_renderer -am -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add \
  odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/core/JsonProcessor.java \
  odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/JsonLinkUnmarshaller.java
git commit -m "refactor(renderer): migrate JsonProcessor and JsonLinkUnmarshaller to Jackson 3"
```

---

## Task 4: Migrate Jackson streaming API in writer classes

**Files:**
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonWriter.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonErrorResponseWriter.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonServiceDocumentWriter.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonPropertyWriter.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonWriterUtil.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/util/JsonWriterUtil.java`

- [ ] **Step 1: Update imports in all writer files**

In each file, replace `com.fasterxml.jackson.core.{JsonEncoding,JsonFactory,JsonGenerator}` with `tools.jackson.core.{JsonEncoding,JsonFactory,JsonGenerator}`. Import only what the file uses.

- [ ] **Step 2: Update `JsonFactory` construction in all writer files**

In every file that has `private static final JsonFactory JSON_FACTORY = new JsonFactory();`, change to:
```java
private static final JsonFactory JSON_FACTORY = JsonFactory.builder().build();
```

Files affected: `JsonWriter.java`, `JsonErrorResponseWriter.java`, `JsonServiceDocumentWriter.java`, `JsonPropertyWriter.java`.

- [ ] **Step 3: Update `JsonGenerator` method calls — "field" → "property"**

Apply these renames across all writer files:

| From | To |
|---|---|
| `jsonGenerator.writeStringField(k, v)` | `jsonGenerator.writeStringProperty(k, v)` |
| `jsonGenerator.writeNumberField(k, v)` | `jsonGenerator.writeNumberProperty(k, v)` |
| `jsonGenerator.writeArrayFieldStart(k)` | `jsonGenerator.writeArrayPropertyStart(k)` |
| `jsonGenerator.writeObjectFieldStart(k)` | `jsonGenerator.writeObjectPropertyStart(k)` |
| `jsonGenerator.writeNullField(k)` | `jsonGenerator.writeNullProperty(k)` |
| `jsonGenerator.writeFieldName(k)` | `jsonGenerator.writePropertyName(k)` |
| `jsonGenerator.writeObject(obj)` | `jsonGenerator.writePOJO(obj)` |

These methods are **NOT renamed** (leave them as-is): `writeStartObject()`, `writeEndObject()`, `writeStartArray()`, `writeEndArray()`, `writeString(v)`, `writeNumber(v)`, `writeBoolean(v)`, `writeNull()`, `writeRaw(s)`, `close()`.

- [ ] **Step 4: Compile**

```bash
mvn compile -pl odata_renderer -am -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonWriter.java \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonErrorResponseWriter.java \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonServiceDocumentWriter.java \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonPropertyWriter.java \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonWriterUtil.java \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/util/JsonWriterUtil.java
git commit -m "refactor(renderer): migrate JSON writer streaming API to Jackson 3"
```

---

## Task 5: Migrate `ObjectMapper` usages to `JsonMapper`

**Files:**
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonCodecMapper.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/ODataJsonActionParser.java`
- Modify: `odata_renderer/src/main/java/com/sdl/odata/renderer/batch/ODataBatchRequestRenderer.java`

- [ ] **Step 1: Update `JsonCodecMapper.java`**

Replace imports:
```java
// FROM
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;

// TO
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.JsonGenerator;
```

Replace class declaration:
```java
// FROM
public class JsonCodecMapper extends ObjectMapper {

// TO
public class JsonCodecMapper extends JsonMapper {
```

Check the overridden `writeValue(JsonGenerator, Object)` method. In Jackson 3, exceptions from Jackson are `JacksonException` (RuntimeException), so the `throws IOException` clause may need to change:
```java
import tools.jackson.core.JacksonException;

@Override
public void writeValue(JsonGenerator gen, Object value) throws JacksonException {
    // existing body — check if UUID handling logic still applies
}
```

- [ ] **Step 2: Update `ODataJsonActionParser.java`**

Replace:
```java
// FROM
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

// TO
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.type.TypeReference;
```

Replace usage:
```java
// FROM
private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

// TO
private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder().build();
```

The `readValue()` call does not change.

- [ ] **Step 3: Update `ODataBatchRequestRenderer.java`**

Replace:
```java
// FROM
import com.fasterxml.jackson.databind.ObjectMapper;

// TO
import tools.jackson.databind.json.JsonMapper;
```

In `getRenderedJSON()`, replace:
```java
// FROM
ObjectMapper objectMapper = new ObjectMapper();

// TO
JsonMapper objectMapper = JsonMapper.builder().build();
```

- [ ] **Step 4: Compile**

```bash
mvn compile -pl odata_renderer -am -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add \
  odata_renderer/src/main/java/com/sdl/odata/renderer/json/writer/JsonCodecMapper.java \
  odata_renderer/src/main/java/com/sdl/odata/unmarshaller/json/ODataJsonActionParser.java \
  odata_renderer/src/main/java/com/sdl/odata/renderer/batch/ODataBatchRequestRenderer.java
git commit -m "refactor(renderer): migrate ObjectMapper usages to Jackson 3 JsonMapper"
```

---

## Task 6: Migrate Jackson test files

**Files:**
- Modify: `odata_renderer/src/test/java/com/sdl/odata/renderer/util/PrettyPrinter.java`
- Modify: `odata_renderer/src/test/java/com/sdl/odata/renderer/json/writer/JsonPropertyWriterTest.java`
- Modify: `odata_renderer/src/test/java/com/sdl/odata/renderer/json/util/JsonWriterUtilTest.java`

- [ ] **Step 1: Update `PrettyPrinter.java`**

```java
// FROM
import com.fasterxml.jackson.databind.ObjectMapper;
// new ObjectMapper()

// TO
import tools.jackson.databind.json.JsonMapper;
// JsonMapper.builder().build()
```

- [ ] **Step 2: Update `JsonPropertyWriterTest.java`**

```java
// FROM
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

// TO
import tools.jackson.core.JsonFactory;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
```

Replace:
- `new JsonFactory().createParser(json)` → `JsonFactory.builder().build().createParser(json)`
- `jsonParser.getText()` → `jsonParser.getString()`
- `JsonToken.FIELD_NAME` → `JsonToken.PROPERTY_NAME` (if used)

- [ ] **Step 3: Update `JsonWriterUtilTest.java`**

```java
// FROM
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

// TO
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonFactory;
import tools.jackson.core.JsonGenerator;
```

Replace:
- `new JsonFactory().createGenerator(stream, JsonEncoding.UTF8)` → `JsonFactory.builder().build().createGenerator(stream, JsonEncoding.UTF8)`

- [ ] **Step 4: Run renderer tests**

```bash
mvn test -pl odata_renderer -q
```

Expected: BUILD SUCCESS with all tests passing.

- [ ] **Step 5: Commit**

```bash
git add \
  odata_renderer/src/test/java/com/sdl/odata/renderer/util/PrettyPrinter.java \
  odata_renderer/src/test/java/com/sdl/odata/renderer/json/writer/JsonPropertyWriterTest.java \
  odata_renderer/src/test/java/com/sdl/odata/renderer/json/util/JsonWriterUtilTest.java
git commit -m "test(renderer): migrate test Jackson imports and API calls to Jackson 3"
```

---

## Task 7: Full project build and test

- [ ] **Step 1: Run full build**

```bash
mvn clean install -q
```

Expected: BUILD SUCCESS. The most common failures will be:
- Spring Boot starter renames missed in a module (check error for the artifact ID)
- Spring Framework class/package changes in other modules
- Jackson 3 symbols in places not covered by Tasks 3–6

For each failure, locate the class/method in the error, look it up in the Jackson 3 migration reference table above or the Spring Boot 4 migration guide, and apply the fix.

- [ ] **Step 2: Run all tests**

```bash
mvn test -q
```

Expected: BUILD SUCCESS with all tests passing.

- [ ] **Step 3: Commit any additional fixes**

```bash
git add -u
git commit -m "fix: resolve remaining Spring Boot 4 / Spring 7 / Jackson 3 compilation issues"
```

---

## Task 8: Fix `odata_assembly` stale plugin version (housekeeping)

**Files:**
- Modify: `odata_assembly/pom.xml`

- [ ] **Step 1: Remove the explicit plugin version override**

In `odata_assembly/pom.xml`, find and remove the `<version>2.6</version>` line from the `maven-resources-plugin` declaration so it inherits `3.5.0` from the parent.

- [ ] **Step 2: Build the assembly module**

```bash
mvn package -pl odata_assembly -am -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
git add odata_assembly/pom.xml
git commit -m "chore: remove stale maven-resources-plugin 2.6 override in odata_assembly"
```

---

## Notes

- **`jackson-annotations` stays on `com.fasterxml.jackson.core`**: Jackson 3 continues to ship `jackson-annotations` under the old group ID. Do NOT change that dependency's group ID.
- **No `@JsonComponent`/`@JsonMixin` annotations exist** in this codebase, so the Spring Boot 4 annotation renames do not apply.
- **No `spring.jackson.*` properties** exist in any config files, so those renames also do not apply.
- **Undertow**: This project uses Tomcat — Undertow was dropped in Spring Boot 4 but was never used here.
- **Jakarta EE 11**: The project already uses `jakarta.*` namespaces. Current `jakarta.servlet-api` is `6.1.0` which meets Spring Boot 4's requirement.
- **JUnit 5 version**: The project pins JUnit Jupiter at `5.9.2`. Spring Boot 4 BOM ships a newer version. Consider removing the explicit `junit-jupiter.version` property to let Boot manage it, or update it to `5.12+` to avoid stale test framework behavior.
