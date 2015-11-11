/*
 * Copyright 2015 geoint.org.
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
package org.geoint.canon.server.transport.jetty;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.geoint.canon.Canon;
import org.geoint.canon.server.transport.EventServerTransport;

/**
 * Uses Jetty instance to expose the canon instance over HTTP.
 *
 * @author steve_siebert
 */
public class JettyEventTransport implements EventServerTransport {

    private final Canon canon;
    private final Server server;

    private final static Logger LOGGER
            = Logger.getLogger(JettyEventTransport.class.getName());

    public JettyEventTransport(Canon canon, Server server) {
        this.canon = canon;
        this.server = server;
    }

    @Override
    public void start() throws Exception {
        LOGGER.log(Level.FINE, () -> "Starting jetty transport.");
        try {
            server.setStopAtShutdown(true);
            
            canon.getStreams().
            server.setHandler(new JettyEventHandler());
            
            server.start();
            LOGGER.log(Level.FINE, ()
                    -> String.format("Jetty transport started: %s ",
                            server.getURI().toString()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unable to start jetty transport.", ex);
            if (!server.isStopped()) {
                try {
                    server.stop();
                } catch (Exception ex1) {
                }
            }
            throw ex;
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            server.stop();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Jetty transport did not shutdown "
                    + "gracefully.", ex);
        }
    }

    private class JettyEventHandler extends AbstractHandler {

        @Override
        public void handle(String string, Request rqst, 
                HttpServletRequest hsr, HttpServletResponse hsr1)
                throws IOException, ServletException {
            
        }

        
    }
}
