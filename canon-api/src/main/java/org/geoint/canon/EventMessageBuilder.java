/*
 * Copyright 2015 Steve Siebert <steve@t-3-solutions.com>.
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
package org.geoint.canon;

import java.io.InputStream;
import org.geoint.canon.codec.EventCodec;

/**
 * Creates an EventMessage wrapper for an event.
 *
 * @author Steve Siebert <steve@t-3-solutions.com>
 */
public interface EventMessageBuilder {

    /**
     * Reference to the creator/publisher of the event.
     *
     * @param authorizerId
     * @return
     */
    EventMessageBuilder authorizedBy(String authorizerId);

    /**
     * Optional event id which caused indicates an event which caused this event
     * to be created.
     *
     * @param eventId
     * @return
     */
    EventMessageBuilder triggeredBy(String eventId);

    /**
     * Adds an optional event message header to the event.
     * <p>
     * Header names must not be null and may be any value. It's recommended that
     * applications namespace their header names to avoid collision with other
     * applications, however this is not mandatory. Header names starting with
     * {@code canon.} are reserved and may not be used.
     *
     * @param name
     * @param value
     * @return
     */
    EventMessageBuilder header(String name, String value);

    /**
     * Terminating method providing the event message payload.
     * <p>
     * This, or overloaded method, must be called last; any additional calls to
     * the EventMessageBuilder after the event message payload has been provided
     * will be ignored.
     *
     * @param event
     * @return
     */
    EventMessageBuilder event(InputStream event);

    /**
     * Terminating method providing the event message payload.
     * <p>
     * The provided event object will (attempt to be) converted to bytes using
     * the the resolved event codec.
     * <p>
     * This, or overloaded method, must be called last; any additional calls to
     * the EventMessageBuilder after the event message payload has been provided
     * will be ignored.
     *
     * @param event
     * @return
     */
    void event(Object event);

    /**
     * Terminating method providing the event message payload and a specific
     * codec to use to convert the event to bytes.
     * <p>
     * This, or overloaded method, must be called last; any additional calls to
     * the EventMessageBuilder after the event message payload has been provided
     * will be ignored.
     *
     * @param event
     * @param codec
     */
    void event(Object event, EventCodec<?> codec);
}
