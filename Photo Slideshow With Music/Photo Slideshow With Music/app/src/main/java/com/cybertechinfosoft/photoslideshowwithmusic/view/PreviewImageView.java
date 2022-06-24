package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.widget.*;
import android.content.*;
import android.util.*;
import com.cybertechinfosoft.photoslideshowwithmusic.*;
import com.cybertechinfosoft.photoslideshowwithmusic.R;

import android.content.res.*;

public class PreviewImageView extends ImageView
{
    public static int mAspectRatioHeight = 480;
    public static int mAspectRatioWidth = 720;

    public PreviewImageView(final Context context) {
        super(context);
    }

    public PreviewImageView(final Context context, final AttributeSet set) {
        super(context, set);
        this.Init(context, set);
    }

    public PreviewImageView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.Init(context, set);
    }

    public PreviewImageView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.Init(context, set);
    }

    private void Init(final Context context, final AttributeSet set) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.PreviewImageView);
        PreviewImageView.mAspectRatioWidth = obtainStyledAttributes.getInt(1, MyApplication.VIDEO_WIDTH);
        PreviewImageView.mAspectRatioHeight = obtainStyledAttributes.getInt(0, MyApplication.VIDEO_HEIGHT);
        final StringBuilder sb = new StringBuilder();
        sb.append("mAspectRatioWidth:");
        sb.append(PreviewImageView.mAspectRatioWidth);
        sb.append(" mAspectRatioHeight:");
        sb.append(PreviewImageView.mAspectRatioHeight);
        obtainStyledAttributes.recycle();
    }

    protected void onMeasure(int size, int size2) {
        size = MeasureSpec.getSize(size);
        size2 = MeasureSpec.getSize(size2);
        final int n = PreviewImageView.mAspectRatioHeight * size / PreviewImageView.mAspectRatioWidth;
        if (n > size2) {
            size = PreviewImageView.mAspectRatioWidth * size2 / PreviewImageView.mAspectRatioHeight;
        }
        else {
            size2 = n;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, 1073741824), MeasureSpec.makeMeasureSpec(size2, 1073741824));
    }
}
