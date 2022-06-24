package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.media.ExifInterface;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import java.io.IOException;

public class ScalingUtilities {
    public static Bitmap ConvetrSameSize(final Bitmap bitmap, final int n, final int n2) {
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, n, n2);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRect(rect, paint);
        paint.setXfermode((Xfermode) new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(newscaleBitmap(bitmap, n, n2), 0.0f, 0.0f, paint);
        return bitmap2;
    }

    public static Bitmap ConvetrSameSize(final Bitmap bitmap, final Bitmap bitmap2, final int n, final int n2) {
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint();
        new Rect(0, 0, n, n2);
        canvas.drawBitmap(newscaleBitmap(bitmap, n, n2), 0.0f, 0.0f, paint);
        return bitmap2;
    }

    public static Bitmap ConvetrSameSize(final Bitmap bitmap, Bitmap copy, final int n, final int n2, final float n3, final float n4) {
        copy = copy.copy(copy.getConfig(), true);
        new Canvas(FastBlur.doBlur(copy, 25, true)).drawBitmap(newscaleBitmap(bitmap, n, n2, n3, n4), 0.0f, 0.0f, new Paint());
        return copy;
    }

    public static Bitmap ConvetrSameSizeNew(final Bitmap bitmap, Bitmap doBlur, final int n, final int n2) {
        doBlur = FastBlur.doBlur(doBlur, 25, true);
        final Canvas canvas = new Canvas(doBlur);
        final Paint paint = new Paint();
        final float n3 = bitmap.getWidth();
        final float n4 = bitmap.getHeight();
        final float n5 = n;
        float n6 = n5 / n3;
        final float n7 = n2;
        final float n8 = n7 / n4;
        float n9 = (n7 - n4 * n6) / 2.0f;
        float n12;
        if (n9 < 0.0f) {
            final float n10 = (n5 - n3 * n8) / 2.0f;
            n6 = n8;
            final float n11 = 0.0f;
            n12 = n10;
            n9 = n11;
        } else {
            n12 = 0.0f;
        }
        final Matrix matrix = new Matrix();
        matrix.postTranslate(n12, n9);
        matrix.preScale(n6, n6);
        canvas.drawBitmap(bitmap, matrix, paint);
        return doBlur;
    }

    public static Bitmap ConvetrSameSizeTransBg(final Bitmap bitmap, final int n, final int n2) {
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint();
        new Rect(0, 0, n, n2);
        canvas.drawBitmap(newscaleBitmap(bitmap, n, n2), 0.0f, 0.0f, paint);
        return bitmap2;
    }

    public static Bitmap addShadow(final Bitmap bitmap, final int color, final int n, final float n2, final float n3) {
        final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth() + n * 2, bitmap.getHeight(), Bitmap.Config.ALPHA_8);
        final Matrix matrix = new Matrix();
        final RectF rectF = new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
        final float n4 = bitmap.getWidth();
        final float n5 = n;
        matrix.setRectToRect(rectF, new RectF(0.0f, 0.0f, n4 - n2 - n5, bitmap.getHeight() - n3), Matrix.ScaleToFit.CENTER);
        final Matrix matrix2 = new Matrix(matrix);
        matrix2.postTranslate(n2, n3);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint(1);
        canvas.drawBitmap(bitmap, matrix, paint);
        paint.setXfermode((Xfermode) new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawBitmap(bitmap, matrix2, paint);
        final BlurMaskFilter maskFilter = new BlurMaskFilter(n5, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setMaskFilter((MaskFilter) maskFilter);
        paint.setFilterBitmap(true);
        final Bitmap bitmap3 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas2 = new Canvas(bitmap3);
        canvas2.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
        canvas2.drawBitmap(bitmap, matrix, (Paint) null);
        bitmap2.recycle();
        return bitmap3;
    }

    @SuppressLint({"NewApi"})
    public static Bitmap blurBitmap(final Bitmap bitmap, final Context context) {
        final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final RenderScript create = RenderScript.create(context);
        final ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        final Allocation fromBitmap = Allocation.createFromBitmap(create, bitmap);
        final Allocation fromBitmap2 = Allocation.createFromBitmap(create, bitmap2);
        create2.setRadius(25.0f);
        create2.setInput(fromBitmap);
        create2.forEach(fromBitmap2);
        fromBitmap2.copyTo(bitmap2);
        bitmap.recycle();
        create.destroy();
        return bitmap2;
    }

    public static Rect calculateDstRect(final int n, final int n2, final int n3, final int n4, final ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.FIT) {
            return new Rect(0, 0, n3, n4);
        }
        final float n5 = n / n2;
        final float n6 = n3;
        final float n7 = n4;
        if (n5 > n6 / n7) {
            return new Rect(0, 0, n3, (int) (n6 / n5));
        }
        return new Rect(0, 0, (int) (n7 * n5), n4);
    }

    public static int calculateSampleSize(final int n, final int n2, final int n3, final int n4, final ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            if (n / n2 > n3 / n4) {
                return n / n3;
            }
            return n2 / n4;
        } else {
            if (n / n2 > n3 / n4) {
                return n2 / n4;
            }
            return n / n3;
        }
    }

    public static Rect calculateSrcRect(int n, int n2, int n3, final int n4, final ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.CROP) {
            return new Rect(0, 0, n, n2);
        }
        final float n5 = n;
        final float n6 = n2;
        final float n7 = n5 / n6;
        final float n8 = n3 / n4;
        if (n7 > n8) {
            n3 = (int) (n6 * n8);
            n = (n - n3) / 2;
            return new Rect(n, 0, n3 + n, n2);
        }
        n3 = (int) (n5 / n8);
        n2 = (n2 - n3) / 2;
        return new Rect(0, n2, n, n3 + n2);
    }

    public static Bitmap checkBitmap(String path) {
        int orientation = 1;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        Bitmap bm = BitmapFactory.decodeFile(path, new BitmapFactory.Options());
        try {
            String orientString = new ExifInterface(path)
                    .getAttribute("Orientation");
            if (orientString != null) {
                orientation = Integer.parseInt(orientString);
            }
            int rotationAngle = 0;
            if (orientation == 6) {
                rotationAngle = 90;
            }
            if (orientation == 3) {
                rotationAngle = 180;
            }
            if (orientation == 8) {
                rotationAngle = 270;
            }
            Matrix matrix = new Matrix();
            matrix.setRotate((float) rotationAngle,
                    ((float) bm.getWidth()) / 2.0f,
                    ((float) bm.getHeight()) / 2.0f);
            return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth,
                    bounds.outHeight, matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createScaledBitmap(final Bitmap bitmap, final int n, final int n2, final ScalingLogic scalingLogic) {
        final Rect calculateSrcRect = calculateSrcRect(bitmap.getWidth(), bitmap.getHeight(), n, n2, scalingLogic);
        final Rect calculateDstRect = calculateDstRect(bitmap.getWidth(), bitmap.getHeight(), n, n2, scalingLogic);
        final Bitmap bitmap2 = Bitmap.createBitmap(calculateDstRect.width(), calculateDstRect.height(), Bitmap.Config.ARGB_8888);
        new Canvas(bitmap2).drawBitmap(bitmap, calculateSrcRect, calculateDstRect, new Paint(2));
        return bitmap2;
    }

    public static Bitmap decodeResource(final Resources resources, final int n, final int n2, final int n3, final ScalingLogic scalingLogic) {
        final BitmapFactory.Options bitmapFactory$Options = new BitmapFactory.Options();
        bitmapFactory$Options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, n, bitmapFactory$Options);
        bitmapFactory$Options.inJustDecodeBounds = false;
        bitmapFactory$Options.inSampleSize = calculateSampleSize(bitmapFactory$Options.outWidth, bitmapFactory$Options.outHeight, n2, n3, scalingLogic);
        return BitmapFactory.decodeResource(resources, n, bitmapFactory$Options);
    }

    private static Bitmap newscaleBitmap(final Bitmap bitmap, final int n, final int n2) {
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, Bitmap.Config.ARGB_8888);
        final float n3 = bitmap.getWidth();
        final float n4 = bitmap.getHeight();
        final Canvas canvas = new Canvas(bitmap2);
        final float n5 = n;
        float n6 = n5 / n3;
        final float n7 = n2;
        float n8 = (n7 - n4 * n6) / 2.0f;
        float n9;
        if (n8 < 0.0f) {
            n6 = n7 / n4;
            n9 = (n5 - n3 * n6) / 2.0f;
            n8 = 0.0f;
        } else {
            n9 = 0.0f;
        }
        final Matrix matrix = new Matrix();
        matrix.postTranslate(n9, n8);
        final StringBuilder sb = new StringBuilder();
        sb.append("xTranslation :");
        sb.append(n9);
        sb.append(" yTranslation :");
        sb.append(n8);
        matrix.preScale(n6, n6);
        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, matrix, paint);
        return bitmap2;
    }

    private static Bitmap newscaleBitmap(final Bitmap bitmap, final int n, final int n2, final float n3, final float n4) {
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, Bitmap.Config.ARGB_8888);
        final float n5 = bitmap.getWidth();
        final float n6 = bitmap.getHeight();
        final Canvas canvas = new Canvas(bitmap2);
        final float n7 = n;
        float n8 = n7 / n5;
        final float n9 = n2;
        final float n10 = n9 / n6;
        float n11 = (n9 - n6 * n8) / 2.0f;
        float n14;
        if (n11 < 0.0f) {
            final float n12 = (n7 - n5 * n10) / 2.0f;
            n8 = n10;
            final float n13 = 0.0f;
            n14 = n12;
            n11 = n13;
        } else {
            n14 = 0.0f;
        }
        final Matrix matrix = new Matrix();
        matrix.postTranslate(n3 * n14, n4 + n11);
        final StringBuilder sb = new StringBuilder();
        sb.append("xTranslation :");
        sb.append(n14);
        sb.append(" yTranslation :");
        sb.append(n11);
        matrix.preScale(n8, n8);
        canvas.drawBitmap(bitmap, matrix, new Paint());
        return bitmap2;
    }

    public static Bitmap overlay(final Bitmap bitmap, final Bitmap bitmap2, final int alpha) {
        final Bitmap bitmap3 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        final Paint paint = new Paint();
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
        return bitmap3;
    }

    public static Bitmap scaleCenterCrop(final Bitmap bitmap, final int n, final int n2) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        if (width == n && height == n2) {
            return bitmap;
        }
        final float n3 = n;
        final float n4 = width;
        final float n5 = n3 / n4;
        final float n6 = n2;
        final float n7 = height;
        final float max = Math.max(n5, n6 / n7);
        final float n8 = n4 * max;
        final float n9 = max * n7;
        final float n10 = (n3 - n8) / 2.0f;
        final float n11 = (n6 - n9) / 2.0f;
        final RectF rectF = new RectF(n10, n11, n8 + n10, n9 + n11);
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, bitmap.getConfig());
        new Canvas(bitmap2).drawBitmap(bitmap, (Rect) null, rectF, (Paint) null);
        return bitmap2;

    }

    public enum ScalingLogic {
        CROP,
        FIT;
    }
}
