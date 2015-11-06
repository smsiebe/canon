/*
 * Copyright 2015 geoint.org.
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
package org.geoint.canon.stream.handler;

import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.event.StreamRolledBack;

/**
 * Event callback interface notified as events are appended to, or a relevant
 * reconciliation related event is published for, the stream the handler is
 * registered.
 * <p>
 * The EventHandler is the most basic of the callbacks and potentially be be
 * notified of significant number of events including domain and
 * channel/administrative. Applications are encouraged to make use of specific
 * handler interfaces, such as the the {@link TypeEventObserver} or
 * {@link StreamSanitizer} to limit the amount of logic contained in each
 * handler.
 *
 * @author steve_siebert
 * @param <E>
 */
public interface EventHandler<E extends EventMessage> {

    /**
     * Next sequential event on the stream.
     *
     * @param event next event to handle
     */
    void handle(E event);

    /**
     * Called when a stream/channel must be rolled back to the specified
     * position.
     *
     * @param event sanitized event details.
     */
    void rollback(StreamRolledBack event);
}
