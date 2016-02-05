package org.geoint.canon.codec;

import java.util.Optional;

/**
 * Resolves a codec for the specified event type.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface CodecResolver {

    Optional<EventCodec> findCodec(String eventType);
}
