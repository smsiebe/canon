package org.geoint.canon.impl.stream;

import java.util.Collections;
import java.util.Map;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.stream.memory.MemoryEventStream;
import org.geoint.canon.stream.EventStream;

/**
 * In-memory/volatile/transient manager of streams.
 *
 * @author steve_siebert
 */
public class MemoryStreamManager extends AbstractStreamManager {

    public MemoryStreamManager(HierarchicalCodecResolver canonCodecs) {
        super(canonCodecs, Collections.EMPTY_LIST);
    }

    @Override
    public EventStream createStream(String streamName,
            Map<String, String> streamProperties){
        return new MemoryEventStream(streamName);
    }

}
