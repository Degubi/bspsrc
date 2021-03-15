package bsplib.struct;

import io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DOverlaySystemLevel implements DStruct {

    public int minCPULevel;
    public int maxCPULevel;
    public int minGPULevel;
    public int maxGPULevel;

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public void read(DataReader in) throws IOException {
        minCPULevel = in.readUnsignedByte();
        maxCPULevel = in.readUnsignedByte();
        minGPULevel = in.readUnsignedByte();
        maxGPULevel = in.readUnsignedByte();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeUnsignedByte(minCPULevel);
        out.writeUnsignedByte(maxCPULevel);
        out.writeUnsignedByte(minGPULevel);
        out.writeUnsignedByte(maxGPULevel);
    }

}
