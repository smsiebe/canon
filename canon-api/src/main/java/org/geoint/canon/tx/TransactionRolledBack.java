package org.geoint.canon.tx;

import java.time.ZonedDateTime;

/**
 * Event created upon transaction rollback.
 *
 * @author steve_siebert
 */
public class TransactionRolledBack extends TransactionResult {

    public TransactionRolledBack(String streamName, String transactionId,
            ZonedDateTime rollbackTime) {
        super(streamName, transactionId, rollbackTime);
    }

    @Override
    public String toString() {
        return String.format("Transaction '%s' rolled backon stream %s.",
                getTransactionId(), getStreamName());
    }

}
