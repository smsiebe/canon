package org.geoint.canon.codec;

/**
 * Thrown when a codec could not be found for an event type.
 *
 * @author steve_siebert
 */
public class CodecNotFoundException extends EventCodecException {

    public CodecNotFoundException(String eventType) {
        super(getMessage(eventType));
    }

    public CodecNotFoundException(String eventType, String message) {
        super(getMessage(eventType) + ": " + message);
    }

    public CodecNotFoundException(String eventType,
            String message, Throwable cause) {
        super(getMessage(eventType) + ": " + message, cause);
    }

    public CodecNotFoundException(String eventType, Throwable cause) {
        super(getMessage(eventType), cause);
    }

    private static String getMessage(String eventType) {
        return String.format("Could not find event codec for event type '%s'",
                eventType);
    }

}
