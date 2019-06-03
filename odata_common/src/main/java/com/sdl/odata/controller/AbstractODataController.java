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
package com.sdl.odata.controller;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.api.service.ODataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * The abstract OData Controller.
 */
public abstract class AbstractODataController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractODataController.class);

    private static final int BUFFER_SIZE = 1024;
    private static final int DEFAULT_PORT_NUMBER = 80;
    private static final int DEFAULT_SSL_PORT_NUMBER = 443;

    @Autowired
    private ODataService oDataService;

    @RequestMapping(method = {
            GET, POST, PATCH, PUT, DELETE
    })
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws ServletException, IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Start processing request from: {}", servletRequest.getRemoteAddr());
        }

        ODataResponse oDataResponse;
        try {
            ODataRequest oDataRequest = buildODataRequest(servletRequest);
            doWireLogging(oDataRequest);
            oDataResponse = oDataService.handleRequest(oDataRequest);
            fillServletResponse(oDataResponse, servletResponse);
        } catch (ODataException e) {
            throw new ServletException(e);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Finished processing request from: {}", servletRequest.getRemoteAddr());
        }
    }

    /**
     * Converts an {@code HttpServletRequest} to an {@code ODataRequest}.
     *
     * @param servletRequest The {@code HttpServletRequest}.
     * @return An {@code ODataRequest} containing the request information.
     * @throws java.io.IOException If an I/O error occurs.
     */
    private ODataRequest buildODataRequest(HttpServletRequest servletRequest) throws IOException {
        ODataRequest.Builder builder = new ODataRequest.Builder();

        builder.setMethod(ODataRequest.Method.valueOf(servletRequest.getMethod()));

        // Unfortunately, HttpServletRequest makes it difficult to get the full URI
        StringBuilder sb = getRequestURL(servletRequest);
        String queryString = servletRequest.getQueryString();
        if (!isNullOrEmpty(queryString)) {
            sb.append('?').append(queryString);
        }
        builder.setUri(sb.toString());

        // Unfortunately, HttpServletRequest has a very old-style API to iterate the headers
        Enumeration e = servletRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = servletRequest.getHeader(name);
            builder.setHeader(name, value);
        }

        // Read the request body
        InputStream in = servletRequest.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int count;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        builder.setBody(out.toByteArray());

        builder.addAdditionalData(new MapMDCAdapter(MDC.getCopyOfContextMap()));

        return builder.build();
    }

    /**
     * In cases when {@link HttpServletRequest} is wrapped, request url will consist values from top wrapper now.
     * Instead of schema, port and server name from inner {@link org.apache.coyote.Request}.
     * Default case is when service is behind load balancer and {@link org.apache.catalina.filters.RemoteIpFilter}
     * is used for X-Forwarded headers.
     *
     * @param request wrapped/original request.
     * @return request URL based on values from wrapping Request.
     */
    private StringBuilder getRequestURL(HttpServletRequest request) {
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0) {
            port = DEFAULT_PORT_NUMBER;
        }

        StringBuilder url = new StringBuilder();
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != DEFAULT_PORT_NUMBER))
                || (scheme.equals("https") && (port != DEFAULT_SSL_PORT_NUMBER))) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getRequestURI());
        return url;
    }

    /**
     * Transfers data from an {@code ODataResponse} into an {@code HttpServletResponse}.
     *
     * @param oDataResponse   The {@code ODataResponse}.
     * @param servletResponse The {@code HttpServletResponse}
     * @throws java.io.IOException If an I/O error occurs.
     */
    private void fillServletResponse(ODataResponse oDataResponse, HttpServletResponse servletResponse)
            throws IOException, ODataException {
        servletResponse.setStatus(oDataResponse.getStatus().getCode());

        for (Map.Entry<String, String> entry : oDataResponse.getHeaders().entrySet()) {
            servletResponse.setHeader(entry.getKey(), entry.getValue());
        }

        byte[] body = oDataResponse.getBody();
        if (body != null && body.length != 0) {
            OutputStream out = servletResponse.getOutputStream();
            out.write(oDataResponse.getBody());
            out.flush();
        } else if (oDataResponse.getStreamingContent() != null) {
            oDataResponse.getStreamingContent().write(servletResponse);
        }
    }

    private void doWireLogging(ODataRequest request) throws UnsupportedEncodingException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("RAW REQUEST LOGGING");

            LOG.trace("{} request for URL: {}", request.getMethod().name(), request.getUri());
            for (Map.Entry<String, String> headerEntry : request.getHeaders().entrySet()) {
                LOG.trace("Header: {} value: {}", headerEntry.getKey(), headerEntry.getValue());
            }

            LOG.trace("BODY: {}", request.getBodyText(UTF_8.name()));
        }
    }

}
