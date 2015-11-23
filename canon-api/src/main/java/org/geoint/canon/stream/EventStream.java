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
package org.geoint.canon.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.util.function.Predicate;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.CommittedEventMessage;

/**
 * A readable view of sequential events on an {@link EventChannel}.
 * <p>
 * An event stream may provide access to all events on the channel, or just a
 * subset.
 *
 * @author steve_siebert
 * @param <E> event message type
 */
public interface EventStream<E extends EventMessage>
        extends CodecResolver, Closeable, Flushable, AutoCloseable {

    /**
     * Channel-unique name of the stream.
     * <p>
     * Stream names starting with "canon.*" are reserved.
     *
     * @return stream name
     */
    String getName();

    /**
     * Returns the last event id associated with the stream.
     *
     * @return the last event id associated with the stream
     */
    String getLastEventId();

    /**
     * Returns a new event reader for the stream.
     *
     * @return new event reader
     */
    EventReader getReader();

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the first event in the stream.
     *
     * @param handler
     */
    void addHandler(EventHandler handler);

    /**
     * Registers an event handler which will be called for each event on the
     * stream that passes the provided filters test.
     *
     * @param handler
     * @param filter
     */
    void addHandler(EventHandler handler, Predicate<CommittedEventMessage> filter);

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the specified event.
     *
     * @param handler
     * @param lastEventId
     */
    void addHandler(EventHandler handler, String lastEventId);

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the specified event.
     *
     * @param handler
     * @param filter
     * @param lastEventId
     */
    void addHandler(EventHandler handler, Predicate<CommittedEventMessage> filter,
            String lastEventId);

    /**
     * Removes the handler from the stream.
     *
     * @param handler
     */
    void removeHandler(EventHandler handler);

    /**
     * Create a new transactional EventAppender which will append all events
     * added to the tail of the stream.
     *
     * @return transactional event appender
     */
    EventAppender appendToEnd();

    /**
     * Create a new transaction EventAppender which will attempt to append
     * events after the specified event.
     * <p>
     * If the stream has advanced past the specified event id on commit the
     * transaction will fail.
     *
     * @param previousEventId
     * @return transactional event appender
     * @throws AppendOutOfSequenceException thrown if the previous event id is
     * not current event id
     */
    EventAppender appendAfter(String previousEventId)
            throws AppendOutOfSequenceException;

    /**
     * Assigns a specific to use to read/write event payloads on this stream.
     * <p>
     * Setting an event codec on a stream will take precedence over any matching
     * codec set at at a higher level.
     *
     * @param codec
     */
    void useCodec(EventCodec<?> codec);

    /**
     * Determine if this stream implementation is offline capable.
     *
     * @return true if offline capable, otherwise false
     */
    boolean isOfflineable();

    /**
     *
     *
     * @param offline if true makes offline capable, if false remove offline
     * capability if not intrinsic
     */
    void setOffline(boolean offline);

}
