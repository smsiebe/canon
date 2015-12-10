package org.geoint.canon.stream.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.EventChannel;
import org.geoint.canon.stream.EventStream;

/**
 * Resolves events streams on the local file system.
 *
 * @author steve_siebert
 */
public class FileChannelProvider implements EventChannelProvider {

    public static final String SCHEME = "FILE";
    public static final String PROPERTY_STREAM_BASEDIR = "baseDir";
    private final static Logger LOGGER 
            = Logger.getLogger(FileChannelProvider.class.getName());

    public FileChannelProvider() {
    }

    @Override
    public boolean provides(String scheme, Map<String, String> channelProperties) {

    }

    @Override
    public EventChannel getChannel(String channelName,
            Map<String, String> channelProperties)
            throws UnableToResolveChannelException {

    }

    private Collection<EventStream> loadExisting() {

        //discover any existing streams
        Arrays.stream(baseDir.listFiles())
                .filter(File::isDirectory)
                .map((f) -> new File(f, f.getName() + ".uri")) //stream config as uri
                .filter(File::exists)
                .map((f) -> { //read URI string from file, or return null
                    try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                        return reader.readLine();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, String.format("Possible stream "
                                + "discovered during canon initialization but was "
                                + "unable to read stream uri from file '%s'",
                                f.getAbsolutePath()), ex);
                        return null;
                    }
                })
                .filter(Objects::nonNull) //filter out any errors to read URI
                .forEach((uri) -> {
                    try {
                        canon.getOrCreateStream(uri);
                    } catch (UnableToResolveChannelException ex) {
                        LOGGER.log(Level.SEVERE, String.format("Unable to add "
                                + "existing stream '%s' to canon instance during "
                                + "initialization", uri), ex);
                    }
                });
    }
}
