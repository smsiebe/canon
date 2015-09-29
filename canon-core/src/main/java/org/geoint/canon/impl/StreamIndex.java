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
package org.geoint.canon.impl;

import java.io.Closeable;
import java.io.Flushable;

/**
 * Implementation interface - not for public use.
 * <p>
 * Low-level access to an event stream index.
 *
 * @author steve_siebert
 */
public interface StreamIndex extends Closeable, AutoCloseable, Flushable {

    /**
     * Return the first event id of the stream, or null if there isn't an event
     * in the stream.
     *
     * @return first event id or null
     */
    default String getFirstId() {
        return getNextId(null);
    }

    /**
     * Returns the next event ID, or null if there isn't a next id.
     *
     * @param eventId event id, or null to return the first index id
     * @return
     */
    String getNextId(String eventId);

    /**
     * Returns the last event ID of the stream.
     *
     * @return last event id of the stream, or null if no events exist in the
     * stream
     */
    String getLastId();

    /**
     * Check if the stream index contains the specified event, by event ID.
     *
     * @param eventId
     * @return true if the stream contains the event, otherwise false
     */
    boolean eventExists(String eventId);

}
