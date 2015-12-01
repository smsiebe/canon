package org.geoint.canon.impl.stream;

import org.geoint.canon.stream.EventAppenderDecorator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.concurrent.FutureWatcher;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventsAppended;
import org.geoint.canon.stream.StreamAppendException;

/**
 * Implements common event stream methods requiring event stream implementations
 * extending from this class to implement only a subset of methods.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventStream implements EventStream {

    protected final String streamName;
    protected final HierarchicalCodecResolver streamCodecs;
    protected final Map<String, String> streamProperties;

    private static final Logger LOGGER
            = Logger.getLogger(EventStream.class.getPackage().getName());

    /**
     *
     * @param streamName
     * @param streamProperties
     * @param codecs
     */
    public AbstractEventStream(String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs) {
        this.streamName = streamName;
        this.streamProperties = streamProperties;
        this.streamCodecs = new HierarchicalCodecResolver(codecs);

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
    public EventAppender getAppender() {
        /*
         * decorates the stream with a TrackingEventAppender to keep the 
         * lasteventid accurate
         */
        return new TrackingEventAppender(newAppender());
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


    /**
     * Returns a new stream appender.
     * <p>
     * The appender needs to ensure ACID compliant transactions but need not
     * worry about inter-transaction sequence integrity, which is managed by the
     * appender decorators used by this implementation.
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
        public Future<EventsAppended> append()
                throws StreamAppendException {
            Future<EventsAppended> result = super.append();
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

        @Override
        public Future<EventsAppended> commit() 
                throws EventTransactionException {

            
            super.commit();
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
