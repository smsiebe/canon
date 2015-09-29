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

import org.geoint.canon.EventChannel;
import java.io.Closeable;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.stream.handler.EventHandler;
import org.geoint.canon.EventMessage;
import org.geoint.canon.stream.reader.EventReader;

/**
 * A readable view of sequential events on an {@link EventChannel}.
 * <p>
 * An event stream may provide access to all events on the channel, or just a
 * subset.
 *
 * @author steve_siebert
 * @param <E> event message type
 */
public interface EventStream<E extends EventMessage> extends Closeable, AutoCloseable {

    /**
     * Channel-unique name of the stream.
     * <p>
     * Stream names starting with "canon.*" are reserved.
     * 
     * @return stream name
     */
    String getName();

    /**
     * Returns a new event reader for the stream.
     *
     * @return new event reader
     */
    EventReader<E> getReader();

    /**
     * The underlying channel of this stream.
     *
     * @return channel
     */
    EventChannel getChannel();

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the first event in the stream.
     *
     * @param handler
     */
    void addHandler(EventHandler<E> handler);

    /**
     * Registers an event handler which will be called for each event on the
     * stream, starting at the specified event.
     *
     * @param handler
     * @param eventId
     */
    void addHandlerAtEventId(EventHandler<E> handler, String eventId);

    /**
     * Removes the handler from the stream.
     *
     * @param handler
     */
    void removeHandler(EventHandler<E> handler);

    /**
     * Returns the last event id associated with the stream.
     *
     * @return the last event id associated with the stream
     */
    String getLastEventId();

    /**
     * Sets the event codec to use for events on this stream.
     * <p>
     * Setting an event codec on a stream will take precedence over any matching
     * codec set on the channel. This may result in streams on the same channel
     * using different codecs to read/write events. While this functionality is
     * extremely powerful, it should be
     *
     * @param eventType
     * @param codec
     */
    void useCodec(String eventType, EventCodec<?> codec);

}
