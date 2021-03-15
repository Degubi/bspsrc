package bsplib.vector;

public final class Vector4f {
    public static final Vector4f NULL = new Vector4f(0, 0, 0, 0);
    public static final Vector4f MAX_VALUE = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector4f MIN_VALUE = MAX_VALUE.scalar(-1);

    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Vector dot product: this . that
     *
     * @param that the Vector4f to take dot product with
     * @return the dot product of the two vectors
     */
    public float dot(Vector4f that) {
        return this.x * that.x + this.y * that.y + this.z * that.z;
    }


    /**
     * Vector normalisation: ^this
     *
     * @return the normalised vector
     */
    public Vector4f normalize() {
        var len = length();
        return new Vector4f(x / len, y / len, z / len, w / len);
    }

    /**
     * Vector addition: this + that
     *
     * @param that The vector to add
     * @return The sum of the two vectors
     */
    public Vector4f add(Vector4f that) {
        return new Vector4f(this.x + that.x, this.y + that.y, this.z + that.z, this.w + that.w);
    }

    /**
     * Vector subtraction: this - that
     *
     * @param that The vector to subtract
     * @return The difference of the two vectors
     */
    public Vector4f sub(Vector4f that) {
        return new Vector4f(this.x - that.x, this.y - that.y, this.z - that.z, this.w - that.w);
    }

    /**
     * Snap vector to nearest value: round(this / value) * value
     *
     * @param value snap value
     * @return This vector snapped to the nearest values of 'value'
     */
    public Vector4f snap(float value) {
        return new Vector4f(Math.round(x / value) * value, Math.round(y / value) * value, Math.round(z / value) * value, Math.round(w / value) * value);
    }

    /**
     * Calculate the length of this vector
     *
     * @return length of this vector
     */
    public float length() {
        return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2) + Math.pow(this.w, 2));
    }

    /**
     * Performs a scalar multiplication on this vector: this * mul
     *
     * @param mul multiplicator
     * @return scalar multiplied vector
     */
    public Vector4f scalar(float mul) {
        return new Vector4f(this.x * mul, this.y * mul, this.z * mul, this.w * mul);
    }

    /**
     * Performs a scalar multiplication on this vector: this * that
     *
     * @param that multiplicator vector
     * @return scalar multiplied vector
     */
    public Vector4f scalar(Vector4f that) {
        return new Vector4f(this.x * that.x, this.y * that.y, this.z * that.z, this.w * that.w);
    }

    /**
     * Returns the minima between this vector and another vector.
     *
     * @param that other vector to compare
     * @return minima of this and that vector
     */
    public Vector4f min(Vector4f that) {
        return new Vector4f(Math.min(this.x, that.x), Math.min(this.y, that.y), Math.min(this.z, that.z), Math.min(this.w, that.w));
    }

    /**
     * Returns the maxima between this vector and another vector.
     *
     * @param that other vector to compare
     * @return maxima of this and that vector
     */
    public Vector4f max(Vector4f that) {
        return new Vector4f(Math.max(this.x, that.x), Math.max(this.y, that.y), Math.max(this.z, that.z), Math.max(this.w, that.w));
    }
}