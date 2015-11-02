package org.geoint.canon;

/**
 * Thrown if an event was attempted to be appended out of sequence.
 *
 * @author steve_siebert
 */
public class AppendOutOfSequenceException extends StreamAppendException {

    public AppendOutOfSequenceException(String streamName) {
        super(streamName);
    }

    public AppendOutOfSequenceException(String streamName, String message) {
        super(streamName, message);
    }

    public AppendOutOfSequenceException(String streamName, String message, Throwable cause) {
        super(streamName, message, cause);
    }

    public AppendOutOfSequenceException(String streamName, Throwable cause) {
        super(streamName, cause);
    }

}
