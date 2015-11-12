/*
 * Copyright 2015 Steve Siebert <steve@t-3-solutions.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.canon.tx;

import java.util.concurrent.Future;
import org.geoint.canon.stream.AppendOutOfSequenceException;

/**
 * ACID compliant transaction used to commit multiple events in a single commit.
 * <p>
 * Unless specified by the specific transaction implementation, event
 * transactions should be considered not thread-safe.
 *
 * @author Steve Siebert <steve@t-3-solutions.com>
 */
public interface EventTransaction {

    /**
     * Return the unique transaction id.
     *
     * @return transaction id
     */
    String getId();

    /**
     * Determine if the transaction is active.
     *
     * @return true if the transaction is active, otherwise false
     */
    boolean isActive();

    /**
     * Cancels the transaction, rolling back any changes that may have taken
     * place.
     *
     * @throws EventTransactionException thrown if there was a problem appending
     * events to the stream
     */
    void rollback() throws EventTransactionException;

    /**
     * Commits all the events included in this transaction to the end of the
     * event stream.
     * <p>
     * This method, or the future, may throw an EventPublicationException if the
     * transaction failed (depending on the cause of the failure). If the
     * transaction failed no events in the transaction will be published.
     *
     * @return future commit results
     * @throws EventTransactionException thrown if there was a problem appending
     * events to the stream
     */
    Future<TransactionCommitted> commit() throws EventTransactionException;

    /**
     * Commits all the events included in this transaction after the specified
     * event in the stream.
     *
     *
     * @param previousEventId
     * @return commit results
     * @throws EventTransactionException thrown if there was a problem appending
     * events to the stream
     * @throws AppendOutOfSequenceException thrown if the transaction could not
     * be appended because the previous event id is not the last event id of the
     * stream
     */
    Future<TransactionCommitted> commit(String previousEventId)
            throws EventTransactionException, AppendOutOfSequenceException;
}