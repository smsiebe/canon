package org.geoint.canon.impl.stream;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.concurrent.FutureWatcher;
import org.geoint.canon.impl.stream.file.FileStreamProvider;
import org.geoint.canon.spi.stream.EventStreamProvider;
import org.geoint.canon.stream.AppendOutOfSequenceException;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.tx.EventTransactionException;
import org.geoint.canon.tx.TransactionCommitted;

/**
 * Implements common event stream methods requiring event stream implementations
 * extending from this class to implement only a subset of methods.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventStream implements EventStream {

    protected volatile String lastEventId;
    protected final String streamName;
    protected final HierarchicalCodecResolver streamCodecs;
    protected final Map<String, String> streamProperties;
    private final EventStreamProvider offlineStreamProvider;
    private EventStream offlineStream;

    private static final Logger LOGGER
            = Logger.getLogger(EventStream.class.getPackage().getName());

    /**
     *
     * @param streamName
     * @param streamProperties
     * @param codecs
     * @param offline
     */
    public AbstractEventStream(String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs,
            boolean offline) {
        this(streamName, streamProperties, codecs, null, offline);
    }

    /**
     *
     * @param streamName
     * @param streamProperties
     * @param codecs
     * @param offlineStreamProvider if null uses a file stream provider
     * @param offline
     */
    public AbstractEventStream(String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs,
            EventStreamProvider offlineStreamProvider,
            boolean offline) {
        this.streamName = streamName;
        this.streamProperties = streamProperties;
        this.streamCodecs = new HierarchicalCodecResolver(codecs);
        this.offlineStreamProvider = (offlineStreamProvider != null)
                ? offlineStreamProvider
                : getDefaultOfflineStreamProvider();
        if (offline) {
            createOffline();
        }
    }

    @Override
    public boolean isOfflineable() {
        return offlineStream != null;
    }

    @Override
    public void setOffline(boolean offline) {
        if (offlineStream == null) {
            createOffline();
        }
    }

    @Override
    public String getName() {
        return streamName;
    }

    @Override
    public String getLastEventId() {
        return lastEventId;
    }

    @Override
    public EventAppender appendToEnd() {
        /*
         * decorates the stream with a TrackingEventAppender to keep the 
         * lasteventid accurate
         */
        return new TrackingEventAppender(newAppender());
    }

    @Override
    public EventAppender appendAfter(String previousEventId)
            throws AppendOutOfSequenceException {
        if (!lastEventId.contentEquals(previousEventId)) {
            //eliminate the need to wait until transaction commit if the 
            //stream is already advanced past the requested position
            throw new AppendOutOfSequenceException(streamName, String.format(
                    "Unable to append an event after %s, stream is currently "
                    + "positioned at %s", previousEventId, lastEventId));
        }
        /*
         * decorates the stream with a TrackingPositionedEventAppender to 
         * ensure the events are only committed if the lasteventid is the same 
         * as the one the appender was created with AND to keep the 
         * lasteventid accurate
         */
        return new TrackingPositionedEventAppender(previousEventId, newAppender());
    }

    @Override
    public void addHandler(EventHandler handler) {

    }

    @Override
    public void addHandler(EventHandler handler, Predicate filter) {

    }

    @Override
    public void addHandler(EventHandler handler, String lastEventId) {

    }

    @Override
    public void addHandler(EventHandler handler, Predicate filter, String lastEventId) {

    }

    @Override
    public void removeHandler(EventHandler handler) {

    }

    @Override
    public void useCodec(EventCodec codec) {
        this.streamCodecs.add(codec);
    }

    @Override
    public Optional<EventCodec> getCodec(String eventType) {
        return streamCodecs.getCodec(eventType);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    protected final void createOffline() {

    }

    private String getOfflineStreamName() {
        return String.format("org.canon.offline.%s" + streamName);
    }

    private static EventStreamProvider getDefaultOfflineStreamProvider() {
        return new FileStreamProvider();
    }

    /**
     * Returns a new event appender for the stream.
     * <p>
     * The appender needs only to append to the tail of the stream, the
     * decorators used by this implementation will ensure the events are
     * sequenced properly.
     *
     * @return returns a new appender that writes an event to the tail of the
     * stream
     */
    protected abstract EventAppender newAppender();

    private class TrackingEventAppender extends EventAppenderDecorator {

        public TrackingEventAppender(EventAppender appender) {
            super(appender);
        }

        @Override
        public Future<TransactionCommitted> commit()
                throws EventTransactionException {
            Future<TransactionCommitted> result = super.commit();
            //asynchrnously update the last event id when the append operation 
            //completes
            FutureWatcher.INSTANCE.onSuccess(result, (r) -> lastEventId = r.getLastEventId());
            return result;
        }

    }

    /**
     * Decorator which that will only commit an event to the stream at a
     * specific position and updates the streams event position (tracking).
     *
     * If the last event id of the stream does not equal the position defined at
     * construction the appender will throw an exception at commit time.
     *
     */
    private class TrackingPositionedEventAppender extends EventAppenderDecorator {

        private final String previousEventId;

        public TrackingPositionedEventAppender(String previousEventId,
                EventAppender appender) {
            super(new TrackingEventAppender(appender));
            this.previousEventId = previousEventId;
        }

    }

//    @Override
//    public void deleteOfflineCopy() {
//        if (offlineStream == null) {
//            LOGGER.log(Level.FINE, String.format("Offline copy of stream '%s' "
//                    + "could not be found", providerStream.getName()));
//        }
//
//        LOGGER.log(Level.INFO, String.format("Deleting offline copy of stream"
//                + " '%s'", providerStream.getName()));
//
//        final LocalEventStream oldOfflineStream = offlineStream;
//        offlineStream = null;
//
//        assert offlineDir != null : "Offline stream is set but offline dir is unknown";
//
//        final File oldOfflineDir = offlineDir;
//        offlineDir = null;
//
//        if (oldOfflineStream.isArchiving()) {
//            oldOfflineStream.deleteArchive();
//        }
//        try {
//            oldOfflineStream.close();
//        } catch (IOException ex) {
//            LOGGER.log(Level.SEVERE, String.format("Offline copy of stream "
//                    + "'%s' located at '%s' was not closed gracefully.",
//                    providerStream.getName(),
//                    oldOfflineDir.getAbsolutePath()));
//        }
//
//        try {
//            recursiveDeleteDirectory(oldOfflineDir);
//        } catch (Exception ex) {
//            LOGGER.log(Level.SEVERE, String.format("Problems deleting offline "
//                    + "copy of stream '%s' located at '%s', copy of stream "
//                    + "may still exist on filesystem.",
//                    providerStream.getName(),
//                    oldOfflineDir.getAbsolutePath()
//            ), ex);
//        }
//    }
}
