package bspsrc.modules.entity;

import bsplib.vector.*;

/**
 * Structure for a Hammer viewport camera.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class Camera {

    public final Vector3f pos;
    public final Vector3f look;

    public Camera(Vector3f pos, Vector3f look) {
        this.pos = pos;
        this.look = look;
    }
}
