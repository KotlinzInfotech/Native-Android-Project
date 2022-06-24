package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.media.*;
import android.graphics.*;
import java.io.*;

public class BitmapCompression
{

    public static int calculateInSampleSize(final BitmapFactory.Options bitmapFactory$Options, final int n, final int n2) {
        final int outHeight = bitmapFactory$Options.outHeight;
        final int outWidth = bitmapFactory$Options.outWidth;
        if (outWidth >= outHeight) {
            return Math.round(outWidth / n);
        }
        return Math.round(outHeight / n2);
    }

    public static Bitmap checkBitmap(String attribute) {
        final File file = new File(attribute);
        final double n = file.length();
        int int1 = 1;
        int n2;
        if (n >= 3670016.0) {
            n2 = 8;
        }
        else if (file.length() >= 2306867.2) {
            n2 = 4;
        }
        else if (file.length() >= 1258291.2) {
            n2 = 2;
        }
        else {
            n2 = 1;
        }
        while (true) {
            final BitmapFactory.Options bitmapFactory$Options = new BitmapFactory.Options();
            bitmapFactory$Options.inJustDecodeBounds = true;
            bitmapFactory$Options.inSampleSize = n2;
            BitmapFactory.decodeFile(attribute, bitmapFactory$Options);
            final BitmapFactory.Options bitmapFactory$Options2 = new BitmapFactory.Options();
            bitmapFactory$Options.inJustDecodeBounds = true;
            bitmapFactory$Options2.inSampleSize = n2;
            final Bitmap decodeFile = BitmapFactory.decodeFile(attribute, bitmapFactory$Options2);
            while (true) {
                Label_0215: {
                    try {
                        attribute = new ExifInterface(attribute).getAttribute("Orientation");
                        if (attribute != null) {
                            int1 = Integer.parseInt(attribute);
                        }
                        break Label_0215;
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }
                int n3 = 0;
                if (int1 == 6) {
                    n3 = 90;
                }
                if (int1 == 3) {
                    n3 = 180;
                }
                if (int1 == 8) {
                    n3 = 270;
                    continue;
                }
                continue;
            }
        }
    }

    public static Bitmap combineImages(final Bitmap bitmap, final Bitmap bitmap2) {
        int n;
        int n2;
        if (bitmap.getWidth() > bitmap2.getWidth()) {
            n = bitmap.getWidth() + bitmap2.getWidth();
            n2 = bitmap.getHeight();
        }
        else {
            n = bitmap2.getWidth() + bitmap2.getWidth();
            n2 = bitmap.getHeight();
        }
        final Bitmap bitmap3 = Bitmap.createBitmap(n, n2, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint)null);
        canvas.drawBitmap(bitmap2, (float)bitmap.getWidth(), 0.0f, (Paint)null);
        return bitmap3;
    }

    public static Bitmap cropToSquare(final Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int n;
        if (height > width) {
            n = width;
        }
        else {
            n = height;
        }
        int n2;
        if (height > width) {
            n2 = height - (height - width);
        }
        else {
            n2 = height;
        }
        int n3;
        if ((n3 = (width - height) / 2) < 0) {
            n3 = 0;
        }
        int n4;
        if ((n4 = (height - width) / 2) < 0) {
            n4 = 0;
        }
        return Bitmap.createBitmap(bitmap, n3, n4, n, n2);
    }

    public static Bitmap decodeFile(final File file, final int n, final int n2) {
        try {
            final BitmapFactory.Options bitmapFactory$Options = new BitmapFactory.Options();
            bitmapFactory$Options.inJustDecodeBounds = false;
            bitmapFactory$Options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmapFactory$Options.inDither = true;
            return BitmapFactory.decodeStream((InputStream)new FileInputStream(file), (Rect)null, bitmapFactory$Options);
        }
        catch (FileNotFoundException ex) {
            return null;
        }
    }

    public static Bitmap getCropSquare(final Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int n;
        if (height > width) {
            n = width;
        }
        else {
            n = height;
        }
        int n2;
        if (height > width) {
            n2 = height - (height - width);
        }
        else {
            n2 = height;
        }
        int n3;
        if ((n3 = (width - height) / 2) < 0) {
            n3 = 0;
        }
        int n4;
        if ((n4 = (height - width) / 2) < 0) {
            n4 = 0;
        }
        return Bitmap.createBitmap(bitmap, n3, n4, n, n2);
    }

    public static Bitmap resizeImage(final Bitmap bitmap, final int n, final int n2) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final float n3 = (n + 0.0f) / width;
        final float n4 = (n2 + 0.0f) / height;
        float n5 = n3;
        if (n3 > n4) {
            n5 = n4;
        }
        final Matrix matrix = new Matrix();
        matrix.postScale(n5, n5);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
