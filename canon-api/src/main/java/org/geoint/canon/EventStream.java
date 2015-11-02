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

import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.tx.EventTransaction;
import org.geoint.canon.stream.handler.EventHandler;
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
public interface EventStream<E extends EventMessage> extends EventAppender {

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
     * Creates a new event transaction which may be used to append multiple
     * events to the stream in a single ACID compliant transaction.
     *
     * @return new append transaction
     */
    EventTransaction newTransaction();

    /**
     * Create and append a new event message to the tail of the event stream.
     *
     * @param eventType
     * @return builder used to create the event message
     * @throws StreamAppendException thrown if there was a problem appending
     * events to the stream
     */
    @Override
    EventMessageBuilder newMessage(String eventType)
            throws StreamAppendException;

    /**
     * Create and append a new event message after the specified event in the
     * stream.
     *
     * @param eventType
     * @param previousEventId
     * @return
     * @throws StreamAppendException thrown if there was a problem appending
     * events to the stream
     * @throws AppendOutOfSequenceException thrown if the transaction could not
     * be appended because the previous event id is not the last event id of the
     * stream
     */
    EventMessageBuilder newMessage(String eventType, String previousEventId)
            throws StreamAppendException, AppendOutOfSequenceException;

    /**
     * Returns the last event id associated with the stream.
     *
     * @return the last event id associated with the stream
     */
    String getLastEventId();

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
