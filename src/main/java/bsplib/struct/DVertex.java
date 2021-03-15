package bsplib.struct;

import bsplib.vector.*;
import io.*;
import java.io.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DVertex implements DStruct {

    public Vector3f point;

    @Override
    public int getSize() {
        return 12;
    }

    @Override
    public void read(DataReader in) throws IOException {
        point = Vector3f.read(in);
    }

    @Override
    public void write(DataWriter out) throws IOException {
        Vector3f.write(out, point);
    }

}
