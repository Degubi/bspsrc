package bsplib.struct;

import bsplib.io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV8 extends DStaticPropV5 {

    public byte minCPULevel;
    public byte maxCPULevel;
    public byte minGPULevel;
    public byte maxGPULevel;
    public Color32 diffuseModulation;

    @Override
    public int getSize() {
        return super.getSize() + 8; // 68
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        minCPULevel = in.readByte();
        maxCPULevel = in.readByte();
        minGPULevel = in.readByte();
        maxGPULevel = in.readByte();
        diffuseModulation = new Color32(in.readInt());
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeByte(minCPULevel);
        out.writeByte(maxCPULevel);
        out.writeByte(minGPULevel);
        out.writeByte(maxGPULevel);
        out.writeInt(diffuseModulation.rgba);
    }
}
