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
package org.geoint.canon.server;

import java.io.File;
import javax.enterprise.inject.Produces;
import org.eclipse.jetty.server.Server;
import org.geoint.canon.Canon;
import org.geoint.canon.CanonInitializationException;

/**
 * Runs Canon as a network service.
 */
public class CanonServer {

    private final Canon canon;
    private Server jetty;

    public CanonServer(Canon canon) {
        this.canon = canon;
    }

    public void start() {

    }

    public void stop() {

    }

    @Produces
    public Canon getCanonInstance() {
        return canon;
    }

    private Server initJetty() {
        Server server = new Server();

        return server;
    }

    public static CanonServer load(File basePath)
            throws CanonInitializationException {
        throw new UnsupportedOperationException();
    }

    public static CanonServer newTempServer()
            throws CanonInitializationException {
        return new CanonServer(Canon.newTempInstance());
    }

    public static void main(String... args) {

    }

}
