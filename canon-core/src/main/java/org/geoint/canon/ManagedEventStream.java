package org.geoint.canon;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.impl.stream.LocalEventStream;
import org.geoint.canon.impl.stream.file.PartitionedFileEventStream;
import org.geoint.canon.stream.AppendOutOfSequenceException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.tx.EventTransaction;

/**
 *
 * @author steve_siebert
 */
public class ManagedEventStream implements EventStream {

    private final EventStream providerStream;
    private File offlineDir;
    private LocalEventStream offlineStream;

    private static final Logger LOGGER
            = Logger.getLogger(ManagedEventStream.class.getName());

    public ManagedEventStream(EventStream providerStream) {
        this.providerStream = providerStream;
    }

    public ManagedEventStream(EventStream providerStream, File offlineDir) {
        this.providerStream = providerStream;
        this.offlineDir = offlineDir;
    }

    public boolean isOfflineable() {
        return !(providerStream instanceof LocalEventStream)
                && offlineStream == null;
    }

    public void setOfflineDir(File offlineDir) {
        if (!isOfflineable()) {
            this.offlineDir = offlineDir;
            offlineStream = new PartitionedFileEventStream(providerStream.getName(),
                    offlineDir);
        }
    }

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
    public void addHandlerAtEventId(EventHandler handler, String eventId) {

    }

    @Override
    public void removeHandler(EventHandler handler) {

    }

    @Override
    public EventTransaction newTransaction() {

    }

    @Override
    public EventMessageBuilder newMessage(String eventType) throws StreamAppendException {

    }

    @Override
    public EventMessageBuilder newMessage(String eventType, String previousEventId) throws StreamAppendException, AppendOutOfSequenceException {

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
