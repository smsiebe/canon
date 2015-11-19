package org.geoint.canon.impl.stream;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.stream.EventStream;

/**
 * Persistently manages a canon instances streams by storing stream
 * data/configuration in a data structure.
 *
 * The file structure, configuration files, and data formats contained managed
 * by this class is not defined by contract. As such, access to this information
 * must be made through this class rather than another implementation that
 * infers information from the directory meta or data contained within the
 * directory.
 *
 * @author steve_siebert
 */
/**
 * The file structure managed by this class is relative to the provided base
 * directory.
 *
 * Every stream configuration is stored by its unique name, leveraging the
 * directory structure to ensure uniqueness, in the following structure:
 * baseDir/streams/streamName.  
 * 
 * Stream properties/configuration is stored within each stream directory in a
 * file named '[streamName].xml'.
 *
 */
public class FileStreamManager extends AbstractStreamManager {

    private final File baseDir;

    private FileStreamManager(File baseDir, HierarchicalCodecResolver canonCodecs,
            Collection<EventStream> registeredStreams) {
        super(canonCodecs, registeredStreams);
        this.baseDir = baseDir;
    }

    public static FileStreamManager fromBase(HierarchicalCodecResolver canonCodecs,
            File baseDir) {

    }

    public String getInstanceId() {

    }

    @Override
    public EventStream createStream(String streamName,
            Map<String, String> streamProperties) {

    }

}
