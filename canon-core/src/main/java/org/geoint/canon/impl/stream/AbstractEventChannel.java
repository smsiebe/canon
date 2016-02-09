/*
 * Copyright 2016 geoint.org.
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
package org.geoint.canon.impl.stream;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.spi.id.EventIdGenerator;
import org.geoint.canon.stream.ChannelInitializationException;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventChannel;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.StreamInitializationException;
import org.geoint.canon.stream.event.StreamCreated;

/**
 * This abstraction handles a lot of the common channel behaviors, leaving
 * subclasses to handle implementation specific concerns.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventChannel implements EventChannel {

    /**
     * name of channel admin stream created in every channel
     */
    public static final String CHANNEL_ADMIN_STREAM_NAME
            = "org.geoint.canon.channelAdmin";

    protected final String name;
    protected final Map<String, String> channelProperties;
    protected final HierarchicalCodecResolver codecs;
    private final EventIdGenerator idGenerator;

    public AbstractEventChannel(String channelName,
            Map<String, String> channelProperties,
            CodecResolver channelCodecs)
            throws ChannelInitializationException {
        this.name = channelName;
        this.channelProperties = Collections.unmodifiableMap(channelProperties); // defensive copy
        this.codecs = new HierarchicalCodecResolver(channelCodecs);

        //set event id generator, loading custom class if class name is set
        //as channel property
        if (channelProperties.containsKey(EventIdGenerator.CHANNEL_PROPERTY_NAME)) {
            try {
                this.idGenerator = (EventIdGenerator) Class.forName(
                        channelProperties.get(EventIdGenerator.CHANNEL_PROPERTY_NAME))
                        .newInstance();
            } catch (ClassNotFoundException | IllegalAccessException |
                    InstantiationException | ClassCastException ex) {
                throw new ChannelInitializationException(channelName, String.format(
                        "Unable to initialize channel '%s', event id generator "
                        + "class '%s' could not be loaded.",
                        channelName, channelProperties.get(EventIdGenerator.CHANNEL_PROPERTY_NAME),
                        ex));
            }
        } else {
            //used default id generator
            this.idGenerator = new UuidIdGenerator();
        }

    }

    @Override
    public String getChannelName() {
        return name;
    }

    @Override
    public EventStream getChannelAdminStream() {
        return findStream(CHANNEL_ADMIN_STREAM_NAME).get();
    }

    @Override
    public void useCodec(EventCodec codec) {
        codecs.add(codec);
    }

    @Override
    public Optional<EventStream> findStream(String streamName) {
        return listStreams().stream()
                .filter((s) -> s.getName().equalsIgnoreCase(streamName))
                .findFirst();
    }

    @Override
    public EventStream getOrCreateStream(String streamName)
            throws StreamInitializationException {
        try {
            return findStream(streamName)
                    .orElse(createAndInitStream(streamName));
        } catch (StreamAlreadyExistsException ex) {
            //will get here if the stream is created between the call to 
            //findStream and createStream...try finding the stream again
            return findStream(streamName).get();
        } catch (StreamAppendException ex) {
            throw new StreamInitializationException(streamName,
                    "Could not write to channel admin stream.", ex);
        }
    }

    public String generateEventId(EventMessage msg) {
        return idGenerator.generate(msg, channelProperties);
    }

    /**
     * Create a new event stream.
     *
     * @param streamName stream name
     * @param codecs codecs to use with the stream
     * @return requested stream
     * @throws StreamAlreadyExistsException if stream could not be created on
     * channel because it already exists
     */
    protected abstract EventStream createStream(String streamName,
            CodecResolver codecs)
            throws StreamAlreadyExistsException;

    /**
     * Creates the requested stream and published creation event to the channel
     * admin stream.
     *
     * @param streamName name of the stream to create
     * @return new stream
     * @throws StreamAlreadyExistsException thrown if a stream by this name
     * already exists in the channel
     * @throws StreamAppendException thrown if the stream creation event could
     * not be appended to the channel admin stream
     */
    private EventStream createAndInitStream(String streamName)
            throws StreamAlreadyExistsException, StreamAppendException {
        EventStream s = createStream(streamName, codecs);

        //add the stream creation event to the channels own admin stream
        EventAppender appender = getChannelAdminStream().newAppender();
        appender.create(StreamCreated.class.getName())
                .event(new StreamCreated(name, streamName));
        appender.append();

        return s;
    }
    
}
