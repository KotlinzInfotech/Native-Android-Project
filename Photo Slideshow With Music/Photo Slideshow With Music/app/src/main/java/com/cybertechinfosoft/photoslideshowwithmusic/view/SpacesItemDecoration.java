package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.graphics.*;
import android.view.*;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration
{
    private int space;

    public SpacesItemDecoration(final int space) {
        this.space = space;
    }

    public void getItemOffsets(final Rect rect, final View view, final RecyclerView recyclerView, final RecyclerView.State recyclerView$State) {
        rect.left = this.space;
        rect.right = this.space;
        rect.bottom = this.space;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int spanCount;
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager)layoutManager).getSpanCount();
        }
        else {
            spanCount = 1;
        }
        if (recyclerView.getChildLayoutPosition(view) < spanCount) {
            rect.top = this.space * 2;
            return;
        }
        rect.top = 0;
    }
}
