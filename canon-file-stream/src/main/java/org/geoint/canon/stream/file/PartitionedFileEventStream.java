package org.geoint.canon.stream.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.impl.stream.LocalEventStream;
import org.geoint.canon.stream.AppendOutOfSequenceException;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.tx.EventTransaction;

/**
 *
 * @author steve_siebert
 */
public class PartitionedFileEventStream extends LocalEventStream {

    private final String streamName;
    private final File baseDir;

    public PartitionedFileEventStream(String streamName, File baseDir) {
        this.streamName = streamName;
        this.baseDir = baseDir;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventReader newReader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addHandler(EventHandler handler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addHandlerAtEventId(EventHandler handler, String eventId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeHandler(EventHandler handler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventTransaction newTransaction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventMessageBuilder newMessage(String eventType) throws StreamAppendException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventMessageBuilder newMessage(String eventType, String previousEventId) throws StreamAppendException, AppendOutOfSequenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLastEventId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void useCodec(EventCodec codec) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
