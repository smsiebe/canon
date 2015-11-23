package org.geoint.canon.tx;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author steve_siebert
 */
public class TransactionResult {

    private final String streamName;
    private final String transactionId;
    private final ZonedDateTime commitTime;

    public TransactionResult(String streamName, String transactionId,
            ZonedDateTime commitTime) {
        this.streamName = streamName;
        this.transactionId = transactionId;
        this.commitTime = commitTime;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public ZonedDateTime getCommitTime() {
        return commitTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.streamName);
        hash = 23 * hash + Objects.hashCode(this.transactionId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransactionResult other = (TransactionResult) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        if (!Objects.equals(this.transactionId, other.transactionId)) {
            return false;
        }
        return true;
    }

}
