package bsplib.struct;

import bsplib.io.*;
import bsplib.io.Seekable.*;
import bsplib.util.*;
import bsplib.vector.*;
import java.io.*;

/**
 * DStaticProp V9 variant for Dear Esther
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DStaticPropV9DE extends DStaticPropV8 {

    @Override
    public int getSize() {
        return super.getSize() + 8; // 76
    }

    @Override
    public void read(DataReader in) throws IOException {
        origin = Vector3f.read(in);
        angles = Vector3f.read(in);
        propType = in.readUnsignedShort();
        firstLeaf = in.readUnsignedShort();
        leafCount = in.readUnsignedShort();
        solid = in.readUnsignedByte();
        flags = EnumConverter.fromInteger(StaticPropFlag.class, in.readUnsignedByte());
        in.seek(4, Origin.CURRENT);
        skin = in.readInt();
        fademin = in.readFloat();
        fademax = in.readFloat();
//        lightingOrigin = lio.readVector3f();
        in.seek(12, Origin.CURRENT); // invalid lighting origin vector?
        forcedFadeScale = in.readFloat();
        minCPULevel = in.readByte();
        maxCPULevel = in.readByte();
        minGPULevel = in.readByte();
        maxGPULevel = in.readByte();
        in.seek(1, Origin.CURRENT);
        diffuseModulation = new Color32(in.readInt());
        in.seek(3, Origin.CURRENT);
    }

    @Override
    public boolean usesLightingOrigin() {
        // workaround for the invalid lighting origin vector
        return false;
    }

    @Override
    public void write(DataWriter out) throws IOException {
        throw new UnsupportedOperationException();
    }
}
