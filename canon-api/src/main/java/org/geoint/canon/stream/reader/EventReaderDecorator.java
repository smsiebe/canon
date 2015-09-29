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

import java.io.IOException;
import org.geoint.canon.EventMessage;
import org.geoint.canon.UnknownEventException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamReadException;

/**
 * Decorates an EventReader.
 *
 * @author steve_siebert
 * @param <E>
 */
public abstract class EventReaderDecorator<E extends EventMessage>
        implements EventReader<E> {

    protected final EventReader<E> reader;
    protected String currentEventId;

    public EventReaderDecorator(EventReader<E> reader)
            throws StreamReadException {
        this.reader = reader;
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @return
     */
    @Override
    public boolean hasNext() {
        return reader.hasNext();
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @return
     * @throws StreamReadException
     */
    @Override
    public E next() throws StreamReadException {
        return reader.next();
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @param eventId
     * @throws UnknownEventException
     */
    @Override
    public void setPositionByEventId(String eventId) throws UnknownEventException {
        this.currentEventId = eventId;
        reader.setPositionByEventId(eventId);
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @return
     */
    @Override
    public EventStream getStream() {
        return reader.getStream();
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }

}
