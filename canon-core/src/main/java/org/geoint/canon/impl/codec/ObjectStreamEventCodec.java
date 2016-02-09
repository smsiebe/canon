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
package org.geoint.canon.impl.codec;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.EventMessage;

/**
 * Uses Java object serialization to convert event objects which are marked with
 * the {@link Serializable} or {@link Externalizable} interfaces.
 * <p>
 * For events to make use of this codec the eventType must be the fully
 * qualified class name, and the class must be available on the classpath of the
 * calling thread.
 *
 * @see ObjectInputStream
 * @see ObjectOutputStream
 * @author steve_siebert
 */
public class ObjectStreamEventCodec implements EventCodec {

    private static final Logger LOGGER
            = Logger.getLogger(ObjectStreamEventCodec.class.getName());

    @Override
    public boolean isSupported(String eventType) {
        //for this to work the eventType must be a fully qualified class name, 
        //resolvable on this classpath
        try {
            Class<?> eventClass = Class.forName(eventType);
            return Serializable.class.isAssignableFrom(eventClass)
                    || Externalizable.class.isAssignableFrom(eventClass);
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.FINE, String.format("Event type '%s' is"
                    + " not serializable, cannot find class.", eventType), ex);
            return false;
        }
    }

    @Override
    public void encode(Object event, OutputStream out)
            throws IOException, EventCodecException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        try {
            oos.writeObject(event);
        } catch (NotSerializableException ex) {
            throw new EventCodecException("Cannot serializable event using "
                    + "java serialization.", ex);
        }
    }

    @Override
    public Object decode(EventMessage e) throws IOException, EventCodecException {
        ObjectInputStream oin = new ObjectInputStream(e.getEventContent());
        try {
            return oin.readObject();
        } catch (ClassNotFoundException ex) {
            throw new EventCodecException(String.format("Unable to decode "
                    + "content of '%s' event.", e.getEventType()), ex);
        }
    }

}
