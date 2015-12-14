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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.stream.ChannelProviderManager;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.stream.EventChannel;
import org.geoint.canon.stream.StreamReadException;

/**
 * A Canon event database instance.
 *
 * @author steve_siebert
 */
public class Canon {

    private final ChannelProviderManager channelProviders;
    private final EventStream adminStream;
    private final HierarchicalCodecResolver codecs;
    private final Map<String, EventChannel> channels; //key is channel name

    private static final String ADMIN_STREAM_NAME = "canon.admin";
    private static final String DEFAULT_ADMIN_URI = "mem://" + ADMIN_STREAM_NAME;
    private static final Logger LOGGER = Logger.getLogger(Canon.class.getName());

    private Canon(EventStream adminStream, ChannelProviderManager providers) {
        channelProviders = providers;
        this.codecs = new HierarchicalCodecResolver();
        this.adminStream = adminStream;
        this.channels = new HashMap<>();
    }

    /**
     * Creates a new Canon instance using the default in-memory channel provider
     * as the admin provider.
     *
     * @return
     * @throws StreamReadException
     * @throws UnableToResolveChannelException
     */
    public static Canon newInstance()
            throws StreamReadException, UnableToResolveChannelException {

        return newInstance(DEFAULT_ADMIN_URI);
    }

    /**
     * Creates a new Canon instance using the provided admin URI.
     *
     * @param adminUri
     * @return
     * @throws StreamReadException
     * @throws UnableToResolveChannelException
     */
    public static Canon newInstance(String adminUri)
            throws StreamReadException, UnableToResolveChannelException {
        return newInstance(adminUri,
                ChannelProviderManager.getDefaultInstance());
    }

    /**
     * Creates a new Canon instance using the provided channel provider for the
     * admin channel and the provided loader to load additional providers.
     *
     * @param adminUri
     * @param channelLoader
     * @return
     * @throws StreamReadException
     * @throws UnableToResolveChannelException
     */
    public static Canon newInstance(String adminUri, ClassLoader channelLoader)
            throws StreamReadException, UnableToResolveChannelException {
        return newInstance(adminUri,
                ChannelProviderManager.getInstance(channelLoader));
    }

    /**
     * Creates the canon instance using the provider manager to load the admin
     * channel.
     * <p>
     * This method is intentionally private to prevent public exposure of the
     * ChannelProviderManager class.
     *
     * @param adminUri
     * @param providerManager
     * @return
     * @throws StreamReadException
     * @throws UnableToResolveChannelException
     */
    private static Canon newInstance(String adminUri,
            ChannelProviderManager providerManager)
            throws StreamReadException, UnableToResolveChannelException {
        return new Canon(getStream(adminUri, providerManager), providerManager);
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
        return adminStream;
    }

    /**
     * Return all the channels registered to this canon instance.
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
     * @return registered event channel
     * @throws UnableToResolveChannelException thrown if no provider could load
     * the channel
     */
    public EventChannel getOrCreateChannel(String scheme, String channelName,
            Map<String, String> channelProperties)
            throws UnableToResolveChannelException {

        //channels.computeIfAbsent(channelName, (n) -> getChannel(channelProviders, scheme, n, channelProperties));
        synchronized (channels) {
            if (channels.containsKey(channelName)) {
                return channels.get(channelName);
            }

            EventChannel c = getChannel(channelProviders, scheme,
                    channelName, channelProperties);
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
     */
    public EventChannel getOrCreateChannel(String channelUrl)
            throws UnableToResolveChannelException {
        URI uri = URI.create(channelUrl);
        final String channelName = getChannelName(uri);
        synchronized (channels) {
            if (channels.containsKey(channelName)) {
                return channels.get(channelName);
            }

            final String channelScheme = getChannelScheme(uri);
            final Map<String, String> channelProperties = getUriProperties(uri);
            EventChannel c = getChannel(channelProviders, channelScheme, channelName, channelProperties);
            channels.put(channelName, c);
            return c;
        }
    }

    public EventStream getOrCreateStream(String streamUri)
            throws UnableToResolveChannelException {
        URI uri = URI.create(streamUri);

        final String channelScheme = getChannelScheme(uri);
        final String channelName = getChannelName(uri);
        final String streamName = getStreamName(uri);
        final Map<String, String> properties = getUriProperties(uri);
        return getOrCreateStream(channelScheme, channelName,
                streamName, properties);
    }

    public EventStream getOrCreateStream(String scheme, String channelName,
            String streamName, Map<String, String> streamProperties)
            throws UnableToResolveChannelException {
        EventChannel channel = getOrCreateChannel(scheme, channelName, streamProperties);
        return channel.getStream(streamName, streamProperties);
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
     * Returns the event channel or throws an Exception.
     *
     * @param providerManager
     * @param channelScheme
     * @param channelName
     * @param channelProperties
     * @return
     * @throws UnableToResolveChannelException
     */
    private static EventChannel getChannel(ChannelProviderManager providerManager,
            String channelScheme,
            String channelName,
            Map<String, String> channelProperties)
            throws UnableToResolveChannelException {
        return providerManager.findProvider(channelScheme, channelProperties)
                .orElseThrow(() -> new UnableToResolveChannelException(
                        String.format("No channel provider found for channel "
                                + "'%s' with channel scheme '%s'",
                                channelName, channelScheme)))
                .getChannel(channelName, channelProperties);
    }

    private static EventStream getStream(String streamUri,
            ChannelProviderManager providerManager)
            throws StreamReadException, UnableToResolveChannelException {
        final URI uri = URI.create(streamUri);
        final String channelScheme = getChannelScheme(uri);
        final String channelName = getChannelName(uri);
        final String streamName = getStreamName(uri);
        final Map<String, String> streamProperties = getUriProperties(uri);
        EventChannel channel = getChannel(providerManager, channelScheme,
                channelName, streamProperties);
        return channel.getStream(streamName, streamProperties);
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
}
