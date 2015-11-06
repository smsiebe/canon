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
package org.geoint.canon.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.geoint.canon.event.EventMessage;

/**
 * Converts an object to/from event messages.
 *
 * @author steve_siebert
 * @param <E> domain type
 */
public interface EventCodec<E> {

    /**
     * Determines if this codec can encode/decode the specified event type.
     *
     * @return true if this codec can encode/decode this event
     */
    String getSupportedEventType ();

    /**
     * Convert the domain event instance to event headers and encoded event
     * content.
     *
     * @param event
     * @param headers
     * @param out
     * @throws IOException thrown if there are problems writing to the stream
     * @throws EventCodecException thrown if there is a problem encoding the
     * domain event
     */
    void encode(E event, Map<String, String> headers, OutputStream out)
            throws IOException, EventCodecException;

    /**
     * Converts an EventMessage to a domain event.
     *
     * @param e event message
     * @param headers map to add event headers
     * @return domain event instance
     * @throws IOException thrown if there are problems reading from the stream
     * @throws EventCodecException thrown if there is a problem decoding the
     * domain event
     */
    E decode(EventMessage e, Map<String, String> headers)
            throws IOException, EventCodecException;
}
