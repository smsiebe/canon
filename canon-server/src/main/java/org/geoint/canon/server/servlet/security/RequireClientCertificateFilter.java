/*
 * Copyright 2016 geoint.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.canon.server.servlet.security;

import java.io.IOException;
import java.io.PrintWriter;
import javax.security.cert.X509Certificate;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.geoint.canon.server.security.ClientCertificateUtil;

/**
 * Filter which requires at least one X509 client certificate be provided by the
 * client.
 *
 *
 */
public class RequireClientCertificateFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        X509Certificate[] certs
                = ClientCertificateUtil.getClientCertificates((HttpServletRequest) request);
        if (certs == null || certs.length == 0) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Canon requires mutual authentication.");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
