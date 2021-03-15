package bsplib.modules.texture;

import bsplib.vector.*;

/**
 * A data structure for UV texture coordinates.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TextureAxis {

    public final Vector3f axis;
    public final float tw;
    public final int shift;

    public TextureAxis(Vector3f axis, int shift, float tw) {
        this.axis = axis;
        this.tw = tw;
        this.shift = shift;
    }

    public TextureAxis(float x, float y, float z, int shift, float tw) {
        this.axis = new Vector3f(x, y, z);
        this.tw = tw;
        this.shift = shift;
    }

    public TextureAxis(Vector3f axis) {
        this(axis, 0, 0.25f);
    }

    public TextureAxis(float x, float y, float z) {
        this(x, y, z, 0, 0.25f);
    }

    @Override
    public String toString() {
        return axis + " " + shift + " [" + tw + "]";
    }
}
