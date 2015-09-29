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
    }
    
    

    @Override
    public boolean hasNext() {

    }

    @Override
    public E next() throws StreamReadException {

    }

    @Override
    public void setPositionByEventId(String eventId) throws UnknownEventException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventStream getStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
