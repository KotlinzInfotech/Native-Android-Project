package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

class PinView extends View {
    private static final float DEFAULT_THUMB_RADIUS_DP = 14.0f;
    private static final float MINIMUM_TARGET_RADIUS_DP = 24.0f;
    private IRangeBarFormatter formatter;
    private Rect mBounds;
    private Paint mCirclePaint;
    private float mCircleRadiusPx;
    private float mDensity;
    private boolean mHasBeenPressed;
    private boolean mIsPressed;
    private float mMaxPinFont;
    private float mMinPinFont;
    private Drawable mPin;
    private ColorFilter mPinFilter;
    private float mPinPadding;
    private int mPinRadiusPx;
    private boolean mPinsAreTemporary;
    private Resources mRes;
    private float mTargetRadiusPx;
    private Paint mTextPaint;
    private float mTextYPadding;
    private String mValue;
    private float mX;
    private float mY;

    public PinView(final Context context) {
        super(context);
        this.mIsPressed = false;
        this.mBounds = new Rect();
        this.mMinPinFont = 8.0f;
        this.mMaxPinFont = 24.0f;
        this.mHasBeenPressed = false;
    }

    private void calibrateTextSize(final Paint paint, final String s, float n) {
        paint.setTextSize(10.0f);
        final float n2 = n * 8.0f / paint.measureText(s) / this.mDensity;
        if (n2 < this.mMinPinFont) {
            n = this.mMinPinFont;
        } else {
            n = n2;
            if (n2 > this.mMaxPinFont) {
                n = this.mMaxPinFont;
            }
        }
        paint.setTextSize(n * this.mDensity);
    }

    public void draw(final Canvas canvas) {
        canvas.drawCircle(this.mX, this.mY, this.mCircleRadiusPx, this.mCirclePaint);
        if (this.mPinRadiusPx > 0 && (this.mHasBeenPressed || !this.mPinsAreTemporary)) {
            this.mBounds.set((int) this.mX - this.mPinRadiusPx, (int) this.mY - this.mPinRadiusPx * 2 - (int) this.mPinPadding, (int) this.mX + this.mPinRadiusPx, (int) this.mY - (int) this.mPinPadding);
            this.mPin.setBounds(this.mBounds);
            String s2;
            final String s = s2 = this.mValue;
            if (this.formatter != null) {
                s2 = this.formatter.format(s);
            }
            this.calibrateTextSize(this.mTextPaint, s2, this.mBounds.width());
            this.mTextPaint.getTextBounds(s2, 0, s2.length(), this.mBounds);
            this.mTextPaint.setTextAlign(Paint.Align.CENTER);
            this.mPin.setColorFilter(this.mPinFilter);
            this.mPin.draw(canvas);
            canvas.drawText(s2, this.mX, this.mY - this.mPinRadiusPx - this.mPinPadding + this.mTextYPadding, this.mTextPaint);
        }
        super.draw(canvas);
    }

    public float getX() {
        return this.mX;
    }

    public void init(final Context context, final float my, final float n, final int n2, final int color, final float mCircleRadiusPx, final int color2, final float n3, final float n4, final boolean mPinsAreTemporary) {
        this.mRes = context.getResources();
        this.mPin = ContextCompat.getDrawable(context, R.drawable.rotate);
        this.mDensity = this.getResources().getDisplayMetrics().density;
        this.mMinPinFont = n3 / this.mDensity;
        this.mMaxPinFont = n4 / this.mDensity;
        this.mPinsAreTemporary = mPinsAreTemporary;
        this.mPinPadding = (int) TypedValue.applyDimension(1, 15.0f, this.mRes.getDisplayMetrics());
        this.mCircleRadiusPx = mCircleRadiusPx;
        this.mTextYPadding = (int) TypedValue.applyDimension(1, 3.5f, this.mRes.getDisplayMetrics());
        if (n == -1.0f) {
            this.mPinRadiusPx = (int) TypedValue.applyDimension(1, 14.0f, this.mRes.getDisplayMetrics());
        } else {
            this.mPinRadiusPx = (int) TypedValue.applyDimension(1, n, this.mRes.getDisplayMetrics());
        }
        final int n5 = (int) TypedValue.applyDimension(2, 15.0f, this.mRes.getDisplayMetrics());
        (this.mTextPaint = new Paint()).setColor(color);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize((float) n5);
        (this.mCirclePaint = new Paint()).setColor(color2);
        this.mCirclePaint.setAntiAlias(true);
        this.mPinFilter = (ColorFilter) new LightingColorFilter(n2, n2);
        this.mTargetRadiusPx = TypedValue.applyDimension(1, (float) (int) Math.max(24.0f, this.mPinRadiusPx), this.mRes.getDisplayMetrics());
        this.mY = my;
    }

    public boolean isInTargetZone(final float n, final float n2) {
        return Math.abs(n - this.mX) <= this.mTargetRadiusPx && Math.abs(n2 - this.mY + this.mPinPadding) <= this.mTargetRadiusPx;
    }

    public boolean isPressed() {
        return this.mIsPressed;
    }

    public void press() {
        this.mIsPressed = true;
        this.mHasBeenPressed = true;
    }

    public void release() {
        this.mIsPressed = false;
    }

    public void setFormatter(final IRangeBarFormatter formatter) {
        this.formatter = formatter;
    }

    public void setSize(final float n, final float n2) {
        this.mPinPadding = (int) n2;
        this.mPinRadiusPx = (int) n;
        this.invalidate();
    }

    public void setX(final float mx) {
        this.mX = mx;
    }

    public void setXValue(final String mValue) {
        this.mValue = mValue;
    }
}
