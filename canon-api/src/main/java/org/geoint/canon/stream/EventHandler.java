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
package org.geoint.canon.stream;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.event.AppendedEventMessage;

/**
 * Event callback interface notified as events are appended to, or a relevant
 * reconciliation related event is published for, the stream the handler is
 * registered.
 * <p>
 * The EventHandler is the most basic of the callbacks and potentially be be
 * notified of significant number of events.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface EventHandler {

    static final Logger LOGGER
            = Logger.getLogger(EventHandler.class.getName());

    /**
     * Next sequential event on the stream.
     *
     * @param event next event to handle
     * @throws Exception thrown if there is a problem processing the event
     */
    void handle(AppendedEventMessage event) throws Exception;

    /**
     * Called when the handler is unable to process an event.
     *
     * @param event
     * @param ex
     * @return
     */
    default EventHandlerAction onFailure(AppendedEventMessage event,
            Exception ex) {
        LOGGER.log(Level.WARNING,
                String.format("Unable to handle event %s of type %s,"
                        + " removing handler.",
                        event.getSequence(),
                        event.getEventType()), ex);
        return EventHandlerAction.FAIL;
    }
}
