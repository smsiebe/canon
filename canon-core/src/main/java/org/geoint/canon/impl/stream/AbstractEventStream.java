package org.geoint.canon.impl.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.stream.file.PartitionedFileEventStream;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventReader;

/**
 * Implements common event stream methods requiring event stream implementations
 * extending from this class to implement only a subset of methods.
 *
 * @author steve_siebert
 */
public class AbstractEventStream implements EventStream {

    private final String streamName;
    private final HierarchicalCodecResolver streamCodecs;
    private EventStream offlineStream;

    private static final Logger LOGGER
            = Logger.getLogger(AbstractEventStream.class.getName());

    public AbstractEventStream(String streamName, CodecResolver codecs) {
        this(streamName, codecs, null);
    }

    public AbstractEventStream(String streamName, CodecResolver codecs,
            EventStream offlineStream) {
        this.streamName = streamName;
        this.streamCodecs = new HierarchicalCodecResolver(codecs);
        this.offlineStream = offlineStream;
    }

    @Override
    public boolean isOfflineable() {
        return offlineStream != null;
    }

    @Override
    public void setOffline(boolean offline) {
        if (!isOfflineable()) {
            this.offlineDir = offlineDir;
            offlineStream = new PartitionedFileEventStream(providerStream.getName(),
                    offlineDir);
        }
    }

    @Override
    public void deleteOfflineCopy() {
        if (offlineStream == null) {
            LOGGER.log(Level.FINE, String.format("Offline copy of stream '%s' "
                    + "could not be found", providerStream.getName()));
        }

        LOGGER.log(Level.INFO, String.format("Deleting offline copy of stream"
                + " '%s'", providerStream.getName()));

        final LocalEventStream oldOfflineStream = offlineStream;
        offlineStream = null;

        assert offlineDir != null : "Offline stream is set but offline dir is unknown";

        final File oldOfflineDir = offlineDir;
        offlineDir = null;

        if (oldOfflineStream.isArchiving()) {
            oldOfflineStream.deleteArchive();
        }
        try {
            oldOfflineStream.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, String.format("Offline copy of stream "
                    + "'%s' located at '%s' was not closed gracefully.",
                    providerStream.getName(),
                    oldOfflineDir.getAbsolutePath()));
        }

        try {
            recursiveDeleteDirectory(oldOfflineDir);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Problems deleting offline "
                    + "copy of stream '%s' located at '%s', copy of stream "
                    + "may still exist on filesystem.",
                    providerStream.getName(),
                    oldOfflineDir.getAbsolutePath()
            ), ex);
        }
    }

    @Override
    public String getName() {
        return providerStream.getName();
    }

    @Override
    public EventReader getReader() {
        return new ManagedStreamEventReader();
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
    public EventAppender appendToEnd() {

    }

    @Override
    public EventAppender appendAfter(String previousEventId) {

    }

    @Override
    public String getLastEventId() {

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
