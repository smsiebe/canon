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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.event.AppendedEventMessage;

/**
 * Sequentially reads events from an {@link EventStream stream}.
 * <p>
 * EventReader instances are <b>NOT</b> thread-safe.
 * @author steve_siebert
 */
public interface EventReader extends Closeable, AutoCloseable {

    /**
     * Name of the channel of which this stream belongs.
     *
     * @return name of the channel
     */
    String getChannelName();

    /**
     * Channel-unique name of this stream.
     *
     * @return name of the stream
     */
    String getStreamName();

    /**
     * Checks if there is a sequential event available after the readers current
     * position.
     *
     * @return true if there are more events, otherwise false
     */
    boolean hasNext();

    /**
     * Returns the current event at the reader position, not changing the
     * position of the reader.
     *
     * @return current event at the reader position
     * @throws StreamReadException thrown if there is a problem reading the
     * current event from the stream
     */
    public abstract AppendedEventMessage read() throws StreamReadException;

    /**
     * Returns the next event in the stream, if exists, or null if there are no
     * events after the readers current position.
     *
     * @return next event or null
     * @throws StreamReadException thrown if there is a problem reading events
     * from the event stream
     */
    Optional<AppendedEventMessage> poll() throws StreamReadException;

    /**
     * Returns the next event in the stream, if exists, or waiting up to the
     * specified time for an event returning null if no event exists at timeout.
     *
     * @param timeout how long to wait
     * @param unit time unit of timeout
     * @return next event or null if timed out
     * @throws StreamReadException thrown if there is a problem reading events
     * @throws InterruptedException thrown if the blocked thread was interrupted
     */
    Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit)
            throws StreamReadException, InterruptedException;

    /**
     * Returns the next event in the stream, waiting forever if necessary.
     *
     * @return next event in the stream
     * @throws StreamReadException thrown if there is a problem reading events
     * @throws InterruptedException if the waiting thread was interrupted
     */
    AppendedEventMessage take()
            throws StreamReadException, InterruptedException;

    /**
     * Set the readers position.
     *
     * @param sequence event sequence
     * @throws StreamReadException thrown if there was a problem setting the
     * stream position
     * @throws UnknownEventException thrown if the provided event id is not
     * known to the underlying channel
     */
    void setPosition(String sequence)
            throws StreamReadException, UnknownEventException;

    /**
     * Returns the current position of the reader.
     *
     * @return current position of the reader
     */
    String getPosition();

}
