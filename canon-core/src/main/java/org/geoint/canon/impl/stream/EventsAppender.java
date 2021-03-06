/*
 * Copyright 2016 geoint.org.
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
package org.geoint.canon.impl.stream;

import java.util.List;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.event.EventAppended;

/**
 * Used by {@link MemoryEventAppender} as a callback function when executing the
 * append function.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface EventsAppender {

    /**
     * Appends a collection of events to the event stream.
     *
     * @param events events to append to the stream
     * @return results of the successful append operation
     * @throws StreamAppendException thrown if the events failed to be appended
     * to the event stream
     */
    EventAppended append(List<EventMessage> events)
            throws StreamAppendException;
}
