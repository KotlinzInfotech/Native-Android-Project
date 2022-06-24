package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import java.util.*;

public class CheckableRelativeLayout extends RippleView implements Checkable
{
    private List<Checkable> checkableViews;
    private boolean isChecked;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckableRelativeLayout(final Context context) {
        super(context, null);
        this.initialise(null);
    }

    public CheckableRelativeLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.initialise(set);
    }

    private void findCheckableChildren(final View view) {
        if (view instanceof Checkable) {
            this.checkableViews.add((Checkable)view);
        }
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                this.findCheckableChildren(viewGroup.getChildAt(i));
            }
        }
    }

    private void initialise(final AttributeSet set) {
        this.isChecked = false;
        this.checkableViews = new ArrayList<Checkable>(5);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            this.findCheckableChildren(this.getChildAt(i));
        }
    }

    public void setChecked(final boolean b) {
        this.isChecked = b;
        final Iterator<Checkable> iterator = this.checkableViews.iterator();
        while (iterator.hasNext()) {
            iterator.next().setChecked(b);
        }
        if (this.onCheckedChangeListener != null) {
            this.onCheckedChangeListener.onCheckedChanged(this, b);
        }
    }

    public void setOnCheckedChangeListener(final OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void toggle() {
        this.isChecked ^= true;
        final Iterator<Checkable> iterator = this.checkableViews.iterator();
        while (iterator.hasNext()) {
            iterator.next().toggle();
        }
    }

    public interface OnCheckedChangeListener
    {
        void onCheckedChanged(final CheckableRelativeLayout p0, final boolean p1);
    }
}
