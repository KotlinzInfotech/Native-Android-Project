package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import com.cybertechinfosoft.photoslideshowwithmusic.R;


@SuppressLint({"NewApi"})
public class VerticalSlidingPanel extends ViewGroup {
    private static int[] f10x1b1434 = null;
    private static final int[] DEFAULT_ATTRS;
    private static final int DEFAULT_FADE_COLOR = -1728053248;
    private static final int DEFAULT_MIN_FLING_VELOCITY = 400;
    private static final boolean DEFAULT_OVERLAY_FLAG = false;
    private static final int DEFAULT_PANEL_HEIGHT = 68;
    private static final int DEFAULT_PARALAX_OFFSET = 0;
    private static final int DEFAULT_SHADOW_HEIGHT = 4;
    private static final String TAG = "VerticalSlidingPanel";
    private float mAnchorPoint;
    private TranslateAnimation mAnimation;
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    private final Paint mCoveredFadePaint;
    private final ViewDragHelper mDragHelper;
    private View mDragView;
    private int mDragViewResId;
    private boolean mFirstLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsSlidingEnabled;
    private boolean mIsSlidingUp;
    private boolean mIsUnableToDrag;
    private boolean mIsUsingDragViewTouchEvents;
    private View mMainView;
    private int mMinFlingVelocity;
    private boolean mOverlayContent;
    private int mPanelHeight;
    private PanelSlideListener mPanelSlideListener;
    private int mParalaxOffset;
    private final int mScrollTouchSlop;
    private final Drawable mShadowDrawable;
    private int mShadowHeight;
    private float mSlideOffset;
    private int mSlideRange;
    private SlideState mSlideState;
    private View mSlideableView;
    private final Rect mTmpRect;

    static {
        DEFAULT_ATTRS = new int[]{16842927};
    }

    public VerticalSlidingPanel(final Context context) {
        this(context, null);
    }

    public VerticalSlidingPanel(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }

    public VerticalSlidingPanel(final Context context, final AttributeSet set, int int1) {
        super(context, set, int1);
        this.mMinFlingVelocity = 400;
        this.mCoveredFadeColor = -1728053248;
        this.mCoveredFadePaint = new Paint();
        this.mPanelHeight = -1;
        this.mShadowHeight = -1;
        this.mParalaxOffset = -1;
        this.mOverlayContent = false;
        this.mDragViewResId = -1;
        this.mSlideState = SlideState.COLLAPSED;
        this.mAnchorPoint = 0.0f;
        this.mFirstLayout = true;
        this.mTmpRect = new Rect();
        if (set != null) {
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, VerticalSlidingPanel.DEFAULT_ATTRS);
            if (obtainStyledAttributes != null) {
                int1 = obtainStyledAttributes.getInt(0, 0);
                if (int1 != 48 && int1 != 80) {
                    throw new IllegalArgumentException("gravity must be set to either top or bottom");
                }
                this.mIsSlidingUp = (int1 == 80);
            }
            obtainStyledAttributes.recycle();
            final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R.styleable.VerticalSlidingPanel);
            if (obtainStyledAttributes2 != null) {
                this.mPanelHeight = obtainStyledAttributes2.getDimensionPixelSize(4, -1);
                this.mShadowHeight = obtainStyledAttributes2.getDimensionPixelSize(7, -1);
                this.mParalaxOffset = obtainStyledAttributes2.getDimensionPixelSize(5, -1);
                this.mMinFlingVelocity = obtainStyledAttributes2.getInt(2, 400);
                this.mCoveredFadeColor = obtainStyledAttributes2.getColor(1, -1728053248);
                this.mDragViewResId = obtainStyledAttributes2.getResourceId(0, -1);
                this.mOverlayContent = obtainStyledAttributes2.getBoolean(3, false);
            }
            obtainStyledAttributes2.recycle();
        }
        final float density = context.getResources().getDisplayMetrics().density;
        if (this.mPanelHeight == -1) {
            this.mPanelHeight = (int) (68.0f * density + 0.5f);
        }
        if (this.mShadowHeight == -1) {
            this.mShadowHeight = (int) (4.0f * density + 0.5f);
        }
        if (this.mParalaxOffset == -1) {
            this.mParalaxOffset = (int) (0.0f * density);
        }
        if (this.mShadowHeight > 0) {
            if (this.mIsSlidingUp) {
                this.mShadowDrawable = this.getResources().getDrawable(R.drawable.above_shadow);
            } else {
                this.mShadowDrawable = this.getResources().getDrawable(R.drawable.below_shadow);
            }
        } else {
            this.mShadowDrawable = null;
        }
        this.setWillNotDraw(false);
        (this.mDragHelper = ViewDragHelper.create((ViewGroup) this, 0.5f, (ViewDragHelper.Callback) new DragHelperCallback())).setMinVelocity(this.mMinFlingVelocity * density);
        this.mCanSlide = true;
        this.mIsSlidingEnabled = true;
        this.mScrollTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private boolean collapsePane(final View view, final int n) {
        return this.mFirstLayout || this.smoothSlideTo(1.0f, n);
    }

    private boolean expandPane(final View view, final int n, final float n2) {
        return this.mFirstLayout || this.smoothSlideTo(n2, n);
    }

    private int getSlidingTop() {
        if (this.mSlideableView == null) {
            return this.getMeasuredHeight() - this.getPaddingBottom();
        }
        if (this.mIsSlidingUp) {
            return this.getMeasuredHeight() - this.getPaddingBottom() - this.mSlideableView.getMeasuredHeight();
        }
        return this.getPaddingTop();
    }

    private static boolean hasOpaqueBackground(final View view) {
        final Drawable background = view.getBackground();
        boolean b = false;
        if (background != null) {
            if (background.getOpacity() == -1) {
                b = true;
            }
            return b;
        }
        return false;
    }

    private boolean isDragViewUnder(int n, int n2) {
        View view;
        if (this.mDragView != null) {
            view = this.mDragView;
        } else {
            view = this.mSlideableView;
        }
        if (view == null) {
            return false;
        }
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        final int[] array2 = new int[2];
        this.getLocationOnScreen(array2);
        n += array2[0];
        n2 += array2[1];
        return n >= array[0] && n < array[0] + view.getWidth() && n2 >= array[1] && n2 < array[1] + view.getHeight();
    }

    private void onPanelDragged(int currentParalaxOffset) {
        final int slidingTop = this.getSlidingTop();
        float n;
        if (this.mIsSlidingUp) {
            n = currentParalaxOffset - slidingTop;
        } else {
            n = slidingTop - currentParalaxOffset;
        }
        this.mSlideOffset = n / this.mSlideRange;
        this.dispatchOnPanelSlide(this.mSlideableView);
        if (this.mParalaxOffset > 0) {
            currentParalaxOffset = this.getCurrentParalaxOffset();
            if (Build.VERSION.SDK_INT >= 11) {
                this.mMainView.setTranslationY((float) currentParalaxOffset);
                return;
            }
            this.mMainView.animate().translationY((float) currentParalaxOffset);
        }
    }

    protected boolean canScroll(final View view, final boolean b, final int n, final int n2, final int n3) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;
            final int scrollX = view.getScrollX();
            final int scrollY = view.getScrollY();
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                final int n4 = n2 + scrollX;
                if (n4 >= child.getLeft() && n4 < child.getRight()) {
                    final int n5 = n3 + scrollY;
                    if (n5 >= child.getTop() && n5 < child.getBottom() && this.canScroll(child, true, n, n4 - child.getLeft(), n5 - child.getTop())) {
                        return true;
                    }
                }
            }
        }
        return b && ViewCompat.canScrollHorizontally(view, -n);
    }

    protected boolean checkLayoutParams(final ViewGroup.LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }

    public boolean collapsePane() {
        return this.collapsePane(this.mSlideableView, 0);
    }

    public void computeScroll() {
        if (this.mDragHelper.continueSettling(true)) {
            if (!this.mCanSlide) {
                this.mDragHelper.abort();
                return;
            }
            ViewCompat.postInvalidateOnAnimation((View) this);
        }
    }

    void dispatchOnPanelAnchored(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelAnchored(view);
        }
        this.sendAccessibilityEvent(32);
    }

    void dispatchOnPanelCollapsed(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelCollapsed(view);
        }
        this.sendAccessibilityEvent(32);
    }

    void dispatchOnPanelExpanded(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelExpanded(view);
        }
        this.sendAccessibilityEvent(32);
    }

    void dispatchOnPanelSlide(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelSlide(view, this.mSlideOffset);
        }
    }

    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (this.mSlideableView == null) {
            return;
        }
        final int right = this.mSlideableView.getRight();
        int bottom;
        int top;
        if (this.mIsSlidingUp) {
            bottom = this.mSlideableView.getTop() - this.mShadowHeight;
            top = this.mSlideableView.getTop();
        } else {
            bottom = this.mSlideableView.getBottom();
            top = this.mSlideableView.getBottom() + this.mShadowHeight;
        }
        final int left = this.mSlideableView.getLeft();
        if (this.mShadowDrawable != null) {
            this.mShadowDrawable.setBounds(left, bottom, right, top);
            this.mShadowDrawable.draw(canvas);
        }
    }

    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        final int save = canvas.save();
        boolean b = false;
        Label_0139:
        {
            if (this.mCanSlide && !layoutParams.mSlideableViewable && this.mSlideableView != null) {
                if (!this.mOverlayContent) {
                    canvas.getClipBounds(this.mTmpRect);
                    if (this.mIsSlidingUp) {
                        this.mTmpRect.bottom = Math.min(this.mTmpRect.bottom, this.mSlideableView.getTop());
                    } else {
                        this.mTmpRect.top = Math.max(this.mTmpRect.top, this.mSlideableView.getBottom());
                    }
                    canvas.clipRect(this.mTmpRect);
                }
                if (this.mSlideOffset < 1.0f) {
                    b = true;
                    break Label_0139;
                }
            }
            b = false;
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        if (b) {
            this.mCoveredFadePaint.setColor((int) (((this.mCoveredFadeColor & 0xFF000000) >>> 24) * (1.0f - this.mSlideOffset)) << 24 | (this.mCoveredFadeColor & 0xFFFFFF));
            canvas.drawRect(this.mTmpRect, this.mCoveredFadePaint);
        }
        return drawChild;
    }

    public boolean expandPane() {
        return this.expandPane(0.0f);
    }

    public boolean expandPane(final float n) {
        if (!this.isPaneVisible()) {
            this.showPane();
        }
        return this.expandPane(this.mSlideableView, 0, n);
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup.LayoutParams) new LayoutParams();
    }

    public ViewGroup.LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup.LayoutParams) new LayoutParams(this.getContext(), set);
    }

    protected ViewGroup.LayoutParams generateLayoutParams(final ViewGroup.LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof MarginLayoutParams) {
            return (ViewGroup.LayoutParams) new LayoutParams((MarginLayoutParams) viewGroup$LayoutParams);
        }
        return (ViewGroup.LayoutParams) new LayoutParams(viewGroup$LayoutParams);
    }

    public int getCoveredFadeColor() {
        return this.mCoveredFadeColor;
    }

    public int getCurrentParalaxOffset() {
        int n = (int) (this.mParalaxOffset * (1.0f - this.mSlideOffset));
        if (this.mIsSlidingUp) {
            n = -n;
        }
        return n;
    }

    public int getPanelHeight() {
        return this.mPanelHeight;
    }

    public void hidePane() {
        if (this.mSlideableView == null) {
            return;
        }
        int n;
        if (this.mIsSlidingUp) {
            n = 1;
        } else {
            n = -1;
        }
        (this.mAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (n * this.getPanelHeight()))).setDuration(500L);
        this.mAnimation.setAnimationListener((Animation.AnimationListener) new Animation.AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                VerticalSlidingPanel.this.mSlideableView.setVisibility(View.GONE);
                VerticalSlidingPanel.this.requestLayout();
                VerticalSlidingPanel.this.mAnimation = null;
            }

            public void onAnimationRepeat(final Animation animation) {
            }

            public void onAnimationStart(final Animation animation) {
            }
        });
        this.mSlideableView.startAnimation((Animation) this.mAnimation);
    }

    public boolean isAnchored() {
        return this.mSlideState == SlideState.ANCHORED;
    }

    public boolean isExpanded() {
        return this.mSlideState == SlideState.EXPANDED;
    }

    public boolean isOverlayed() {
        return this.mOverlayContent;
    }

    public boolean isPaneVisible() {
        return this.getChildCount() >= 2 && this.getChildAt(1).getVisibility() == 0;
    }

    public boolean isSlideable() {
        return this.mCanSlide;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.mDragViewResId != -1) {
            this.mDragView = this.findViewById(this.mDragViewResId);
        }
    }

    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        if (this.mAnimation != null || !this.mCanSlide || !this.mIsSlidingEnabled || (this.mIsUnableToDrag && actionMasked != 0)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (actionMasked != 3 && actionMasked != 1) {
            final float x = motionEvent.getX();
            final float y = motionEvent.getY();
            if (actionMasked != 0) {
                if (actionMasked == 2) {
                    final float abs = Math.abs(x - this.mInitialMotionX);
                    final float abs2 = Math.abs(y - this.mInitialMotionY);
                    final int touchSlop = this.mDragHelper.getTouchSlop();
                    if (this.mIsUsingDragViewTouchEvents) {
                        if (abs > this.mScrollTouchSlop && abs2 < this.mScrollTouchSlop) {
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                        if (abs2 > this.mScrollTouchSlop) {
                            this.isDragViewUnder((int) x, (int) y);
                        }
                    }
                    if ((abs2 > touchSlop && abs > abs2) || !this.isDragViewUnder((int) x, (int) y)) {
                        this.mDragHelper.cancel();
                        this.mIsUnableToDrag = true;
                        return false;
                    }
                }
            } else {
                this.mIsUnableToDrag = false;
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (this.isDragViewUnder((int) x, (int) y)) {
                    final boolean mIsUsingDragViewTouchEvents = this.mIsUsingDragViewTouchEvents;
                }
            }
            return this.mDragHelper.shouldInterceptTouchEvent(motionEvent);
        }
        this.mDragHelper.cancel();
        return false;
    }


    static int[] m17xee1b1434() {
        int[] iArr = f10x1b1434;
        if (iArr == null) {
            iArr = new int[SlideState.values().length];
            try {
                iArr[SlideState.ANCHORED.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[SlideState.COLLAPSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[SlideState.EXPANDED.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            f10x1b1434 = iArr;
        }
        return iArr;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int slidingTop = getSlidingTop();
        int childCount = getChildCount();
        if (this.mFirstLayout) {
            switch (m17xee1b1434()[this.mSlideState.ordinal()]) {
                case 1 /* 1 */:
                    this.mSlideOffset = this.mCanSlide ? 0.0f
                            : 1.0f;
                    break;
                case 3 /* 3 */:
                    this.mSlideOffset = this.mCanSlide ? this.mAnchorPoint
                            : 1.0f;
                    break;
                default:
                    this.mSlideOffset = 1.0f;
                    break;
            }
        }
        for (int i = DEFAULT_PARALAX_OFFSET; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childTop;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childHeight = child.getMeasuredHeight();
                if (lp.mSlideableViewable) {
                    this.mSlideRange = childHeight - this.mPanelHeight;
                }
                if (!this.mIsSlidingUp) {
                    if (lp.mSlideableViewable) {
                        childTop = slidingTop
                                - ((int) (((float) this.mSlideRange) * this.mSlideOffset));
                    } else {
                        childTop = paddingTop;
                    }
                    if (!(lp.mSlideableViewable || this.mOverlayContent)) {
                        childTop += this.mPanelHeight;
                    }
                } else if (lp.mSlideableViewable) {
                    childTop = slidingTop
                            + ((int) (((float) this.mSlideRange) * this.mSlideOffset));
                } else {
                    childTop = paddingTop;
                }
                int childLeft = paddingLeft;
                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + childHeight);
            }
        }
        if (this.mFirstLayout) {
            updateObscuredViewVisibility();
        }
        this.mFirstLayout = DEFAULT_OVERLAY_FLAG;
    }

    protected void onMeasure(int n, int n2) {
        final int mode = MeasureSpec.getMode(n);
        final int size = MeasureSpec.getSize(n);
        n = MeasureSpec.getMode(n2);
        final int size2 = MeasureSpec.getSize(n2);
        if (mode != 1073741824) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        }
        if (n != 1073741824) {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }
        final int n3 = size2 - this.getPaddingTop() - this.getPaddingBottom();
        int mPanelHeight = this.mPanelHeight;
        final int childCount = this.getChildCount();
        if (childCount > 2) {
        } else if (this.getChildAt(1).getVisibility() == GONE) {
            mPanelHeight = 0;
        }
        this.mSlideableView = null;
        this.mCanSlide = false;
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() == GONE) {
                layoutParams.dimWhenOffset = false;
            } else {
                if (i == 1) {
                    layoutParams.mSlideableViewable = true;
                    layoutParams.dimWhenOffset = true;
                    this.mSlideableView = child;
                    this.mCanSlide = true;
                    n2 = n3;
                } else {
                    if (!this.mOverlayContent) {
                        n = n3 - mPanelHeight;
                    } else {
                        n = n3;
                    }
                    this.mMainView = child;
                    n2 = n;
                }
                if (layoutParams.width == -2) {
                    n = MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
                } else if (layoutParams.width == -1) {
                    n = MeasureSpec.makeMeasureSpec(size, 1073741824);
                } else {
                    n = MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
                }
                if (layoutParams.height == -2) {
                    n2 = MeasureSpec.makeMeasureSpec(n2, Integer.MIN_VALUE);
                } else if (layoutParams.height == -1) {
                    n2 = MeasureSpec.makeMeasureSpec(n2, 1073741824);
                } else {
                    n2 = MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824);
                }
                child.measure(n, n2);
            }
        }
        this.setMeasuredDimension(size, size2);
    }

    protected void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mSlideState = savedState.mSlideState;
    }

    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mSlideState = this.mSlideState;
        return (Parcelable) savedState;
    }

    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n2 != n4) {
            this.mFirstLayout = true;
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mCanSlide && this.mIsSlidingEnabled && this.mAnimation == null) {
            this.mDragHelper.processTouchEvent(motionEvent);
            switch (motionEvent.getAction() & 0xFF) {
                default: {
                    return true;
                }
                case 1: {
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    final float n = x - this.mInitialMotionX;
                    final float n2 = y - this.mInitialMotionY;
                    final int touchSlop = this.mDragHelper.getTouchSlop();
                    View view;
                    if (this.mDragView != null) {
                        view = this.mDragView;
                    } else {
                        view = this.mSlideableView;
                    }
                    if (n * n + n2 * n2 >= touchSlop * touchSlop || !this.isDragViewUnder((int) x, (int) y)) {
                        break;
                    }
                    view.playSoundEffect(0);
                    if (!this.isExpanded() && !this.isAnchored()) {
                        this.expandPane(this.mAnchorPoint);
                        return true;
                    }
                    this.collapsePane();
                    return true;
                }
                case 0: {
                    final float x2 = motionEvent.getX();
                    final float y2 = motionEvent.getY();
                    if (this.mSlideState == SlideState.COLLAPSED && y2 < this.mSlideableView.getTop()) {
                        return false;
                    }
                    this.mInitialMotionX = x2;
                    this.mInitialMotionY = y2;
                    break;
                }
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    void setAllChildrenVisible() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setAnchorPoint(final float mAnchorPoint) {
        if (mAnchorPoint > 0.0f && mAnchorPoint < 1.0f) {
            this.mAnchorPoint = mAnchorPoint;
        }
    }

    public void setCoveredFadeColor(final int mCoveredFadeColor) {
        this.mCoveredFadeColor = mCoveredFadeColor;
        this.invalidate();
    }

    public void setDragView(final View mDragView) {
        this.mDragView = mDragView;
    }

    public void setEnableDragViewTouchEvents(final boolean mIsUsingDragViewTouchEvents) {
        this.mIsUsingDragViewTouchEvents = mIsUsingDragViewTouchEvents;
    }

    public void setOverlayed(final boolean mOverlayContent) {
        this.mOverlayContent = mOverlayContent;
    }

    public void setPanelHeight(final int mPanelHeight) {
        this.mPanelHeight = mPanelHeight;
        this.requestLayout();
    }

    public void setPanelSlideListener(final PanelSlideListener mPanelSlideListener) {
        this.mPanelSlideListener = mPanelSlideListener;
    }

    public void setSlidingEnabled(final boolean mIsSlidingEnabled) {
        this.mIsSlidingEnabled = mIsSlidingEnabled;
    }

    public void showPane() {
        if (this.getChildCount() < 2) {
            return;
        }
        int n = 1;
        final View child = this.getChildAt(1);
        if (!this.mIsSlidingUp) {
            n = -1;
        }
        (this.mAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (n * this.getPanelHeight()), 0.0f)).setDuration(400L);
        this.mAnimation.setAnimationListener((Animation.AnimationListener) new Animation.AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                VerticalSlidingPanel.this.requestLayout();
                VerticalSlidingPanel.this.mAnimation = null;
            }

            public void onAnimationRepeat(final Animation animation) {
            }

            public void onAnimationStart(final Animation animation) {
                child.setVisibility(View.VISIBLE);
            }
        });
        child.startAnimation((Animation) this.mAnimation);
    }

    boolean smoothSlideTo(float n, int slidingTop) {
        if (!this.mCanSlide) {
            return false;
        }
        slidingTop = this.getSlidingTop();
        if (this.mIsSlidingUp) {
            n = slidingTop + n * this.mSlideRange;
        } else {
            n = slidingTop - n * this.mSlideRange;
        }
        slidingTop = (int) n;
        if (this.mDragHelper.smoothSlideViewTo(this.mSlideableView, this.mSlideableView.getLeft(), slidingTop)) {
            this.setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation((View) this);
            return true;
        }
        return false;
    }

    void updateObscuredViewVisibility() {
        if (this.getChildCount() == 0) {
            return;
        }
        final int paddingLeft = this.getPaddingLeft();
        final int width = this.getWidth();
        final int paddingRight = this.getPaddingRight();
        final int paddingTop = this.getPaddingTop();
        final int height = this.getHeight();
        final int paddingBottom = this.getPaddingBottom();
        final View mSlideableView = this.mSlideableView;
        final int n = 0;
        int left;
        int right;
        int top;
        int bottom;
        if (mSlideableView != null && hasOpaqueBackground(this.mSlideableView)) {
            left = this.mSlideableView.getLeft();
            right = this.mSlideableView.getRight();
            top = this.mSlideableView.getTop();
            bottom = this.mSlideableView.getBottom();
        } else {
            left = 0;
            right = 0;
            top = 0;
            bottom = 0;
        }
        final View child = this.getChildAt(0);
        final int max = Math.max(paddingLeft, child.getLeft());
        final int max2 = Math.max(paddingTop, child.getTop());
        final int min = Math.min(width - paddingRight, child.getRight());
        final int min2 = Math.min(height - paddingBottom, child.getBottom());
        int visibility = n;
        if (max >= left) {
            visibility = n;
            if (max2 >= top) {
                visibility = n;
                if (min <= right) {
                    visibility = n;
                    if (min2 <= bottom) {
                        visibility = 4;
                    }
                }
            }
        }
        child.setVisibility(visibility);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        public int clampViewPositionVertical(final View view, final int n, int access$1100) {
            int paddingTop;
            if (VerticalSlidingPanel.this.mIsSlidingUp) {
                access$1100 = VerticalSlidingPanel.this.getSlidingTop();
                paddingTop = VerticalSlidingPanel.this.mSlideRange + access$1100;
            } else {
                paddingTop = VerticalSlidingPanel.this.getPaddingTop();
                access$1100 = paddingTop - VerticalSlidingPanel.this.mSlideRange;
            }
            return Math.min(Math.max(n, access$1100), paddingTop);
        }

        public int getViewVerticalDragRange(final View view) {
            return VerticalSlidingPanel.this.mSlideRange;
        }

        public void onViewCaptured(final View view, final int n) {
            VerticalSlidingPanel.this.setAllChildrenVisible();
        }

        public void onViewDragStateChanged(int n) {
            n = (int) (VerticalSlidingPanel.this.mAnchorPoint * VerticalSlidingPanel.this.mSlideRange);
            if (VerticalSlidingPanel.this.mDragHelper.getViewDragState() == 0) {
                if (VerticalSlidingPanel.this.mSlideOffset == 0.0f) {
                    if (VerticalSlidingPanel.this.mSlideState != SlideState.EXPANDED) {
                        VerticalSlidingPanel.this.updateObscuredViewVisibility();
                        VerticalSlidingPanel.this.dispatchOnPanelExpanded(VerticalSlidingPanel.this.mSlideableView);
                        VerticalSlidingPanel.this.mSlideState = SlideState.EXPANDED;
                    }
                } else if (VerticalSlidingPanel.this.mSlideOffset == n / VerticalSlidingPanel.this.mSlideRange) {
                    if (VerticalSlidingPanel.this.mSlideState != SlideState.ANCHORED) {
                        VerticalSlidingPanel.this.updateObscuredViewVisibility();
                        VerticalSlidingPanel.this.dispatchOnPanelAnchored(VerticalSlidingPanel.this.mSlideableView);
                        VerticalSlidingPanel.this.mSlideState = SlideState.ANCHORED;
                    }
                } else if (VerticalSlidingPanel.this.mSlideState != SlideState.COLLAPSED) {
                    VerticalSlidingPanel.this.dispatchOnPanelCollapsed(VerticalSlidingPanel.this.mSlideableView);
                    VerticalSlidingPanel.this.mSlideState = SlideState.COLLAPSED;
                }
            }
        }

        public void onViewPositionChanged(final View view, final int n, final int n2, final int n3, final int n4) {
            VerticalSlidingPanel.this.onPanelDragged(n2);
            VerticalSlidingPanel.this.invalidate();
        }

        public void onViewReleased(final View view, float n, final float n2) {
            int access$1100;
            if (VerticalSlidingPanel.this.mIsSlidingUp) {
                access$1100 = VerticalSlidingPanel.this.getSlidingTop();
            } else {
                access$1100 = VerticalSlidingPanel.this.getSlidingTop() - VerticalSlidingPanel.this.mSlideRange;
            }
            int n3 = 0;
            Label_0309:
            {
                if (VerticalSlidingPanel.this.mAnchorPoint != 0.0f) {
                    if (VerticalSlidingPanel.this.mIsSlidingUp) {
                        n = (int) (VerticalSlidingPanel.this.mAnchorPoint * VerticalSlidingPanel.this.mSlideRange) / VerticalSlidingPanel.this.mSlideRange;
                    } else {
                        n = (VerticalSlidingPanel.this.mPanelHeight - (VerticalSlidingPanel.this.mPanelHeight - (int) (VerticalSlidingPanel.this.mAnchorPoint * VerticalSlidingPanel.this.mSlideRange))) / VerticalSlidingPanel.this.mSlideRange;
                    }
                    if (n2 <= 0.0f && (n2 != 0.0f || VerticalSlidingPanel.this.mSlideOffset < (n + 1.0f) / 2.0f)) {
                        n3 = access$1100;
                        if (n2 == 0.0f) {
                            n3 = access$1100;
                            if (VerticalSlidingPanel.this.mSlideOffset < (1.0f + n) / 2.0f) {
                                n3 = access$1100;
                                if (VerticalSlidingPanel.this.mSlideOffset >= n / 2.0f) {
                                    n3 = (int) (access$1100 + VerticalSlidingPanel.this.mSlideRange * VerticalSlidingPanel.this.mAnchorPoint);
                                }
                            }
                        }
                    } else {
                        n3 = access$1100 + VerticalSlidingPanel.this.mSlideRange;
                    }
                } else {
                    if (n2 <= 0.0f) {
                        n3 = access$1100;
                        if (n2 != 0.0f) {
                            break Label_0309;
                        }
                        n3 = access$1100;
                        if (VerticalSlidingPanel.this.mSlideOffset <= 0.5f) {
                            break Label_0309;
                        }
                    }
                    n3 = access$1100 + VerticalSlidingPanel.this.mSlideRange;
                }
            }
            VerticalSlidingPanel.this.mDragHelper.settleCapturedViewAt(view.getLeft(), n3);
            VerticalSlidingPanel.this.invalidate();
        }

        public boolean tryCaptureView(final View view, final int n) {
            return !VerticalSlidingPanel.this.mIsUnableToDrag && ((LayoutParams) view.getLayoutParams()).mSlideableViewable;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS;
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean mSlideableViewable;

        static {
            ATTRS = new int[]{16843137};
        }

        public LayoutParams() {
            super(-1, -1);
        }

        public LayoutParams(final int n, final int n2) {
            super(n, n2);
        }

        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            context.obtainStyledAttributes(set, LayoutParams.ATTRS).recycle();
        }

        public LayoutParams(final ViewGroup.LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }

        public LayoutParams(final MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
        }

        public LayoutParams(final LayoutParams layoutParams) {
            super((MarginLayoutParams) layoutParams);
        }
    }

    public interface PanelSlideListener {
        void onPanelAnchored(final View p0);

        void onPanelCollapsed(final View p0);

        void onPanelExpanded(final View p0);

        void onPanelShown(final View p0);

        void onPanelSlide(final View p0, final float p1);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        SlideState mSlideState;

        static {
            CREATOR = new Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }

                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }

        private SavedState(final Parcel parcel) {
            super(parcel);
            try {
                this.mSlideState = Enum.valueOf(SlideState.class,
                        parcel.readString());
            } catch (IllegalArgumentException ex) {
                this.mSlideState = SlideState.COLLAPSED;
            }
        }

        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeString(this.mSlideState.toString());
        }
    }

    public static class SimplePanelSlideListener implements PanelSlideListener {
        @Override
        public void onPanelAnchored(final View view) {
        }

        @Override
        public void onPanelCollapsed(final View view) {
        }

        @Override
        public void onPanelExpanded(final View view) {
        }

        @Override
        public void onPanelShown(final View view) {
        }

        @Override
        public void onPanelSlide(final View view, final float n) {
        }
    }

    private enum SlideState {
        ANCHORED,
        COLLAPSED,
        EXPANDED;
    }
}
