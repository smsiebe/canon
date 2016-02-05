package org.geoint.canon.spi.id;

import java.util.Map;
import org.geoint.canon.event.EventMessage;

/**
 * An EventIdGenerator is used generate a unique identity for each event.
 * <p>
 * The EventIdGenerator may be used to provide a unique identity generator for
 * the canon server to use for appended events. Support for a custom
 * EventIdGenerator is {@link EventChannelProvider} implementation specific,
 * though it is recommended for all channel providers to support custom event
 * identities.
 * <p>
 * For those channel providers that do support a custom id generator they must
 * provide a default generator, if not specified, and must support overriding
 * the default id generator by passing the class name of the EventIdGenerator
 * implication to the provider as the value of the channel property
 * <i>org.geoint.canon.idGenClass</i>.
 * <p>
 * Implementations of this interface must be thread safe.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface EventIdGenerator {

    public static final String CHANNEL_PROPERTY_NAME
            = "org.geoint.canon.idGenClass";

    /**
     * Generate a new unique event identity.
     *
     * @param eventContext event for which the identifier is to be generated
     * @param channelProperties properties of the channel for which this id 
     * generator is being used
     * @return new unique event identity
     */
    String generate(EventMessage eventContext, 
            Map<String, String> channelProperties);
}
