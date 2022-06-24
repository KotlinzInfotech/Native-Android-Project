package com.cybertechinfosoft.photoslideshowwithmusic.view;


import android.content.*;
import android.util.*;
import com.cybertechinfosoft.photoslideshowwithmusic.*;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import android.content.res.*;

import androidx.cardview.widget.CardView;

public class ScaleCardLayout extends CardView {
    public int mAspectRatioHeight;
    public int mAspectRatioWidth;

    public ScaleCardLayout(final Context context) {
        super(context);
        this.mAspectRatioWidth = 640;
        this.mAspectRatioHeight = 360;
    }

    public ScaleCardLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mAspectRatioWidth = 640;
        this.mAspectRatioHeight = 360;
        this.Init(context, set);
    }

    public ScaleCardLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mAspectRatioWidth = 640;
        this.mAspectRatioHeight = 360;
        this.Init(context, set);
    }

    private void Init(final Context context, final AttributeSet set) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.PreviewImageView);
        this.mAspectRatioWidth = obtainStyledAttributes.getInt(R.styleable.PreviewImageView_aspectRatioWidth, MyApplication.VIDEO_WIDTH);
        this.mAspectRatioHeight = obtainStyledAttributes.getInt(R.styleable.PreviewImageView_aspectRatioHeight, MyApplication.VIDEO_HEIGHT);
        obtainStyledAttributes.recycle();
    }

    protected void onMeasure(int size, int size2) {
        if (this.mAspectRatioHeight == this.mAspectRatioWidth) {
            super.onMeasure(size, size);
        }
        size = MeasureSpec.getSize(size);
        size2 = MeasureSpec.getSize(size2);
        final int n = this.mAspectRatioHeight * size / this.mAspectRatioWidth;
        if (n > size2) {
            size = this.mAspectRatioWidth * size2 / this.mAspectRatioHeight;
        } else {
            size2 = n;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size2, MeasureSpec.EXACTLY));
    }
}
