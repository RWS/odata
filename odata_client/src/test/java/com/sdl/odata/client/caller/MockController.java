/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.client.caller;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToString;

/**
 * MockController.
 */
@Component
@RequestMapping("/")
class MockController {

    private static final String RESPONSE = "response.xml";

    @RequestMapping(value = "{code:\\d{3}}")
    ResponseEntity<?> respondWithCode(@PathVariable int code) throws IOException {
        return ResponseEntity.status(HttpStatus.valueOf(code)).body(String.valueOf(code));
    }

    @RequestMapping(value = RESPONSE)
    ResponseEntity<?> respondWithXML() {
        try (InputStream stream = getClass().getResourceAsStream("/" + RESPONSE)) {
            return ResponseEntity.ok().body(copyToString(stream, UTF_8));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
