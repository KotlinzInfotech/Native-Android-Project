package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.content.*;
import android.util.*;
import android.graphics.*;

public class ConnectingLine
{
    private final Paint mPaint;
    private final float mY;

    public ConnectingLine(final Context context, final float my, float applyDimension, final int color) {
        applyDimension = TypedValue.applyDimension(1, applyDimension, context.getResources().getDisplayMetrics());
        (this.mPaint = new Paint()).setColor(color);
        this.mPaint.setStrokeWidth(applyDimension);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setAntiAlias(true);
        this.mY = my;
    }

    public void draw(final Canvas canvas, final float n, final PinView pinView) {
        canvas.drawLine(n, this.mY, pinView.getX(), this.mY, this.mPaint);
    }

    public void draw(final Canvas canvas, final PinView pinView, final PinView pinView2) {
        canvas.drawLine(pinView.getX(), this.mY, pinView2.getX(), this.mY, this.mPaint);
    }
}
