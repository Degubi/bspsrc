package bsplib.struct;

import bsplib.io.*;
import bsplib.vector.*;
import java.io.*;

/**
 * Displacement multiblend data structure.
 *
 * @author Sandern
 */
public class DDispMultiBlend implements DStruct {

    public static final int MAX_MULTIBLEND_CHANNELS = 4;

    public Vector4f multiblend;
    public Vector4f alphablend;
    public Vector3f[] multiblendcolors;

    public DDispMultiBlend() {
        this.multiblendcolors = new Vector3f[MAX_MULTIBLEND_CHANNELS];
    }

    @Override
    public int getSize() {
        return 80;
    }

    @Override
    public void read(DataReader in) throws IOException {
        multiblend = Vector4f.read(in);
        alphablend = Vector4f.read(in);

        for (int i = 0; i < MAX_MULTIBLEND_CHANNELS; i++) {
            multiblendcolors[i] = Vector3f.read(in);
        }
    }

    @Override
    public void write(DataWriter out) throws IOException {
        Vector4f.write(out, multiblend);
        Vector4f.write(out, alphablend);

        for (int i = 0; i < MAX_MULTIBLEND_CHANNELS; i++) {
            Vector3f.write(out, multiblendcolors[i]);
        }
    }
}
