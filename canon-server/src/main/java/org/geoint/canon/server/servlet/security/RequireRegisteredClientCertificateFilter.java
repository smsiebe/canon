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
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Inject;
import javax.security.cert.X509Certificate;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.geoint.canon.server.security.AuthenticatedProfile;
import org.geoint.canon.server.security.ClientCertificateUtil;
import org.geoint.canon.server.security.IdentityManager;

/**
 * Validates the client certificate requesting its profile from the
 * {@link IdentityManager}.
 * <p>
 * This filter is expected to be chained after
 * {@link RequireClientCertificateFilter}, which ensures there is a client cert
 * provided.
 * <p>
 * This filter is intended to be used on all protected canon resources that
 * require the requestor has a profile and its client certificate has been
 * linked with that profile.
 *
 * <h2>On Success</h2>
 * On success the associated profile is set to the
 * {@code org.geoint.canon.request.profile} attribute on the request.
 *
 * <h2>On Failure</h2>
 * If the certificate is not registered with the identity manager this filter
 * returns an HTTP 401.
 *
 */
public class RequireRegisteredClientCertificateFilter implements Filter {

    public static final String PROFILE_ATTRIBUTE_NAME
            = "org.geoint.canon.request.profile";

    @Inject
    IdentityManager identity;
    @Inject
    ServletContext context;
    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        //assumes there are certs here, use with RequireClientCertificateFilter
        X509Certificate[] certs
                = ClientCertificateUtil.getClientCertificates((HttpServletRequest) request);

        Optional<AuthenticatedProfile> profile = identity.findProfile(certs);

        if (!profile.isPresent()) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unknown client certificate, you must first register your "
                    + "certificate.");
        }

        AuthenticatedProfile p = profile.get();
        if (!p.isEnabled()) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Client profile is disabled, please contact "
                    + "the Canon administrator to enable your profile.");
        }

        /*
         * Set the profile to the specified attribute.
         *
         * Setting this as a request attribute, rather than providing it as 
         * a Principal or in the session is intentional, as this implementation 
         * breaks from the JAAS API and impelementation (and I don't want to 
         * give the impression that it is JAAS compliant) and I want to give 
         * the IdentitiyManager implementation the responsibility for caching 
         * (and thus invalidation) rather than state managed on each individual 
         * session.
         */
        request.setAttribute(PROFILE_ATTRIBUTE_NAME, p);

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }

}
