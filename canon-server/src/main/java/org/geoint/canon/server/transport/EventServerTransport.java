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

/**
 * Creates a remotely accessible interface for the Canon instance.
 *
 * @author steve_siebert
 */
public interface EventServerTransport {

    /**
     * Synchronously start the transport.
     *
     * @throws Exception thrown if the transport cannot be started; transport
     * must be in a non-started state
     */
    void start() throws Exception;

    /**
     * Synchronously stop the transport.
     *
     * @throws Exception thrown if the transport shutdown in a non-graceful
     * manner; transport must be stopped
     */
    void stop() throws Exception;

}
