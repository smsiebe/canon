package org.geoint.canon.impl.stream;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.spi.stream.EventChannelProvider;

/**
 * Discovers and initializes EventChannelProvider instances and provides
 * EventChannel resolution convenience methods.
 * <p>
 * Currently this manager discovers/loads instances of the EventChannelProvider
 * using only {@link ServiceLoader}.
 *
 * @author steve_siebert
 */
public class ChannelProviderManager {

    private static final ChannelProviderManager DEFAULT_MANAGER
            = getInstance(ChannelProviderManager.class.getClassLoader());
    private static final Logger LOGGER 
            = Logger.getLogger(ChannelProviderManager.class.getName());
    
    //used as synchronized lock to manage providers and lookup new channels
    private final Set<EventChannelProvider> loadedProviders;
    private final ServiceLoader<EventChannelProvider> providerLoader;

    private ChannelProviderManager(ServiceLoader<EventChannelProvider> loader) {
        this.providerLoader = loader;
        //load all  providers available on the classpath
        loadedProviders = new HashSet<>();
        providerLoader.forEach(this::registerProvider);
    }

    public static ChannelProviderManager getDefaultInstance() {
        return DEFAULT_MANAGER;
    }

    public static ChannelProviderManager getInstance(ClassLoader classloader) {
        return new ChannelProviderManager(
                ServiceLoader.load(EventChannelProvider.class, classloader));
    }

    public Optional<EventChannelProvider> findProvider(String scheme, 
            Map<String, String> channelProperties) {
        Optional<EventChannelProvider> provider = loadedProviders.stream()
                .findFirst((p) -> )
        if (!loadedProviders.(scheme)) {
            //reload providers if the scheme is not available
            reloadProviders();
        }

        return Optional.ofNullable(loadedProviders.get(scheme));
    }

    public Optional<EventChannelProvider> findProvider(URI channelUri) {
        return findProvider(channelUri.getScheme());
    }

    /**
     * Reload providers.
     */
    public void reloadProviders() {
        synchronized (loadedProviders) {
            providerLoader.reload();
            providerLoader.forEach(this::registerProvider);
        }
    }

    /**
     * Register the channel provider.
     * <p>
     * NOTE: This method intentionally implements the {@link Consumer}
     * functional interface.
     */
    private void registerProvider(EventChannelProvider channelProvider) {
        synchronized (loadedProviders) {
            if (loadedProviders.contains(channelProvider)) {
                LOGGER.log(Level.FINE, String.format("Channel provider %s "
                        + "is already loaded.", 
                        channelProvider.class.getName()));
            } else {
                loadedProviders.putIfAbsent(channelProvider.(), channelProvider);
            }
        }
    }
}
