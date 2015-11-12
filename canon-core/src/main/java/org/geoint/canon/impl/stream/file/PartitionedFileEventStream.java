package org.geoint.canon.impl.stream.file;

import java.io.File;
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
    public EventReader getReader() {
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

}
