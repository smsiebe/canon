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
import org.geoint.canon.EventChannel;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.handler.EventHandler;
import org.geoint.canon.stream.reader.EventReader;

/**
 * EventStore-backed indexed event stream.
 * 
 * @author steve_siebert
 */
public class IndexedEventStream implements EventStream {
    
    private final AbstractEventChannel channel;
    private final String streamName;
    private final EventStore events;
    private final StreamIndex index;
    private final 

    public IndexedEventStream(AbstractEventChannel channel, String streamName, 
            EventStore events, StreamIndex index) {
        this.channel = channel;
        this.streamName = streamName;
        this.events = events;
        this.index = index;
    }
    
    StreamIndex getIndex() {
        return index;
    }
    
    EventStore getEventStore() {
        return events;
    }

    @Override
    public String getName() {
        return streamName;
    }

    @Override
    public EventReader getReader() {
        return new IndexedEventStoreReader(this);
    }

    @Override
    public EventChannel getChannel() {
        return channel;
    }

    @Override
    public void addHandler(EventHandler handler) {

    }

    @Override
    public void addHandlerAtEventId(EventHandler handler, String eventId) {

    }

    @Override
    public void removeHandler(EventHandler handler) {

    }

    @Override
    public String getLastEventId() {
        return index.getLastEventId();
    }

    @Override
    public void useCodec(String eventType, EventCodec codec) {

    }

    @Override
    public void close() throws IOException {
        index.close();
    }
    
    
}
