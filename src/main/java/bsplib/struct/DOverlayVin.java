package bsplib.struct;

import bsplib.vector.*;
import io.*;
import java.io.*;

/**
 * DOverlay variant for Vindictus that uses integers in place of shorts.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DOverlayVin extends DOverlay {

    @Override
    public int getSize() {
        return 356;
    }

    @Override
    public void read(DataReader in) throws IOException {
        id = in.readInt();
        texinfo = (short) in.readInt();
        faceCountAndRenderOrder = in.readInt();

        for (int j = 0; j < OVERLAY_BSP_FACE_COUNT; j++) {
            ofaces[j] = in.readInt();
        }

        u[0] = in.readFloat();
        u[1] = in.readFloat();
        v[0] = in.readFloat();
        v[1] = in.readFloat();

        for (int j = 0; j < 4; j++) {
            uvpoints[j] = Vector3f.read(in);
        }

        origin = Vector3f.read(in);
        basisNormal = Vector3f.read(in);
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeInt(id);
        out.writeInt(texinfo);
        out.writeInt(faceCountAndRenderOrder);

        for (int j = 0; j < OVERLAY_BSP_FACE_COUNT; j++) {
            out.writeInt(ofaces[j]);
        }

        out.writeFloat(u[0]);
        out.writeFloat(u[1]);
        out.writeFloat(v[0]);
        out.writeFloat(v[1]);

        for (int j = 0; j < 4; j++) {
            Vector3f.write(out, origin);
        }
    }
}
