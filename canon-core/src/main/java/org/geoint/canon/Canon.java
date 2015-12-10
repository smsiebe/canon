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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.stream.ChannelProviderManager;
import org.geoint.canon.impl.stream.memory.MemoryChannelProvider;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.stream.EventChannel;

/**
 * A Canon event database instance.
 *
 * @author steve_siebert
 */
public class Canon {

    private static final ChannelProviderManager channelProviders
            = ChannelProviderManager.getDefaultInstance();

    private final HierarchicalCodecResolver codecs;
    private final Map<String, EventChannel> channels = new HashMap<>();
    private final EventChannelProvider adminProvider;
    private static Map<String, String> DEFAULT_STREAM_PROPERTIES;

    private static final String INSTANCE_ADMIN_STREAM_NAME = "canon.admin";
    private static final String CANON_BASE_DIR_PROPERTY = "canonBaseDir";

    private static final Logger LOGGER = Logger.getLogger(Canon.class.getName());

    private Canon(EventChannelProvider adminProvider) {
        this.codecs = new HierarchicalCodecResolver();
        this.adminProvider = adminProvider;
    }

    /**
     * Creates a new Canon using the provided channel provider for the admin
     * channel.
     *
     * @param adminprovider
     * @return
     */
    public static Canon newInstance(EventChannelProvider adminprovider) {
        return new Canon(adminprovider);
    }

    /**
     * Creates a new Canon instance with an in-memory channel provider as the
     * admin provider.
     *
     * @return
     */
    public static Canon newInstance() {
        return new Canon(new MemoryChannelProvider());
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
     * @throws UnableToResolveChannelException thrown if the admin stream could
     * not be resolved
     */
    public EventStream getAdminStream() throws UnableToResolveChannelException {
        try {
            return getOrCreateDefaultStream(INSTANCE_ADMIN_STREAM_NAME);
        } catch (UnableToResolveChannelException ex) {
            LOGGER.log(Level.SEVERE, "Unable to create admin stream", ex);
            throw ex;
        }
    }

    /**
     * Return all the channels registered to this canon instance.
     *
     * @return channel registered to this canon instance; changes to the
     * returned collection will not change any Canon state
     */
    public Collection<EventChannel> getChannels() {
        return new ArrayList(channels.values());
    }

    /**
     * Returns the named event channel if registered.
     *
     * @param channelName
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
     * @return registered event stream
     * @throws UnableToResolveChannelException thrown if no provider could load
     * the channel
     */
    public EventStream getOrCreateChannel(String scheme, String channelName,
            Map<String, String> channelProperties)
            throws UnableToResolveChannelException {

        //add default properties
        Map<String, String> streamPropertiesCopy
                = new HashMap<>(streamProperties); //defensive copy
        DEFAULT_STREAM_PROPERTIES
                .forEach((k, v) -> streamPropertiesCopy.putIfAbsent(k, v));

        return provider.getStream(streamName, streamPropertiesCopy, codecs);

        synchronized (streams) {
            if (streams.containsKey(streamName)) {
                return streams.get(streamName);
            }

            Optional<EventChannelProvider> provider = streamProviders.findProvider(scheme);
            if (!provider.isPresent()) {
                //scheme is still not available after provider reload
                final String error = String.format("Unable to resolve stream "
                        + "'%s' with provider scheme '%s', no provider found.",
                        streamName, scheme);
                LOGGER.warning(error);
                throw new UnableToResolveChannelException(error);
            }

            //stream not registered, create a default stream by this name
            EventStream stream = createStream(streamName, streamProperties, provider.get());
            streams.put(streamName, stream);
            return stream;
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
     */
    public EventStream getOrCreateStream(String channelUrl)
            throws UnableToResolveChannelException {
        try {
            URI uri = URI.create(streamUrl);
            return getOrCreateStream(uri.getScheme(), uri.getHost(),
                    queryToStreamProperties(uri.getQuery()));
        } catch (NullPointerException | IllegalArgumentException ex) {
            final String error = String.format("Invalid stream url '%s' "
                    + "provided when attempting to register a new stream.",
                    streamUrl);
            LOGGER.log(Level.WARNING, error, ex);
            throw new UnableToResolveChannelException(error);
        }
    }

    /**
     * Set the default event codec for the specified event type/object type.
     *
     * @param codec
     */
    public void useCodec(EventCodec<?> codec) {
        codecs.add(codec);
    }

    /**
     * Convert a URI query string to a map of stream properties.
     *
     * @param uriQuery decoded URI query portion or null
     * @return stream properties map
     */
    private Map<String, String> queryToStreamProperties(String uriQuery) {
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
}
