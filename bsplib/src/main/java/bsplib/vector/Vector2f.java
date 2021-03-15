package bsplib.vector;

public final class Vector2f {
	public static final Vector2f NULL = new Vector2f(0, 0);
	public static final Vector2f MAX_VALUE = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vector2f MIN_VALUE = MAX_VALUE.scalar(-1);

	public final float x;
	public final float y;

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Vector dot product: this . that
	 *
	 * @param that the Vector2f to take dot product with
	 * @return the dot product of the two vectors
	 */
	public float dot(Vector2f that) {
		return this.x * that.x + this.y * that.y;
	}

	/**
	 * Vector normalisation: ^this
	 *
	 * @return the normalised vector
	 */
	public Vector2f normalize() {
		var len = length();
		return new Vector2f(x / len, y / len);
	}

	/**
	 * Vector addition: this + that
	 *
	 * @param that The vector to add
	 * @return The sum of the two vectors
	 */
	public Vector2f add(Vector2f that) {
		return new Vector2f(this.x + that.x, this.y + that.y);
	}

	/**
	 * Vector subtraction: this - that
	 *
	 * @param that The vector to subtract
	 * @return The difference of the two vectors
	 */
	public Vector2f sub(Vector2f that) {
		return new Vector2f(this.x - that.x, this.y - that.y);
	}

	/**
	 * Snap vector to nearest value: round(this / value) * value
	 *
	 * @param value snap value
	 * @return This vector snapped to the nearest values of 'value'
	 */
	public Vector2f snap(float value) {
		return new Vector2f(Math.round(x / value) * value, Math.round(y / value) * value);
	}

	/**
	 * Calculate the length of this vector
	 *
	 * @return length of this vector
	 */
	public float length() {
		return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
	}

	/**
	 * Performs a scalar multiplication on this vector: this * mul
	 *
	 * @param mul multiplicator
	 * @return scalar multiplied vector
	 */
	public Vector2f scalar(float mul) {
		return new Vector2f(this.x * mul, this.y * mul);
	}

	/**
	 * Performs a scalar multiplication on this vector: this * that
	 *
	 * @param that multiplicator vector
	 * @return scalar multiplied vector
	 */
	public Vector2f scalar(Vector2f that) {
		return new Vector2f(this.x * that.x, this.y * that.y);
	}

	/**
	 * Rotates the vector.
	 *
	 * @param angle angle rotation in degrees
	 * @return rotated vector
	 */
	public Vector2f rotate(float angle) {
		// normalize angle
		angle %= 360;

		// special cases
		if (angle == 0)
			return this;
		if (angle == 90)
			return new Vector2f(-y, x);
		if (angle == 180)
			return new Vector2f(-x, -y);
		if (angle == 270)
			return new Vector2f(y, -x);

		// convert degrees to radians
		var radians = Math.toRadians(angle);
		var r = Math.hypot(x, y);
		var theta = Math.atan2(y, x);
		var rx = r * Math.cos(theta + radians);
		var ry = r * Math.sin(theta + radians);

		return new Vector2f((float) rx, (float) ry);
	}

	/**
	 * Returns the minima between this vector and another vector.
	 *
	 * @param that other vector to compare
	 * @return minima of this and that vector
	 */
	public Vector2f min(Vector2f that) {
		return new Vector2f(Math.min(this.x, that.x), Math.min(this.y, that.y));
	}

	/**
	 * Returns the maxima between this vector and another vector.
	 *
	 * @param that other vector to compare
	 * @return maxima of this and that vector
	 */
	public Vector2f max(Vector2f that) {
		return new Vector2f(Math.max(this.x, that.x), Math.max(this.y, that.y));
	}
}