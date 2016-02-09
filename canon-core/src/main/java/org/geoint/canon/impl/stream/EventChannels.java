package org.geoint.canon.impl.stream;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.EventChannel;

/**
 * Discovers and initializes EventChannelProvider implementations and provides
 * EventChannel resolution convenience methods at class load time.
 * <p>
 * This manager discovers/loads instances of the EventChannelProvider using
 * {@link ServiceLoader}.
 *
 * @author steve_siebert
 */
public enum EventChannels {

    INSTANCE;

    private static final Set<EventChannelProvider> providers;
    private static final Logger LOGGER
            = Logger.getLogger(EventChannels.class
                    .getName());

    static {
        ServiceLoader<EventChannelProvider> loader
                = ServiceLoader.load(EventChannelProvider.class);
        providers = new HashSet<>();
        loader.forEach((p) -> {
            LOGGER.log(Level.FINER,
                    () -> String.format("Loaded event channel provider '%s'",
                            p.getClass().getCanonicalName())
            );
            providers.add(p);
        });
    }

    /**
     * Attempts to find a channel provider for the provided channel details.
     *
     * @param scheme
     * @param channelProperties
     * @return matching provider, if available
     */
    public Optional<EventChannelProvider> findProvider(String scheme,
            Map<String, String> channelProperties) {
        return providers.stream()
                .filter((p) -> p.provides(scheme, channelProperties))
                .findFirst();
    }

    /**
     * Returns all channel providers registered to this manager.
     *
     * @return all registered channel providers
     */
    public Stream<EventChannelProvider> stream() {
        return providers.stream();
    }

}
