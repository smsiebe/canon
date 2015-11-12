package org.geoint.canon.tx;

import org.geoint.canon.event.EventException;

/**
 *
 * @author steve_siebert
 */
public class EventTransactionException extends EventException {

    public EventTransactionException() {
    }

    public EventTransactionException(String message) {
        super(message);
    }

    public EventTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventTransactionException(Throwable cause) {
        super(cause);
    }

}
