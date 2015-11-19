package org.geoint.canon.impl.stream;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import org.geoint.canon.spi.stream.EventStreamProvider;

/**
 * Discovers and initializes EventStreamProvider instances and provides
 * EventStream resolution convenience methods.
 *
 * @author steve_siebert
 */
public class StreamProviderManager {

    private static final StreamProviderManager DEFAULT_MANAGER
            = getInstance(StreamProviderManager.class.getClassLoader());

    //used as synchronized lock to manage providers and lookup new streams
    private final Map<String, EventStreamProvider> streamProviders;
    private final ServiceLoader<EventStreamProvider> streamProviderLoader;

    private StreamProviderManager(ServiceLoader<EventStreamProvider> loader) {
        this.streamProviderLoader = loader;
        //load all stream providers available on the classpath
        streamProviders = new HashMap<>();
        streamProviderLoader.forEach(this::registerStreamProvider);
    }

    public static StreamProviderManager getDefaultInstance() {
        return DEFAULT_MANAGER;
    }

    public static StreamProviderManager getInstance(ClassLoader classloader) {
        return new StreamProviderManager(
                ServiceLoader.load(EventStreamProvider.class, classloader));
    }

    public Optional<EventStreamProvider> findProvider(String scheme) {
        if (!streamProviders.containsKey(scheme)) {
            //reload providers if the scheme is not available
            reloadProviders();
        }

        return Optional.ofNullable(streamProviders.get(scheme));
    }

    public Optional<EventStreamProvider> findProvider(URI streamUri) {
        return findProvider(streamUri.getScheme());
    }

    /**
     * Reload the stream providers,
     */
    public void reloadProviders() {
        synchronized (streamProviders) {
            streamProviders.clear();
            streamProviderLoader.reload();
            streamProviderLoader.forEach(this::registerStreamProvider);
        }
    }

    /**
     * Register the event stream provider.
     * <p>
     * NOTE: This method intentionally implements the {@link Consumer}
     * functional interface.
     */
    private void registerStreamProvider(EventStreamProvider streamProvider) {
        synchronized (streamProviders) {
            streamProviders.putIfAbsent(streamProvider.getScheme(), streamProvider);
        }
    }
}
