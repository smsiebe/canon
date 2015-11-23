package org.geoint.canon.impl.tx;

import java.util.Collection;
import org.geoint.canon.impl.concurrent.FallibleExecutable;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.geoint.canon.event.CommittedEventMessage;
import org.geoint.canon.tx.EventTransaction;

/**
 * Default event transaction implementation.
 *
 * @author steve_siebert
 */
public class DefaultEventTransaction implements EventTransaction {

    private final String txId;
    private final Callable<Collection<CommittedEventMessage>> onCommit;
    private final FallibleExecutable onRollback;

    public DefaultEventTransaction(Callable<Collection<CommittedEventMessage>> onCommit,
            FallibleExecutable onRollback) {
        this.txId = UUID.randomUUID().toString();
        this.onCommit = onCommit;
        this.onRollback = onRollback;
    }

}
