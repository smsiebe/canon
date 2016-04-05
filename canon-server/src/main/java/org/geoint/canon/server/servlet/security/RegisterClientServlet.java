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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.security.cert.X509Certificate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.geoint.canon.Canon;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventException;
import org.geoint.canon.server.security.ClientCertificateUtil;
import org.geoint.canon.server.security.IdentityManager;
import org.geoint.canon.server.servlet.security.RequireClientCertificateFilter;
import org.geoint.canon.server.security.event.RegisterClientCertRequest;

/**
 * Allows a client to self-register a new client certificate for an existing
 * profile, or register a new client certificate for an existing profile.
 * <p>
 * This endpoint expects that the client has provided a client certificate, so
 * it should be used along with {@link RequireClientCertificateFilter}, but that
 * certificate must not already be registered with the {@link IdentityManager}.
 *
 */
@WebServlet(name = "registerClient",
        description = "Special endpoint that allows a client to register itself")
public class RegisterClientServlet extends HttpServlet {

    private static final String PROPERTY_PROFILE_ID = "profileId";
    private static final Logger LOGGER
            = Logger.getLogger(RegisterClientServlet.class.getName());

    @Inject
    IdentityManager identity;
    @Inject
    Canon canon;

    /**
     * Client is requesting a new profile and/or a client certificate be linked
     * to an existing profile.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //assumes there are certs here, use with RequireClientCertificateFilter
        final X509Certificate[] certs
                = ClientCertificateUtil.getClientCertificates(req);

        //if the client provided a profileId, associate the client certificate
        //to that profile (if it exists).  We allow the client to specify the 
        //profileId here because approval requires a human.  We could have 
        //looked at the alternative subject identifier of the client certificate, 
        //but since this approach is designed to support self-signed 
        //certificates in a secure way, there really is no difference between 
        //passing it as a parameter or checking a self-signed cert.
        String profileId = req.getParameter(PROPERTY_PROFILE_ID);

        //validating this request is intentionally deferred for the 
        //handler, allowing events to be registered as they are requested.  
        //not only does this resolver any potential issue stemming from 
        //asynchronous handling, we want to record every attempt to be 
        //published, regardless of its validity, because its a security-related 
        //issue.
        try {
            AppendedEventMessage appended = canon.getAdminStream()
                    .createMessage(RegisterClientCertRequest.class)
                    //.authorizedBy()
                    .event(RegisterClientCertRequest.newRequest(profileId, certs[0]));
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(String.format("Registration request %s "
                        + "is processing.", appended));
            }
        } catch (EventException ex) {
            LOGGER.log(Level.SEVERE, String.format("Failed to complete client "
                    + "registration request for certificate '%s'",
                    certs[0].getSubjectDN().getName()),
                    ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to complete registration request, please try again "
                    + "later.");
        }
    }

//    
//    //verify the client certificate isn't already linked to a profile
//        Optional<AuthenticatedProfile> profile = identity.findProfile(certs);
//        if (profile.isPresent()) {
//            resp.sendError(HttpServletResponse.SC_CONFLICT,
//                    "Client certificate is already registered with an existing "
//                    + "profile.");
//            return;
//        }
}
