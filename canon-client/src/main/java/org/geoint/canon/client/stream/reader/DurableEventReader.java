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
package org.geoint.canon.client.stream.reader;

import org.geoint.canon.EventMessage;
import org.geoint.canon.StreamReadException;
import org.geoint.canon.stream.reader.EventReader;

/**
 * An event reader that keeps track of the events which have been successfully
 * read/processed.
 *
 * @author steve_siebert
 * @param <E>
 */
public interface DurableEventReader<E extends EventMessage>
        extends EventReader<E> {

    /**
     * Tells the durable reader that the current event ID has been successfully
     * read.
     *
     * @throws StreamReadException
     */
    void ok() throws StreamReadException;

    /**
     * Tells the durable reader to skip the current event.
     *
     * @throws StreamReadException
     */
    void skip() throws StreamReadException;

}
