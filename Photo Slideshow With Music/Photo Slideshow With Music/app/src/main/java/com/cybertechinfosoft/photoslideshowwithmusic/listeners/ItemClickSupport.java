package com.cybertechinfosoft.photoslideshowwithmusic.listeners;


import android.view.*;

import androidx.recyclerview.widget.RecyclerView;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

public class ItemClickSupport
{
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private final RecyclerView mRecyclerView;

    private ItemClickSupport(final RecyclerView mRecyclerView) {
        this.mOnClickListener = (View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View view) {
                if (ItemClickSupport.this.mOnItemClickListener != null) {
                    ItemClickSupport.this.mOnItemClickListener.onItemClicked(ItemClickSupport.this.mRecyclerView, ItemClickSupport.this.mRecyclerView.getChildViewHolder(view).getAdapterPosition(), view);
                }
            }
        };
        this.mOnLongClickListener = (View.OnLongClickListener)new View.OnLongClickListener() {
            public boolean onLongClick(final View view) {
                return ItemClickSupport.this.mOnItemLongClickListener != null && ItemClickSupport.this.mOnItemLongClickListener.onItemLongClicked(ItemClickSupport.this.mRecyclerView, ItemClickSupport.this.mRecyclerView.getChildViewHolder(view).getAdapterPosition(), view);
            }
        };
        this.mAttachListener = (RecyclerView.OnChildAttachStateChangeListener)new RecyclerView.OnChildAttachStateChangeListener() {
            public void onChildViewAttachedToWindow(final View view) {
                if (ItemClickSupport.this.mOnItemClickListener != null) {
                    view.setOnClickListener(ItemClickSupport.this.mOnClickListener);
                }
                if (ItemClickSupport.this.mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(ItemClickSupport.this.mOnLongClickListener);
                }
            }

            public void onChildViewDetachedFromWindow(final View view) {
            }
        };
        (this.mRecyclerView = mRecyclerView).setTag(R.anim.design_snackbar_out, (Object)this);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this.mAttachListener);
    }

    public static ItemClickSupport addTo(final RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport;
        if ((itemClickSupport = (ItemClickSupport)recyclerView.getTag(2131296562)) == null) {
            itemClickSupport = new ItemClickSupport(recyclerView);
        }
        return itemClickSupport;
    }

    private void detach(final RecyclerView recyclerView) {
        recyclerView.removeOnChildAttachStateChangeListener(this.mAttachListener);
        recyclerView.setTag(2131296562, (Object)null);
    }

    public static ItemClickSupport removeFrom(final RecyclerView recyclerView) {
        final ItemClickSupport itemClickSupport = (ItemClickSupport)recyclerView.getTag(2131296562);
        if (itemClickSupport != null) {
            itemClickSupport.detach(recyclerView);
        }
        return itemClickSupport;
    }

    public ItemClickSupport setOnItemClickListener(final OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
        return this;
    }

    public interface OnItemClickListener
    {
        void onItemClicked(final RecyclerView p0, final int p1, final View p2);
    }

    public interface OnItemLongClickListener
    {
        boolean onItemLongClicked(final RecyclerView p0, final int p1, final View p2);
    }
}
