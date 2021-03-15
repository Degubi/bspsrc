package bspsrc.modules;

import bsplib.*;
import bsplib.lump.*;
import java.io.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import org.apache.commons.io.output.*;

/**
 * BSP checksum calculator based on Source's server map CRC check.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspChecksum extends ModuleRead {

    public BspChecksum(BspFileReader reader) {
        super(reader);
    }

    public long getMapCRC() throws IOException {
        CRC32 crc = new CRC32();

        // CRC across all lumps except for the Entities lump
        for (Lump lump : bspFile.getLumps()) {
            if (lump.getType() == LumpType.LUMP_ENTITIES) {
                continue;
            }

            try (InputStream in = new CheckedInputStream(lump.getInputStream(), crc)) {
                // copy to /dev/null, we need the checksum only
                IOUtils.copy(in, new NullOutputStream());
            }
        }

        return crc.getValue();
    }

    public long getFileCRC() throws IOException {
        return FileUtils.checksumCRC32(bspFile.getFile().toFile());
    }
}
