package bspsrc.util;

import bsplib.vector.*;

/**
 * Class for axis-aligned bounding boxes.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class AABB {

    private final Vector3f min;
    private final Vector3f max;

    public AABB(Vector3f mins, Vector3f maxs) {
        this.min = mins;
        this.max = maxs;
    }

    public AABB() {
        this(Vector3f.MAX_VALUE, Vector3f.MIN_VALUE);
    }

    public Vector3f getMin() {
        return min;
    }

    public Vector3f getMax() {
        return max;
    }

    public Vector3f getSize() {
        return max.sub(min);
    }

    public boolean intersectsWith(AABB that) {
        return that.max.x > this.min.x && that.min.x < this.max.x &&
               that.max.y > this.min.y && that.min.y < this.max.y &&
               that.max.z > this.min.z && that.min.z < this.max.z;
    }

    public AABB include(AABB that) {
        return new AABB(min.min(that.min), max.max(that.max));
    }

    public AABB expand(Vector3f v) {
        return new AABB(min.sub(v), max.add(v));
    }

    public AABB expand(float e) {
        return expand(new Vector3f(e, e, e));
    }

    @Override
    public String toString() {
        return min.toString() + " -> " + max.toString();
    }
}
