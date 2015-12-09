package org.geoint.canon.stream.file;

import org.geoint.canon.spi.stream.EventChannelProvider;

/**
 * Resolves events streams on the local file system.
 * 
 * @author steve_siebert
 */
public class FileChannelProvider implements EventChannelProvider{
    
    public static final String SCHEME = "FILE";
    public static final String PROPERTY_STREAM_BASEDIR = "baseDir";
    
}
