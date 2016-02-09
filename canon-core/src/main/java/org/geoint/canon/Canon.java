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
package org.geoint.canon;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.codec.ObjectStreamEventCodec;
import org.geoint.canon.impl.stream.EventChannels;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.stream.ChannelInitializationException;
import org.geoint.canon.stream.EventChannel;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.StreamInitializationException;
import org.geoint.canon.stream.event.ChannelCreated;
import org.geoint.canon.stream.event.StreamCreated;

/**
 * A Canon event database instance.
 *
 * @author steve_siebert
 */
public final class Canon {

    /**
     * name of global canon channel
     */
    public static final String DEFAULT_CANON_CHANNEL_NAME = "org.geoint.canon";
    /**
     * name of global admin stream name
     */
    public static final String CANON_ADMIN_STREAM = "canon.admin";

    private final HierarchicalCodecResolver codecs;
    private final Map<String, EventChannel> channels; //key is channel name
    private final EventStream adminStream;

    private final EventHandler channelAdminCopyHandler;

    private static final String DEFAULT_ADMIN_CHANNEL_URI
            = String.format("mem://%s", DEFAULT_CANON_CHANNEL_NAME);
    private static final Logger LOGGER = Logger.getLogger(Canon.class.getName());

    private Canon(EventChannel canonAdminChannel, EventStream adminStream) {
        LOGGER.log(Level.FINE, () -> String.format("Creating canon instance "
                + "using admin channel %s", canonAdminChannel.toString()));

        this.codecs = defaultCodecs();
        this.channels = new HashMap<>();
        this.channels.put(canonAdminChannel.getChannelName(), canonAdminChannel);
        this.channels.put(DEFAULT_CANON_CHANNEL_NAME, canonAdminChannel);
        this.adminStream = adminStream;

        //every channel admin stream managed by this canon instance will 
        //have certain events copied into the global admin stream
        this.channelAdminCopyHandler = (msg) -> {
            final String type = msg.getEventType();

            if (type.contentEquals(StreamCreated.class.getName())
                    || type.contentEquals(ChannelCreated.class.getName())) {
                adminStream.newAppender().add(msg).append();
            }
        };
        //even the canon global admin channel is joined to the global admin stream
        canonAdminChannel.getChannelAdminStream()
                .addHandler(channelAdminCopyHandler);
    }

    /**
     * Creates a new Canon instance using the default in-memory channel provider
     * as the admin provider.
     *
     * @return temporary (in-memory) canon instance
     * @throws CanonInitializationException if there was a problem initializing
     * the canon instance
     */
    public static Canon newTempInstance() throws CanonInitializationException {
        return newInstance(DEFAULT_ADMIN_CHANNEL_URI);
    }

    /**
     * Creates a new Canon instance using the provided admin URI.
     *
     * @param adminChannelUri canon admin channel URI
     * @return canon instance
     * @throws CanonInitializationException if there was a problem initializing
     * the canon instance
     */
    public static Canon newInstance(String adminChannelUri)
            throws CanonInitializationException {
        URI uri = URI.create(adminChannelUri);

        final String channelScheme = getChannelScheme(uri);
        final String channelName = getChannelName(uri);

        EventChannel adminChannel;
        try {
            adminChannel = resolveChannel(
                    channelScheme, channelName,
                    getUriProperties(uri),
                    defaultCodecs());
            return newInstance(adminChannel);
        } catch (UnableToResolveChannelException | ChannelInitializationException ex) {
            throw new CanonInitializationException("Problem initializing canon "
                    + "administration channel", ex);
        }

    }

    /**
     * Create a new Canon instance using the provided Canon administration
     * channel.
     *
     * @param adminChannel canon admin channel
     * @return canon instance
     * @throws CanonInitializationException if there was a problem initializing
     * the canon instance
     *
     */
    public static Canon newInstance(EventChannel adminChannel)
            throws CanonInitializationException {

        if (adminChannel == null) {
            throw new CanonInitializationException("Canon administration "
                    + "channel must not be null");
        }

        try {
            EventStream globalAdminStream = adminChannel.getOrCreateStream(CANON_ADMIN_STREAM);

            return new Canon(adminChannel, globalAdminStream);
        } catch (StreamInitializationException ex) {
            throw new CanonInitializationException(String.format("Unable to "
                    + "initialize canon admin channnel '%s'm global admin stream "
                    + "cannot be initialized: %s",
                    adminChannel.getChannelName(), ex.getMessage()), ex);
        }
    }

    /**
     * Return the "administrative" event stream for this instance.
     * <p>
     * The administrative stream contains only those events that are related to
     * the management of the channel or the events on the channel. This includes
     * management of streams, handlers, mediators, collisions, reconciliation,
     * etc.
     * <p>
     * The administrative stream cannot be directly written to by applications
     * but my be observed either directly from read/handlers or indirectly
     * through registration of {@link EventMediator mediators} or
     * {@link EventHandler handlers}.
     *
     * @return admin stream
     */
    public EventStream getAdminStream() {
        return adminStream;
    }

    /**
     * Return the canon administrative channel for this canon instance.
     *
     * @return canon administrative stream
     */
    public EventChannel getAdminChannel() {
        return channels.get(DEFAULT_CANON_CHANNEL_NAME);
    }

    /**
     * Return all the channels currently registered to this canon instance.
     *
     * @return channel registered to this canon instance; changes to the
     * returned collection will not change any Canon state
     */
    public Collection<EventChannel> getChannels() {
        return Collections.unmodifiableCollection(channels.values());
    }

    /**
     * Returns the named event channel if registered.
     *
     * @param channelName name of the channel to return, if already registered
     * @return event channel, if registered
     */
    public Optional<EventChannel> findChannel(String channelName) {
        return Optional.ofNullable(channels.get(channelName));
    }

    /**
     * Retrieve an event channel, loading/registering the channel if necessary.
     *
     * @param scheme channel provider scheme
     * @param channelName name of channel
     * @param channelProperties channel properties
     * @return registered event channel
     * @throws UnableToResolveChannelException thrown if no provider could load
     * the channel
     * @throws ChannelInitializationException if the channel could not be
     * initialized
     */
    public EventChannel getOrCreateChannel(String scheme, String channelName,
            Map<String, String> channelProperties)
            throws UnableToResolveChannelException, ChannelInitializationException {

        synchronized (channels) {
            if (channels.containsKey(channelName)) {
                return channels.get(channelName);
            }

            //channel doesn't exists
            EventChannel c = resolveChannel(scheme, channelName,
                    channelProperties, codecs);
            EventStream ca = c.getChannelAdminStream();
            ca.addHandler(channelAdminCopyHandler);

            channels.put(channelName, c);
            return c;
        }
    }

    /**
     * Retrieve or create a new event channel by channel url.
     * <p>
     * Canon will attempt to load the channel by checking if any
     * {@link EventChannelProvider provider} can resolve the channel.
     *
     * @param channelUrl, format is defined by the provider
     * @return resolved event channel
     * @throws UnableToResolveChannelException if no channel provider could be
     * found or the provider could not load the requested channel
     * @throws ChannelInitializationException if the channel could not be
     * initialized
     */
    public EventChannel getOrCreateChannel(String channelUrl)
            throws UnableToResolveChannelException,
            ChannelInitializationException {
        URI uri = URI.create(channelUrl);

        final String channelName = getChannelName(uri);
        final String channelScheme = getChannelScheme(uri);
        final Map<String, String> channelProperties = getUriProperties(uri);

        return getOrCreateChannel(channelScheme, channelName, channelProperties);
    }

    public EventStream getOrCreateStream(String streamUri)
            throws UnableToResolveChannelException, ChannelInitializationException,
            StreamAppendException, StreamInitializationException {
        URI uri = URI.create(streamUri);

        final String channelScheme = getChannelScheme(uri);
        final String channelName = getChannelName(uri);
        final String streamName = getStreamName(uri);
        final Map<String, String> properties = getUriProperties(uri);
        return getOrCreateStream(channelScheme, channelName,
                properties, streamName);
    }

    public EventStream getOrCreateStream(String scheme, String channelName,
            Map<String, String> channelProperties, String streamName)
            throws UnableToResolveChannelException, ChannelInitializationException,
            StreamAppendException, StreamInitializationException {
        EventChannel channel = getOrCreateChannel(scheme, channelName, channelProperties);

        Optional<EventStream> existing = channel.findStream(streamName);
        if (existing.isPresent()) {
            return existing.get();
        }

        EventStream s = channel.getOrCreateStream(streamName); //create stream
        StreamCreated created
                = new StreamCreated(channel.getChannelName(),
                        streamName);
        adminStream.newAppender().create(StreamCreated.class.getName())
                .event(created);
        return s;

    }

    /**
     * Set the default event codec for the specified event type/object type.
     *
     * @param codec
     */
    public void useCodec(EventCodec<?> codec) {
        codecs.add(codec);
    }

    public void shutdown() {
        synchronized (channels) {
            channels.entrySet().stream()
                    .map(Entry::getValue)
                    .forEach((c) -> {
                        try {
                            c.close();
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, String.format("Channel "
                                    + "'%s' did not close gracefully.",
                                    c.getChannelName()), ex);
                        }
                    });
        }
    }

    /**
     * Resolves a channel from the channel providers or throws an if the channel
     * could not be resolved.
     *
     * @param providerManager provider manager
     * @param channelScheme channel scheme
     * @param channelName channel name
     * @param channelProperties channel properties
     * @return channel
     * @throws UnableToResolveChannelException thrown if the channel could not
     * be resolved using the available channel providers
     */
    private static EventChannel resolveChannel(String channelScheme,
            String channelName,
            Map<String, String> channelProperties,
            CodecResolver codecs)
            throws UnableToResolveChannelException,
            ChannelInitializationException {

        return EventChannels.INSTANCE.findProvider(channelScheme, channelProperties)
                .orElseThrow(() -> new UnableToResolveChannelException(
                        String.format("No channel provider found for channel "
                                + "'%s' with channel scheme '%s'",
                                channelName, channelScheme)))
                .getChannel(channelName, channelProperties, codecs);

    }

    private static String getChannelScheme(URI uri) {
        return uri.getScheme();
    }

    private static String getStreamName(URI uri) {
        return uri.getPath();
    }

    private static String getChannelName(URI uri) {
        return uri.getHost();
    }

    /**
     * Convert a URI query string to a map of stream properties.
     *
     * @param uri d
     * @return stream properties map
     */
    private static Map<String, String> getUriProperties(URI uri) {
        final String uriQuery = uri.getQuery();
        final Map<String, String> streamProperties;
        if (uriQuery == null || uriQuery.isEmpty()) {
            streamProperties = Collections.EMPTY_MAP;
        } else {
            String[] propKV = uriQuery.split("&");
            streamProperties = new HashMap<>(propKV.length);
            for (String kv : propKV) {

                final String key;
                final String value;

                String[] pair = kv.split("=");
                key = pair[0];
                value = (pair.length == 2)
                        ? pair[1]
                        : null;//no value for this property, assign null

                streamProperties.put(key, value);
            }
        }
        return streamProperties;
    }

    private static HierarchicalCodecResolver defaultCodecs() {
        HierarchicalCodecResolver codecs = new HierarchicalCodecResolver();
        codecs.add(new ObjectStreamEventCodec()); //use java serialization 
        return codecs;
    }

}
