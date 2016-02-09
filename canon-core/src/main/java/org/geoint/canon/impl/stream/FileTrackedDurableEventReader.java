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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.stream.DurableEventReader;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.StreamReadException;

/**
 * DurableEventReader implementation that uses a file-per-stream to persistently
 * keep track of reader positioning.
 *
 * @author steve_siebert
 */
public class FileTrackedDurableEventReader extends DurableEventReader {

    /*
     * Since the underlying reader need only implement the EventReader 
     * interface, which advances its position on read, the underlying 
     * readers position is 
     */
    private final AtomicReference<AppendedEventMessage> currentEvent
            = new AtomicReference<>();
    private final File trackingFile;
    private final RandomAccessFile trackingFileRAF;
    private static final String TRACKING_FILE_MODE = "rwd";

    //magic bytes used to uniquely identify file as a canon reader track file
    private static final byte[] MAGIC_BYTES
            = "CANON.RTF".getBytes(StandardCharsets.US_ASCII);
    private static final byte CURRENT_VERSION = 0;
    private static final int HEADER_LENGTH = MAGIC_BYTES.length + 1;
    private static final byte[] VALID_HEADER = Arrays.copyOf(MAGIC_BYTES, HEADER_LENGTH); //current version is 0, which is filled by Arrays.copyOf
    private static final Logger LOGGER
            = Logger.getLogger(FileTrackedDurableEventReader.class.getName());

    private FileTrackedDurableEventReader(String readerId,
            EventReader reader,
            File trackingFile,
            RandomAccessFile raf) {
        super(readerId, reader);
        this.trackingFile = trackingFile;
        this.trackingFileRAF = raf;
    }

    /**
     * Persistently track the readers position in the provided file.
     * <p>
     * This factory method makes no assumptions about the file naming
     * conventions but does assume the content of the file is in the format
     * known by this file. This format is not considered a part of the public
     * interface and its content should be accessed only by this class.
     *
     * @param readerId stream-unique reader identifier
     * @param reader reader to use
     * @param trackingFile backing track file
     * @return durable reader
     * @throws StreamReadException thrown if there is a problem initializing the
     * durable reader
     */
    public static FileTrackedDurableEventReader trackFile(String readerId,
            EventReader reader, File trackingFile) throws StreamReadException {

        if (!trackingFile.exists()) {
            try {
                trackingFile.getParentFile().mkdirs();
                trackingFile.createNewFile();
            } catch (IOException ex) {
                throw new StreamReadException(reader.getChannelName(),
                        reader.getStreamName(), String.format("Unable to create "
                                + "the backing file for durable event stream"
                                + " reader '%s'", readerId), ex);
            }
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(trackingFile, TRACKING_FILE_MODE);

            if (raf.length() > 0) {
                //if the track file contains data, first check if the file is a 
                //valid tracking file 
                byte[] header = new byte[HEADER_LENGTH];
                try {
                    raf.readFully(header);
                } catch (IOException ex) {
                    throw new IOException("File is not a valid durable reader"
                            + "tracking file.");
                }
                //validate header - since there is only one file format version
                //this is done rather simplisticly but, if in the future 
                //there is need to create a new tracking file version, the 
                //version can be incremented and the reader/writer switched
                //on the version value
                if (!Arrays.equals(VALID_HEADER, header)) {
                    throw new IOException("Invalid file header, file is not "
                            + "a durable tracking file.");
                }

                //now extract the position information from the file (if 
                //exists) and set the readers current position
                try {
                    String position = raf.readUTF();
                    reader.setPosition(position);
                } catch (EOFException ex) {
                    //no position set, use the current position of the reader
                    String position = reader.getPosition();
                    if (position != null && !position.isEmpty()) {
                        raf.writeUTF(position);
                    }
                }
            } else {
                //track file contains no data, set the header and the 
                //current position of the reader
                String position = reader.getPosition();
                if (position != null && !position.isEmpty()) {
                    raf.writeUTF(position);
                }
            }

            return new FileTrackedDurableEventReader(readerId, reader,
                    trackingFile, raf);

        } catch (IOException | UnknownEventException ex) {
            //we know we won't get here, because we ensure the tracking files 
            //existence before we get here
            throw new StreamReadException(reader.getChannelName(),
                    reader.getStreamName(), "Unable to initialize file-backed "
                    + "durable event reader.", ex);
        }

    }

    /**
     * Persistently track the readers position using a track file contained
     * within the track directory managed by this class.
     * <p>
     * This factory method assumes that the track directory, file naming, and
     * file content is managed by this class. The way this class manages the
     * tracking directory is not to be considered static/part of the public
     * interface - access should always be done through this class.
     *
     * @param readerId stream-unique reader identifier
     * @param reader reader to use
     * @param trackDirectory tracking directory containing track files
     * @return durable reader
     * @throws StreamReadException thrown if there is a problem initializing the
     * durable reader
     */
    public static FileTrackedDurableEventReader trackDirectory(
            String readerId, EventReader reader, File trackDirectory)
            throws StreamReadException {

        File trackFile = new File(trackDirectory, String.join(File.separator,
                reader.getChannelName(),
                reader.getStreamName(),
                readerId));

        return trackFile(readerId, reader, trackFile);
    }

    @Override
    public void deleteReader() throws IOException {
        this.close();
        this.trackingFile.delete();
    }

    @Override
    protected String readPosition() throws IOException {
        trackingFileRAF.seek(HEADER_LENGTH);
        return trackingFileRAF.readUTF();
    }

    @Override
    protected void writePosition(String position) throws IOException {
        trackingFileRAF.seek(HEADER_LENGTH);
        trackingFileRAF.writeUTF(position);
    }

    @Override
    public void close() throws IOException {
        super.close();
        trackingFileRAF.close();
    }

}
