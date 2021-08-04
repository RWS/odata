/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.client;

import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.mockito.Mockito.when;

/**
 * URL Test Utils.
 */
public final class URLTestUtils {

    private URLTestUtils() {
    }

    public static String loadTextFile(String fileName) {
        URL url = URLTestUtils.class.getResource(fileName);
        File file;
        try {
            file = new File(url.toURI());
            return getFileContent(file);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileContent(File file) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                printWriter.print(line + System.lineSeparator());
            }
        }
        return stringWriter.toString();
    }

    public static URL getIOExceptionThrowingUrl(boolean throwExceptionOnOpen) throws IOException {

        if (throwExceptionOnOpen) {
            final URLStreamHandler openConnectionExceptionThrowingHandler = new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(final URL arg0)
                        throws IOException {
                    throw new IOException("Mock IOException from openConnection");
                }

                @Override
                protected URLConnection openConnection(final URL arg0, final Proxy proxy)
                        throws IOException {
                    throw new IOException("Mock IOException from openConnection");
                }
            };
            return new URL("http://mock.com", "service", 80, "",
                    openConnectionExceptionThrowingHandler);
        } else {

            final URLStreamHandler getInputStreamExceptionThrowingHandler = new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(final URL arg0)
                        throws IOException {
                    URLConnection mockURLConnection = Mockito.mock(URLConnection.class);
                    when(mockURLConnection.getInputStream()).thenThrow(
                            new IOException("Mock IOException from getInputStream"));

                    return mockURLConnection;
                }
            };
            return new URL("http://mock.com", "service", 80, "",
                    getInputStreamExceptionThrowingHandler);
        }
    }


}
