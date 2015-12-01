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
import java.util.function.Predicate;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventSequence;

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
        extends CodecResolver, Closeable {

    /**
     * Channel-unique name of the stream.
     * <p>
     * Stream names starting with "canon.*" are reserved.
     *
     * @return stream name
     */
    String getName();

    /**
     * The name of the channel of this stream.
     *
     * @return channel name
     */
    String getChannelName();

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
    void addHandler(EventHandler handler, Predicate<AppendedEventMessage> filter);

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the specified event.
     *
     * @param handler
     * @param sequence
     */
    void addHandler(EventHandler handler, EventSequence sequence);

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the specified event.
     *
     * @param handler
     * @param filter
     * @param sequence
     */
    void addHandler(EventHandler handler, Predicate<AppendedEventMessage> filter,
            EventSequence sequence);

    /**
     * Removes the handler from the stream.
     *
     * @param handler
     */
    void removeHandler(EventHandler handler);

    /**
     * Create a new EventAppender to add new events to the stream.
     *
     * @return event appender
     */
    EventAppender getAppender();

    /**
     * Assigns a specific to use to read/write event payloads on this stream.
     * <p>
     * Setting an event codec on a stream will take precedence over any matching
     * codec set at at a higher level.
     *
     * @param codec
     */
    void useCodec(EventCodec<?> codec);

}
