package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;

public class ProportionalRelativeLayout extends RelativeLayout {
    private float mProportion;

    @SuppressLint({"Recycle"})
    public ProportionalRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mProportion = context.obtainStyledAttributes(attributeSet, R.styleable.ProportionalRelativeLayout).getFloat(0, ((float) MyApplication.VIDEO_HEIGHT) / ((float) MyApplication.VIDEO_WIDTH));
    }

    protected void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        size = MeasureSpec.makeMeasureSpec((int) Math.ceil((double) (this.mProportion * ((float) size))), MeasureSpec.getMode(i2));
        if (size2 != 0) {
            size2 = MeasureSpec.makeMeasureSpec((int) Math.ceil((double) (((float) size2) / this.mProportion)), MeasureSpec.getMode(i2));
            if (i2 > size) {
                super.onMeasure(i, size);
                return;
            } else {
                super.onMeasure(size2, i2);
                return;
            }
        }
        super.onMeasure(i, size);
    }
}
