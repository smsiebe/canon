package org.geoint.canon.impl.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final ExecutorService executor;

    /**
     * Creates the piped event encoder with 5 worker threads.
     */
    public PipedEventEncoder() {
        executor = Executors.newFixedThreadPool(5);
    }

    public PipedEventEncoder(ExecutorService executor) {
        this.executor = executor;
    }

    private static final Logger LOGGER
            = Logger.getLogger(PipedEventEncoder.class.getName());

    /**
     * Encodes the object using the codec provided in the constructor.
     *
     * @param codec codec used to encode event
     * @param event event to encode
     * @return stream to read the encoded event
     * @throws EventCodecException
     */
    public InputStream encode(EventCodec codec, Object event)
            throws EventCodecException {
        try {
            return executor.submit(new PipedEncodeTask(codec, event)).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new EventCodecException(String.format("Problem which "
                    + "asynchronously executing event encoding on with "
                    + "'%s' codec", codec.getClass().getName()), ex);
        }
    }

    /*
             * Encoding operation is done on a separate task because it may 
             * cause a deadlock (see PipedInputStream javadoc).
     */
    private class PipedEncodeTask implements Callable<InputStream> {

        private final EventCodec codec;
        private final Object event;

        public PipedEncodeTask(EventCodec codec, Object event) {
            this.codec = codec;
            this.event = event;
        }

        @Override
        public InputStream call() throws EventCodecException {
            PipedInputStream in = new PipedInputStream();
            PipedOutputStream out = null;
            try {
                out = new PipedOutputStream(in);

                try {
                    codec.encode(event, out);
                } catch (IOException | EventCodecException ex) {
                    LOGGER.log(Level.WARNING, "Event encoding failed", ex);
                    throw ex;
                }
                return in;

            } catch (Exception ex) {
                throw new EventCodecException(String.format("Failed to encode "
                        + "event using codec %s", codec.getClass().getCanonicalName()),
                        ex);
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException ex) {
                        throw new EventCodecException("Failed to clean up encoding "
                                + "operation", ex);
                    }
                }
            }
        }

    }
}
