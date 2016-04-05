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
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A domain-defined occurrence.
 * <p>
 * EventMessage instances are wrappers around the an application domain-defined
 * event associated with a domain entity.
 *
 * @author Steve Siebert 
 */
public interface EventMessage {

    /**
     * Name of the event channel.
     *
     * @return channel name
     */
    String getChannelName();

    /**
     * Name of the stream where this event is published/committed.
     *
     * @return stream name
     */
    String getStreamName();

    /**
     * ID of the entity which authorized the event to be published.
     *
     * @return authorizer id
     */
    String getAuthorizerId();

    /**
     * Event sequence identifiers that "triggered" (caused) this event.
     *
     * @return triggering event ids
     */
    String[] getTriggerIds();

    /**
     * Event type as defined by the application/domain.
     *
     * @return event type
     */
    String getEventType();

    /**
     * Event headers.
     * <p>
     * It's recommended that header names at a minimum be namespaced and
     * suggested that domain managers consider maintaining a registry for header
     * names.
     * <p>
     * The header namespace "org.geoint.canon.*" is reserved for use by Canon,
     * but does not guarantee any headers will be set by Canon.
     * <p>
     * The returned event header map may be immutable and may throw exceptions
     * permitted by the Map interface if mutations are attempted. Any changes
     * that are made to the returned map will not change the event headers.
     *
     * @return event headers
     */
    Map<String, String> getHeaders();

    /**
     * Return the requested event header, if set.
     *
     * @param headerName header name
     * @return optional header
     */
    Optional<String> findHeader(String headerName);

    /**
     * Return the requested event header or the default value.
     *
     * @param headerName header name
     * @param defaultValue default value returned if the header is not set
     * @return header value
     */
    String getHeader(String headerName, Supplier<String> defaultValue);

//    /**
//     * Return the event content.
//     * 
//     * @return event payload
//     */
//    EventPayload getPayload();
    
    /**
     * Domain event content byte stream.
     *
     * @return event content stream
     */
    InputStream getEventContent();
//
//    /**
//     * Return the domain event type wrapped by this message using the provided
//     * codec to decode the event content.
//     *
//     * @param <E>
//     * @param codec used to decode the event content
//     * @return domain event
//     * @throws IOException
//     * @throws EventCodecException
//     */
//    <E> E getEvent(EventCodec<E> codec) throws IOException, EventCodecException;
}
