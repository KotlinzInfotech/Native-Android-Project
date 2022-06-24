package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.widget.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import android.view.*;

public class MarkerView extends ImageView
{
    private MarkerListener mListener;
    private int mVelocity;

    public MarkerView(final Context context, final AttributeSet set) {
        super(context, set);
        this.setFocusable(true);
        this.mVelocity = 0;
        this.mListener = null;
    }

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mListener != null) {
            this.mListener.markerDraw();
        }
    }

    protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
        if (b && this.mListener != null) {
            this.mListener.markerFocus(this);
        }
        super.onFocusChanged(b, n, rect);
    }

    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        ++this.mVelocity;
        final int n2 = (int)Math.sqrt(this.mVelocity / 2 + 1);
        if (this.mListener != null) {
            if (n == 21) {
                this.mListener.markerLeft(this, n2);
                return true;
            }
            if (n == 22) {
                this.mListener.markerRight(this, n2);
                return true;
            }
            if (n == 23) {
                this.mListener.markerEnter(this);
                return true;
            }
        }
        return super.onKeyDown(n, keyEvent);
    }

    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        this.mVelocity = 0;
        if (this.mListener != null) {
            this.mListener.markerKeyUp();
        }
        return super.onKeyDown(n, keyEvent);
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 2: {
                this.mListener.markerTouchMove(this, motionEvent.getRawX());
                break;
            }
            case 1: {
                this.mListener.markerTouchEnd(this);
                break;
            }
            case 0: {
                this.requestFocus();
                this.mListener.markerTouchStart(this, motionEvent.getRawX());
                break;
            }
        }
        return true;
    }

    public void setListener(final MarkerListener mListener) {
        this.mListener = mListener;
    }

    public interface MarkerListener
    {
        void markerDraw();

        void markerEnter(final MarkerView p0);

        void markerFocus(final MarkerView p0);

        void markerKeyUp();

        void markerLeft(final MarkerView p0, final int p1);

        void markerRight(final MarkerView p0, final int p1);

        void markerTouchEnd(final MarkerView p0);

        void markerTouchMove(final MarkerView p0, final float p1);

        void markerTouchStart(final MarkerView p0, final float p1);
    }
}
