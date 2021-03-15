package bsplib.modules.entity;

import bsplib.vector.*;

public final class Camera {

    public final Vector3f pos;
    public final Vector3f look;

    public Camera(Vector3f pos, Vector3f look) {
        this.pos = pos;
        this.look = look;
    }
}