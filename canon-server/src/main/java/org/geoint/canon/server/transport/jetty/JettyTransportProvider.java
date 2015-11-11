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

import java.util.concurrent.ExecutorService;
import org.geoint.canon.Canon;
import org.geoint.canon.server.transport.EventServerTransport;
import org.geoint.canon.server.transport.ServerTransportProvider;

/**
 * Transport provider for HTTP services using an embedded Jetty server.
 *
 * @author steve_siebert
 */
public class JettyTransportProvider implements ServerTransportProvider {

    public final static String SCHEME = "jetty";

    @Override
    public String getTransportScheme() {
        return SCHEME;
    }

    @Override
    public EventServerTransport newTransport(String transportUrl, 
            Canon canon, ExecutorService executor) {

    }

}
