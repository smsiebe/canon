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
package org.geoint.canon.codec;

import org.geoint.canon.EventException;

/**
 * Thrown if there is a problem encoding an event.
 *
 * @author steve_siebert
 */
public class EventCodecException extends EventException {

    private final Class<?> eventClass;
    private final String eventType;
    private final Class<? extends EventCodec> codec;

    public EventCodecException(Class<?> eventClass, String eventType,
            Class<? extends EventCodec> codec, String message) {
        super(message(eventClass, eventType, codec, message));
        this.eventClass = eventClass;
        this.eventType = eventType;
        this.codec = codec;
    }

    public EventCodecException(Class<?> eventClass, String eventType,
            Class<? extends EventCodec> codec, String message, Throwable cause) {
        super(message(eventClass, eventType, codec, message), cause);
        this.eventClass = eventClass;
        this.eventType = eventType;
        this.codec = codec;
    }

    private static String message(Class<?> eventClass, String eventType,
            Class<? extends EventCodec> codec, String message) {
        return String.format("Unable to encode domain event type '%s' from "
                + "instance of '%s' using codec '%s'. %s",
                eventType, eventClass.getCanonicalName(),
                codec.getCanonicalName(), (message != null) ? message : "");
    }

    public Class<?> getEventClass() {
        return eventClass;
    }

    public String getEventType() {
        return eventType;
    }

    public Class<? extends EventCodec> getCodec() {
        return codec;
    }

}
