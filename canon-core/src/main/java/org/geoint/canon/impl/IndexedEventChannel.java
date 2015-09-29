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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.geoint.canon.EventChannel;
import org.geoint.canon.EventMessage;
import org.geoint.canon.TypedEventMessage;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.publish.EventPublisher;
import org.geoint.canon.stream.ChannelStream;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;
import org.geoint.canon.stream.TypedEventStream;
import org.geoint.canon.stream.reconciliation.EventArbitor;

/**
 * Implementation interface - not for public use.
 * <p>
 * Provides a common EventChannel implementation used for both local and remote
 * channels by encapsulating IO handling in private/implementation API.
 *
 * @author steve_siebert
 */
public abstract class IndexedEventChannel implements EventChannel {

    private final String channelName;
    private final EventStore events;
    private final IndexedStreamManager streams;
    private final Map<String, IndexedEventStream> indexedStreams = new HashMap<>();
    private final EventArbitor arbitor;

    private static final String DEFAULT_STREAM_NAME = "canon.stream.defaultStream";
    private static final String CHANNEL_STREAM_NAME = "canon.stream.adminStream";

    public IndexedEventChannel(String channelName, EventStore events,
            IndexedStreamManager streams) {
        this(channelName, events, streams, new DefaultEventArbitor());
    }

    public IndexedEventChannel(String channelName, EventStore events,
            IndexedStreamManager streams, EventArbitor arbitor) {
        this.channelName = channelName;
        this.events = events;
        this.streams = streams;
        this.arbitor = arbitor;
    }

    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public Collection<EventStream> getStreams() {
        return Collections.unmodifiableCollection(streams.getAll());
    }

    @Override
    public EventStream getDefaultStream() {
        return streams.getStream(DEFAULT_STREAM_NAME);
    }

    @Override
    public ChannelStream getChannelStream() {
        return new IndexedChannelStream(getIndexedStream(CHANNEL_STREAM_NAME));
    }

    @Override
    public EventStream getStream(String streamName) {
        return streams.getStream(streamName);
    }

    @Override
    public <T> TypedEventStream<T> getTypedStream(String streamName,
            Class<T> eventClass) {

    }

    @Override
    public EventStream createStream(String streamName,
            Predicate<EventMessage> filter) throws StreamAlreadyExistsException {

    }

    @Override
    public <T> TypedEventStream<T> createTypedStream(String streamName,
            Class<T> eventClass) throws StreamAlreadyExistsException {

    }

    @Override
    public <T> TypedEventStream<T> createTypedStream(String streamName,
            Class<T> eventClass, Predicate<TypedEventMessage<T>> filter)
            throws StreamAlreadyExistsException {

    }

    @Override
    public EventStream findOrCreateStream(String streamName,
            Predicate<EventMessage> event) {

    }

    @Override
    public <T> TypedEventStream<T> findOrCreateTypedStream(String streamName,
            Class<T> eventClass) {

    }

    @Override
    public <T> TypedEventStream<T> findOrCreateTypedStream(String streamName,
            Class<T> eventClass, Predicate<TypedEventMessage<T>> filter) {

    }

    @Override
    public EventPublisher getPublisher() {
        return new EventStorePublisher(events,
                getIndexedStream(CHANNEL_STREAM_NAME).getIndex()
        );
    }

    @Override
    public EventArbitor getArbitor() {
        return arbitor;
    }

    @Override
    public void useCodec(String eventType, EventCodec<?> codec) {

    }

    @Override
    public void close() throws IOException {
        events.close();
    }

    @Override
    public void flush() throws IOException {
        events.flush();
    }

    IndexedEventStream getIndexedStream(String streamName) {
        indexedStreams.computeIfAbsent(streamName, this::createIndexedStream);
        return indexedStreams.get(streamName);
    }

    private IndexedEventStream createIndexedStream(String streamName) {
        return new IndexedEventStream(
                this, streamName, events, streams.findOrCreateIndex(streamName));
    }
}
