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
package org.geoint.canon.event;

import java.io.InputStream;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.stream.StreamAppendException;

/**
 * Creates an EventMessage wrapper for an event.
 *
 * @author Steve Siebert
 */
public interface EventMessageBuilder {

    /**
     * Reference to the creator/publisher of the event.
     *
     * @param authorizerId unique identity of the individual authorizing the
     * event
     * @return this builder (fluid interface)
     */
    EventMessageBuilder authorizedBy(String authorizerId);

    /**
     * Optional event id which caused indicates an event which caused this event
     * to be created.
     *
     * @param eventSequence sequence of the event which triggered this event
     * @return this builder (fluid interface)
     */
    EventMessageBuilder triggeredBy(String eventSequence);

    /**
     * Optional - event which triggered this event.
     *
     * @param event triggering event
     * @return this builder (fluid interface)
     */
    EventMessageBuilder triggeredBy(AppendedEventMessage event);

    /**
     * Adds an optional event message header to the event.
     * <p>
     * Header names must not be null and may be any value. It's recommended that
     * applications namespace their header names to avoid collision with other
     * applications, however this is not mandatory. Header names starting with
     * {@code canon.} are reserved and may not be used.
     *
     * @param name header name
     * @param value header value
     * @return this builder (fluid interface)
     */
    EventMessageBuilder header(String name, String value);

    /**
     * Terminating method providing the event message payload.
     * <p>
     * This, or overloaded method, must be called last; any additional calls to
     * the EventMessageBuilder after the event message payload has been provided
     * will be ignored.
     *
     * @param event event content
     * @return appended event
     * @throws StreamAppendException if there was a problem appending to the
     * stream
     */
    AppendedEventMessage event(InputStream event) throws StreamAppendException;

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
     * @param event event content
     * @return appended event
     * @throws StreamAppendException if there was a problem appending to the
     * stream
     */
    AppendedEventMessage event(Object event)
            throws StreamAppendException;

    /**
     * Terminating method providing the event message payload and a specific
     * codec to use to convert the event to bytes.
     * <p>
     * This, or overloaded method, must be called last; any additional calls to
     * the EventMessageBuilder after the event message payload has been provided
     * will be ignored.
     *
     * @param <T> event type
     * @param event event
     * @param codec codec for event
     * @return appended event
     * @throws StreamAppendException if there was a problem appending to the
     * stream
     */
    <T> AppendedEventMessage event(T event, EventCodec<T> codec)
            throws StreamAppendException;
}
