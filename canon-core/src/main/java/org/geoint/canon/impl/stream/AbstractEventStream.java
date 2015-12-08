package org.geoint.canon.impl.stream;

import org.geoint.canon.impl.event.EventSequencer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.impl.event.EventSequence;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.event.SimpleIncrementSequencer;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventHandlerAction;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.EventReaderDecorator;
import org.geoint.canon.stream.FilteredEventReader;
import org.geoint.canon.stream.HandlerNotifier;
import org.geoint.canon.stream.StreamReadException;

/**
 * Implements common event stream methods requiring event stream implementations
 * extending from this class to implement only a subset of methods.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventStream implements EventStream {

    enum NotifierState {
        RUNNING, PAUSED, STOPPED
    }

    protected final String channelName;
    protected final String streamName;
    protected final Map<String, String> streamProperties;
    protected final HierarchicalCodecResolver streamCodecs;
    protected final EventSequencer sequencer;
    private final Set<HandlerNotifierImpl> notifiers;
    //each notifier is executed asynchronously
    private final ExecutorService notifierExecutor;

    private static final Logger LOGGER
            = Logger.getLogger(EventStream.class.getPackage().getName());

    /**
     *
     * @param channelName
     * @param streamName
     * @param streamProperties
     * @param codecs
     * @param executor
     */
    public AbstractEventStream(String channelName, String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs, ExecutorService executor) {
        this(channelName, streamName,
                streamProperties,
                codecs,
                new SimpleIncrementSequencer(),
                executor);
    }

    public AbstractEventStream(String channelName, String streamName,
            Map<String, String> streamProperties,
            ExecutorService notifierExecutor) {
        this(channelName, streamName, streamProperties,
                new HierarchicalCodecResolver(),
                new SimpleIncrementSequencer(),
                notifierExecutor);
    }

    public AbstractEventStream(String channelName,
            String streamName,
            Map<String, String> streamProperties,
            EventSequencer sequencer,
            ExecutorService notifierExecutor) {
        this(channelName, streamName, streamProperties,
                new HierarchicalCodecResolver(),
                sequencer,
                notifierExecutor);
    }

    public AbstractEventStream(String channelName, String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs,
            EventSequencer sequencer,
            ExecutorService notifierExecutor) {
        this.channelName = channelName;
        this.streamName = streamName;
        this.streamProperties = streamProperties;
        this.streamCodecs = new HierarchicalCodecResolver(codecs);
        this.sequencer = sequencer;
        this.notifiers = new HashSet<>();
        this.notifierExecutor = notifierExecutor;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public String getName() {
        return streamName;
    }

    @Override
    public HandlerNotifier addHandler(EventHandler handler) {
        //use a default reader, starting at zero
        return addHandler(handler, newReader());
    }

    @Override
    public HandlerNotifier addHandler(EventHandler handler, Predicate filter) {
        return addHandler(handler, new FilteredEventReader(newReader(), filter));
    }

    @Override
    public HandlerNotifier addHandler(EventHandler handler, String sequence)
            throws UnknownEventException {
        EventReader reader = newReader();
        reader.setPosition(sequence);
        return addHandler(handler, reader);
    }

    @Override
    public HandlerNotifier addHandler(EventHandler handler, Predicate filter,
            String sequence) throws UnknownEventException {
        EventReader reader = newReader();
        reader.setPosition(sequence);
        return addHandler(handler, new FilteredEventReader(reader, filter));
    }

    @Override
    public HandlerNotifier addHandler(EventHandler handler, EventReader reader) {

        HandlerNotifierImpl notifier = new HandlerNotifierImpl(reader, handler);
        synchronized (notifiers) {
            Optional<HandlerNotifierImpl> existing = notifiers.stream()
                    .filter((n) -> Objects.equals(notifier, n))
                    .findAny();

            if (existing.isPresent()) {
                //handler already exists for this handler/reader pair, return 
                //the handle
                return existing.get();
            }

            notifierExecutor.submit(notifier);
            notifiers.add(notifier);
            return notifier;
        }
    }

    @Override
    public Collection<HandlerNotifier> listHandlers() {
        return new ArrayList(notifiers);
    }

    @Override
    public void useCodec(EventCodec codec) {
        this.streamCodecs.add(codec);
    }

    @Override
    public Optional<EventCodec> getCodec(String eventType) {
        return streamCodecs.getCodec(eventType);
    }

    /**
     * Subclasses must call {@code super.close()} to prevent resource leakage.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        //shut down notifiers
        notifiers.stream().forEach(HandlerNotifierImpl::stop);

        /*
         * humans are silly
         * and if a reference is made
         * an island is formed
         */
        notifiers.clear();

        //shutdown notifier executor
        notifierExecutor.shutdown();
    }

    /**
     * EventReader used by the HandlerNotifierImpl which provides methods to
     * inject "poison pill" events used to manage the state of the asynchronous
     * notifier.
     */
    private class DopeableEventReader extends EventReaderDecorator {

        //intentionally did not synchronize...the performance hit vs the 
        //benefit isn't worth it.  Event without synchronization, we're still 
        //thread-safe
        private PoisonPillMessage pill;

        public DopeableEventReader(EventReader reader) {
            super(reader);
        }

        @Override
        public Optional<AppendedEventMessage> poll() throws StreamReadException {
            if (pill != null) {
                try {
                    return Optional.of(pill);
                } finally {
                    pill = null;
                }
            }
            return reader.poll();
        }

        void stop() {
            pill = new ShutdownNotifierEventMessage();
        }

        void pause() {
            pill = new PauseNotifierEventMessage();
        }

        void resume() {
            pill = new ResumeNotifierEventMessage();
        }
    }

    /**
     * Handler resource which calls an EventHandler for every event read from an
     * EventReader.
     * <p>
     * Instances of this class are thread-safe, intended to be executed
     * asynchronously from the initializing thread and are manageable from any
     * thread.
     * <p>
     * To prevent any assumptions on the way instances are pooled/executed,
     * pausing and shutting down of a notifier is done through a poison-pill
     * pattern. The event reader used by this notifier is decorated to allow for
     * poison pill injection without modification of the stream. The poison pill
     * causes the notifier to either pause or shutdown, and are never sent to
     * the callback handler.
     */
    private class HandlerNotifierImpl implements HandlerNotifier, Runnable {

        private final DopeableEventReader eventReader;
        private final EventHandler handler;
        private volatile NotifierState state;
        private long RETRY_DELAY;
        private final static long DEFAULT_RETRY_DELAY = 3000L;

        private final Logger LOGGER
                = Logger.getLogger(HandlerNotifier.class.getName());

        public HandlerNotifierImpl(EventReader eventReader, EventHandler handler) {
            this.eventReader = new DopeableEventReader(eventReader);
            this.handler = handler;
            this.RETRY_DELAY = DEFAULT_RETRY_DELAY;
        }

        public void setRetryDelay(long retryDelay, TimeUnit unit) {
            //intentionally didn't synchronize, it doesn't matter
            RETRY_DELAY = unit.toMillis(retryDelay);
        }

        @Override
        public void pause() throws IllegalStateException {
            switch (state) {
                case RUNNING:
                    eventReader.pause(); //add poison pill
                    break;
                case STOPPED:
                    throw new IllegalStateException("Stopped handler cannot be "
                            + "paused.");
            }
        }

        @Override
        public void restart() throws IllegalStateException {
            switch (state) {
                case PAUSED:
                    eventReader.resume(); //add poison pill
                    break;
                case STOPPED:
                    throw new IllegalStateException("Stopped handler cannot be "
                            + "resumed.");
            }
        }

        @Override
        public void stop() {
            if (state.equals(NotifierState.STOPPED)) {
                return;
            }

            eventReader.stop(); //add poison pill
        }

        @Override
        public boolean isRunning() {
            return state.equals(NotifierState.RUNNING);
        }

        @Override
        public boolean isStopped() {
            return state.equals(NotifierState.STOPPED);
        }

        @Override
        public boolean isPaused() {
            return state.equals(NotifierState.PAUSED);
        }

        @Override
        public void run() {
            LOGGER.log(Level.FINE, () -> "Initializing handler notifier on stream "
                    + eventReader.getStream().toString());
            state = NotifierState.RUNNING;

            while (!state.equals(NotifierState.STOPPED)) {
                try {
                    AppendedEventMessage msg = eventReader.take();

                    //handle poison pill
                    if (msg instanceof PoisonPillMessage) {

                        if (msg instanceof ShutdownNotifierEventMessage) {
                            LOGGER.log(Level.FINEST, "Shutdown poison pill "
                                    + "received, shutting down handler.");
                            break; //break out of processing loop
                        }

                        if (state.equals(NotifierState.RUNNING)
                                && msg instanceof PauseNotifierEventMessage) {
                            LOGGER.log(Level.FINE, String.format("Pausing "
                                    + "handler %s",
                                    handler.getClass().getCanonicalName())
                            );
                            state = NotifierState.PAUSED;
                            continue; //poison message is eaten
                        } else if (state.equals(NotifierState.PAUSED)
                                && msg instanceof ResumeNotifierEventMessage) {
                            LOGGER.log(Level.FINE, String.format("Resuming "
                                    + "handler %s",
                                    handler.getClass().getCanonicalName())
                            );
                            state = NotifierState.RUNNING;
                            continue; //poison message is eaten
                        }

                        LOGGER.log(Level.WARNING, String.format("Attempt to "
                                + "change notifier '%s' state from %s with pill"
                                + " %s has failed; swollowing pill.",
                                handler.getClass().getCanonicalName(),
                                state.name(),
                                msg.getClass().getName()));
                        continue; //poison message is eaten
                    }

                    //not a posion pill, handle event
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
                                        msg.getSequence()));
                                break;
                            case RETRY:
                                LOGGER.log(Level.FINER, String.format("Handler %s "
                                        + "on stream %s failed to process event %s, "
                                        + "retrying event.",
                                        handler.getClass().getCanonicalName(),
                                        eventReader.getStream().toString(),
                                        msg.getSequence()));
                                Thread.sleep(RETRY_DELAY);
                                break;
                            case FAIL:
                                LOGGER.log(Level.FINE, String.format("Handler %s "
                                        + "on stream %s failed to process event %s, "
                                        + "shutting down handler.",
                                        handler.getClass().getCanonicalName(),
                                        eventReader.getStream().toString(),
                                        msg.getSequence()));
                                state = NotifierState.STOPPED;
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

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 61 * hash + Objects.hashCode(this.eventReader);
            hash = 61 * hash + Objects.hashCode(this.handler);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final HandlerNotifierImpl other = (HandlerNotifierImpl) obj;
            if (!Objects.equals(this.eventReader, other.eventReader)) {
                return false;
            }
            if (!Objects.equals(this.handler, other.handler)) {
                return false;
            }
            return true;
        }

    }

// <editor-fold desc="Poison pill implementation used by HandlerNotifierImpl.">
    /**
     * Poison-pill event message used to manage a HandlerNotifierImpl in a
     * thread-safe manner.
     *
     * @see HandlerNotifierImpl
     */
    private class PoisonPillMessage implements AppendedEventMessage {

        @Override
        public String getSequence() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public int getEventLength() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public String getChannelName() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public String getStreamName() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public String getAuthorizerId() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public String[] getTriggerIds() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public String getEventType() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public Map<String, String> getHeaders() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public Optional<String> findHeader(String headerName) {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public String getHeader(String headerName, Supplier<String> defaultValue) {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public InputStream getEventContent() {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

        @Override
        public <E> E getEvent(EventCodec<E> codec)
                throws IOException, EventCodecException {
            throw new UnsupportedOperationException("PoisonPillMessage is not "
                    + "a valid EventMessage.");
        }

    }

    /**
     * Shuts down the HandlerNotifierImpl.
     *
     */
    private class ShutdownNotifierEventMessage extends PoisonPillMessage {

    }

    /**
     * Pauses the HandlerNotifierImpl.
     */
    private class PauseNotifierEventMessage extends PoisonPillMessage {

    }

    /**
     * Resumes a HandlerNotifierImpl.
     */
    private class ResumeNotifierEventMessage extends PoisonPillMessage {

    }

// </editor-fold>
}
