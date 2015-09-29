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
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.geoint.canon.EventChannel;
import org.geoint.canon.EventMessage;
import org.geoint.canon.TypedEventMessage;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.publish.EventPublisher;
import org.geoint.canon.stream.ChannelStream;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;
import org.geoint.canon.stream.TypedEventStream;
import org.geoint.canon.stream.reader.EventReader;
import org.geoint.canon.stream.reconciliation.EventArbitor;

/**
 * Implementation interface - not for public use.
 * <p>
 * Provides common event channel logic for both local and remote channels,
 * encaby creating a private/implementation API
 *
 * @author steve_siebert
 */
public abstract class AbstractEventChannel implements EventChannel {

    private final String channelName;
    private final EventStore events;
    private final StreamManager streams;
    private final Supplier<EventPublisher> publisherFactory;
    private final Supplier<EventReader> readerFactory;
    private final EventArbitor arbitor;

    public AbstractEventChannel(String channelName, EventStore events,
            StreamManager streams, Supplier<EventPublisher> publisherFactory,
            Supplier<EventReader> readerFactory, EventArbitor arbitor) {
        this.channelName = channelName;
        this.events = events;
        this.streams = streams;
        this.publisherFactory = publisherFactory;
        this.readerFactory = readerFactory;
        this.arbitor = arbitor;
    }

    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public Collection<EventStream> getStreams() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventStream getDefaultStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ChannelStream getChannelStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventStream getStream(String streamName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> TypedEventStream<T> getTypedStream(String streamName, Class<T> eventClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventStream createStream(String streamName, Predicate<EventMessage> filter) throws StreamAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> TypedEventStream<T> createTypedStream(String streamName, Class<T> eventClass) throws StreamAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> TypedEventStream<T> createTypedStream(String streamName, Class<T> eventClass, Predicate<TypedEventMessage<T>> filter) throws StreamAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventStream findOrCreateStream(String streamName, Predicate<EventMessage> event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> TypedEventStream<T> findOrCreateTypedStream(String streamName, Class<T> eventClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> TypedEventStream<T> findOrCreateTypedStream(String streamName, Class<T> eventClass, Predicate<TypedEventMessage<T>> filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventPublisher getPublisher() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventArbitor getArbitor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void useCodec(String eventType, EventCodec<?> codec) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
