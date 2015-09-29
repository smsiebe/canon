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

import org.geoint.canon.publish.EventPublisher;
import java.io.Closeable;
import java.io.Flushable;
import java.util.Collection;
import java.util.function.Predicate;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.stream.ChannelStream;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;
import org.geoint.canon.stream.TypedEventStream;
import org.geoint.canon.stream.handler.EventHandler;
import org.geoint.canon.stream.reconciliation.EventArbitor;
import org.geoint.canon.stream.reconciliation.EventMediator;

/**
 * A named event sequence.
 * <p>
 * An event channel is a single sequential stream of events that supports
 * random-access and sequential/batch reads, appending of additional events
 * through ACID-compliant transactions, and asynchronous transaction rollbacks.
 * <p>
 * <h2>Event Streams</h2>
 * All event read/append operations must be done from the perspective of an
 * {@link EventStream}. All events on the channel can be read from the
 * {@link #getDefaultStream() default channel stream}, though typically
 * application code will access events through named event streams.
 * <p>
 * While event streams may provide a filtered view of events on the channel,
 * events are still read sequentially (skipping events that do not pass the
 * stream filters).
 * <p>
 * The decision on how and when to use an EventChannel or an EventStream to
 * limit/filter an sequence of events is application, and potentially
 * environmentally, specific. In comparison, streams are significantly less
 * expensive (in terms of server and network resources) and allow for ACID
 * complaint transactions across streams, however all events are stored on the
 * same channel, potentially creating a bottleneck. Channels are comparatively
 * more expensive, requiring additional server and network resources, though is
 * easier to scale.
 *
 *
 * @author steve_siebert
 */
public interface EventChannel extends Closeable, AutoCloseable, Flushable {

    /**
     * Channel name.
     *
     * @return event channel name
     */
    String getName();

    /**
     * Returns an immutable, backed, collection of streams associated with this
     * channel.
     *
     * @return immutable backed collection of streams associated with the
     * channel
     */
    Collection<EventStream> getStreams();

    /**
     * Return the default, unfiltered, channel event stream.
     *
     * @return unfiltered event stream
     */
    EventStream getDefaultStream();

    /**
     * Return the "administrative" event stream for the channel.
     * <p>
     * The ChannelStream contains only those events that are related to the
     * management of the channel or the events on the channel. This includes
     * management of streams, handlers, mediators, collisions, reconciliation,
     * etc.
     * <p>
     * The channel stream cannot be directly written to by applications but my
     * be observed either directly from read/handlers or indirectly through
     * registration of {@link EventMediator mediators} or {@link EventHandler
     * handlers}.
     *
     * @return
     */
    ChannelStream getChannelStream();

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
     * Publisher used to append new events to this channel.
     *
     * @return event publisher
     */
    EventPublisher getPublisher();

    /**
     * Arbitor used to manage and reconcile event collision.
     *
     * @return event arbitor
     */
    EventArbitor getArbitor();

    /**
     * Set the default event codec for the specified event type/object type.
     *
     * @param eventType
     * @param codec
     */
    void useCodec(String eventType, EventCodec<?> codec);

}
