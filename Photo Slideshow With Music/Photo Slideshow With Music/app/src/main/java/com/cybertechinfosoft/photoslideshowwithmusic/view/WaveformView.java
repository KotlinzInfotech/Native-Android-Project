package com.cybertechinfosoft.photoslideshowwithmusic.view;

import com.audiocutter.soundfile.*;
import com.cybertechinfosoft.photoslideshowwithmusic.R;

import android.content.*;
import android.util.*;
import android.view.*;
import android.content.res.*;
import android.graphics.*;

public class WaveformView extends View {
    private Paint mBorderLinePaint;
    private float mDensity;
    private GestureDetector mGestureDetector;
    private Paint mGridPaint;
    private int[] mHeightsAtThisZoomLevel;
    private float mInitialScaleSpan;
    private boolean mInitialized;
    private int[] mLenByZoomLevel;
    private WaveformListener mListener;
    private int mNumZoomLevels;
    private int mOffset;
    private Paint mPlaybackLinePaint;
    private int mPlaybackPos;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private ScaleGestureDetector mScaleGestureDetector;
    private Paint mSelectedLinePaint;
    private int mSelectionEnd;
    private int mSelectionStart;
    private CheapSoundFile mSoundFile;
    private Paint mTimecodePaint;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mUnselectedLinePaint;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int mZoomLevel;

    public WaveformView(final Context context, final AttributeSet set) {
        super(context, set);
        this.setFocusable(false);
        final Resources resources = this.getResources();
        (this.mGridPaint = new Paint()).setAntiAlias(false);
        this.mGridPaint.setColor(resources.getColor(R.color.grid_line));
        (this.mSelectedLinePaint = new Paint()).setAntiAlias(false);
        this.mSelectedLinePaint.setColor(resources.getColor(R.color.waveform_selected));
        (this.mUnselectedLinePaint = new Paint()).setAntiAlias(false);
        this.mUnselectedLinePaint.setColor(resources.getColor(R.color.waveform_unselected));
        (this.mUnselectedBkgndLinePaint = new Paint()).setAntiAlias(false);
        this.mUnselectedBkgndLinePaint.setColor(resources.getColor(R.color.waveform_unselected_bkgnd_overlay));
        (this.mBorderLinePaint = new Paint()).setAntiAlias(true);
        this.mBorderLinePaint.setStrokeWidth(1.5f);
        this.mBorderLinePaint.setPathEffect((PathEffect) new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        this.mBorderLinePaint.setColor(resources.getColor(R.color.selection_border));
        (this.mPlaybackLinePaint = new Paint()).setAntiAlias(false);
        this.mPlaybackLinePaint.setColor(resources.getColor(R.color.playback_indicator));
        (this.mTimecodePaint = new Paint()).setTextSize(12.0f);
        this.mTimecodePaint.setAntiAlias(true);
        this.mTimecodePaint.setColor(resources.getColor(R.color.timecode));
        this.mTimecodePaint.setShadowLayer(2.0f, 1.0f, 1.0f, resources.getColor(R.color.timecode_shadow));
        this.mGestureDetector = new GestureDetector(context, (GestureDetector.OnGestureListener) new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(final MotionEvent motionEvent, final MotionEvent motionEvent2, final float n, final float n2) {
                WaveformView.this.mListener.waveformFling(n);
                return true;
            }
        });
        this.mScaleGestureDetector = new ScaleGestureDetector(context, (ScaleGestureDetector.OnScaleGestureListener) new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                final float abs = Math.abs(scaleGestureDetector.getCurrentSpanX());
                final StringBuilder sb = new StringBuilder();
                sb.append("Scale ");
                sb.append(abs - WaveformView.this.mInitialScaleSpan);
                if (abs - WaveformView.this.mInitialScaleSpan > 40.0f) {
                    WaveformView.this.mListener.waveformZoomIn();
                    WaveformView.this.mInitialScaleSpan = abs;
                }
                if (abs - WaveformView.this.mInitialScaleSpan < -40.0f) {
                    WaveformView.this.mListener.waveformZoomOut();
                    WaveformView.this.mInitialScaleSpan = abs;
                }
                return true;
            }

            public boolean onScaleBegin(final ScaleGestureDetector scaleGestureDetector) {
                final StringBuilder sb = new StringBuilder();
                sb.append("ScaleBegin ");
                sb.append(scaleGestureDetector.getCurrentSpanX());
                WaveformView.this.mInitialScaleSpan = Math.abs(scaleGestureDetector.getCurrentSpanX());
                return true;
            }

            public void onScaleEnd(final ScaleGestureDetector scaleGestureDetector) {
                final StringBuilder sb = new StringBuilder();
                sb.append("ScaleEnd ");
                sb.append(scaleGestureDetector.getCurrentSpanX());
            }
        });
        this.mSoundFile = null;
        this.mLenByZoomLevel = null;
        this.mValuesByZoomLevel = null;
        this.mHeightsAtThisZoomLevel = null;
        this.mOffset = 0;
        this.mPlaybackPos = -1;
        this.mSelectionStart = 0;
        this.mSelectionEnd = 0;
        this.mDensity = 1.0f;
        this.mInitialized = false;
    }

    private void computeDoublesForAllZoomLevels() {
        final int numFrames = this.mSoundFile.getNumFrames();
        final int[] frameGains = this.mSoundFile.getFrameGains();
        final double[] array = new double[numFrames];
        if (numFrames == 1) {
            array[0] = frameGains[0];
        } else if (numFrames == 2) {
            array[0] = frameGains[0];
            array[1] = frameGains[1];
        } else if (numFrames > 2) {
            array[0] = frameGains[0] / 2.0 + frameGains[1] / 2.0;
            int n = 1;
            int n2;
            while (true) {
                n2 = numFrames - 1;
                if (n >= n2) {
                    break;
                }
                final double n3 = frameGains[n - 1] / 3.0;
                final double n4 = frameGains[n] / 3.0;
                final int n5 = n + 1;
                array[n] = n3 + n4 + frameGains[n5] / 3.0;
                n = n5;
            }
            array[n2] = frameGains[numFrames - 2] / 2.0 + frameGains[n2] / 2.0;
        }
        double n6 = 1.0;
        double n7;
        for (int i = 0; i < numFrames; ++i, n6 = n7) {
            n7 = n6;
            if (array[i] > n6) {
                n7 = array[i];
            }
        }
        double n8;
        if (n6 > 255.0) {
            n8 = 255.0 / n6;
        } else {
            n8 = 1.0;
        }
        final int[] array2 = new int[256];
        int j = 0;
        double n9 = 0.0;
        while (j < numFrames) {
            int n10;
            if ((n10 = (int) (array[j] * n8)) < 0) {
                n10 = 0;
            }
            int n11;
            if ((n11 = n10) > 255) {
                n11 = 255;
            }
            final double n12 = n11;
            double n13 = n9;
            if (n12 > n9) {
                n13 = n12;
            }
            ++array2[n11];
            ++j;
            n9 = n13;
        }
        double n14 = 0.0;
        for (int n15 = 0; n14 < 255.0 && n15 < numFrames / 20; n15 += array2[(int) n14], ++n14) {
        }
        int n16;
        double n17;
        for (n16 = 0, n17 = n9; n17 > 2.0 && n16 < numFrames / 100; n16 += array2[(int) n17], --n17) {
        }
        final double[] array3 = new double[numFrames];
        for (int k = 0; k < numFrames; ++k) {
            double n18;
            if ((n18 = (array[k] * n8 - n14) / (n17 - n14)) < 0.0) {
                n18 = 0.0;
            }
            double n19 = n18;
            if (n18 > 1.0) {
                n19 = 1.0;
            }
            array3[k] = n19 * n19;
        }
        this.mNumZoomLevels = 5;
        this.mLenByZoomLevel = new int[5];
        this.mZoomFactorByZoomLevel = new double[5];
        this.mValuesByZoomLevel = new double[5][];
        this.mLenByZoomLevel[0] = numFrames * 2;
        this.mZoomFactorByZoomLevel[0] = 2.0;
        this.mValuesByZoomLevel[0] = new double[this.mLenByZoomLevel[0]];
        if (numFrames > 0) {
            this.mValuesByZoomLevel[0][0] = array3[0] * 0.5;
            this.mValuesByZoomLevel[0][1] = array3[0];
        }
        for (int l = 1; l < numFrames; ++l) {
            final double[] array4 = this.mValuesByZoomLevel[0];
            final int n20 = l * 2;
            array4[n20] = (array3[l - 1] + array3[l]) * 0.5;
            this.mValuesByZoomLevel[0][n20 + 1] = array3[l];
        }
        this.mLenByZoomLevel[1] = numFrames;
        this.mValuesByZoomLevel[1] = new double[this.mLenByZoomLevel[1]];
        this.mZoomFactorByZoomLevel[1] = 1.0;
        for (int n21 = 0; n21 < this.mLenByZoomLevel[1]; ++n21) {
            this.mValuesByZoomLevel[1][n21] = array3[n21];
        }
        for (int n22 = 2; n22 < 5; ++n22) {
            final int[] mLenByZoomLevel = this.mLenByZoomLevel;
            final int[] mLenByZoomLevel2 = this.mLenByZoomLevel;
            final int n23 = n22 - 1;
            mLenByZoomLevel[n22] = mLenByZoomLevel2[n23] / 2;
            this.mValuesByZoomLevel[n22] = new double[this.mLenByZoomLevel[n22]];
            this.mZoomFactorByZoomLevel[n22] = this.mZoomFactorByZoomLevel[n23] / 2.0;
            for (int n24 = 0; n24 < this.mLenByZoomLevel[n22]; ++n24) {
                final double[] array5 = this.mValuesByZoomLevel[n22];
                final double[] array6 = this.mValuesByZoomLevel[n23];
                final int n25 = n24 * 2;
                array5[n24] = (array6[n25] + this.mValuesByZoomLevel[n23][n25 + 1]) * 0.5;
            }
        }
        if (numFrames > 5000) {
            this.mZoomLevel = 3;
        } else if (numFrames > 1000) {
            this.mZoomLevel = 2;
        } else if (numFrames > 300) {
            this.mZoomLevel = 1;
        } else {
            this.mZoomLevel = 0;
        }
        this.mInitialized = true;
    }

    private void computeIntsForThisZoomLevel() {
        final int n = this.getMeasuredHeight() / 2;
        this.mHeightsAtThisZoomLevel = new int[this.mLenByZoomLevel[this.mZoomLevel]];
        for (int i = 0; i < this.mLenByZoomLevel[this.mZoomLevel]; ++i) {
            this.mHeightsAtThisZoomLevel[i] = (int) (this.mValuesByZoomLevel[this.mZoomLevel][i] * (n - 1));
        }
    }

    public boolean canZoomIn() {
        return this.mZoomLevel > 0;
    }

    public boolean canZoomOut() {
        return this.mZoomLevel < this.mNumZoomLevels - 1;
    }

    protected void drawWaveformLine(final Canvas canvas, final int n, final int n2, final int n3, final Paint paint) {
        final float n4 = n;
        canvas.drawLine(n4, (float) n2, n4, (float) n3, paint);
    }

    public int getEnd() {
        return this.mSelectionEnd;
    }

    public int getOffset() {
        return this.mOffset;
    }

    public int getStart() {
        return this.mSelectionStart;
    }

    public int getZoomLevel() {
        return this.mZoomLevel;
    }

    public boolean hasSoundFile() {
        return this.mSoundFile != null;
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public int maxPos() {
        return this.mLenByZoomLevel[this.mZoomLevel];
    }

    public int millisecsToPixels(final int n) {
        return (int) (n * 1.0 * this.mSampleRate * this.mZoomFactorByZoomLevel[this.mZoomLevel] / (this.mSamplesPerFrame * 1000.0) + 0.5);
    }

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mSoundFile == null) {
            return;
        }
        if (this.mHeightsAtThisZoomLevel == null) {
            this.computeIntsForThisZoomLevel();
        }
        final int measuredWidth = this.getMeasuredWidth();
        final int measuredHeight = this.getMeasuredHeight();
        final int mOffset = this.mOffset;
        int n = this.mHeightsAtThisZoomLevel.length - mOffset;
        final int n2 = measuredHeight / 2;
        if (n > measuredWidth) {
            n = measuredWidth;
        }
        final double pixelsToSeconds = this.pixelsToSeconds(1);
        final boolean b = pixelsToSeconds > 0.02;
        double n3 = this.mOffset * pixelsToSeconds;
        int n4 = (int) n3;
        int i = 0;
        while (i < n) {
            ++i;
            n3 += pixelsToSeconds;
            final int n5 = (int) n3;
            int n6;
            if (n5 != (n6 = n4)) {
                if (!b || n5 % 5 == 0) {
                    final float n7 = i;
                    canvas.drawLine(n7, 0.0f, n7, (float) measuredHeight, this.mGridPaint);
                }
                n6 = n5;
            }
            n4 = n6;
        }
        for (int j = 0; j < n; ++j) {
            final int n8 = j + mOffset;
            Paint paint;
            if (n8 >= this.mSelectionStart && n8 < this.mSelectionEnd) {
                paint = this.mSelectedLinePaint;
            } else {
                this.drawWaveformLine(canvas, j, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
                paint = this.mUnselectedLinePaint;
            }
            this.drawWaveformLine(canvas, j, n2 - this.mHeightsAtThisZoomLevel[n8], n2 + 1 + this.mHeightsAtThisZoomLevel[n8], paint);
            if (n8 == this.mPlaybackPos) {
                final float n9 = j;
                canvas.drawLine(n9, 0.0f, n9, (float) measuredHeight, this.mPlaybackLinePaint);
            }
        }
        for (int k = n; k < measuredWidth; ++k) {
            this.drawWaveformLine(canvas, k, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
        }
        canvas.drawLine(this.mSelectionStart - this.mOffset + 0.5f, 30.0f, this.mSelectionStart - this.mOffset + 0.5f, (float) measuredHeight, this.mBorderLinePaint);
        canvas.drawLine(this.mSelectionEnd - this.mOffset + 0.5f, 0.0f, this.mSelectionEnd - this.mOffset + 0.5f, (float) (measuredHeight - 30), this.mBorderLinePaint);
        double n10 = 1.0;
        if (1.0 / pixelsToSeconds < 50.0) {
            n10 = 5.0;
        }
        double n11 = n10;
        if (n10 / pixelsToSeconds < 50.0) {
            n11 = 15.0;
        }
        double n12 = this.mOffset * pixelsToSeconds;
        int n13 = (int) (n12 / n11);
        int l = 0;
        while (l < n) {
            ++l;
            n12 += pixelsToSeconds;
            final int n14 = (int) n12;
            final int n15 = (int) (n12 / n11);
            if (n15 != n13) {
                final StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(n14 / 60);
                final String string = sb.toString();
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                final int n16 = n14 % 60;
                sb2.append(n16);
                String s = sb2.toString();
                if (n16 < 10) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("0");
                    sb3.append(s);
                    s = sb3.toString();
                }
                final StringBuilder sb4 = new StringBuilder();
                sb4.append(string);
                sb4.append(":");
                sb4.append(s);
                final String string2 = sb4.toString();
                canvas.drawText(string2, l - (float) (this.mTimecodePaint.measureText(string2) * 0.5), (float) (int) (this.mDensity * 12.0f), this.mTimecodePaint);
                n13 = n15;
            }
        }
        if (this.mListener != null) {
            this.mListener.waveformDraw();
        }
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.mScaleGestureDetector.onTouchEvent(motionEvent);
        if (this.mGestureDetector.onTouchEvent(motionEvent)) {
            return true;
        }
        switch (motionEvent.getAction()) {
            default: {
                return true;
            }
            case 2: {
                this.mListener.waveformTouchMove(motionEvent.getX());
                return true;
            }
            case 1: {
                this.mListener.waveformTouchEnd();
                return true;
            }
            case 0: {
                this.mListener.waveformTouchStart(motionEvent.getX());
                return true;
            }
        }
    }

    public int pixelsToMillisecs(final int n) {
        return (int) (n * (this.mSamplesPerFrame * 1000.0) / (this.mSampleRate * this.mZoomFactorByZoomLevel[this.mZoomLevel]) + 0.5);
    }

    public double pixelsToSeconds(final int n) {
        return n * this.mSamplesPerFrame / (this.mSampleRate * this.mZoomFactorByZoomLevel[this.mZoomLevel]);
    }

    public void recomputeHeights(final float mDensity) {
        this.mHeightsAtThisZoomLevel = null;
        this.mDensity = mDensity;
        this.mTimecodePaint.setTextSize((float) (int) (mDensity * 12.0f));
        this.invalidate();
    }

    public int secondsToFrames(final double n) {
        return (int) (n * 1.0 * this.mSampleRate / this.mSamplesPerFrame + 0.5);
    }

    public int secondsToPixels(final double n) {
        return (int) (this.mZoomFactorByZoomLevel[this.mZoomLevel] * n * this.mSampleRate / this.mSamplesPerFrame + 0.5);
    }

    public void setListener(final WaveformListener mListener) {
        this.mListener = mListener;
    }

    public void setParameters(final int mSelectionStart, final int mSelectionEnd, final int mOffset) {
        this.mSelectionStart = mSelectionStart;
        this.mSelectionEnd = mSelectionEnd;
        this.mOffset = mOffset;
    }

    public void setPlayback(final int mPlaybackPos) {
        this.mPlaybackPos = mPlaybackPos;
    }

    public void setSoundFile(final CheapSoundFile mSoundFile) {
        this.mSoundFile = mSoundFile;
        this.mSampleRate = this.mSoundFile.getSampleRate();
        this.mSamplesPerFrame = this.mSoundFile.getSamplesPerFrame();
        this.computeDoublesForAllZoomLevels();
        this.mHeightsAtThisZoomLevel = null;
    }

    public void setZoomLevel(final int n) {
        while (this.mZoomLevel > n) {
            this.zoomIn();
        }
        while (this.mZoomLevel < n) {
            this.zoomOut();
        }
    }

    public void zoomIn() {
        if (this.canZoomIn()) {
            --this.mZoomLevel;
            this.mSelectionStart *= 2;
            this.mSelectionEnd *= 2;
            this.mHeightsAtThisZoomLevel = null;
            this.mOffset = (this.mOffset + this.getMeasuredWidth() / 2) * 2 - this.getMeasuredWidth() / 2;
            if (this.mOffset < 0) {
                this.mOffset = 0;
            }
            this.invalidate();
        }
    }

    public void zoomOut() {
        if (this.canZoomOut()) {
            ++this.mZoomLevel;
            this.mSelectionStart /= 2;
            this.mSelectionEnd /= 2;
            this.mOffset = (this.mOffset + this.getMeasuredWidth() / 2) / 2 - this.getMeasuredWidth() / 2;
            if (this.mOffset < 0) {
                this.mOffset = 0;
            }
            this.mHeightsAtThisZoomLevel = null;
            this.invalidate();
        }
    }

    public interface WaveformListener {
        void waveformDraw();

        void waveformFling(final float p0);

        void waveformTouchEnd();

        void waveformTouchMove(final float p0);

        void waveformTouchStart(final float p0);

        void waveformZoomIn();

        void waveformZoomOut();
    }
}
