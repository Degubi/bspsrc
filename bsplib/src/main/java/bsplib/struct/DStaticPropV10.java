package bsplib.struct;

import bsplib.io.*;
import bsplib.util.*;
import java.io.*;

/**
 * Newer V10 structure found in Source 2013.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV10 extends DStaticPropV6 {

    public int lightmapResolutionX;
    public int lightmapResolutionY;

    @Override
    public int getSize() {
        return super.getSize() + 8; // 72
    }

    @Override
    public void read(DataReader in) throws IOException {
        super.read(in);
        flags = EnumConverter.fromInteger(StaticPropFlag.class, in.readInt());
        lightmapResolutionX = in.readUnsignedShort();
        lightmapResolutionY = in.readUnsignedShort();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeInt(EnumConverter.toInteger(flags));
        out.writeUnsignedShort(lightmapResolutionX);
        out.writeUnsignedShort(lightmapResolutionY);
    }
}
