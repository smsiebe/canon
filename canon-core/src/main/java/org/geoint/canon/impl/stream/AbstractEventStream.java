package org.geoint.canon.impl.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.stream.file.FileStreamProvider;
import org.geoint.canon.spi.stream.EventStreamProvider;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.EventHandler;

/**
 * Implements common event stream methods requiring event stream implementations
 * extending from this class to implement only a subset of methods.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventStream implements EventStream {

    private final String streamName;
    private final HierarchicalCodecResolver streamCodecs;
    private final Map<String, String> streamProperties;
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

    }
    
    @Override
    public EventAppender appendToEnd() {

    }

    @Override
    public EventAppender appendAfter(String previousEventId) {

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

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

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
    
    protected final void createOffline() {

    }

    private String getOfflineStreamName() {
        return String.format("org.canon.offline.%s" + streamName);
    }

    private static EventStreamProvider getDefaultOfflineStreamProvider() {
        return new FileStreamProvider();
    }

    private void recursiveDeleteDirectory(File directory) throws IOException {
        Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

        });
    }


}
