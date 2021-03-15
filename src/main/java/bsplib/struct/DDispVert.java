package bsplib.struct;

import bsplib.vector.*;
import io.*;
import java.io.*;

/**
 * Displacement vertex data structure.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DDispVert implements DStruct {

    public Vector3f vector;
    public float dist;
    public float alpha;

    @Override
    public int getSize() {
        return 20;
    }

    @Override
    public void read(DataReader in) throws IOException {
        vector = Vector3f.read(in);
        dist = in.readFloat();
        alpha = in.readFloat();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        Vector3f.write(out, vector);
        out.writeFloat(dist);
        out.writeFloat(alpha);
    }
}
