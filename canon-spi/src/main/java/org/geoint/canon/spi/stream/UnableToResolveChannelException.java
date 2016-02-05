package org.geoint.canon.spi.stream;

import org.geoint.canon.event.EventException;

/**
 * Thrown if an event stream could not be resolved, either because no
 * EventChannelProvider was available for this type or because the corresponding
 * provider could not create the channel.
 *
 * @author steve_siebert
 */
public class UnableToResolveChannelException extends EventException {

    public UnableToResolveChannelException() {
    }

    public UnableToResolveChannelException(String message) {
        super(message);
    }

    public UnableToResolveChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToResolveChannelException(Throwable cause) {
        super(cause);
    }

}
