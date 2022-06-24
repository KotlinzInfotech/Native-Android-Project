package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.text.style.*;
import android.graphics.*;
import android.text.*;

public class CustomTypefaceSpan extends TypefaceSpan
{
    private final Typeface newType;

    public CustomTypefaceSpan(final String s, final Typeface newType) {
        super(s);
        this.newType = newType;
    }

    private static void applyCustomTypeFace(final Paint paint, final Typeface typeface) {
        final Typeface typeface2 = paint.getTypeface();
        int style;
        if (typeface2 == null) {
            style = 0;
        }
        else {
            style = typeface2.getStyle();
        }
        final int n = style & ~typeface.getStyle();
        if ((n & 0x1) != 0x0) {
            paint.setFakeBoldText(true);
        }
        if ((n & 0x2) != 0x0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(typeface);
    }

    public void updateDrawState(final TextPaint textPaint) {
        applyCustomTypeFace((Paint)textPaint, this.newType);
    }

    public void updateMeasureState(final TextPaint textPaint) {
        applyCustomTypeFace((Paint)textPaint, this.newType);
    }
}
