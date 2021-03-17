package bsplib.struct;

import bsplib.io.*;
import bsplib.vector.*;
import java.io.*;

/**
 * Displacement info data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DDispInfo implements DStruct {

    public static final int DISP_INFO_FLAG_HAS_MULTIBLEND = 0x40000000;
    public static final int DISP_INFO_FLAG_MAGIC = 0x80000000;

    public Vector3f startPos;           // start position
    public int dispVertStart;           // index into disp verts
    public int dispTriStart;            // index into disp tris
    public int power;                   // power (size)
    public int minTess;                 // min tesselation
    public float smoothingAngle;        // smoothing angle
    public int contents;                // surf contents
    public int mapFace;                 // map face
    public int lightmapAlphaStart;
    public int lightmapSamplePositionStart;
    protected byte[] neighbors = new byte[90]; // TODO: use structures
    public int[] allowedVerts = new int[10]; // allowed verts

    public int getPowerSize() {
        return 1 << power;
    }

    public int getVertexCount() {
        return (getPowerSize() + 1) * (getPowerSize() + 1);
    }

    public int getTriangleTagCount() {
        return 2 * getPowerSize() * getPowerSize();
    }

    @Override
    public int getSize() {
        return 176;
    }

    @Override
    public void read(DataReader in) throws IOException {
        startPos = Vector3f.read(in);
        dispVertStart = in.readInt();
        dispTriStart = in.readInt();
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
        out.writeInt(dispTriStart);
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

    public boolean hasMultiBlend() {
        return ((minTess + DDispInfo.DISP_INFO_FLAG_MAGIC) & DDispInfo.DISP_INFO_FLAG_HAS_MULTIBLEND) != 0;
    }

    public int getSurfaceFlags() {
        if ((minTess & DDispInfo.DISP_INFO_FLAG_MAGIC) != 0) {
            return minTess & ~(DDispInfo.DISP_INFO_FLAG_MAGIC | DDispInfo.DISP_INFO_FLAG_HAS_MULTIBLEND);
        } else {
            return 0;
        }
    }
}