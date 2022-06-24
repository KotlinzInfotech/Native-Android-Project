package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.content.*;
import android.util.*;
import android.view.*;
import androidx.viewpager.widget.ViewPager;

public class ScrollableViewPager extends ViewPager
{
    private boolean canScroll;

    public ScrollableViewPager(final Context context) {
        super(context);
        this.canScroll = true;
    }

    public ScrollableViewPager(final Context context, final AttributeSet set) {
        super(context, set);
        this.canScroll = true;
    }

    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.canScroll && super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.canScroll && super.onTouchEvent(motionEvent);
    }

    public void setCanScroll(final boolean canScroll) {
        this.canScroll = canScroll;
    }
}
