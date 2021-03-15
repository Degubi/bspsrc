package bsplib.struct;

import io.*;
import java.io.*;

/**
 * DLeaf structure variant used in the release version of Half-Life 2 only.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DLeafV0 extends DLeaf {

    public byte[] ambientLighting = new byte[24];

    @Override
    public int getSize() {
        return super.getSize() + ambientLighting.length + 2;
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        in.readBytes(ambientLighting);
        in.readShort(); // padding
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeBytes(ambientLighting);
        out.writeUnsignedShort(0); // padding
    }
}
