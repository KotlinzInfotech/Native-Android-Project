package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;

public class Bar {
    private final Paint mBarPaint = new Paint();
    private final float mLeftX;
    private int mNumSegments;
    private final float mRightX;
    private float mTickDistance;
    private final float mTickHeight;
    private final Paint mTickPaint;
    private final float mY;

    public Bar(Context context, float f, float f2, float f3, int i, float f4, int i2, float f5, int i3) {
        this.mLeftX = f;
        this.mRightX = f + f3;
        this.mY = f2;
        this.mNumSegments = i - 1;
        this.mTickDistance = f3 / ((float) this.mNumSegments);
        this.mTickHeight = TypedValue.applyDimension(1, f4, context.getResources().getDisplayMetrics());
        this.mBarPaint.setColor(i3);
        this.mBarPaint.setStrokeWidth(f5);
        this.mBarPaint.setAntiAlias(true);
        this.mTickPaint = new Paint();
        this.mTickPaint.setColor(i2);
        this.mTickPaint.setStrokeWidth(f5);
        this.mTickPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        canvas.drawLine(this.mLeftX, this.mY, this.mRightX, this.mY, this.mBarPaint);
    }

    public float getLeftX() {
        return this.mLeftX;
    }

    public float getRightX() {
        return this.mRightX;
    }

    public float getNearestTickCoordinate(PinView pinView) {
        return this.mLeftX + (((float) getNearestTickIndex(pinView)) * this.mTickDistance);
    }

    public int getNearestTickIndex(PinView pinView) {
        return (int) (((pinView.getX() - this.mLeftX) + (this.mTickDistance / 2.0f)) / this.mTickDistance);
    }

    public void setTickCount(int i) {
        float f = this.mRightX - this.mLeftX;
        this.mNumSegments = i - 1;
        this.mTickDistance = f / ((float) this.mNumSegments);
    }

    public void drawTicks(Canvas canvas) {
        for (int i = 0; i < this.mNumSegments; i++) {
            canvas.drawCircle((((float) i) * this.mTickDistance) + this.mLeftX, this.mY, this.mTickHeight, this.mTickPaint);
        }
        canvas.drawCircle(this.mRightX, this.mY, this.mTickHeight, this.mTickPaint);
    }
}
