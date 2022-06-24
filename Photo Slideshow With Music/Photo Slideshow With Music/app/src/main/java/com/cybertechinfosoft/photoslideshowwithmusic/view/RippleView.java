package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

public class RippleView extends RelativeLayout {
    private int HEIGHT;
    private int WIDTH;
    private boolean animationRunning;
    private Handler canvasHandler;
    private int durationEmpty;
    private int frameRate;
    private GestureDetector gestureDetector;
    private Boolean hasToZoom;
    private Boolean isCentered;
    private OnRippleCompleteListener onCompletionListener;
    private Bitmap originBitmap;
    private Paint paint;
    private float radiusMax;
    private int rippleAlpha;
    private int rippleColor;
    private int rippleDuration;
    private int ripplePadding;
    private Integer rippleType;
    private final Runnable runnable;
    private ScaleAnimation scaleAnimation;
    private int timer;
    private int timerEmpty;
    private float x;
    private float y;
    private int zoomDuration;
    private float zoomScale;

    public RippleView(final Context context) {
        super(context);
        this.frameRate = 10;
        this.rippleDuration = 300;
        this.rippleAlpha = 90;
        this.radiusMax = 0.0f;
        this.animationRunning = false;
        this.timer = 0;
        this.timerEmpty = 0;
        this.durationEmpty = -1;
        this.x = -1.0f;
        this.y = -1.0f;
        this.runnable = new Runnable() {
            @Override
            public void run() {
                RippleView.this.invalidate();
            }
        };
    }

    public RippleView(final Context context, final AttributeSet set) {
        super(context, set);
        this.frameRate = 10;
        this.rippleDuration = 300;
        this.rippleAlpha = 90;
        this.radiusMax = 0.0f;
        this.animationRunning = false;
        this.timer = 0;
        this.timerEmpty = 0;
        this.durationEmpty = -1;
        this.x = -1.0f;
        this.y = -1.0f;
        this.runnable = new Runnable() {
            @Override
            public void run() {
                RippleView.this.invalidate();
            }
        };
        this.init(context, set);
    }

    public RippleView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.frameRate = 10;
        this.rippleDuration = 300;
        this.rippleAlpha = 90;
        this.radiusMax = 0.0f;
        this.animationRunning = false;
        this.timer = 0;
        this.timerEmpty = 0;
        this.durationEmpty = -1;
        this.x = -1.0f;
        this.y = -1.0f;
        this.runnable = new Runnable() {
            @Override
            public void run() {
                RippleView.this.invalidate();
            }
        };
        this.init(context, set);
    }

    private void createAnimation(final float x, final float y) {
        if (this.isEnabled() && !this.animationRunning) {
            if (this.hasToZoom) {
                this.startAnimation((Animation) this.scaleAnimation);
            }
            this.radiusMax = Math.max(this.WIDTH, this.HEIGHT);
            if (this.rippleType != 2) {
                this.radiusMax /= 2.0f;
            }
            this.radiusMax -= this.ripplePadding;
            if (!this.isCentered && this.rippleType != 1) {
                this.x = x;
                this.y = y;
            } else {
                this.x = this.getMeasuredWidth() / 2;
                this.y = this.getMeasuredHeight() / 2;
            }
            this.animationRunning = true;
            if (this.rippleType == 1 && this.originBitmap == null) {
                this.originBitmap = this.getDrawingCache(true);
            }
            this.invalidate();
        }
    }

    @TargetApi(21)
    private int fetchPrimaryColor() {
        try {
            final TypedValue typedValue = new TypedValue();
            this.getContext().getTheme().resolveAttribute(2130968745, typedValue, true);
            return typedValue.data;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Color.rgb(155, 155, 155);
        }
    }

    private Bitmap getCircleBitmap(final int n) {
        final Bitmap bitmap = Bitmap.createBitmap(this.originBitmap.getWidth(), this.originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        final float x = this.x;
        final float n2 = n;
        final Rect rect = new Rect((int) (x - n2), (int) (this.y - n2), (int) (this.x + n2), (int) (this.y + n2));
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(this.x, this.y, n2, paint);
        paint.setXfermode((Xfermode) new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(this.originBitmap, rect, rect, paint);
        return bitmap;
    }

    private void init(final Context context, final AttributeSet set) {
        if (this.isInEditMode()) {
            return;
        }
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.RippleView);
        this.rippleColor = obtainStyledAttributes.getColor(2, this.fetchPrimaryColor());
        this.rippleType = obtainStyledAttributes.getInt(6, 0);
        this.hasToZoom = obtainStyledAttributes.getBoolean(7, false);
        this.isCentered = obtainStyledAttributes.getBoolean(1, false);
        this.rippleDuration = obtainStyledAttributes.getInteger(4, this.rippleDuration);
        this.frameRate = obtainStyledAttributes.getInteger(3, this.frameRate);
        this.rippleAlpha = obtainStyledAttributes.getInteger(0, this.rippleAlpha);
        this.ripplePadding = obtainStyledAttributes.getDimensionPixelSize(5, 0);
        this.canvasHandler = new Handler();
        this.zoomScale = obtainStyledAttributes.getFloat(9, 1.03f);
        this.zoomDuration = obtainStyledAttributes.getInt(8, 200);
        obtainStyledAttributes.recycle();
        (this.paint = new Paint()).setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(this.rippleColor);
        this.paint.setAlpha(this.rippleAlpha);
        this.setWillNotDraw(false);
        this.gestureDetector = new GestureDetector(context, (GestureDetector.OnGestureListener) new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(final MotionEvent motionEvent) {
                super.onLongPress(motionEvent);
                RippleView.this.animateRipple(motionEvent);
                RippleView.this.sendClickEvent(true);
            }

            public boolean onSingleTapConfirmed(final MotionEvent motionEvent) {
                return true;
            }

            public boolean onSingleTapUp(final MotionEvent motionEvent) {
                return true;
            }
        });
        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
    }

    private void sendClickEvent(final Boolean b) {
        if (this.getParent() instanceof AdapterView) {
            final AdapterView adapterView = (AdapterView) this.getParent();
            final int positionForView = adapterView.getPositionForView((View) this);
            final long itemIdAtPosition = adapterView.getItemIdAtPosition(positionForView);
            if (b) {
                if (adapterView.getOnItemLongClickListener() != null) {
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, (View) this, positionForView, itemIdAtPosition);
                }
            } else if (adapterView.getOnItemClickListener() != null) {
                adapterView.getOnItemClickListener().onItemClick(adapterView, (View) this, positionForView, itemIdAtPosition);
            }
        }
    }

    public void animateRipple(final float n, final float n2) {
        this.createAnimation(n, n2);
    }

    public void animateRipple(final MotionEvent motionEvent) {
        this.createAnimation(motionEvent.getX(), motionEvent.getY());
    }

    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (this.animationRunning) {
            if (this.rippleDuration <= this.timer * this.frameRate) {
                this.animationRunning = false;
                this.timer = 0;
                this.durationEmpty = -1;
                this.timerEmpty = 0;
                if (Build.VERSION.SDK_INT != 23) {
                    canvas.save();
                    canvas.restore();
                }
                this.invalidate();
                if (this.onCompletionListener != null) {
                    this.onCompletionListener.onComplete(this);
                }
                return;
            }
            this.canvasHandler.postDelayed(this.runnable, (long) this.frameRate);
            if (this.timer == 0) {
                canvas.save();
            }
            canvas.drawCircle(this.x, this.y, this.radiusMax * (this.timer * this.frameRate / this.rippleDuration), this.paint);
            this.paint.setColor(Color.parseColor("#ffff4444"));
            if (this.rippleType == 1 && this.originBitmap != null && this.timer * this.frameRate / this.rippleDuration > 0.4f) {
                if (this.durationEmpty == -1) {
                    this.durationEmpty = this.rippleDuration - this.timer * this.frameRate;
                }
                ++this.timerEmpty;
                final Bitmap circleBitmap = this.getCircleBitmap((int) (this.radiusMax * (this.timerEmpty * this.frameRate / this.durationEmpty)));
                canvas.drawBitmap(circleBitmap, 0.0f, 0.0f, this.paint);
                circleBitmap.recycle();
            }
            this.paint.setColor(this.rippleColor);
            if (this.rippleType == 1) {
                if (this.timer * this.frameRate / this.rippleDuration > 0.6f) {
                    this.paint.setAlpha((int) (this.rippleAlpha - this.rippleAlpha * (this.timerEmpty * this.frameRate / this.durationEmpty)));
                } else {
                    this.paint.setAlpha(this.rippleAlpha);
                }
            } else {
                this.paint.setAlpha((int) (this.rippleAlpha - this.rippleAlpha * (this.timer * this.frameRate / this.rippleDuration)));
            }
            ++this.timer;
        }
    }

    public int getFrameRate() {
        return this.frameRate;
    }

    public int getRippleAlpha() {
        return this.rippleAlpha;
    }

    public int getRippleColor() {
        return this.rippleColor;
    }

    public int getRippleDuration() {
        return this.rippleDuration;
    }

    public int getRipplePadding() {
        return this.ripplePadding;
    }

    public RippleType getRippleType() {
        return RippleType.values()[this.rippleType];
    }

    public int getZoomDuration() {
        return this.zoomDuration;
    }

    public float getZoomScale() {
        return this.zoomScale;
    }

    public Boolean isZooming() {
        return this.hasToZoom;
    }

    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        this.onTouchEvent(motionEvent);
        return super.onInterceptTouchEvent(motionEvent);
    }

    protected void onSizeChanged(final int width, final int height, final int n, final int n2) {
        super.onSizeChanged(width, height, n, n2);
        this.WIDTH = width;
        this.HEIGHT = height;
        (this.scaleAnimation = new ScaleAnimation(1.0f, this.zoomScale, 1.0f, this.zoomScale, (float) (width / 2), (float) (height / 2))).setDuration((long) this.zoomDuration);
        this.scaleAnimation.setRepeatMode(2);
        this.scaleAnimation.setRepeatCount(1);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.gestureDetector.onTouchEvent(motionEvent)) {
            this.animateRipple(motionEvent);
            this.sendClickEvent(false);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setCentered(final Boolean isCentered) {
        this.isCentered = isCentered;
    }

    public void setFrameRate(final int frameRate) {
        this.frameRate = frameRate;
    }

    public void setOnRippleCompleteListener(final OnRippleCompleteListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    public void setRippleAlpha(final int rippleAlpha) {
        this.rippleAlpha = rippleAlpha;
    }

    public void setRippleColor(final int n) {
        this.rippleColor = this.getResources().getColor(R.color.amber_300);
    }

    public void setRippleDuration(final int rippleDuration) {
        this.rippleDuration = rippleDuration;
    }

    public void setRipplePadding(final int ripplePadding) {
        this.ripplePadding = ripplePadding;
    }

    public void setRippleType(final RippleType rippleType) {
        this.rippleType = rippleType.ordinal();
    }

    public void setZoomDuration(final int zoomDuration) {
        this.zoomDuration = zoomDuration;
    }

    public void setZoomScale(final float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public void setZooming(final Boolean hasToZoom) {
        this.hasToZoom = hasToZoom;
    }

    public interface OnRippleCompleteListener {
        void onComplete(final RippleView p0);
    }

    public enum RippleType {
        DOUBLE(1),
        RECTANGLE(2),
        SIMPLE(0);

        int type;

        private RippleType(final int type) {
            this.type = type;
        }
    }
}
