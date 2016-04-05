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
import org.geoint.canon.event.EventMessage;

/**
 * Converts an object to/from event messages.
 *
 * @author steve_siebert
 * @param <E> domain type
 */
public interface EventCodec<E> {

    /**
     * Determine if this codec can encode/decode this provided event type.
     *
     * @param eventType event type/name
     * @return true if this codec can support this event type, otherwise false
     */
    boolean isSupported(String eventType);

    /**
     * Convert the domain event instance to encoded event content.
     *
     * @param event domain event to convert
     * @param out stream to write converted event
     * @throws IOException thrown if there are problems writing to the stream
     * @throws EventCodecException thrown if there is a problem encoding the
     * domain event
     */
    void encode(E event, OutputStream out)
            throws IOException, EventCodecException;

    /**
     * Converts the {@link EventMessage#getEventContent() content} of an
     * EventMessage to a domain event.
     *
     * @param e event message
     * @return domain event instance
     * @throws IOException thrown if there are problems reading from the stream
     * @throws EventCodecException thrown if there is a problem decoding the
     * domain event
     */
    E decode(EventMessage e)
            throws IOException, EventCodecException;
}
