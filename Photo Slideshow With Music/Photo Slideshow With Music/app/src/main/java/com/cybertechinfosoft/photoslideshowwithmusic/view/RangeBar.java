package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

import java.util.HashMap;

public class RangeBar extends View {
    private static final int DEFAULT_BAR_COLOR = -3355444;
    private static final float DEFAULT_BAR_PADDING_BOTTOM_DP = 24.0f;
    private static final float DEFAULT_BAR_WEIGHT_PX = 2.0f;
    private static final float DEFAULT_CIRCLE_SIZE_DP = 5.0f;
    private static final int DEFAULT_CONNECTING_LINE_COLOR = -12627531;
    private static final float DEFAULT_CONNECTING_LINE_WEIGHT_PX = 4.0f;
    private static final float DEFAULT_EXPANDED_PIN_RADIUS_DP = 12.0f;
    public static final float DEFAULT_MAX_PIN_FONT_SP = 24.0f;
    public static final float DEFAULT_MIN_PIN_FONT_SP = 8.0f;
    private static final int DEFAULT_PIN_COLOR = -12627531;
    private static final float DEFAULT_PIN_PADDING_DP = 16.0f;
    private static final int DEFAULT_TEXT_COLOR = -1;
    private static final int DEFAULT_TICK_COLOR = -16777216;
    private static final float DEFAULT_TICK_END = 5.0f;
    private static final float DEFAULT_TICK_HEIGHT_DP = 1.0f;
    private static final float DEFAULT_TICK_INTERVAL = 1.0f;
    private static final float DEFAULT_TICK_START = 0.0f;
    private static final String TAG = "RangeBar";
    private boolean drawTicks;
    private int mActiveBarColor;
    private int mActiveCircleColor;
    private int mActiveConnectingLineColor;
    private int mActiveTickColor;
    private boolean mArePinsTemporary;
    private Bar mBar;
    private int mBarColor;
    private float mBarPaddingBottom;
    private float mBarWeight;
    private int mCircleColor;
    private float mCircleSize;
    private ConnectingLine mConnectingLine;
    private int mConnectingLineColor;
    private float mConnectingLineWeight;
    private int mDefaultHeight;
    private int mDefaultWidth;
    private int mDiffX;
    private int mDiffY;
    private float mExpandedPinRadius;
    private boolean mFirstSetTickCount;
    private IRangeBarFormatter mFormatter;
    private boolean mIsRangeBar;
    private float mLastX;
    private float mLastY;
    private int mLeftIndex;
    private PinView mLeftThumb;
    private OnRangeBarChangeListener mListener;
    private float mMaxPinFont;
    private float mMinPinFont;
    private int mPinColor;
    private float mPinPadding;
    private PinTextFormatter mPinTextFormatter;
    private OnRangeBarTextListener mPinTextListener;
    private int mRightIndex;
    private PinView mRightThumb;
    private int mTextColor;
    private float mThumbRadiusDP;
    private int mTickColor;
    private int mTickCount;
    private float mTickEnd;
    private float mTickHeightDP;
    private float mTickInterval;
    private HashMap<Float, String> mTickMap;
    private float mTickStart;

    public RangeBar(final Context context) {
        super(context);
        this.mTickHeightDP = 1.0f;
        this.mTickStart = 0.0f;
        this.mTickEnd = 5.0f;
        this.mTickInterval = 1.0f;
        this.mBarWeight = 2.0f;
        this.mBarColor = -3355444;
        this.mPinColor = -12627531;
        this.mTextColor = -1;
        this.mConnectingLineWeight = 4.0f;
        this.mConnectingLineColor = -12627531;
        this.mThumbRadiusDP = 12.0f;
        this.mTickColor = -16777216;
        this.mExpandedPinRadius = 12.0f;
        this.mCircleColor = -12627531;
        this.mCircleSize = 5.0f;
        this.mMinPinFont = 8.0f;
        this.mMaxPinFont = 24.0f;
        this.mFirstSetTickCount = true;
        this.mDefaultWidth = 500;
        this.mDefaultHeight = 150;
        this.mTickCount = (int) ((this.mTickEnd - this.mTickStart) / this.mTickInterval) + 1;
        this.mIsRangeBar = true;
        this.mPinPadding = 16.0f;
        this.mBarPaddingBottom = 24.0f;
        this.drawTicks = true;
        this.mArePinsTemporary = true;
        this.mPinTextFormatter = (PinTextFormatter) new PinTextFormatter() {
            @Override
            public String getText(final String s) {
                if (s.length() > 4) {
                    return s.substring(0, 4);
                }
                return s;
            }
        };
    }

    public RangeBar(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTickHeightDP = 1.0f;
        this.mTickStart = 0.0f;
        this.mTickEnd = 5.0f;
        this.mTickInterval = 1.0f;
        this.mBarWeight = 2.0f;
        this.mBarColor = -3355444;
        this.mPinColor = -12627531;
        this.mTextColor = -1;
        this.mConnectingLineWeight = 4.0f;
        this.mConnectingLineColor = -12627531;
        this.mThumbRadiusDP = 12.0f;
        this.mTickColor = -16777216;
        this.mExpandedPinRadius = 12.0f;
        this.mCircleColor = -12627531;
        this.mCircleSize = 5.0f;
        this.mMinPinFont = 8.0f;
        this.mMaxPinFont = 24.0f;
        this.mFirstSetTickCount = true;
        this.mDefaultWidth = 500;
        this.mDefaultHeight = 150;
        this.mTickCount = (int) ((this.mTickEnd - this.mTickStart) / this.mTickInterval) + 1;
        this.mIsRangeBar = true;
        this.mPinPadding = 16.0f;
        this.mBarPaddingBottom = 24.0f;
        this.drawTicks = true;
        this.mArePinsTemporary = true;
        this.mPinTextFormatter = (PinTextFormatter) new PinTextFormatter() {
            @Override
            public String getText(final String s) {
                if (s.length() > 4) {
                    return s.substring(0, 4);
                }
                return s;
            }
        };
        this.rangeBarInit(context, set);
    }

    public RangeBar(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mTickHeightDP = 1.0f;
        this.mTickStart = 0.0f;
        this.mTickEnd = 5.0f;
        this.mTickInterval = 1.0f;
        this.mBarWeight = 2.0f;
        this.mBarColor = -3355444;
        this.mPinColor = -12627531;
        this.mTextColor = -1;
        this.mConnectingLineWeight = 4.0f;
        this.mConnectingLineColor = -12627531;
        this.mThumbRadiusDP = 12.0f;
        this.mTickColor = -16777216;
        this.mExpandedPinRadius = 12.0f;
        this.mCircleColor = -12627531;
        this.mCircleSize = 5.0f;
        this.mMinPinFont = 8.0f;
        this.mMaxPinFont = 24.0f;
        this.mFirstSetTickCount = true;
        this.mDefaultWidth = 500;
        this.mDefaultHeight = 150;
        this.mTickCount = (int) ((this.mTickEnd - this.mTickStart) / this.mTickInterval) + 1;
        this.mIsRangeBar = true;
        this.mPinPadding = 16.0f;
        this.mBarPaddingBottom = 24.0f;
        this.drawTicks = true;
        this.mArePinsTemporary = true;
        this.mPinTextFormatter = (PinTextFormatter) new PinTextFormatter() {
            @Override
            public String getText(final String s) {
                if (s.length() > 4) {
                    return s.substring(0, 4);
                }
                return s;
            }
        };
        this.rangeBarInit(context, set);
    }

    private void createBar() {
        this.mBar = new Bar(this.getContext(), this.getMarginLeft(), this.getYPos(), this.getBarLength(), this.mTickCount, this.mTickHeightDP, this.mTickColor, this.mBarWeight, this.mBarColor);
        this.invalidate();
    }

    private void createConnectingLine() {
        this.mConnectingLine = new ConnectingLine(this.getContext(), this.getYPos(), this.mConnectingLineWeight, this.mConnectingLineColor);
        this.invalidate();
    }

    private void createPins() {
        final Context context = this.getContext();
        final float yPos = this.getYPos();
        if (this.mIsRangeBar) {
            (this.mLeftThumb = new PinView(context)).init(context, yPos, 0.0f, this.mPinColor, this.mTextColor, this.mCircleSize, this.mCircleColor, this.mMinPinFont, this.mMaxPinFont, false);
        }
        (this.mRightThumb = new PinView(context)).init(context, yPos, 0.0f, this.mPinColor, this.mTextColor, this.mCircleSize, this.mCircleColor, this.mMinPinFont, this.mMaxPinFont, false);
        final float marginLeft = this.getMarginLeft();
        final float barLength = this.getBarLength();
        if (this.mIsRangeBar) {
            this.mLeftThumb.setX(this.mLeftIndex / (this.mTickCount - 1) * barLength + marginLeft);
            this.mLeftThumb.setXValue(this.getPinValue(this.mLeftIndex));
        }
        this.mRightThumb.setX(marginLeft + this.mRightIndex / (this.mTickCount - 1) * barLength);
        this.mRightThumb.setXValue(this.getPinValue(this.mRightIndex));
        this.invalidate();
    }

    private float getBarLength() {
        return this.getWidth() - this.getMarginLeft() * 2.0f;
    }

    private float getMarginLeft() {
        return Math.max(this.mExpandedPinRadius, this.mCircleSize);
    }

    private String getPinValue(final int n) {
        if (this.mPinTextListener != null) {
            return this.mPinTextListener.getPinValue(this, n);
        }
        float mTickEnd;
        if (n == this.mTickCount - 1) {
            mTickEnd = this.mTickEnd;
        } else {
            mTickEnd = n * this.mTickInterval + this.mTickStart;
        }
        String s;
        if ((s = this.mTickMap.get(mTickEnd)) == null) {
            final double n2 = mTickEnd;
            if (n2 == Math.ceil(n2)) {
                s = String.valueOf((int) mTickEnd);
            } else {
                s = String.valueOf(mTickEnd);
            }
        }
        return this.mPinTextFormatter.getText(s);
    }

    private float getYPos() {
        return this.getHeight() - this.mBarPaddingBottom;
    }

    private boolean indexOutOfRange(final int n, final int n2) {
        return n < 0 || n >= this.mTickCount || n2 < 0 || n2 >= this.mTickCount;
    }

    private boolean isValidTickCount(final int n) {
        return n > 1;
    }

    private void movePin(final PinView pinView, final float x) {
        if (x >= this.mBar.getLeftX()) {
            if (x > this.mBar.getRightX()) {
                return;
            }
            if (pinView != null) {
                pinView.setX(x);
                this.invalidate();
            }
        }
    }

    private void onActionDown(final float n, final float n2) {
        if (this.mIsRangeBar) {
            if (!this.mRightThumb.isPressed() && this.mLeftThumb.isInTargetZone(n, n2)) {
                this.pressPin(this.mLeftThumb);
                return;
            }
            if (!this.mLeftThumb.isPressed() && this.mRightThumb.isInTargetZone(n, n2)) {
                this.pressPin(this.mRightThumb);
            }
        } else if (this.mRightThumb.isInTargetZone(n, n2)) {
            this.pressPin(this.mRightThumb);
        }
    }

    private void onActionMove(final float n) {
        if (this.mIsRangeBar && this.mLeftThumb.isPressed()) {
            this.movePin(this.mLeftThumb, n);
        } else if (this.mRightThumb.isPressed()) {
            this.movePin(this.mRightThumb, n);
        }
        if (this.mIsRangeBar && this.mLeftThumb.getX() > this.mRightThumb.getX()) {
            final PinView mLeftThumb = this.mLeftThumb;
            this.mLeftThumb = this.mRightThumb;
            this.mRightThumb = mLeftThumb;
        }
        int nearestTickIndex;
        if (this.mIsRangeBar) {
            nearestTickIndex = this.mBar.getNearestTickIndex(this.mLeftThumb);
        } else {
            nearestTickIndex = 0;
        }
        int nearestTickIndex2 = this.mBar.getNearestTickIndex(this.mRightThumb);
        final int n2 = this.getLeft() + this.getPaddingLeft();
        final int right = this.getRight();
        final int paddingRight = this.getPaddingRight();
        int mLeftIndex;
        if (n <= n2) {
            this.movePin(this.mLeftThumb, this.mBar.getLeftX());
            mLeftIndex = 0;
        } else {
            mLeftIndex = nearestTickIndex;
            if (n >= right - paddingRight - n2) {
                nearestTickIndex2 = this.getTickCount() - 1;
                this.movePin(this.mRightThumb, this.mBar.getRightX());
                mLeftIndex = nearestTickIndex;
            }
        }
        if (mLeftIndex != this.mLeftIndex || nearestTickIndex2 != this.mRightIndex) {
            this.mLeftIndex = mLeftIndex;
            this.mRightIndex = nearestTickIndex2;
            if (this.mIsRangeBar) {
                this.mLeftThumb.setXValue(this.getPinValue(this.mLeftIndex));
            }
            this.mRightThumb.setXValue(this.getPinValue(this.mRightIndex));
            if (this.mListener != null) {
                this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
            }
        }
    }

    private void onActionUp(final float n, float abs) {
        if (this.mIsRangeBar && this.mLeftThumb.isPressed()) {
            this.releasePin(this.mLeftThumb);
            return;
        }
        if (this.mRightThumb.isPressed()) {
            this.releasePin(this.mRightThumb);
            return;
        }
        if (this.mIsRangeBar) {
            abs = Math.abs(this.mLeftThumb.getX() - n);
        } else {
            abs = 0.0f;
        }
        if (abs < Math.abs(this.mRightThumb.getX() - n)) {
            if (this.mIsRangeBar) {
                this.mLeftThumb.setX(n);
                this.releasePin(this.mLeftThumb);
            }
        } else {
            this.mRightThumb.setX(n);
            this.releasePin(this.mRightThumb);
        }
        int nearestTickIndex;
        if (this.mIsRangeBar) {
            nearestTickIndex = this.mBar.getNearestTickIndex(this.mLeftThumb);
        } else {
            nearestTickIndex = 0;
        }
        final int nearestTickIndex2 = this.mBar.getNearestTickIndex(this.mRightThumb);
        if (nearestTickIndex != this.mLeftIndex || nearestTickIndex2 != this.mRightIndex) {
            this.mLeftIndex = nearestTickIndex;
            this.mRightIndex = nearestTickIndex2;
            if (this.mListener != null) {
                this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
            }
        }
    }

    private void pressPin(final PinView pinView) {
        if (this.mFirstSetTickCount) {
            this.mFirstSetTickCount = false;
        }
        if (this.mArePinsTemporary) {
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, this.mExpandedPinRadius});
            ofFloat.addUpdateListener((ValueAnimator.AnimatorUpdateListener) new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    RangeBar.this.mThumbRadiusDP = (float) valueAnimator.getAnimatedValue();
                    pinView.setSize(RangeBar.this.mThumbRadiusDP, RangeBar.this.mPinPadding * valueAnimator.getAnimatedFraction());
                    RangeBar.this.invalidate();
                }
            });
            ofFloat.start();
        }
        pinView.press();
    }

    private void rangeBarInit(Context context, AttributeSet attrs) {
        if (this.mTickMap == null) {
            this.mTickMap = new HashMap();
        }
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.RangeBar, 0, 0);
        try {
            float tickStart = ta.getFloat(0, DEFAULT_TICK_START);
            float tickEnd = ta.getFloat(1, DEFAULT_TICK_END);
            float tickInterval = ta.getFloat(2, DEFAULT_TICK_INTERVAL);
            int tickCount = ((int) ((tickEnd - tickStart) / tickInterval)) + 1;
            if (isValidTickCount(tickCount)) {
                this.mTickCount = tickCount;
                this.mTickStart = tickStart;
                this.mTickEnd = tickEnd;
                this.mTickInterval = tickInterval;
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount + DEFAULT_TEXT_COLOR;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex,
                            this.mRightIndex, getPinValue(this.mLeftIndex),
                            getPinValue(this.mRightIndex));
                }
            } else {
            }
            this.mTickHeightDP = ta.getDimension(3, DEFAULT_TICK_INTERVAL);
            this.mBarWeight = ta.getDimension(5, DEFAULT_BAR_WEIGHT_PX);
            this.mBarColor = ta.getColor(6, DEFAULT_BAR_COLOR);
            this.mTextColor = ta.getColor(7, DEFAULT_TEXT_COLOR);
            this.mPinColor = ta.getColor(8, DEFAULT_PIN_COLOR);
            this.mActiveBarColor = this.mBarColor;
            this.mCircleSize = ta.getDimension(9, TypedValue.applyDimension(1,
                    DEFAULT_TICK_END, getResources().getDisplayMetrics()));
            this.mCircleColor = ta.getColor(14, DEFAULT_PIN_COLOR);
            this.mActiveCircleColor = this.mCircleColor;
            this.mTickColor = ta.getColor(4, DEFAULT_TICK_COLOR);
            this.mActiveTickColor = this.mTickColor;
            this.mConnectingLineWeight = ta.getDimension(17,
                    DEFAULT_CONNECTING_LINE_WEIGHT_PX);
            this.mConnectingLineColor = ta.getColor(18, DEFAULT_PIN_COLOR);
            this.mActiveConnectingLineColor = this.mConnectingLineColor;
            this.mExpandedPinRadius = ta.getDimension(19, TypedValue
                    .applyDimension(1, DEFAULT_EXPANDED_PIN_RADIUS_DP,
                            getResources().getDisplayMetrics()));
            this.mPinPadding = ta
                    .getDimension(10, TypedValue.applyDimension(1,
                            DEFAULT_PIN_PADDING_DP, getResources()
                                    .getDisplayMetrics()));
            this.mBarPaddingBottom = ta.getDimension(13, TypedValue
                    .applyDimension(1, DEFAULT_MAX_PIN_FONT_SP, getResources()
                            .getDisplayMetrics()));
            this.mIsRangeBar = ta.getBoolean(15, true);
            this.mArePinsTemporary = ta.getBoolean(16, true);
            float density = getResources().getDisplayMetrics().density;
            this.mMinPinFont = ta.getDimension(11, DEFAULT_MIN_PIN_FONT_SP
                    * density);
            this.mMaxPinFont = ta.getDimension(12, DEFAULT_MAX_PIN_FONT_SP
                    * density);
            this.mIsRangeBar = ta.getBoolean(15, true);
        } finally {
            ta.recycle();
        }
    }


    private void releasePin(final PinView pinView) {
        pinView.setX(this.mBar.getNearestTickCoordinate(pinView));
        pinView.setXValue(this.getPinValue(this.mBar.getNearestTickIndex(pinView)));
        if (this.mArePinsTemporary) {
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mExpandedPinRadius, 0.0f});
            ofFloat.addUpdateListener((ValueAnimator.AnimatorUpdateListener) new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    RangeBar.this.mThumbRadiusDP = (float) valueAnimator.getAnimatedValue();
                    pinView.setSize(RangeBar.this.mThumbRadiusDP, RangeBar.this.mPinPadding - RangeBar.this.mPinPadding * valueAnimator.getAnimatedFraction());
                    RangeBar.this.invalidate();
                }
            });
            ofFloat.start();
        } else {
            this.invalidate();
        }
        pinView.release();
    }

    private boolean valueOutOfRange(final float n, final float n2) {
        return n < this.mTickStart || n > this.mTickEnd || n2 < this.mTickStart || n2 > this.mTickEnd;
    }

    public int getLeftIndex() {
        return this.mLeftIndex;
    }

    public String getLeftPinValue() {
        return this.getPinValue(this.mLeftIndex);
    }

    public int getRightIndex() {
        return this.mRightIndex;
    }

    public String getRightPinValue() {
        return this.getPinValue(this.mRightIndex);
    }

    public int getTickCount() {
        return this.mTickCount;
    }

    public float getTickEnd() {
        return this.mTickEnd;
    }

    public double getTickInterval() {
        return this.mTickInterval;
    }

    public float getTickStart() {
        return this.mTickStart;
    }

    public boolean isRangeBar() {
        return this.mIsRangeBar;
    }

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        this.mBar.draw(canvas);
        if (this.mIsRangeBar) {
            this.mConnectingLine.draw(canvas, this.mLeftThumb, this.mRightThumb);
            if (this.drawTicks) {
                this.mBar.drawTicks(canvas);
            }
            this.mLeftThumb.draw(canvas);
        } else {
            this.mConnectingLine.draw(canvas, this.getMarginLeft(), this.mRightThumb);
            if (this.drawTicks) {
                this.mBar.drawTicks(canvas);
            }
        }
        this.mRightThumb.draw(canvas);
    }

    protected void onMeasure(int n, int mDefaultWidth) {
        final int mode = MeasureSpec.getMode(n);
        final int mode2 = MeasureSpec.getMode(mDefaultWidth);
        final int size = MeasureSpec.getSize(n);
        n = MeasureSpec.getSize(mDefaultWidth);
        if (mode == Integer.MIN_VALUE) {
            mDefaultWidth = size;
        } else if (mode == 1073741824) {
            mDefaultWidth = size;
        } else {
            mDefaultWidth = this.mDefaultWidth;
        }
        if (mode2 == Integer.MIN_VALUE) {
            n = Math.min(this.mDefaultHeight, n);
        } else if (mode2 != 1073741824) {
            n = this.mDefaultHeight;
        }
        this.setMeasuredDimension(mDefaultWidth, n);
    }

    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            final Bundle bundle = (Bundle) parcelable;
            this.mTickCount = bundle.getInt("TICK_COUNT");
            this.mTickStart = bundle.getFloat("TICK_START");
            this.mTickEnd = bundle.getFloat("TICK_END");
            this.mTickInterval = bundle.getFloat("TICK_INTERVAL");
            this.mTickColor = bundle.getInt("TICK_COLOR");
            this.mTickHeightDP = bundle.getFloat("TICK_HEIGHT_DP");
            this.mBarWeight = bundle.getFloat("BAR_WEIGHT");
            this.mBarColor = bundle.getInt("BAR_COLOR");
            this.mCircleSize = bundle.getFloat("CIRCLE_SIZE");
            this.mCircleColor = bundle.getInt("CIRCLE_COLOR");
            this.mConnectingLineWeight = bundle.getFloat("CONNECTING_LINE_WEIGHT");
            this.mConnectingLineColor = bundle.getInt("CONNECTING_LINE_COLOR");
            this.mThumbRadiusDP = bundle.getFloat("THUMB_RADIUS_DP");
            this.mExpandedPinRadius = bundle.getFloat("EXPANDED_PIN_RADIUS_DP");
            this.mPinPadding = bundle.getFloat("PIN_PADDING");
            this.mBarPaddingBottom = bundle.getFloat("BAR_PADDING_BOTTOM");
            this.mIsRangeBar = bundle.getBoolean("IS_RANGE_BAR");
            this.mArePinsTemporary = bundle.getBoolean("ARE_PINS_TEMPORARY");
            this.mLeftIndex = bundle.getInt("LEFT_INDEX");
            this.mRightIndex = bundle.getInt("RIGHT_INDEX");
            this.mFirstSetTickCount = bundle.getBoolean("FIRST_SET_TICK_COUNT");
            this.mMinPinFont = bundle.getFloat("MIN_PIN_FONT");
            this.mMaxPinFont = bundle.getFloat("MAX_PIN_FONT");
            this.setRangePinsByIndices(this.mLeftIndex, this.mRightIndex);
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("TICK_COUNT", this.mTickCount);
        bundle.putFloat("TICK_START", this.mTickStart);
        bundle.putFloat("TICK_END", this.mTickEnd);
        bundle.putFloat("TICK_INTERVAL", this.mTickInterval);
        bundle.putInt("TICK_COLOR", this.mTickColor);
        bundle.putFloat("TICK_HEIGHT_DP", this.mTickHeightDP);
        bundle.putFloat("BAR_WEIGHT", this.mBarWeight);
        bundle.putInt("BAR_COLOR", this.mBarColor);
        bundle.putFloat("CONNECTING_LINE_WEIGHT", this.mConnectingLineWeight);
        bundle.putInt("CONNECTING_LINE_COLOR", this.mConnectingLineColor);
        bundle.putFloat("CIRCLE_SIZE", this.mCircleSize);
        bundle.putInt("CIRCLE_COLOR", this.mCircleColor);
        bundle.putFloat("THUMB_RADIUS_DP", this.mThumbRadiusDP);
        bundle.putFloat("EXPANDED_PIN_RADIUS_DP", this.mExpandedPinRadius);
        bundle.putFloat("PIN_PADDING", this.mPinPadding);
        bundle.putFloat("BAR_PADDING_BOTTOM", this.mBarPaddingBottom);
        bundle.putBoolean("IS_RANGE_BAR", this.mIsRangeBar);
        bundle.putBoolean("ARE_PINS_TEMPORARY", this.mArePinsTemporary);
        bundle.putInt("LEFT_INDEX", this.mLeftIndex);
        bundle.putInt("RIGHT_INDEX", this.mRightIndex);
        bundle.putBoolean("FIRST_SET_TICK_COUNT", this.mFirstSetTickCount);
        bundle.putFloat("MIN_PIN_FONT", this.mMinPinFont);
        bundle.putFloat("MAX_PIN_FONT", this.mMaxPinFont);
        return (Parcelable) bundle;
    }

    protected void onSizeChanged(int nearestTickIndex, int nearestTickIndex2, final int n, final int n2) {
        super.onSizeChanged(nearestTickIndex, nearestTickIndex2, n, n2);
        final Context context = this.getContext();
        final float n3 = this.mExpandedPinRadius / this.getResources().getDisplayMetrics().density;
        final float n4 = nearestTickIndex2 - this.mBarPaddingBottom;
        if (this.mIsRangeBar) {
            (this.mLeftThumb = new PinView(context)).setFormatter(this.mFormatter);
            this.mLeftThumb.init(context, n4, n3, this.mPinColor, this.mTextColor, this.mCircleSize, this.mCircleColor, this.mMinPinFont, this.mMaxPinFont, this.mArePinsTemporary);
        }
        (this.mRightThumb = new PinView(context)).setFormatter(this.mFormatter);
        this.mRightThumb.init(context, n4, n3, this.mPinColor, this.mTextColor, this.mCircleSize, this.mCircleColor, this.mMinPinFont, this.mMaxPinFont, this.mArePinsTemporary);
        final float max = Math.max(this.mExpandedPinRadius, this.mCircleSize);
        final float n5 = nearestTickIndex - 2.0f * max;
        this.mBar = new Bar(context, max, n4, n5, this.mTickCount, this.mTickHeightDP, this.mTickColor, this.mBarWeight, this.mBarColor);
        if (this.mIsRangeBar) {
            this.mLeftThumb.setX(this.mLeftIndex / (this.mTickCount - 1) * n5 + max);
            this.mLeftThumb.setXValue(this.getPinValue(this.mLeftIndex));
        }
        this.mRightThumb.setX(max + this.mRightIndex / (this.mTickCount - 1) * n5);
        this.mRightThumb.setXValue(this.getPinValue(this.mRightIndex));
        if (this.mIsRangeBar) {
            nearestTickIndex = this.mBar.getNearestTickIndex(this.mLeftThumb);
        } else {
            nearestTickIndex = 0;
        }
        nearestTickIndex2 = this.mBar.getNearestTickIndex(this.mRightThumb);
        if ((nearestTickIndex != this.mLeftIndex || nearestTickIndex2 != this.mRightIndex) && this.mListener != null) {
            this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
        }
        this.mConnectingLine = new ConnectingLine(context, n4, this.mConnectingLineWeight, this.mConnectingLineColor);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (!this.isEnabled()) {
            return false;
        }
        switch (motionEvent.getAction()) {
            default: {
                return false;
            }
            case 3: {
                this.getParent().requestDisallowInterceptTouchEvent(false);
                this.onActionUp(motionEvent.getX(), motionEvent.getY());
                return true;
            }
            case 2: {
                this.onActionMove(motionEvent.getX());
                this.getParent().requestDisallowInterceptTouchEvent(true);
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                this.mDiffX += (int) Math.abs(x - this.mLastX);
                this.mDiffY += (int) Math.abs(y - this.mLastY);
                this.mLastX = x;
                this.mLastY = y;
                if (this.mDiffX < this.mDiffY) {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                return true;
            }
            case 1: {
                this.getParent().requestDisallowInterceptTouchEvent(false);
                this.onActionUp(motionEvent.getX(), motionEvent.getY());
                return true;
            }
            case 0: {
                this.mDiffX = 0;
                this.mDiffY = 0;
                this.mLastX = motionEvent.getX();
                this.mLastY = motionEvent.getY();
                this.onActionDown(motionEvent.getX(), motionEvent.getY());
                return true;
            }
        }
    }

    public void setBarColor(final int mBarColor) {
        this.mBarColor = mBarColor;
        this.createBar();
    }

    public void setBarWeight(final float mBarWeight) {
        this.mBarWeight = mBarWeight;
        this.createBar();
    }

    public void setConnectingLineColor(final int mConnectingLineColor) {
        this.mConnectingLineColor = mConnectingLineColor;
        this.createConnectingLine();
    }

    public void setConnectingLineWeight(final float mConnectingLineWeight) {
        this.mConnectingLineWeight = mConnectingLineWeight;
        this.createConnectingLine();
    }

    public void setDrawTicks(final boolean drawTicks) {
        this.drawTicks = drawTicks;
    }

    public void setEnabled(final boolean enabled) {
        if (!enabled) {
            this.mBarColor = -3355444;
            this.mConnectingLineColor = -3355444;
            this.mCircleColor = -3355444;
            this.mTickColor = -3355444;
        } else {
            this.mBarColor = this.mActiveBarColor;
            this.mConnectingLineColor = this.mActiveConnectingLineColor;
            this.mCircleColor = this.mActiveCircleColor;
            this.mTickColor = this.mActiveTickColor;
        }
        this.createBar();
        this.createPins();
        this.createConnectingLine();
        super.setEnabled(enabled);
    }

    public void setFormatter(final IRangeBarFormatter mFormatter) {
        if (this.mLeftThumb != null) {
            this.mLeftThumb.setFormatter(mFormatter);
        }
        if (this.mRightThumb != null) {
            this.mRightThumb.setFormatter(mFormatter);
        }
        this.mFormatter = mFormatter;
    }

    public void setOnRangeBarChangeListener(final OnRangeBarChangeListener mListener) {
        this.mListener = mListener;
    }

    public void setPinColor(final int mPinColor) {
        this.mPinColor = mPinColor;
        this.createPins();
    }

    public void setPinRadius(final float mExpandedPinRadius) {
        this.mExpandedPinRadius = mExpandedPinRadius;
        this.createPins();
    }

    public void setPinTextColor(final int mTextColor) {
        this.mTextColor = mTextColor;
        this.createPins();
    }

    public void setPinTextFormatter(final PinTextFormatter mPinTextFormatter) {
        this.mPinTextFormatter = mPinTextFormatter;
    }

    public void setPinTextListener(final OnRangeBarTextListener mPinTextListener) {
        this.mPinTextListener = mPinTextListener;
    }

    public void setRangeBarEnabled(final boolean mIsRangeBar) {
        this.mIsRangeBar = mIsRangeBar;
        this.invalidate();
    }

    public void setRangePinsByIndices(final int mLeftIndex, final int mRightIndex) {
        if (this.indexOutOfRange(mLeftIndex, mRightIndex)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Pin index left ");
            sb.append(mLeftIndex);
            sb.append(", or right ");
            sb.append(mRightIndex);
            sb.append(" is out of bounds. Check that it is greater than the minimum (");
            sb.append(this.mTickStart);
            sb.append(") and less than the maximum value (");
            sb.append(this.mTickEnd);
            sb.append(")");
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Pin index left ");
            sb2.append(mLeftIndex);
            sb2.append(", or right ");
            sb2.append(mRightIndex);
            sb2.append(" is out of bounds. Check that it is greater than the minimum (");
            sb2.append(this.mTickStart);
            sb2.append(") and less than the maximum value (");
            sb2.append(this.mTickEnd);
            sb2.append(")");
            throw new IllegalArgumentException(sb2.toString());
        }
        if (this.mFirstSetTickCount) {
            this.mFirstSetTickCount = false;
        }
        this.mLeftIndex = mLeftIndex;
        this.mRightIndex = mRightIndex;
        this.createPins();
        if (this.mListener != null) {
            this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
        }
        this.invalidate();
        this.requestLayout();
    }

    public void setRangePinsByValue(final float n, final float n2) {
        if (this.valueOutOfRange(n, n2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Pin value left ");
            sb.append(n);
            sb.append(", or right ");
            sb.append(n2);
            sb.append(" is out of bounds. Check that it is greater than the minimum (");
            sb.append(this.mTickStart);
            sb.append(") and less than the maximum value (");
            sb.append(this.mTickEnd);
            sb.append(")");
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Pin value left ");
            sb2.append(n);
            sb2.append(", or right ");
            sb2.append(n2);
            sb2.append(" is out of bounds. Check that it is greater than the minimum (");
            sb2.append(this.mTickStart);
            sb2.append(") and less than the maximum value (");
            sb2.append(this.mTickEnd);
            sb2.append(")");
            throw new IllegalArgumentException(sb2.toString());
        }
        if (this.mFirstSetTickCount) {
            this.mFirstSetTickCount = false;
        }
        this.mLeftIndex = (int) ((n - this.mTickStart) / this.mTickInterval);
        this.mRightIndex = (int) ((n2 - this.mTickStart) / this.mTickInterval);
        this.createPins();
        if (this.mListener != null) {
            this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
        }
        this.invalidate();
        this.requestLayout();
    }

    public void setSeekPinByIndex(final int mRightIndex) {
        if (mRightIndex >= 0 && mRightIndex <= this.mTickCount) {
            if (this.mFirstSetTickCount) {
                this.mFirstSetTickCount = false;
            }
            this.mRightIndex = mRightIndex;
            this.createPins();
            if (this.mListener != null) {
                this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
            }
            this.invalidate();
            this.requestLayout();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Pin index ");
        sb.append(mRightIndex);
        sb.append(" is out of bounds. Check that it is greater than the minimum (");
        sb.append(0);
        sb.append(") and less than the maximum value (");
        sb.append(this.mTickCount);
        sb.append(")");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Pin index ");
        sb2.append(mRightIndex);
        sb2.append(" is out of bounds. Check that it is greater than the minimum (");
        sb2.append(0);
        sb2.append(") and less than the maximum value (");
        sb2.append(this.mTickCount);
        sb2.append(")");
        throw new IllegalArgumentException(sb2.toString());
    }

    public void setSeekPinByValue(final float n) {
        if (n <= this.mTickEnd && n >= this.mTickStart) {
            if (this.mFirstSetTickCount) {
                this.mFirstSetTickCount = false;
            }
            this.mRightIndex = (int) ((n - this.mTickStart) / this.mTickInterval);
            this.createPins();
            if (this.mListener != null) {
                this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
            }
            this.invalidate();
            this.requestLayout();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Pin value ");
        sb.append(n);
        sb.append(" is out of bounds. Check that it is greater than the minimum (");
        sb.append(this.mTickStart);
        sb.append(") and less than the maximum value (");
        sb.append(this.mTickEnd);
        sb.append(")");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Pin value ");
        sb2.append(n);
        sb2.append(" is out of bounds. Check that it is greater than the minimum (");
        sb2.append(this.mTickStart);
        sb2.append(") and less than the maximum value (");
        sb2.append(this.mTickEnd);
        sb2.append(")");
        throw new IllegalArgumentException(sb2.toString());
    }

    public void setSelectorColor(final int mCircleColor) {
        this.mCircleColor = mCircleColor;
        this.createPins();
    }

    public void setTemporaryPins(final boolean mArePinsTemporary) {
        this.mArePinsTemporary = mArePinsTemporary;
        this.invalidate();
    }

    public void setTickColor(final int mTickColor) {
        this.mTickColor = mTickColor;
        this.createBar();
    }

    public void setTickEnd(final float mTickEnd) {
        final int mTickCount = (int) ((mTickEnd - this.mTickStart) / this.mTickInterval) + 1;
        if (this.isValidTickCount(mTickCount)) {
            this.mTickCount = mTickCount;
            this.mTickEnd = mTickEnd;
            if (this.mFirstSetTickCount) {
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount - 1;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
                }
            }
            if (this.indexOutOfRange(this.mLeftIndex, this.mRightIndex)) {
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount - 1;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
                }
            }
            this.createBar();
            this.createPins();
            return;
        }
        throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
    }

    public void setTickHeight(final float mTickHeightDP) {
        this.mTickHeightDP = mTickHeightDP;
        this.createBar();
    }

    public void setTickInterval(final float mTickInterval) {
        final int mTickCount = (int) ((this.mTickEnd - this.mTickStart) / mTickInterval) + 1;
        if (this.isValidTickCount(mTickCount)) {
            this.mTickCount = mTickCount;
            this.mTickInterval = mTickInterval;
            if (this.mFirstSetTickCount) {
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount - 1;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
                }
            }
            if (this.indexOutOfRange(this.mLeftIndex, this.mRightIndex)) {
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount - 1;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
                }
            }
            this.createBar();
            this.createPins();
            return;
        }
        throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
    }

    public void setTickStart(final float mTickStart) {
        final int mTickCount = (int) ((this.mTickEnd - mTickStart) / this.mTickInterval) + 1;
        if (this.isValidTickCount(mTickCount)) {
            this.mTickCount = mTickCount;
            this.mTickStart = mTickStart;
            if (this.mFirstSetTickCount) {
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount - 1;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
                }
            }
            if (this.indexOutOfRange(this.mLeftIndex, this.mRightIndex)) {
                this.mLeftIndex = 0;
                this.mRightIndex = this.mTickCount - 1;
                if (this.mListener != null) {
                    this.mListener.onRangeChangeListener(this, this.mLeftIndex, this.mRightIndex, this.getPinValue(this.mLeftIndex), this.getPinValue(this.mRightIndex));
                }
            }
            this.createBar();
            this.createPins();
            return;
        }
        throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
    }

    public interface OnRangeBarChangeListener {
        void onRangeChangeListener(final RangeBar p0, final int p1, final int p2, final String p3, final String p4);
    }

    public interface OnRangeBarTextListener {
        String getPinValue(final RangeBar p0, final int p1);
    }

    public interface PinTextFormatter {
        String getText(final String p0);
    }
}
