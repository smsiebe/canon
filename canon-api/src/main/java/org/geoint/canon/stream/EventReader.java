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
import java.util.concurrent.TimeoutException;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.event.AppendedEventMessage;

/**
 * Sequential event reader.
 *
 * @author steve_siebert
 */
public interface EventReader extends Closeable, AutoCloseable {

    /**
     * 
     * @return name of the channel
     */
    String getChannelName();
    
    /**
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
     * Returns the next event in the stream or null if there are no events after
     * the readers current position.
     *
     * @return next event or null
     * @throws StreamReadException thrown if there is a problem reading events
     * from the event stream
     */
    Optional<AppendedEventMessage> poll() throws StreamReadException;

    /**
     * Returns the next event in the stream, waiting up to the specified time
     * for an event.
     *
     * @param timeout
     * @param unit
     * @return next event or null if timed out
     * @throws StreamReadException thrown if there is a problem reading events
     * @throws InterruptedException thrown if the blocked thread was interrupted
     */
    Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit)
            throws StreamReadException, InterruptedException;

    /**
     * Returns the next event in the stream, waiting if necessary.
     *
     * @return next event in the stream
     * @throws StreamReadException thrown if there is a problem reading events
     * @throws InterruptedException
     */
    AppendedEventMessage take()
            throws StreamReadException, InterruptedException;

    /**
     * Set the readers position.
     *
     * @param sequence event sequence
     * @throws UnknownEventException thrown if the provided event id is not
     * known to the underlying channel
     */
    void setPosition(String sequence) throws UnknownEventException;

    /**
     * Returns the current position of the reader.
     *
     * @return current position of the reader
     */
    String getPosition();

}
