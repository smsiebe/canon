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
package org.geoint.canon.event;

/**
 * Thrown when an unknown event was requested.
 *
 * @author steve_siebert
 */
public class UnknownEventException extends EventException {

    private final String eventId;

    public UnknownEventException(String eventId) {
        super(message(eventId));
        this.eventId = eventId;
    }

    public UnknownEventException(String eventId, Throwable cause) {
        super(message(eventId), cause);
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    private static String message(String eventId) {
        return String.format("Unknown event '%s'", eventId);
    }

}
