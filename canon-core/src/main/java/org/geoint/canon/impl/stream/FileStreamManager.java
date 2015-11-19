package org.geoint.canon.impl.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.impl.stream.file.FileStreamProvider;
import org.geoint.canon.spi.stream.EventStreamProvider;
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
 * baseDir/streamName.
 *
 * Stream properties/configuration is stored within each stream directory in a
 * file named '[streamName].xml'.
 *
 */
public class FileStreamManager extends AbstractStreamManager {

    private final File baseDir;
    private final Map<String, String> DEFAULT_STREAM_PROPERTIES;
    private final EventStreamProvider DEFAULT_STREAM_PROVIDER
            = new FileStreamProvider();

    private static final Logger LOGGER
            = Logger.getLogger(FileStreamManager.class.getName());

    private FileStreamManager(File baseDir, CodecResolver canonCodecs,
            Collection<EventStream> registeredStreams) {
        super(canonCodecs, registeredStreams);
        this.baseDir = baseDir;
        DEFAULT_STREAM_PROPERTIES = new HashMap<>(1);
        DEFAULT_STREAM_PROPERTIES.put(FileStreamProvider.PROPERTY_STREAM_BASEDIR,
                baseDir.getAbsolutePath());
    }

    public static FileStreamManager fromBase(CodecResolver canonCodecs,
            File baseDir) {

        Collection<EventStream> streams = new ArrayList<>();

        //discover any streams already configured
        Arrays.stream(baseDir.listFiles())
                .filter(File::isDirectory)
                .map((f) -> new File(f, f.getName() + ".uri")) //stream config as uri
                .filter(File::exists)
                .map((f) -> { //read URI string from file, or return null
                    try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                        return reader.readLine();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, String.format("Problems reading "
                                + "stream uri from file '%s'", f.getAbsolutePath()), 
                                ex);
                        return null;
                    }
                })
                .filter(Objects::nonNull) //filter out any errors to read URI
                .map(URI::create)
                
    }

    @Override
    protected Map<String, String> getDefaultStreamProperties() {
        return DEFAULT_STREAM_PROPERTIES;
    }

    @Override
    protected EventStreamProvider getDefaultStreamProvider() {
        return DEFAULT_STREAM_PROVIDER;
    }

}
