package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cybertechinfosoft.photoslideshowwithmusic.R;


public class ExpandIconView extends View {
    private static final long DEFAULT_ANIMATION_DURATION = 150;
    private static final float DELTA_ALPHA = 90.0f;
    private static final int INTERMEDIATE = 2;
    public static final int LESS = 1;
    public static final int MORE = 0;
    private static final float MORE_STATE_ALPHA = -45.0f;
    private static final float PADDING_PROPORTION = 0.16666667f;
    private static final float THICKNESS_PROPORTION = 0.1388889f;
    private float alpha;
    private final float anim_speed;
    @Nullable
    private ValueAnimator arrow_anim;
    private final Point point_center;
    private float center_translation;
    private int color;
    private final int color_less;
    private final int color_more;
    @FloatRange(from = 0.0d, to = 1.0d)
    private float fraction;
    private final Point point_left;
    private int padding;
    @NonNull
    private final Paint pnt;
    private final Path path;
    private final Point point_right;
    private int state;
    private boolean switch_color;
    private final Point temp_left;
    private final Point temp_right;
    private final boolean default_padding;

    class addUpdates implements AnimatorUpdateListener {
        private final ArgbEvaluator colorEvaluator;

        addUpdates() {
            this.colorEvaluator = new ArgbEvaluator();
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            ExpandIconView.this.alpha = ((Float) valueAnimator
                    .getAnimatedValue()).floatValue();
            ExpandIconView.this.updateArrowPath();
            if (ExpandIconView.this.switch_color) {
                ExpandIconView.this.updateColor(this.colorEvaluator);
            }
            ExpandIconView.this.postInvalidateOnAnimationCompat();
        }
    }

    public ExpandIconView(@NonNull Context context) {
        this(context, null);
    }

    public ExpandIconView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, MORE);
    }

    public ExpandIconView(@NonNull Context context,
                          @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alpha = MORE_STATE_ALPHA;
        this.center_translation = 0.0f;
        this.fraction =1.0f;
        this.switch_color = false;
        this.color = Color.BLACK;
        this.point_left = new Point();
        this.point_right = new Point();
        this.point_center = new Point();
        this.temp_left = new Point();
        this.temp_right = new Point();
        this.path = new Path();
        TypedArray array = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ExpandIconView, MORE, MORE);
        try {
            boolean roundedCorners = array.getBoolean(MORE, false);
            this.switch_color = array.getBoolean(LESS, false);
            this.color = array.getColor(INTERMEDIATE, -1);
            this.color_more = array.getColor(3, -1);
            this.color_less = array.getColor(4, -1);
            long animationDuration = (long) array.getInteger(5, 150);
            this.padding = array.getDimensionPixelSize(6, -1);
            this.default_padding = this.padding == -1;
            this.pnt = new Paint(LESS);
            this.pnt.setColor(this.color);
            this.pnt.setStyle(Style.STROKE);
            this.pnt.setDither(true);
            if (roundedCorners) {
                this.pnt.setStrokeJoin(Join.ROUND);
                this.pnt.setStrokeCap(Cap.ROUND);
            }
            this.anim_speed = DELTA_ALPHA / ((float) animationDuration);
            setState(LESS, false);
        } finally {
            array.recycle();
        }
    }

    public void switchState() {
        switchState(true);
    }

    public void switchState(boolean animate) {
        int newState;
        switch (this.state) {
            case MORE:
                newState = LESS;
                break;
            case LESS:
                newState = MORE;
                break;
            case INTERMEDIATE:
                newState = getFinalStateByFraction();
                break;
            default:
                throw new IllegalArgumentException("Unknown state [" + this.state
                        + "]");
        }
        setState(newState, animate);
    }

    public void setState(int state, boolean animate) {
        this.state = state;
        if (state == 0) {
            this.fraction = 0.0f;
        } else if (state == LESS) {
            this.fraction =1.0f;
        } else {
            throw new IllegalArgumentException(
                    "Unknown state, must be one of STATE_MORE = 0,  STATE_LESS = 1");
        }
        updateArrow(animate);
    }

    public void setFraction(@FloatRange(from = 0.0d, to = 1.0d) float fraction,
                            boolean animate) {
        if (fraction < 0.0f || fraction >1.0f) {
            throw new IllegalArgumentException(
                    "Fraction value must be from 0 to 1f, fraction=" + fraction);
        } else if (this.fraction != fraction) {
            this.fraction = fraction;
            if (fraction == 0.0f) {
                this.state = MORE;
            } else if (fraction ==1.0f) {
                this.state = LESS;
            } else {
                this.state = INTERMEDIATE;
            }
            updateArrow(animate);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0.0f, this.center_translation);
        canvas.drawPath(this.path, this.pnt);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateArrowMetrics();
        updateArrowPath();
    }

    private void calculateArrowMetrics() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int arrowMaxHeight = height - (this.padding * INTERMEDIATE);
        int arrowWidth = width - (this.padding * INTERMEDIATE);
        if (arrowMaxHeight < arrowWidth) {
            arrowWidth = arrowMaxHeight;
        }
        if (this.default_padding) {
            this.padding = (int) (PADDING_PROPORTION * ((float) width));
        }
        this.pnt
                .setStrokeWidth((float) ((int) (((float) arrowWidth) * THICKNESS_PROPORTION)));
        this.point_center.set(width / INTERMEDIATE, height / INTERMEDIATE);
        this.point_left.set(this.point_center.x - (arrowWidth / INTERMEDIATE),
                this.point_center.y);
        this.point_right.set(this.point_center.x + (arrowWidth / INTERMEDIATE),
                this.point_center.y);
    }

    private void updateArrow(boolean animate) {
        float toAlpha = MORE_STATE_ALPHA + (this.fraction * DELTA_ALPHA);
        if (animate) {
            animateArrow(toAlpha);
            return;
        }
        cancelAnimation();
        this.alpha = toAlpha;
        if (this.switch_color) {
            updateColor(new ArgbEvaluator());
        }
        updateArrowPath();
        invalidate();
    }

    private void updateArrowPath() {
        this.path.reset();
        if (this.point_left != null && this.point_right != null) {
            rotate(this.point_left, (double) (-this.alpha), this.temp_left);
            rotate(this.point_right, (double) this.alpha, this.temp_right);
            this.center_translation = (float) ((this.point_center.y - this.temp_left.y) / INTERMEDIATE);
            this.path.moveTo((float) this.temp_left.x, (float) this.temp_left.y);
            this.path.lineTo((float) this.point_center.x, (float) this.point_center.y);
            this.path
                    .lineTo((float) this.temp_right.x, (float) this.temp_right.y);
        }
    }

    private void animateArrow(float toAlpha) {
        cancelAnimation();
        float[] fArr = new float[INTERMEDIATE];
        fArr[MORE] = this.alpha;
        fArr[LESS] = toAlpha;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
        valueAnimator.addUpdateListener(new addUpdates());
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(calculateAnimationDuration(toAlpha));
        valueAnimator.start();
        this.arrow_anim = valueAnimator;
    }

    private void cancelAnimation() {
        if (this.arrow_anim != null && this.arrow_anim.isRunning()) {
            this.arrow_anim.cancel();
        }
    }

    private void updateColor(@NonNull ArgbEvaluator colorEvaluator) {
        this.color = ((Integer) colorEvaluator.evaluate((this.alpha + 45.0f)
                        / DELTA_ALPHA, Integer.valueOf(this.color_more),
                Integer.valueOf(this.color_less))).intValue();
        this.pnt.setColor(this.color);
    }

    private long calculateAnimationDuration(float toAlpha) {
        return (long) (Math.abs(toAlpha - this.alpha) / this.anim_speed);
    }

    private void rotate(@NonNull Point startPosition, double degrees,
                        @NonNull Point target) {
        double angle = Math.toRadians(degrees);
        target.set(
                (int) ((((double) this.point_center.x) + (((double) (startPosition.x - this.point_center.x)) * Math
                        .cos(angle))) - (((double) (startPosition.y - this.point_center.y)) * Math
                        .sin(angle))),
                (int) ((((double) this.point_center.y) + (((double) (startPosition.x - this.point_center.x)) * Math
                        .sin(angle))) + (((double) (startPosition.y - this.point_center.y)) * Math
                        .cos(angle))));
    }

    private int getFinalStateByFraction() {
        if (this.fraction <= 0.5f) {
            return MORE;
        }
        return LESS;
    }

    private void postInvalidateOnAnimationCompat() {
        if (VERSION.SDK_INT > 15) {
            postInvalidateOnAnimation();
        } else {
            postInvalidateDelayed(10);
        }
    }
}
