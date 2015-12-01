package org.geoint.canon.impl.stream;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventHandlerAction;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.StreamReadException;

/**
 * Executable task which sending events from the EventReader to an EventHandler.
 *
 * @author steve_siebert
 */
public class HandlerNotifier implements Runnable {

    private final EventReader eventReader;
    private final EventHandler handler;
    private volatile boolean keepRunning;
    private long RETRY_DELAY;
    private final static long DEFAULT_RETRY_DELAY = 3000L;
    
    private final Logger LOGGER
            = Logger.getLogger(HandlerNotifier.class.getName());

    public HandlerNotifier(EventReader eventReader, EventHandler handler) {
        this.eventReader = eventReader;
        this.handler = handler;
        this.RETRY_DELAY = DEFAULT_RETRY_DELAY;
    }
    
    public void setRetryDelay (long retryDelay, TimeUnit unit) {
        //intentionally didn't synchronize, it doesn't matter
        RETRY_DELAY = unit.toMillis(retryDelay);
    }

    @Override
    public void run() {
        LOGGER.log(Level.FINE, () -> "Initializing handler notifier on stream "
                + eventReader.getStream().toString());
        keepRunning = true;

        while (keepRunning) {
            try {

                AppendedEventMessage msg = eventReader.take();
                try {
                    handler.handle(msg);
                } catch (Exception ex) {
                    //thrown by the handler if there were problems handling,
                    //check with handler to figure out what should be done
                    EventHandlerAction action = handler.onFailure(msg, ex);
                    switch (action) {
                        case CONTINUE:
                            //skip the event 
                            LOGGER.log(Level.FINE, String.format("Handler %s "
                                    + "on stream %s failed to process event %s, "
                                    + "skipping event.",
                                    handler.getClass().getCanonicalName(),
                                    eventReader.getStream().toString(),
                                    msg.getSequence().asString()));
                            break;
                        case RETRY:
                            LOGGER.log(Level.FINER, String.format("Handler %s "
                                    + "on stream %s failed to process event %s, "
                                    + "retrying event.",
                                    handler.getClass().getCanonicalName(),
                                    eventReader.getStream().toString(),
                                    msg.getSequence().asString()));
                            Thread.sleep(RETRY_DELAY);
                            break;
                        case FAIL:
                            LOGGER.log(Level.FINE, String.format("Handler %s "
                                    + "on stream %s failed to process event %s, "
                                    + "shutting down handler.",
                                    handler.getClass().getCanonicalName(),
                                    eventReader.getStream().toString(),
                                    msg.getSequence().asString()));
                            keepRunning = false;
                            break;
                        default:
                            LOGGER.log(Level.SEVERE, "Unknown handler action "
                                    + "state, failing handler.");
                    }
                }

            } catch (InterruptedException | TimeoutException ex) {
                Thread.currentThread().isInterrupted();
                //pass through to keepRunning check
            } catch (StreamReadException ex) {
                //problem reading from stream, delay and retry
                LOGGER.log(Level.FINE, String.format("Handler %s could not read "
                        + "from stream %s, retrying after delay.",
                        handler.getClass().getCanonicalName(),
                        eventReader.getStream().toString()), ex);
                try {
                    Thread.currentThread().sleep(RETRY_DELAY);
                } catch (InterruptedException ex1) {
                    Thread.currentThread().isInterrupted();
                    //pass through to keepRunning check
                }
            }

            LOGGER.log(Level.FINE, String.format("Shutting down handler %s "
                    + "on stream %s", handler.getClass().getCanonicalName(),
                    eventReader.getStream().toString()));
        }
    }

}
