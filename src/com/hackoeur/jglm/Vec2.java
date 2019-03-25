package com.hackoeur.jglm;

import com.hackoeur.jglm.support.Compare;

import java.nio.FloatBuffer;

public class Vec2 extends AbstractVec {
    final float x, y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2() {
        x = 0f;
        y = 0f;
    }

    public Vec2(Vec2 v) {
        x = v.x;
        y = v.y;
    }

    @Override
    public int getDimensions() {
        return 2;
    }

    @Override
    public float getLengthSquared() {
        return x * x + y * y;
    }

    @Override
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = allocateFloatBuffer();
        final int startPos = buffer.position();

        buffer.put(x).put(y);

        buffer.position(startPos);

        return buffer;
    }

    public float[] getArray() {
        return new float[]{x, y};
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean equalsWithEpsilon(Vec obj, float epsilon) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Vec2)) {
            return false;
        }
        Vec2 other = (Vec2) obj;

        return Compare.equals(x, other.x, epsilon)
                && Compare.equals(y, other.y, epsilon);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("{")
                .append(String.format("%8.5f %8.5f", x, y))
                .append("}")
                .toString();
    }

    public Vec4 toDirection() {
        return new Vec4(x, y, 0, 0f);
    }

    public Vec4 toPoint() {
        return new Vec4(x, y, 0, 1f);
    }
}
