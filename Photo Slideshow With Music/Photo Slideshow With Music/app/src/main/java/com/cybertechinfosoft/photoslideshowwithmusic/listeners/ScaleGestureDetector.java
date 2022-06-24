package com.cybertechinfosoft.photoslideshowwithmusic.listeners;

import android.view.*;

public class ScaleGestureDetector
{
    private static final float PRESSURE_THRESHOLD = 0.67f;
    private static final String TAG = "ScaleGestureDetector";
    public static float mFocusX;
    public static float mFocusY;
    public static MotionEvent mPrevEvent;
    private boolean mActive0MostRecent;
    private int mActiveId0;
    private int mActiveId1;
    private MotionEvent mCurrEvent;
    private float mCurrFingerDiffX;
    private float mCurrFingerDiffY;
    private float mCurrLen;
    private float mCurrPressure;
    private Vector2D mCurrSpanVector;
    private boolean mGestureInProgress;
    private boolean mInvalidGesture;
    private final OnScaleGestureListener mListener;
    private float mPrevFingerDiffX;
    private float mPrevFingerDiffY;
    private float mPrevLen;
    private float mPrevPressure;
    private float mScaleFactor;
    private long mTimeDelta;

    public ScaleGestureDetector(final OnScaleGestureListener mListener) {
        this.mListener = mListener;
        this.mCurrSpanVector = new Vector2D();
    }

    private int findNewActiveIndex(final MotionEvent motionEvent, int i, final int n) {
        final int pointerCount = motionEvent.getPointerCount();
        final int pointerIndex = motionEvent.findPointerIndex(i);
        for (i = 0; i < pointerCount; ++i) {
            if (i != n && i != pointerIndex) {
                return i;
            }
        }
        return -1;
    }

    private void reset() {
        if (ScaleGestureDetector.mPrevEvent != null) {
            ScaleGestureDetector.mPrevEvent.recycle();
            ScaleGestureDetector.mPrevEvent = null;
        }
        if (this.mCurrEvent != null) {
            this.mCurrEvent.recycle();
            this.mCurrEvent = null;
        }
        this.mGestureInProgress = false;
        this.mActiveId0 = -1;
        this.mActiveId1 = -1;
        this.mInvalidGesture = false;
    }

    private void setContext(final View view, final MotionEvent motionEvent) {
        if (this.mCurrEvent != null) {
            this.mCurrEvent.recycle();
        }
        this.mCurrEvent = MotionEvent.obtain(motionEvent);
        this.mCurrLen = -1.0f;
        this.mPrevLen = -1.0f;
        this.mScaleFactor = -1.0f;
        this.mCurrSpanVector.set(0.0f, 0.0f);
        final MotionEvent mPrevEvent = ScaleGestureDetector.mPrevEvent;
        final int pointerIndex = mPrevEvent.findPointerIndex(this.mActiveId0);
        final int pointerIndex2 = mPrevEvent.findPointerIndex(this.mActiveId1);
        final int pointerIndex3 = motionEvent.findPointerIndex(this.mActiveId0);
        final int pointerIndex4 = motionEvent.findPointerIndex(this.mActiveId1);
        if (pointerIndex >= 0 && pointerIndex2 >= 0 && pointerIndex3 >= 0 && pointerIndex4 >= 0) {
            final float x = mPrevEvent.getX(pointerIndex);
            final float y = mPrevEvent.getY(pointerIndex);
            final float x2 = mPrevEvent.getX(pointerIndex2);
            final float y2 = mPrevEvent.getY(pointerIndex2);
            final float x3 = motionEvent.getX(pointerIndex3);
            final float y3 = motionEvent.getY(pointerIndex3);
            final float x4 = motionEvent.getX(pointerIndex4);
            final float y4 = motionEvent.getY(pointerIndex4);
            final float mCurrFingerDiffX = x4 - x3;
            final float mCurrFingerDiffY = y4 - y3;
            this.mCurrSpanVector.set(mCurrFingerDiffX, mCurrFingerDiffY);
            this.mPrevFingerDiffX = x2 - x;
            this.mPrevFingerDiffY = y2 - y;
            this.mCurrFingerDiffX = mCurrFingerDiffX;
            this.mCurrFingerDiffY = mCurrFingerDiffY;
            ScaleGestureDetector.mFocusX = x3 + mCurrFingerDiffX * 0.5f;
            ScaleGestureDetector.mFocusY = y3 + mCurrFingerDiffY * 0.5f;
            this.mTimeDelta = motionEvent.getEventTime() - mPrevEvent.getEventTime();
            this.mCurrPressure = motionEvent.getPressure(pointerIndex3) + motionEvent.getPressure(pointerIndex4);
            this.mPrevPressure = mPrevEvent.getPressure(pointerIndex) + mPrevEvent.getPressure(pointerIndex2);
            return;
        }
        this.mInvalidGesture = true;
        if (this.mGestureInProgress) {
            this.mListener.onScaleEnd(view, this);
        }
    }

    public float getCurrentSpan() {
        if (this.mCurrLen == -1.0f) {
            final float mCurrFingerDiffX = this.mCurrFingerDiffX;
            final float mCurrFingerDiffY = this.mCurrFingerDiffY;
            this.mCurrLen = (float)Math.sqrt(mCurrFingerDiffX * mCurrFingerDiffX + mCurrFingerDiffY * mCurrFingerDiffY);
        }
        return this.mCurrLen;
    }

    public Vector2D getCurrentSpanVector() {
        return this.mCurrSpanVector;
    }

    public float getCurrentSpanX() {
        return this.mCurrFingerDiffX;
    }

    public float getCurrentSpanY() {
        return this.mCurrFingerDiffY;
    }

    public long getEventTime() {
        return this.mCurrEvent.getEventTime();
    }

    public float getFocusX() {
        return ScaleGestureDetector.mFocusX;
    }

    public float getFocusY() {
        return ScaleGestureDetector.mFocusY;
    }

    public float getPreviousSpan() {
        if (this.mPrevLen == -1.0f) {
            final float mPrevFingerDiffX = this.mPrevFingerDiffX;
            final float mPrevFingerDiffY = this.mPrevFingerDiffY;
            this.mPrevLen = (float)Math.sqrt(mPrevFingerDiffX * mPrevFingerDiffX + mPrevFingerDiffY * mPrevFingerDiffY);
        }
        return this.mPrevLen;
    }

    public float getPreviousSpanX() {
        return this.mPrevFingerDiffX;
    }

    public float getPreviousSpanY() {
        return this.mPrevFingerDiffY;
    }

    public float getScaleFactor() {
        if (this.mScaleFactor == -1.0f) {
            this.mScaleFactor = this.getCurrentSpan() / this.getPreviousSpan();
        }
        return this.mScaleFactor;
    }

    public long getTimeDelta() {
        return this.mTimeDelta;
    }

    public boolean isInProgress() {
        return this.mGestureInProgress;
    }

    public boolean onTouchEvent(final View view, final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.reset();
        }
        final boolean mInvalidGesture = this.mInvalidGesture;
        final boolean b = false;
        if (mInvalidGesture) {
            return false;
        }
        if (!this.mGestureInProgress) {
            if (actionMasked != 5) {
                switch (actionMasked) {
                    case 1: {
                        this.reset();
                        break;
                    }
                    case 0: {
                        this.mActiveId0 = motionEvent.getPointerId(0);
                        this.mActive0MostRecent = true;
                        break;
                    }
                }
            }
            else {
                if (ScaleGestureDetector.mPrevEvent != null) {
                    ScaleGestureDetector.mPrevEvent.recycle();
                }
                ScaleGestureDetector.mPrevEvent = MotionEvent.obtain(motionEvent);
                this.mTimeDelta = 0L;
                final int actionIndex = motionEvent.getActionIndex();
                final int pointerIndex = motionEvent.findPointerIndex(this.mActiveId0);
                this.mActiveId1 = motionEvent.getPointerId(actionIndex);
                if (pointerIndex < 0 || pointerIndex == actionIndex) {
                    this.mActiveId0 = motionEvent.getPointerId(this.findNewActiveIndex(motionEvent, this.mActiveId1, -1));
                }
                this.mActive0MostRecent = false;
                this.setContext(view, motionEvent);
                this.mGestureInProgress = this.mListener.onScaleBegin(view, this);
            }
        }
        else {
            switch (actionMasked) {
                case 6: {
                    final int pointerCount = motionEvent.getPointerCount();
                    final int actionIndex2 = motionEvent.getActionIndex();
                    final int pointerId = motionEvent.getPointerId(actionIndex2);
                    boolean b2 = false;
                    if (pointerCount > 2) {
                        Label_0436: {
                            if (pointerId == this.mActiveId0) {
                                final int newActiveIndex = this.findNewActiveIndex(motionEvent, this.mActiveId1, actionIndex2);
                                if (newActiveIndex >= 0) {
                                    this.mListener.onScaleEnd(view, this);
                                    this.mActiveId0 = motionEvent.getPointerId(newActiveIndex);
                                    this.mActive0MostRecent = true;
                                    ScaleGestureDetector.mPrevEvent = MotionEvent.obtain(motionEvent);
                                    this.setContext(view, motionEvent);
                                    this.mGestureInProgress = this.mListener.onScaleBegin(view, this);
                                    b2 = b;
                                    break Label_0436;
                                }
                            }
                            else {
                                b2 = b;
                                if (pointerId != this.mActiveId1) {
                                    break Label_0436;
                                }
                                final int newActiveIndex2 = this.findNewActiveIndex(motionEvent, this.mActiveId0, actionIndex2);
                                if (newActiveIndex2 >= 0) {
                                    this.mListener.onScaleEnd(view, this);
                                    this.mActiveId1 = motionEvent.getPointerId(newActiveIndex2);
                                    this.mActive0MostRecent = false;
                                    ScaleGestureDetector.mPrevEvent = MotionEvent.obtain(motionEvent);
                                    this.setContext(view, motionEvent);
                                    this.mGestureInProgress = this.mListener.onScaleBegin(view, this);
                                    b2 = b;
                                    break Label_0436;
                                }
                            }
                            b2 = true;
                        }
                        ScaleGestureDetector.mPrevEvent.recycle();
                        ScaleGestureDetector.mPrevEvent = MotionEvent.obtain(motionEvent);
                        this.setContext(view, motionEvent);
                    }
                    else {
                        b2 = true;
                    }
                    if (b2) {
                        this.setContext(view, motionEvent);
                        int mActiveId0;
                        if (pointerId == this.mActiveId0) {
                            mActiveId0 = this.mActiveId1;
                        }
                        else {
                            mActiveId0 = this.mActiveId0;
                        }
                        final int pointerIndex2 = motionEvent.findPointerIndex(mActiveId0);
                        ScaleGestureDetector.mFocusX = motionEvent.getX(pointerIndex2);
                        ScaleGestureDetector.mFocusY = motionEvent.getY(pointerIndex2);
                        this.mListener.onScaleEnd(view, this);
                        this.reset();
                        this.mActiveId0 = mActiveId0;
                        this.mActive0MostRecent = true;
                        break;
                    }
                    break;
                }
                case 5: {
                    this.mListener.onScaleEnd(view, this);
                    int mActiveId2 = this.mActiveId0;
                    final int mActiveId3 = this.mActiveId1;
                    this.reset();
                    ScaleGestureDetector.mPrevEvent = MotionEvent.obtain(motionEvent);
                    if (!this.mActive0MostRecent) {
                        mActiveId2 = mActiveId3;
                    }
                    this.mActiveId0 = mActiveId2;
                    this.mActiveId1 = motionEvent.getPointerId(motionEvent.getActionIndex());
                    this.mActive0MostRecent = false;
                    if (motionEvent.findPointerIndex(this.mActiveId0) < 0 || this.mActiveId0 == this.mActiveId1) {
                        this.mActiveId0 = motionEvent.getPointerId(this.findNewActiveIndex(motionEvent, this.mActiveId1, -1));
                    }
                    this.setContext(view, motionEvent);
                    this.mGestureInProgress = this.mListener.onScaleBegin(view, this);
                    break;
                }
                case 3: {
                    this.mListener.onScaleEnd(view, this);
                    this.reset();
                    break;
                }
                case 2: {
                    this.setContext(view, motionEvent);
                    if (this.mCurrPressure / this.mPrevPressure > 0.67f && this.mListener.onScale(view, this)) {
                        ScaleGestureDetector.mPrevEvent.recycle();
                        ScaleGestureDetector.mPrevEvent = MotionEvent.obtain(motionEvent);
                        break;
                    }
                    break;
                }
                case 1: {
                    this.reset();
                    break;
                }
            }
        }
        return true;
    }

    public interface OnScaleGestureListener
    {
        boolean onScale(final View p0, final ScaleGestureDetector p1);

        boolean onScaleBegin(final View p0, final ScaleGestureDetector p1);

        void onScaleEnd(final View p0, final ScaleGestureDetector p1);
    }

    public static class SimpleOnScaleGestureListener implements OnScaleGestureListener
    {
        @Override
        public boolean onScale(final View view, final ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(final View view, final ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(final View view, final ScaleGestureDetector scaleGestureDetector) {
        }
    }
}
