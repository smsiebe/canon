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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.event.EventAppended;

/**
 * Appender which stores the events appended against it on heap memory until
 * {@link MemoryEventAppender#append append} is called.
 *
 * @author steve_siebert
 */
public class MemoryEventAppender extends AbstractEventAppender {

    private final List<EventMessage> appenderMessages;
    private final EventsAppender doAppend;

    /**
     *
     * @param channelName event channel name
     * @param streamName stream name
     * @param codecs event codecs
     * @param onAppend callback that appends events to the stream
     */
    public MemoryEventAppender(String channelName, String streamName,
            CodecResolver codecs,
            EventsAppender onAppend) {
        super(channelName, streamName, codecs);
        appenderMessages = Collections.synchronizedList(new ArrayList<>());
        this.doAppend = onAppend;
    }

    @Override
    public EventAppender add(EventMessage message)
            throws StreamAppendException {
        appenderMessages.add(message);
        return this;
    }

    @Override
    public EventAppended append() throws StreamAppendException {
        return doAppend.append(appenderMessages);
    }

}
