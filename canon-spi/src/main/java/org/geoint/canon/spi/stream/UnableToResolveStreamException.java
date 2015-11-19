package org.geoint.canon.spi.stream;

import org.geoint.canon.event.EventException;

/**
 * Thrown if an event stream could not be resolved, either because no
 * EventStreamProvider was available for this type or because the corresponding
 * provider could not create the stream.
 *
 * @author steve_siebert
 */
public class UnableToResolveStreamException extends EventException {

    public UnableToResolveStreamException() {
    }

    public UnableToResolveStreamException(String message) {
        super(message);
    }

    public UnableToResolveStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToResolveStreamException(Throwable cause) {
        super(cause);
    }

}
