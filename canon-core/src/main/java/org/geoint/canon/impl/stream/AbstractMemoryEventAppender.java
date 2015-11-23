package org.geoint.canon.impl.stream;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;
import org.geoint.canon.event.CommittedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.impl.tx.EventTransactionFactory;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.tx.EventTransactionException;
import org.geoint.canon.tx.TransactionCommitted;
import org.geoint.canon.tx.TransactionRolledBack;

/**
 * Appender which keeps the events to be committed on heap (in memory) until the
 * transaction is committed or rolled back.
 *
 * @author steve_siebert
 */
public class AbstractMemoryEventAppender extends AbstractEventAppender {

    protected final LinkedList<EventMessage> sequence;

    public AbstractMemoryEventAppender(LinkedList<EventMessage> sequence,
            EventStream stream) {
        super(stream);
        this.sequence = sequence;
    }

    public AbstractMemoryEventAppender(LinkedList<EventMessage> sequence,
            EventStream stream, EventTransactionFactory txFactory) {
        super(stream, txFactory);
        this.sequence = sequence;
    }

    @Override
    protected void addToTransaction(EventMessage event) {
        sequence.add(event);
    }

    @Override
    public Future<TransactionRolledBack> rollback() throws EventTransactionException {
        Future<TransactionRolledBack> results = super.rollback();
        this.sequence.clear();
        return results;
    }

    @Override
    public Future<TransactionCommitted> commit() throws EventTransactionException {
        Future<TransactionCommitted> results = super.commit();
        this.sequence.clear();
        return results;
    }

    @Override
    protected Collection<CommittedEventMessage> doCommit() throws StreamAppendException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doRollback() throws StreamAppendException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}