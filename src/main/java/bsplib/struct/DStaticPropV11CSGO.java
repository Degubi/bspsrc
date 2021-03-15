package bsplib.struct;

import io.*;
import java.io.*;

//CS:GO now uses v11  since the addition of uniform prop scaling
public class DStaticPropV11CSGO extends DStaticPropV10CSGO {

    public float uniformScale;

    @Override
    public int getSize() {
        return super.getSize() + 4; // 80
    }

    @Override
    public void read(DataReader in) throws IOException
    {
        super.read(in);
        uniformScale = in.readFloat();
    }

    @Override
    public void write(DataWriter out) throws IOException {
        super.write(out);
        out.writeFloat(uniformScale);
    }
}
