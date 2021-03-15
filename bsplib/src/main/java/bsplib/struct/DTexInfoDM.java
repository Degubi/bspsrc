package bsplib.struct;

import bsplib.io.*;
import bsplib.util.*;
import java.io.*;

/**
 * DTexInfo structure variant for Dark Messiah (single player only).
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DTexInfoDM extends DTexInfo {

    private byte[] unknown = new byte[24];

    @Override
    public int getSize() {
        return 96;
    }

    @Override
    public void read(DataReader in) throws IOException {
        for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 4; k++) {
                textureVecsTexels[j][k] = in.readFloat();
            }
        }
        for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 4; k++) {
                lightmapVecsLuxels[j][k] = in.readFloat();
            }
        }
        in.readBytes(unknown);
        flags = EnumConverter.fromInteger(SurfaceFlag.class, in.readInt());
        texdata = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 4; k++) {
                out.writeFloat(textureVecsTexels[j][k]);
            }
        }
        for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 4; k++) {
                out.writeFloat(lightmapVecsLuxels[j][k]);
            }
        }
        out.writeBytes(unknown);
        out.writeInt(EnumConverter.toInteger(flags));
        out.writeInt(texdata);
    }
}
