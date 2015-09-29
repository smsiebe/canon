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
package org.geoint.canon.publish;

import java.io.InputStream;
import java.util.Map;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;

/**
 * Appends events to the channel.
 * <p>
 * Although a transaction may be implicitly started, it must be explicitly
 * committed to publish events to the channel:
 *
 * {@code
 *  EventPublisher pub = ....
 *  pub.append(...); //transaction implictly created
 *  pub.getTransaction().commit(); //must explictly commit transaction
 * }
 *
 * <p>
 * Unless otherwise noted by specific implementations event publishers should be
 * considered not thread-safe.
 *
 * @author Steve Siebert <steve@t-3-solutions.com>
 */
public interface EventPublisher {

    /**
     * Returns the contextual transaction, which may already have been created,
     * may already have been started, or may be a new transaction.
     *
     * @return contextual transaction
     */
    EventTransaction getTransaction();

    /**
     * Appends an event to the current transaction.
     * <p>
     * A new transaction will be started, and is accessible from 
     * {@link #getTransaction() }, if not already started.
     *
     * @param eventType
     * @param event
     * @return this publisher (fluid interface)
     * @throws EventCodecException
     * @throws EventPublicationException
     */
    EventPublisher append(String eventType, Object event)
            throws EventCodecException, EventPublicationException;

    /**
     * Appends an event to the current transaction.
     * <p>
     * A new transaction will be started, and is accessible from 
     * {@link #getTransaction() }, if not already started.
     *
     * @param <T>
     * @param event
     * @param codec
     * @return this publisher (fluid interface)
     * @throws EventCodecException
     * @throws EventPublicationException
     */
    <T> EventPublisher append(T event, EventCodec<T> codec)
            throws EventCodecException, EventPublicationException;

    /**
     * Appends an event to the current transaction.
     * <p>
     * A new transaction will be started, and is accessible from 
     * {@link #getTransaction() }, if not already started.
     *
     * @param eventType
     * @param headers
     * @param event
     * @return this publisher (fluid interface)
     */
    EventPublisher append(String eventType, Map<String, String> headers,
            InputStream event);

    /**
     * Returns a new, non thread-safe, event message builder which will be
     * appended to the event transaction in the order it was created.
     *
     * @param eventType
     * @return message builder
     */
    MessageBuilder builder(String eventType);

}
