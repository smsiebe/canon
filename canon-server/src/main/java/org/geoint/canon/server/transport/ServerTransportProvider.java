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
package org.geoint.canon.server.transport;

import java.util.concurrent.ExecutorService;
import org.geoint.canon.Canon;

/**
 * {@link ServerLoader} discovered provider for
 * {@link EventServerTransport transport} implementations.
 *
 * @author steve_siebert
 */
public interface ServerTransportProvider {

    /**
     * Return the URL scheme used for this transport.
     *
     * @return transport URL scheme used to uniquely identify this transport
     */
    String getTransportScheme();

    /**
     * Create a new EventServerTransport instance for the specified transport
     * URL.
     *
     * @param transportUrl
     * @param canon
     * @param executor
     * @return transport
     */
    EventServerTransport newTransport(String transportUrl,
            Canon canon, ExecutorService executor);
}
