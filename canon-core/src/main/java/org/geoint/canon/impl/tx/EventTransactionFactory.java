package org.geoint.canon.impl.tx;

import org.geoint.canon.tx.EventTransaction;

/**
 * Factory used to create new event transactions.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface EventTransactionFactory {

    /**
     * Creates a new transaction.
     *
     * @return new event transaction
     */
    EventTransaction newTransaction();
}
