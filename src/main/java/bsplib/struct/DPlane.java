package bsplib.struct;

import bsplib.vector.*;
import io.*;
import java.io.*;

/**
 * Plane data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DPlane implements DStruct {

    public Vector3f normal;
    public float dist;
    public int type;

    @Override
    public String toString() {
        return "DPlane[n:" + normal + ", d:" + dist + ", t:" + type + "]";
    }

    @Override
    public int getSize() {
        return 20;
    }

    @Override
    public void read(DataReader in) throws IOException {
        normal = Vector3f.read(in);
        dist = in.readFloat();
        type = in.readInt();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        Vector3f.write(out, normal);
        out.writeFloat(dist);
        out.writeInt(type);
    }
}
