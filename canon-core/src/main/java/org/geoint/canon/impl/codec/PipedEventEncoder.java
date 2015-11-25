package org.geoint.canon.impl.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;

/**
 * Utilizes PipedOuputStream/PipedInputStream with an EventCodec to provide a
 * readable (InputStream) encoder interface.
 * <p>
 * So long as the codec is thread-safe, this encoder is thread-safe.
 *
 * @author steve_siebert
 */
public class PipedEventEncoder {

    private final EventCodec codec;

    private static final Logger LOGGER
            = Logger.getLogger(PipedEventEncoder.class.getName());

    /**
     *
     * @param codec codec used to encode the event
     */
    public PipedEventEncoder(EventCodec codec) {
        this.codec = codec;
    }

    /**
     * Encodes the object using the codec provided in the constructor.
     *
     * @param event event to encode
     * @return stream to read the encoded event
     * @throws EventCodecException
     */
    public InputStream encode(Object event) throws EventCodecException {

        try (PipedInputStream in = new PipedInputStream();
                PipedOutputStream out = new PipedOutputStream(in)) {
            /*
             * Encoding operation is done on a separate task because it may 
             * cause a deadlock (see PipedInputStream javadoc).
             */
            return ((Callable<InputStream>) () -> {
                try {
                    codec.encode(event, out);
                } catch (IOException | EventCodecException ex) {
                    LOGGER.log(Level.WARNING, "Event encoding failed", ex);
                    throw ex;
                }
                return in;
            }).call();
        } catch (Exception ex) {
            throw new EventCodecException(String.format("Failed to encode "
                    + "event using codec %s", codec.getClass().getCanonicalName()),
                    ex);
        }
    }

}
