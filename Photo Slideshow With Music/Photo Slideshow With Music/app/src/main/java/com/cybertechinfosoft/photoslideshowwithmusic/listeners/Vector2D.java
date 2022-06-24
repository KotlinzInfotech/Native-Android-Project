package com.cybertechinfosoft.photoslideshowwithmusic.listeners;

import android.graphics.*;

public class Vector2D extends PointF
{
    public Vector2D() {
    }

    public Vector2D(final float n, final float n2) {
        super(n, n2);
    }

    public static float getAngle(final Vector2D vector2D, final Vector2D vector2D2) {
        vector2D.normalize();
        vector2D2.normalize();
        return (float)((Math.atan2(vector2D2.y, vector2D2.x) - Math.atan2(vector2D.y, vector2D.x)) * 57.29577951308232);
    }

    public void normalize() {
        final float n = (float)Math.sqrt(this.x * this.x + this.y * this.y);
        this.x /= n;
        this.y /= n;
    }
}
