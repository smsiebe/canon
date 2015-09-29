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
package org.geoint.canon.impl;

import java.io.IOException;
import org.geoint.canon.EventMessage;
import org.geoint.canon.UnknownEventException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamReadException;
import org.geoint.canon.stream.reader.EventReader;

/**
 * Reads events indexed by a StreamIndex and backed by an IndexedEventStream.
 *
 * @author steve_siebert
 * @param <E>
 */
public class IndexedEventStoreReader<E extends EventMessage>
        implements EventReader<E> {

    private final IndexedEventStream indexedStream;
    private String currentId;
    private String nextId;

    public IndexedEventStoreReader(IndexedEventStream indexedStream) {
        this.indexedStream = indexedStream;
        currentId = indexedStream.getIndex().getFirstId();
    }

    @Override
    public boolean hasNext() {
        if (nextId == null) {
            //cache next event id
            nextId = indexedStream.getIndex().getNextId(currentId);
        }
        return nextId != null;
    }

    @Override
    public E next() throws StreamReadException {
        if (nextId == null) {
            if (!hasNext()) {
                throw new StreamReadException(indexedStream.getChannel().getName(),
                        indexedStream.getName(), "No next event in stream.");
            }
        }
        try {
            return (E) indexedStream.getEventStore().getEventById(currentId);
        } finally {
            setEventIdPosition(nextId);
        }
    }

    @Override
    public void setPositionByEventId(String eventId) throws UnknownEventException {
        if (!indexedStream.getIndex().eventExists(eventId)) {
            throw new UnknownEventException(eventId);
        }
        setEventIdPosition(eventId);
    }

    @Override
    public EventStream getStream() {
        return indexedStream;
    }

    @Override
    public void close() throws IOException {
        indexedStream.close();
    }

    private void setEventIdPosition(String eventId) {
        currentId = nextId;
        nextId = null;
    }
}
