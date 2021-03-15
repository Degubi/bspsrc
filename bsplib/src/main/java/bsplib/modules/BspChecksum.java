package bsplib.modules;

import bsplib.*;
import bsplib.lump.*;
import java.io.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import org.apache.commons.io.output.*;

public class BspChecksum extends ModuleRead {

    public BspChecksum(BspFileReader reader) {
        super(reader);
    }

    public long getMapCRC() throws IOException {
        CRC32 crc = new CRC32();

        for (Lump lump : bspFile.getLumps()) {
            if (lump.getType() == LumpType.LUMP_ENTITIES) {
                continue;
            }

            try (InputStream in = new CheckedInputStream(lump.getInputStream(), crc)) {
                IOUtils.copy(in, new NullOutputStream());
            }
        }

        return crc.getValue();
    }

    public long getFileCRC() throws IOException {
        return FileUtils.checksumCRC32(bspFile.getFile().toFile());
    }
}