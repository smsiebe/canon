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

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.stream.FileStreamManager;
import org.geoint.canon.impl.stream.MemoryStreamManager;
import org.geoint.canon.impl.stream.AbstractStreamManager;
import org.geoint.canon.spi.stream.EventStreamProvider;
import org.geoint.canon.spi.stream.UnableToResolveStreamException;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;

/**
 * A Canon event database instance.
 *
 * @author steve_siebert
 */
public class Canon {

    private final String instanceId;
    private final AbstractStreamManager streamMgr;
    private final HierarchicalCodecResolver codecs;
    private final String INSTANCE_ADMIN_STREAM_NAME = "canon.admin";

    //used as synchronized lock to manage providers and lookup new streams
    private static final Map<String, EventStreamProvider> streamProviders;
    private static final ServiceLoader<EventStreamProvider> streamProviderLoader;

    private static final Logger LOGGER = Logger.getLogger(Canon.class.getName());

    static {
        //load all stream providers available on the classpath
        streamProviders = new HashMap<>();
        streamProviderLoader = ServiceLoader.load(EventStreamProvider.class);
        streamProviderLoader.forEach(Canon::registerStreamProvider);
    }

    private Canon(String instanceId, HierarchicalCodecResolver codecs,
            AbstractStreamManager streamMgr) {
        this.instanceId = instanceId;
        this.codecs = codecs;
        this.streamMgr = streamMgr;
    }

    public static Canon persistentInstance(File baseDir) {
        HierarchicalCodecResolver codecs = new HierarchicalCodecResolver();
        FileStreamManager mgr = FileStreamManager.fromBase(codecs, baseDir);
        return new Canon(mgr.getInstanceId(), codecs, mgr);
    }

    public static Canon transientInstance() {
        HierarchicalCodecResolver codecs = new HierarchicalCodecResolver();
        return new Canon(UUID.randomUUID().toString(), codecs,
                new MemoryStreamManager(codecs));
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
     * @return
     */
    public EventStream getAdminStream() {
        return streamMgr.getOrCreateStream(INSTANCE_ADMIN_STREAM_NAME);
    }

    /**
     * Return all the streams registered to this canon instance.
     *
     * @return stream registered to this canon instance; changes to the returned
     * collection will not change any Canon state
     */
    public Collection<EventStream> getStreams() {
        return streamMgr.getStreams();
    }

    /**
     * Returns the named event stream if registered.
     *
     * @param streamName
     * @return event stream, if registered
     */
    public Optional<EventStream> findStream(String streamName) {
        return streamMgr.findStream(streamName);
    }

    /**
     * Returns the named event stream, creating a local event stream with
     * default stream properties if it is not registered with the instance.
     *
     * @param streamName stream name
     * @return event stream
     * @throws UnableToResolveStreamException thrown if the stream does not
     * exist and the stream could not be created locally
     */
    public EventStream getOrCreateStream(String streamName)
            throws UnableToResolveStreamException {
        return streamMgr.getOrCreateStream(streamName);
    }

    /**
     * Register a new event stream with the canon instance.
     * <p>
     * Canon will attempt to resolve the stream by checking attempting to
     * retrieve a stream instance from each
     * {@link EventStreamProvider provider}.
     *
     * @param streamUrl in the following format:
     * scheme://streamName[?streamProperties]
     * @return resolved event stream
     * @throws StreamAlreadyExistsException the stream could not be registered
     * because a stream by this name already exists
     * @throws UnableToResolveStreamException if no stream provider could be
     * found or the provider could not load the requested stream
     */
    public EventStream registerStream(String streamUrl)
            throws StreamAlreadyExistsException,
            UnableToResolveStreamException {
        try {
            URI uri = URI.create(streamUrl);
            return registerStream(uri.getScheme(), uri.getHost(),
                    queryToStreamProperties(uri.getQuery()));
        } catch (NullPointerException | IllegalArgumentException ex) {
            final String error = String.format("Invalid stream url '%s' "
                    + "provided when attempting to register a new stream.",
                    streamUrl);
            LOGGER.log(Level.WARNING, error, ex);
            throw new UnableToResolveStreamException(error);
        }
    }

    /**
     * Register a new event stream.
     *
     * @param scheme stream provider scheme
     * @param streamName stream name
     * @param streamProperties stream properties
     * @return registered event stream
     * @throws StreamAlreadyExistsException thrown if a stream by this name is
     * already registered with this instance
     * @throws UnableToResolveStreamException thrown if no provider could
     * resolve the stream
     */
    public EventStream registerStream(String scheme, String streamName,
            Map<String, String> streamProperties)
            throws UnableToResolveStreamException, StreamAlreadyExistsException {

        if (streamMgr.streamExists(streamName)) {
            throw new StreamAlreadyExistsException(streamName);
        }

        return streamMgr.getOrCreateStream(streamName, () -> {

            //reload providers if the scheme is not available
            if (!streamProviders.containsKey(scheme)) {
                reloadStreamProviders();
            }

            if (!streamProviders.containsKey(scheme)) {
                //scheme is still not available after provider reload
                final String error = String.format("Unable to resolve stream "
                        + "'%s' with provider scheme '%s', no provider found.",
                        streamName, scheme);
                LOGGER.warning(error);
                throw new UnableToResolveStreamException(error);
            }

            //provider is available, attempt to load stream
            return streamProviders.get(scheme)
                    .getStream(streamName, streamProperties);
        });

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

    /**
     * Reload the stream providers,
     */
    private static void reloadStreamProviders() {
        synchronized (streamProviders) {
            streamProviders.clear();
            streamProviderLoader.reload();
            streamProviderLoader.forEach(Canon::registerStreamProvider);
        }
    }

    /**
     * Register the event stream provider.
     * <p>
     * NOTE: This method intentionally implements the {@link Consumer}
     * functional interface.
     */
    private static void registerStreamProvider(EventStreamProvider streamProvider) {
        synchronized (streamProviders) {
            streamProviders.putIfAbsent(streamProvider.getScheme(), streamProvider);
        }
    }
}
