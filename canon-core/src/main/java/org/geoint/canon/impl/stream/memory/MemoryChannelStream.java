package org.geoint.canon.impl.stream.memory;

import java.util.Map;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.impl.stream.AbstractEventStream;

/**
 *
 * @author steve_siebert
 */
public class MemoryChannelStream extends AbstractEventStream {

    public MemoryChannelStream(String streamName) {
        super(streamName);
    }

    MemoryChannelStream(String streamName, Map<String, String> streamProperties, CodecResolver codecs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
