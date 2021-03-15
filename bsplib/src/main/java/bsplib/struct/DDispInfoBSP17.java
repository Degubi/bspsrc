package bsplib.struct;

import bsplib.io.*;
import bsplib.vector.*;
import java.io.*;

/**
 * DDispInfo structure variant for very old HL2 beta maps that don't seem to
 * have triangle tags.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DDispInfoBSP17 extends DDispInfo {

    @Override
    public int getTriangleTagCount() {
       return 0;
    }

    @Override
    public int getSize() {
        return 172;
    }

    @Override
    public void read(DataReader in) throws IOException {
        startPos = Vector3f.read(in);
        dispVertStart = in.readInt();
        power = in.readInt();
        minTess = in.readInt();
        smoothingAngle = in.readFloat();
        contents = in.readInt();
        mapFace = in.readUnsignedShort();
        lightmapAlphaStart = in.readInt();
        lightmapSamplePositionStart = in.readInt();
        in.readBytes(neighbors);

        for (int i = 0; i < allowedVerts.length; i++) {
            allowedVerts[i] = in.readInt();
        }
    }

    @Override
    public void write(DataWriter out) throws IOException {
        out.writeFloat(startPos.x);
        out.writeFloat(startPos.y);
        out.writeFloat(startPos.z);

        out.writeInt(dispVertStart);
        out.writeInt(power);
        out.writeInt(minTess);
        out.writeFloat(smoothingAngle);
        out.writeInt(contents);
        out.writeUnsignedShort(mapFace);
        out.writeInt(lightmapAlphaStart);
        out.writeInt(lightmapSamplePositionStart);
        out.writeBytes(neighbors);

        for (int i = 0; i < allowedVerts.length; i++) {
            out.writeInt(allowedVerts[i]);
        }
    }
}
