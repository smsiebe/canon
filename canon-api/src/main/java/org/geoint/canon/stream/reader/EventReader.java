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
package org.geoint.canon.stream.reader;

import java.io.Closeable;
import org.geoint.canon.EventMessage;
import org.geoint.canon.UnknownEventException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamReadException;

/**
 * Sequential event reader.
 *
 * @author steve_siebert
 * @param <E>
 */
public interface EventReader<E extends EventMessage>
        extends Closeable, AutoCloseable {

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
    E next() throws StreamReadException;

    /**
     * Set the readers in the event sequence position by event Id.
     *
     * @param eventId event id
     * @throws UnknownEventException thrown if the provided event id is not
     * known to the underlying channel
     */
    void setPositionByEventId(String eventId) throws UnknownEventException;
    
    /**
     * Return the source stream.
     * 
     * @return source event stream
     */
    EventStream getStream();
}
