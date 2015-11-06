package org.geoint.canon.stream;

import org.geoint.canon.event.EventException;

/**
 *
 * @author steve_siebert
 */
public class StreamAppendException extends EventException {

    private final String streamName;

    public StreamAppendException(String streamName) {
        this.streamName = streamName;
    }

    public StreamAppendException(String streamName, String message) {
        super(message);
        this.streamName = streamName;
    }

    public StreamAppendException(String streamName, String message, Throwable cause) {
        super(message, cause);
        this.streamName = streamName;
    }

    public StreamAppendException(String streamName, Throwable cause) {
        super(cause);
        this.streamName = streamName;
    }

    public String getStreamName() {
        return streamName;
    }

}
