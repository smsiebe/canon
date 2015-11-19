package org.geoint.canon.impl.stream;

import org.geoint.canon.spi.stream.UnableToResolveStreamException;
import org.geoint.canon.stream.EventStream;

/**
 * Creates a new EventStream instance.
 *
 * @see AbstractStreamManager
 * @author steve_siebert
 */
@FunctionalInterface
public interface StreamCreator {

    EventStream create() throws UnableToResolveStreamException;
}
