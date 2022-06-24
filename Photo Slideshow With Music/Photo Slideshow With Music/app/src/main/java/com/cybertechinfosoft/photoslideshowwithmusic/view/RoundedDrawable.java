package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.annotation.SuppressLint;
import android.content.res.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.graphics.*;

public class RoundedDrawable extends Drawable
{
    public static final int DEFAULT_BORDER_COLOR = -16777216;
    public static final String TAG = "RoundedDrawable";
    private final Bitmap mBitmap;
    private final int mBitmapHeight;
    private final Paint mBitmapPaint;
    private final RectF mBitmapRect;
    private BitmapShader mBitmapShader;
    private final int mBitmapWidth;
    private ColorStateList mBorderColor;
    private final Paint mBorderPaint;
    private final RectF mBorderRect;
    private float mBorderWidth;
    private final RectF mBounds;
    private float mCornerRadius;
    private final RectF mDrawableRect;
    private boolean mOval;
    private boolean mRebuildShader;
    private ImageView.ScaleType mScaleType;
    private final Matrix mShaderMatrix;
    private Shader.TileMode mTileModeX;
    private Shader.TileMode mTileModeY;

    public RoundedDrawable(final Bitmap mBitmap) {
        this.mBounds = new RectF();
        this.mDrawableRect = new RectF();
        this.mBitmapRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mTileModeX = Shader.TileMode.CLAMP;
        this.mTileModeY = Shader.TileMode.CLAMP;
        this.mRebuildShader = true;
        this.mCornerRadius = 0.0f;
        this.mOval = false;
        this.mBorderWidth = 0.0f;
        this.mBorderColor = ColorStateList.valueOf(-16777216);
        this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        this.mBitmap = mBitmap;
        this.mBitmapWidth = mBitmap.getWidth();
        this.mBitmapHeight = mBitmap.getHeight();
        this.mBitmapRect.set(0.0f, 0.0f, (float)this.mBitmapWidth, (float)this.mBitmapHeight);
        (this.mBitmapPaint = new Paint()).setStyle(Paint.Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);
        (this.mBorderPaint = new Paint()).setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(this.getState(), -16777216));
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
    }

    public static Bitmap drawableToBitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        final int max = Math.max(drawable.getIntrinsicWidth(), 2);
        final int max2 = Math.max(drawable.getIntrinsicHeight(), 2);
        try {
            final Bitmap bitmap = Bitmap.createBitmap(max, max2, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static RoundedDrawable fromBitmap(final Bitmap bitmap) {
        if (bitmap != null) {
            return new RoundedDrawable(bitmap);
        }
        return null;
    }

    public static Drawable fromDrawable(final Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof RoundedDrawable) {
                return drawable;
            }
            if (drawable instanceof LayerDrawable) {
                final LayerDrawable layerDrawable = (LayerDrawable)drawable;
                for (int numberOfLayers = layerDrawable.getNumberOfLayers(), i = 0; i < numberOfLayers; ++i) {
                    layerDrawable.setDrawableByLayerId(layerDrawable.getId(i), fromDrawable(layerDrawable.getDrawable(i)));
                }
                return (Drawable)layerDrawable;
            }
            final Bitmap drawableToBitmap = drawableToBitmap(drawable);
            if (drawableToBitmap != null) {
                return new RoundedDrawable(drawableToBitmap);
            }
        }
        return drawable;
    }
    private void updateShaderMatrix() {
        float scale;
        float dx;
        float dy;

        switch (mScaleType) {
            case CENTER:
                mBorderRect.set(mBounds);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);

                mShaderMatrix.reset();
                mShaderMatrix.setTranslate((int) ((mBorderRect.width() - mBitmapWidth) * 0.5f + 0.5f),
                        (int) ((mBorderRect.height() - mBitmapHeight) * 0.5f + 0.5f));
                break;

            case CENTER_CROP:
                mBorderRect.set(mBounds);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);

                mShaderMatrix.reset();

                dx = 0;
                dy = 0;

                if (mBitmapWidth * mBorderRect.height() > mBorderRect.width() * mBitmapHeight) {
                    scale = mBorderRect.height() / (float) mBitmapHeight;
                    dx = (mBorderRect.width() - mBitmapWidth * scale) * 0.5f;
                } else {
                    scale = mBorderRect.width() / (float) mBitmapWidth;
                    dy = (mBorderRect.height() - mBitmapHeight * scale) * 0.5f;
                }

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth / 2,
                        (int) (dy + 0.5f) + mBorderWidth / 2);
                break;

            case CENTER_INSIDE:
                mShaderMatrix.reset();

                if (mBitmapWidth <= mBounds.width() && mBitmapHeight <= mBounds.height()) {
                    scale = 1.0f;
                } else {
                    scale = Math.min(mBounds.width() / (float) mBitmapWidth,
                            mBounds.height() / (float) mBitmapHeight);
                }

                dx = (int) ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f);
                dy = (int) ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f);

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx, dy);

                mBorderRect.set(mBitmapRect);
                mShaderMatrix.mapRect(mBorderRect);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);
                mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                break;

            default:
            case FIT_CENTER:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.CENTER);
                mShaderMatrix.mapRect(mBorderRect);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);
                mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                break;

            case FIT_END:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.END);
                mShaderMatrix.mapRect(mBorderRect);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);
                mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                break;

            case FIT_START:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.START);
                mShaderMatrix.mapRect(mBorderRect);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);
                mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                break;

            case FIT_XY:
                mBorderRect.set(mBounds);
                mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);
                mShaderMatrix.reset();
                mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                break;
        }

        mDrawableRect.set(mBorderRect);
        mRebuildShader = true;
    }

    public void draw(final Canvas canvas) {
        if (this.mRebuildShader) {
            this.mBitmapShader = new BitmapShader(this.mBitmap, this.mTileModeX, this.mTileModeY);
            if (this.mTileModeX == Shader.TileMode.CLAMP && this.mTileModeY == Shader.TileMode.CLAMP) {
                this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
            }
            this.mBitmapPaint.setShader((Shader)this.mBitmapShader);
            this.mRebuildShader = false;
        }
        if (this.mOval) {
            if (this.mBorderWidth > 0.0f) {
                canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
                canvas.drawOval(this.mBorderRect, this.mBorderPaint);
                return;
            }
            canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
        }
        else {
            if (this.mBorderWidth > 0.0f) {
                canvas.drawRoundRect(this.mDrawableRect, Math.max(this.mCornerRadius, 0.0f), Math.max(this.mCornerRadius, 0.0f), this.mBitmapPaint);
                canvas.drawRoundRect(this.mBorderRect, this.mCornerRadius, this.mCornerRadius, this.mBorderPaint);
                return;
            }
            canvas.drawRoundRect(this.mDrawableRect, this.mCornerRadius, this.mCornerRadius, this.mBitmapPaint);
        }
    }

    public int getBorderColor() {
        return this.mBorderColor.getDefaultColor();
    }

    public ColorStateList getBorderColors() {
        return this.mBorderColor;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @SuppressLint("WrongConstant")
    public int getOpacity() {
        return -3;
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public Shader.TileMode getTileModeX() {
        return this.mTileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return this.mTileModeY;
    }

    public boolean isOval() {
        return this.mOval;
    }

    public boolean isStateful() {
        return this.mBorderColor.isStateful();
    }

    protected void onBoundsChange(final Rect rect) {
        super.onBoundsChange(rect);
        this.mBounds.set(rect);
        this.updateShaderMatrix();
    }

    protected boolean onStateChange(final int[] array) {
        final int colorForState = this.mBorderColor.getColorForState(array, 0);
        if (this.mBorderPaint.getColor() != colorForState) {
            this.mBorderPaint.setColor(colorForState);
            return true;
        }
        return super.onStateChange(array);
    }

    public void setAlpha(final int alpha) {
        this.mBitmapPaint.setAlpha(alpha);
        this.invalidateSelf();
    }

    public RoundedDrawable setBorderColor(final int n) {
        return this.setBorderColor(ColorStateList.valueOf(n));
    }

    public RoundedDrawable setBorderColor(ColorStateList value) {
        if (value == null) {
            value = ColorStateList.valueOf(0);
        }
        this.mBorderColor = value;
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(this.getState(), -16777216));
        return this;
    }

    public RoundedDrawable setBorderWidth(final float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        return this;
    }

    public void setColorFilter(final ColorFilter colorFilter) {
        this.mBitmapPaint.setColorFilter(colorFilter);
        this.invalidateSelf();
    }

    public RoundedDrawable setCornerRadius(final float mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
        return this;
    }

    public void setDither(final boolean dither) {
        this.mBitmapPaint.setDither(dither);
        this.invalidateSelf();
    }

    public void setFilterBitmap(final boolean filterBitmap) {
        this.mBitmapPaint.setFilterBitmap(filterBitmap);
        this.invalidateSelf();
    }

    public RoundedDrawable setOval(final boolean mOval) {
        this.mOval = mOval;
        return this;
    }

    public RoundedDrawable setScaleType(final ImageView.ScaleType imageView$ScaleType) {
        ImageView.ScaleType fit_CENTER = imageView$ScaleType;
        if (imageView$ScaleType == null) {
            fit_CENTER = ImageView.ScaleType.FIT_CENTER;
        }
        if (this.mScaleType != fit_CENTER) {
            this.mScaleType = fit_CENTER;
            this.updateShaderMatrix();
        }
        return this;
    }

    public RoundedDrawable setTileModeX(final Shader.TileMode mTileModeX) {
        if (this.mTileModeX != mTileModeX) {
            this.mTileModeX = mTileModeX;
            this.mRebuildShader = true;
            this.invalidateSelf();
        }
        return this;
    }

    public RoundedDrawable setTileModeY(final Shader.TileMode mTileModeY) {
        if (this.mTileModeY != mTileModeY) {
            this.mTileModeY = mTileModeY;
            this.mRebuildShader = true;
            this.invalidateSelf();
        }
        return this;
    }

    public Bitmap toBitmap() {
        return drawableToBitmap(this);
    }
}
