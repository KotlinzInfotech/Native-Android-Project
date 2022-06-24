package com.cybertechinfosoft.photoslideshowwithmusic.listeners;

import com.cybertechinfosoft.photoslideshowwithmusic.*;
import android.widget.*;
import android.annotation.*;
import android.graphics.*;
import android.view.*;

public class MultiTouchListener implements View.OnTouchListener
{
    private static final int INVALID_POINTER_ID = -1;
    private GestureDetector gestureDetector;
    public boolean isRotateEnabled;
    public boolean isScaleEnabled;
    public boolean isTranslateEnabled;
    private int mActivePointerId;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;
    public float maximumScale;
    public float minimumScale;
    private OnTextStickerListeners textListeners;

    public MultiTouchListener() {
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.minimumScale = 0.5f;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.mScaleGestureDetector = new ScaleGestureDetector((ScaleGestureDetector.OnScaleGestureListener)new ScaleGestureListener());
        this.gestureDetector = new GestureDetector((GestureDetector.OnGestureListener)new SingleTapConfirm());
    }

    public MultiTouchListener(final TextView textView) {
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.minimumScale = 0.5f;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.mScaleGestureDetector = new ScaleGestureDetector((ScaleGestureDetector.OnScaleGestureListener)new ScaleGestureListener());
        this.gestureDetector = new GestureDetector((GestureDetector.OnGestureListener)new SingleTapConfirm());
    }

    public MultiTouchListener(final OnTextStickerListeners textListeners) {
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.minimumScale = 0.5f;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.mScaleGestureDetector = new ScaleGestureDetector((ScaleGestureDetector.OnScaleGestureListener)new ScaleGestureListener());
        this.gestureDetector = new GestureDetector((GestureDetector.OnGestureListener)new SingleTapConfirm());
        this.textListeners = textListeners;
    }

    private static float adjustAngle(final float n) {
        if (n > 180.0f) {
            return n - 360.0f;
        }
        float n2 = n;
        if (n < -180.0f) {
            n2 = n + 360.0f;
        }
        return n2;
    }

    @SuppressLint({ "NewApi" })
    private static void adjustTranslation(final View view, final float n, final float n2) {
        final float[] array = { n, n2 };
        view.getMatrix().mapVectors(array);
        view.setTranslationX(view.getTranslationX() + array[0]);
        view.setTranslationY(view.getTranslationY() + array[1]);
    }

    @SuppressLint({ "NewApi" })
    private static void computeRenderOffset(final View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }
        final float[] array2;
        final float[] array = array2 = new float[2];
        array2[1] = (array2[0] = 0.0f);
        view.getMatrix().mapPoints(array);
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        final float[] array4;
        final float[] array3 = array4 = new float[2];
        array4[1] = (array4[0] = 0.0f);
        view.getMatrix().mapPoints(array3);
        pivotX = array3[0];
        pivotY = array[0];
        final float n = array3[1];
        final float n2 = array[1];
        view.setTranslationX(view.getTranslationX() - (pivotX - pivotY));
        view.setTranslationY(view.getTranslationY() - (n - n2));
    }

    @SuppressLint({ "NewApi" })
    private static void move(final View view, final TransformInfo transformInfo) {
        final float max = Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
        view.setScaleX(max);
        view.setScaleY(max);
        view.setRotation(adjustAngle(view.getRotation() + transformInfo.deltaAngle));
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (this.gestureDetector.onTouchEvent(motionEvent)) {
            view.performClick();
            this.textListeners.onRemoveHold(view);
            return true;
        }
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        if (!this.isTranslateEnabled) {
            return true;
        }
        final int action = motionEvent.getAction();
        final int n = motionEvent.getActionMasked() & action;
        int n2 = 0;
        if (n != 6) {
            switch (n) {
                default: {
                    return true;
                }
                case 3: {
                    this.mActivePointerId = -1;
                    return true;
                }
                case 2: {
                    this.textListeners.onTextFocusChanged(view);
                    this.textListeners.onTextHoldAndMove(view);
                    final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex == -1) {
                        break;
                    }
                    final float x = motionEvent.getX(pointerIndex);
                    final float y = motionEvent.getY(pointerIndex);
                    if (this.mScaleGestureDetector.isInProgress()) {
                        break;
                    }
                    adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    if (view.getY() < -(view.getHeight() / 2)) {
                        this.textListeners.onTextRemovedAnimation(true);
                        return true;
                    }
                    this.textListeners.onTextRemovedAnimation(false);
                    return true;
                }
                case 1: {
                    this.mActivePointerId = -1;
                    this.textListeners.onRemoveHold(view);
                    if (view.getY() < -(view.getHeight() / 2)) {
                        this.textListeners.onTextRemoved(view);
                        return true;
                    }
                    break;
                }
                case 0: {
                    this.mPrevX = motionEvent.getX();
                    this.mPrevY = motionEvent.getY();
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    return true;
                }
            }
        }
        else {
            final int n3 = (0xFF00 & action) >> 8;
            if (motionEvent.getPointerId(n3) == this.mActivePointerId) {
                if (n3 == 0) {
                    n2 = 1;
                }
                this.mPrevX = motionEvent.getX(n2);
                this.mPrevY = motionEvent.getY(n2);
                this.mActivePointerId = motionEvent.getPointerId(n2);
            }
        }
        return true;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        private Vector2D mPrevSpanVector;

        private ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D();
        }

        @Override
        public boolean onScale(final View view, final ScaleGestureDetector scaleGestureDetector) {
            final TransformInfo transformInfo = new TransformInfo();
            float scaleFactor;
            if (MultiTouchListener.this.isScaleEnabled) {
                scaleFactor = scaleGestureDetector.getScaleFactor();
            }
            else {
                scaleFactor = 1.0f;
            }
            transformInfo.deltaScale = scaleFactor;
            float angle;
            if (MultiTouchListener.this.isRotateEnabled) {
                angle = Vector2D.getAngle(this.mPrevSpanVector, scaleGestureDetector.getCurrentSpanVector());
            }
            else {
                angle = 0.0f;
            }
            transformInfo.deltaAngle = angle;
            transformInfo.minimumScale = MultiTouchListener.this.minimumScale - 0.4f;
            transformInfo.maximumScale = MultiTouchListener.this.maximumScale;
            move(view, transformInfo);
            return false;
        }

        @Override
        public boolean onScaleBegin(final View view, final ScaleGestureDetector scaleGestureDetector) {
            this.mPrevSpanVector.set((PointF)scaleGestureDetector.getCurrentSpanVector());
            return true;
        }
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener
    {
        public boolean onDown(final MotionEvent motionEvent) {
            return super.onDown(motionEvent);
        }

        public boolean onSingleTapUp(final MotionEvent motionEvent) {
            return true;
        }
    }

    private class TransformInfo
    {
        public float deltaAngle;
        public float deltaScale;
        public float maximumScale;
        public float minimumScale;
    }
}
