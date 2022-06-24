package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.widget.*;
import android.content.*;
import android.util.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.net.*;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

public class RoundedImageView extends ImageView
{
    public static final float DEFAULT_BORDER_WIDTH = 0.0f;
    public static final float DEFAULT_RADIUS = 0.0f;
    public static final Shader.TileMode DEFAULT_TILE_MODE;
    private static final ScaleType[] SCALE_TYPES;
    public static final String TAG = "RoundedImageView";
    private static final int TILE_MODE_CLAMP = 0;
    private static final int TILE_MODE_MIRROR = 2;
    private static final int TILE_MODE_REPEAT = 1;
    private static final int TILE_MODE_UNDEFINED = -2;
    private ColorStateList borderColor;
    private float borderWidth;
    private float cornerRadius;
    private boolean isOval;
    private Drawable mBackgroundDrawable;
    private Drawable mDrawable;
    private int mResource;
    private ScaleType mScaleType;
    private boolean mutateBackground;
    private Shader.TileMode tileModeX;
    private Shader.TileMode tileModeY;

    static {
        DEFAULT_TILE_MODE = Shader.TileMode.CLAMP;
        SCALE_TYPES = new ScaleType[] { ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE };
    }

    public RoundedImageView(final Context context) {
        super(context);
        this.cornerRadius = 0.0f;
        this.borderWidth = 0.0f;
        this.borderColor = ColorStateList.valueOf(-16777216);
        this.isOval = false;
        this.mutateBackground = false;
        this.tileModeX = RoundedImageView.DEFAULT_TILE_MODE;
        this.tileModeY = RoundedImageView.DEFAULT_TILE_MODE;
    }

    public RoundedImageView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }

    public RoundedImageView(final Context context, final AttributeSet set, int n) {
        super(context, set, n);
        this.cornerRadius = 0.0f;
        this.borderWidth = 0.0f;
        this.borderColor = ColorStateList.valueOf(-16777216);
        this.isOval = false;
        this.mutateBackground = false;
        this.tileModeX = RoundedImageView.DEFAULT_TILE_MODE;
        this.tileModeY = RoundedImageView.DEFAULT_TILE_MODE;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.RoundedImageView, n, 0);
        n = obtainStyledAttributes.getInt(0, -1);
        if (n >= 0) {
            this.setScaleType(RoundedImageView.SCALE_TYPES[n]);
        }
        else {
            this.setScaleType(ScaleType.FIT_CENTER);
        }
        this.cornerRadius = obtainStyledAttributes.getDimensionPixelSize(3, -1);
        this.borderWidth = obtainStyledAttributes.getDimensionPixelSize(2, -1);
        if (this.cornerRadius < 0.0f) {
            this.cornerRadius = 0.0f;
        }
        if (this.borderWidth < 0.0f) {
            this.borderWidth = 0.0f;
        }
        this.borderColor = obtainStyledAttributes.getColorStateList(1);
        if (this.borderColor == null) {
            this.borderColor = ColorStateList.valueOf(-16777216);
        }
        this.mutateBackground = obtainStyledAttributes.getBoolean(4, false);
        this.isOval = obtainStyledAttributes.getBoolean(5, false);
        n = obtainStyledAttributes.getInt(6, -2);
        if (n != -2) {
            this.setTileModeX(parseTileMode(n));
            this.setTileModeY(parseTileMode(n));
        }
        n = obtainStyledAttributes.getInt(7, -2);
        if (n != -2) {
            this.setTileModeX(parseTileMode(n));
        }
        n = obtainStyledAttributes.getInt(8, -2);
        if (n != -2) {
            this.setTileModeY(parseTileMode(n));
        }
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(true);
        obtainStyledAttributes.recycle();
    }

    private static Shader.TileMode parseTileMode(final int n) {
        switch (n) {
            default: {
                return null;
            }
            case 2: {
                return Shader.TileMode.MIRROR;
            }
            case 1: {
                return Shader.TileMode.REPEAT;
            }
            case 0: {
                return Shader.TileMode.CLAMP;
            }
        }
    }

    private Drawable resolveResource() {
        final Resources resources = this.getResources();
        if (resources == null) {
            return null;
        }
        if (this.mResource != 0) {
            try {
                final Drawable drawable = resources.getDrawable(this.mResource);
                return RoundedDrawable.fromDrawable(drawable);
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unable to find resource: ");
                sb.append(this.mResource);
                this.mResource = 0;
            }
        }
        final Drawable drawable = null;
        return RoundedDrawable.fromDrawable(drawable);
    }

    private void updateAttrs(final Drawable drawable) {
        if (drawable == null) {
            return;
        }
        if (drawable instanceof RoundedDrawable) {
            ((RoundedDrawable)drawable).setScaleType(this.mScaleType).setCornerRadius(this.cornerRadius).setBorderWidth(this.borderWidth).setBorderColor(this.borderColor).setOval(this.isOval).setTileModeX(this.tileModeX).setTileModeY(this.tileModeY);
            return;
        }
        if (drawable instanceof LayerDrawable) {
            final LayerDrawable layerDrawable = (LayerDrawable)drawable;
            for (int i = 0; i < layerDrawable.getNumberOfLayers(); ++i) {
                this.updateAttrs(layerDrawable.getDrawable(i));
            }
        }
    }

    private void updateBackgroundDrawableAttrs(final boolean b) {
        if (this.mutateBackground) {
            if (b) {
                this.mBackgroundDrawable = RoundedDrawable.fromDrawable(this.mBackgroundDrawable);
            }
            this.updateAttrs(this.mBackgroundDrawable);
        }
    }

    private void updateDrawableAttrs() {
        this.updateAttrs(this.mDrawable);
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }

    public int getBorderColor() {
        return this.borderColor.getDefaultColor();
    }

    public ColorStateList getBorderColors() {
        return this.borderColor;
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public Shader.TileMode getTileModeX() {
        return this.tileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return this.tileModeY;
    }

    public boolean isOval() {
        return this.isOval;
    }

    public void mutateBackground(final boolean mutateBackground) {
        if (this.mutateBackground == mutateBackground) {
            return;
        }
        this.mutateBackground = mutateBackground;
        this.updateBackgroundDrawableAttrs(true);
        this.invalidate();
    }

    public boolean mutatesBackground() {
        return this.mutateBackground;
    }

    public void setBackground(final Drawable backgroundDrawable) {
        this.setBackgroundDrawable(backgroundDrawable);
    }

    @Deprecated
    public void setBackgroundDrawable(final Drawable mBackgroundDrawable) {
        this.mBackgroundDrawable = mBackgroundDrawable;
        this.updateBackgroundDrawableAttrs(true);
        super.setBackgroundDrawable(this.mBackgroundDrawable);
    }

    public void setBorderColor(final int n) {
        this.setBorderColor(ColorStateList.valueOf(n));
    }

    public void setBorderColor(ColorStateList value) {
        if (this.borderColor.equals(value)) {
            return;
        }
        if (value == null) {
            value = ColorStateList.valueOf(-16777216);
        }
        this.borderColor = value;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        if (this.borderWidth > 0.0f) {
            this.invalidate();
        }
    }

    public void setBorderWidth(final float borderWidth) {
        if (this.borderWidth == borderWidth) {
            return;
        }
        this.borderWidth = borderWidth;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public void setBorderWidth(final int n) {
        this.setBorderWidth(this.getResources().getDimension(n));
    }

    public void setCornerRadius(final float cornerRadius) {
        if (this.cornerRadius == cornerRadius) {
            return;
        }
        this.cornerRadius = cornerRadius;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
    }

    public void setCornerRadius(final int n) {
        this.setCornerRadius(this.getResources().getDimension(n));
    }

    public void setImageBitmap(final Bitmap bitmap) {
        this.mResource = 0;
        this.mDrawable = RoundedDrawable.fromBitmap(bitmap);
        this.updateDrawableAttrs();
        super.setImageDrawable(this.mDrawable);
    }

    public void setImageDrawable(final Drawable drawable) {
        this.mResource = 0;
        this.mDrawable = RoundedDrawable.fromDrawable(drawable);
        this.updateDrawableAttrs();
        super.setImageDrawable(this.mDrawable);
    }

    public void setImageResource(final int mResource) {
        if (this.mResource != mResource) {
            this.mResource = mResource;
            this.mDrawable = this.resolveResource();
            this.updateDrawableAttrs();
            super.setImageDrawable(this.mDrawable);
        }
    }

    public void setImageURI(final Uri imageURI) {
        super.setImageURI(imageURI);
        this.setImageDrawable(this.getDrawable());
    }

    public void setOval(final boolean isOval) {
        this.isOval = isOval;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    //    public void setScaleType(final ImageView.ScaleType imageView$ScaleType) {
//        if (this.mScaleType != imageView$ScaleType) {
//            this.mScaleType = imageView$ScaleType;
//            switch (RoundedImageView$1.$SwitchMap$android$widget$ImageView$ScaleType[imageView$ScaleType.ordinal()]) {
//                default: {
//                    super.setScaleType(imageView$ScaleType);
//                    break;
//                }
//                case 1:
//                case 2:
//                case 3:
//                case 4:
//                case 5:
//                case 6:
//                case 7: {
//                    super.setScaleType(ImageView.ScaleType.FIT_XY);
//                    break;
//                }
//            }
//            this.updateDrawableAttrs();
//            this.updateBackgroundDrawableAttrs(false);
//            this.invalidate();
//        }
//    }
    public void setScaleType(ScaleType scaleType) {
        assert scaleType != null;

        if (mScaleType != scaleType) {
            mScaleType = scaleType;

            switch (scaleType) {
                case CENTER:
                case CENTER_CROP:
                case CENTER_INSIDE:
                case FIT_CENTER:
                case FIT_START:
                case FIT_END:
                case FIT_XY:
                    super.setScaleType(ScaleType.FIT_XY);
                    break;
                default:
                    super.setScaleType(scaleType);
                    break;
            }

            updateDrawableAttrs();
            updateBackgroundDrawableAttrs(false);
            invalidate();
        }
    }

    public void setTileModeX(final Shader.TileMode tileModeX) {
        if (this.tileModeX == tileModeX) {
            return;
        }
        this.tileModeX = tileModeX;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public void setTileModeY(final Shader.TileMode tileModeY) {
        if (this.tileModeY == tileModeY) {
            return;
        }
        this.tileModeY = tileModeY;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }
}
