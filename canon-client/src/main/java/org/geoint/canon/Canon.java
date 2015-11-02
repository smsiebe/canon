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
package org.geoint.canon;

import java.util.function.Predicate;
import org.geoint.canon.codec.EventCodec;

/**
 * A Canon instance 
 * 
 * @author steve_siebert
 */
public class Canon {
    
    
    /**
     * Return the "administrative" event stream for this event store.
     * <p>
     * The administrative stream contains only those events that are related to
     * the management of the channel or the events on the channel. This includes
     * management of streams, handlers, mediators, collisions, reconciliation,
     * etc.
     * <p>
     * The administrative stream cannot be directly written to by applications
     * but my be observed either directly from read/handlers or indirectly
     * through registration of {@link EventMediator mediators} or
     * {@link EventHandler handlers}.
     *
     * @return
     */
    EventStream getAdminStream();
    
    /**
     * Returns the named event stream.
     * <p>
     * If the event stream does not yet exist the returned stream will be a
     * proxy that will receive events if/when the stream is created.
     *
     * @param streamName
     * @return readable named event stream
     */
    EventStream getStream(String streamName);

    /**
     * Returns a typed named event stream.
     * <p>
     * If the event stream does not yet exist the returned stream will be a
     * proxy that will receive events if/when the stream is created.
     *
     * @param <T>
     * @param streamName
     * @param eventClass
     * @return readable typed event stream
     */
    <T> TypedEventStream<T> getTypedStream(String streamName,
            Class<T> eventClass);

    /**
     * Creates a new named stream using the provided predicate to determine if
     * new channel events should be included in the stream.
     *
     * @param streamName
     * @param filter used to determine which events should be included in the
     * stream
     * @return created stream
     * @throws StreamAlreadyExistsException thrown if a stream with this name is
     * already created on this channel
     */
    EventStream createStream(String streamName,
            Predicate<EventMessage> filter)
            throws StreamAlreadyExistsException;

    /**
     * Creates a new typed event stream, adding events which contain the
     * specific type of event to the stream.
     *
     * @param <T>
     * @param streamName
     * @param eventClass
     * @return created stream
     * @throws StreamAlreadyExistsException thrown if a stream with this name is
     * already created on this channel
     */
    <T> TypedEventStream<T> createTypedStream(String streamName,
            Class<T> eventClass)
            throws StreamAlreadyExistsException;

    /**
     * Creates a new typed event stream which uses both the event class as well
     * as the provided predicate to limit the events that are included in the
     * stream.
     *
     * @param <T>
     * @param streamName
     * @param eventClass event type that must be matched
     * @param filter event filter used after the event type is matched
     * @return created stream
     * @throws StreamAlreadyExistsException thrown if a stream with this name is
     * already created on this channel
     */
    <T> TypedEventStream<T> createTypedStream(String streamName,
            Class<T> eventClass, Predicate<TypedEventMessage<T>> filter)
            throws StreamAlreadyExistsException;

    /**
     * Returns the stream or creates a new one, as necessary.
     *
     * @param streamName
     * @param event
     * @return stream
     */
    EventStream findOrCreateStream(
            String streamName, Predicate<EventMessage> event);

    /**
     * Returns the stream or creates a new one, as necessary.
     *
     * @param <T>
     * @param streamName
     * @param eventClass
     * @return stream
     */
    <T> TypedEventStream<T> findOrCreateTypedStream(
            String streamName, Class<T> eventClass);

    /**
     * Returns the stream or creates a new one, as necessary.
     *
     * @param <T>
     * @param streamName
     * @param eventClass
     * @param filter
     * @return stream
     */
    <T> TypedEventStream<T> findOrCreateTypedStream(
            String streamName, Class<T> eventClass,
            Predicate<TypedEventMessage<T>> filter);

    /**
     * Set the default event codec for the specified event type/object type.
     *
     * @param eventType
     * @param codec
     */
    void useCodec(String eventType, EventCodec<?> codec);
    
    
}
