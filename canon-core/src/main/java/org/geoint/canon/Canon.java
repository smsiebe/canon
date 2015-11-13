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
package org.geoint.canon;

import java.util.Collection;
import java.util.Map;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.replication.EventMediator;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;
import org.geoint.canon.stream.StreamReadException;
import org.geoint.canon.stream.EventHandler;

/**
 * A Canon event database instance.
 *
 * @author steve_siebert
 */
public class Canon {

    private final EventStream adminStream;
    private final String instanceId;
    private final Map<String, EventStream> streams;

    /**
     * Return the "administrative" event stream for this event store.
     * <p>
     * The administrative stream contains only those events that are related to
     * the management of the channel or the events on the channel. This includes
     * management of streams, handlers, mediators, collisions, reconciliation,
     * etc.
     * <p>
     * The administrative stream cannot be directly written to by applications
     * but my be observed either directly from read/handlers or indirectly
     * through registration of {@link EventMediator mediators} or
     * {@link EventHandler handlers}.
     *
     * @return
     */
    public EventStream getAdminStream() {
        return adminStream;
    }

    /**
     * Return all the streams registered to this canon instance.
     *
     * @return stream registered to this canon instance
     */
    public Collection<EventStream> getStreams() {

    }

    /**
     * Returns the named event stream.
     * <p>
     * If the event stream does not yet exist the returned stream will be a
     * proxy that will receive events if/when the stream is created.
     *
     * @param streamName
     * @return readable named event stream
     */
    public EventStream getStream(String streamName) {

    }

    /**
     * Register a new event stream with the canon instance.
     *
     * @param stream stream to register
     * @return managed event stream
     * @throws StreamAlreadyExistsException if a stream by this name is already
     * registered to this canon instance
     */
    public void registerStream(EventStream stream)
            throws StreamAlreadyExistsException {
        synchronized (streams) {
            if (streams.containsKey(stream.getName())) {
                throw new StreamAlreadyExistsException(stream.getName());
            }
            streams.put(stream.getName(), stream);
        }
    }

    public EventStream registerStream(String streamUrl)
            throws StreamAlreadyExistsException, StreamReadException {
        
    }

    /**
     * Set the default event codec for the specified event type/object type.
     *
     * @param codec
     */
    public void useCodec(EventCodec<?> codec) {

    }

}
