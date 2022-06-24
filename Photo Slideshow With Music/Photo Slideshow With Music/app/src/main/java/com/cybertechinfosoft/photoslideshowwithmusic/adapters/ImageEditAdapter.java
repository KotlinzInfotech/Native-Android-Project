package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageEditActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ImageEditor;
import java.util.ArrayList;
import java.util.Collections;

public class ImageEditAdapter extends RecyclerView.Adapter<ImageEditAdapter.Holder> {
    final int TYPE_BLANK;
    final int TYPE_IMAGE;
    private MyApplication application;
    private OnItemClickListner<Object> clickListner;
    private RequestManager glide;
    private LayoutInflater inflater;
    private boolean isEdit;
    ArrayList<ImageData> list;
    Context mContext;

    public ImageEditAdapter(final Context mContext) {
        this.TYPE_IMAGE = 0;
        this.TYPE_BLANK = 1;
        this.isEdit = false;
        this.mContext = mContext;
        this.application = MyApplication.getInstance();
        this.inflater = LayoutInflater.from(mContext);
        this.glide = Glide.with(mContext);
        this.list = (ArrayList<ImageData>) this.application.getSelectedImages();
    }

    public ImageData getItem(final int n) {
        if (this.list.size() <= n) {
            return new ImageData();
        }
        return this.list.get(n);
    }

    public int getItemCount() {
        return this.application.getSelectedImages().size();
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        holder.parent.setVisibility(View.VISIBLE);
        final ImageData item = this.getItem(n);
        this.glide.load(item.imagePath).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.ivThumb);
        if (MyApplication.isStoryAdded) {
            if (n != 0 && n != this.getItemCount() - 1) {
                if (this.getItemCount() <= 4) {
                    holder.ivRemove.setVisibility(View.GONE);
                } else {
                    holder.ivRemove.setVisibility(View.VISIBLE);
                }
            } else {
                holder.ivRemove.setVisibility(View.GONE);
            }
        } else if (this.getItemCount() <= 2) {
            holder.ivRemove.setVisibility(View.GONE);
        } else {
            holder.ivRemove.setVisibility(View.VISIBLE);
        }
        if (ImageEditActivity.isEdit) {
            holder.ivEdit.setVisibility(View.VISIBLE);
        }
        holder.ivRemove.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (holder.pbImageLoading.getVisibility() == 0) {
                    Toast.makeText(ImageEditAdapter.this.mContext, R.string.please_wait_untill_this_process_compelete, Toast.LENGTH_SHORT).show();
                    return;
                }
                ImageEditAdapter.this.application.min_pos = Math.min(ImageEditAdapter.this.application.min_pos, Math.max(0, n - 1));
                MyApplication.isBreak = true;
                ImageEditAdapter.this.application.removeSelectedImage(n);
                final StringBuilder sb = new StringBuilder();
                sb.append(n);
                sb.append("");
                view.setTag((Object) sb.toString());
                if (ImageEditAdapter.this.clickListner != null) {
                    ImageEditAdapter.this.clickListner.onItemClick(view, item);
                }
                ImageEditAdapter.this.notifyDataSetChanged();
            }
        });
        holder.ivEdit.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (ImageEditAdapter.this.isEdit) {
                    Toast.makeText(ImageEditAdapter.this.mContext, R.string.please_wait_untill_this_process_compelete, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (holder.pbImageLoading.getVisibility() == 0) {
                    Toast.makeText(ImageEditAdapter.this.mContext, R.string.please_wait_untill_this_process_compelete, Toast.LENGTH_SHORT).show();
                    return;
                }
                MyApplication.TEMP_POSITION = n;
                ImageEditor.startEditing(ImageEditAdapter.this.application.getSelectedImages().get(n).imagePath).Start((Activity) ImageEditAdapter.this.mContext);
            }
        });
        holder.clickableView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (ImageEditAdapter.this.isEdit) {
                    Toast.makeText(ImageEditAdapter.this.mContext, R.string.please_wait_untill_this_process_compelete, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (holder.pbImageLoading.getVisibility() == 0) {
                    Toast.makeText(ImageEditAdapter.this.mContext, R.string.please_wait_untill_this_process_compelete, Toast.LENGTH_SHORT).show();
                    return;
                }
                MyApplication.TEMP_POSITION = n;
                final StringBuilder sb = new StringBuilder();
                sb.append(" ImageEdit adapter path : ");
                sb.append(ImageEditAdapter.this.application.getSelectedImages().get(n).imagePath);
                ImageEditor.startEditing(ImageEditAdapter.this.application.getSelectedImages().get(n).imagePath).Start((Activity) ImageEditAdapter.this.mContext);
            }
        });
    }

    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        final View inflate = this.inflater.inflate(R.layout.grid_selected_item, viewGroup, false);
        final Holder holder = new Holder(inflate);
        if (this.getItemViewType(n) == 1) {
            inflate.setVisibility(View.INVISIBLE);
            return holder;
        }
        inflate.setVisibility(View.VISIBLE);
        return holder;
    }

    public void setOnItemClickListner(final OnItemClickListner<Object> clickListner) {
        this.clickListner = clickListner;
    }

    public void swap(final int n, final int n2) {
        synchronized (this) {
            Collections.swap(this.application.getSelectedImages(), n, n2);
            this.notifyItemMoved(n, n2);
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        private View clickableView;
        private ImageView ivEdit;
        private ImageView ivRemove;
        private ImageView ivThumb;
        View parent;
        public ProgressBar pbImageLoading;

        public Holder(final View parent) {
            super(parent);
            this.parent = parent;
            this.pbImageLoading = (ProgressBar) parent.findViewById(R.id.pbImageLoading);
            this.ivThumb = (ImageView) parent.findViewById(R.id.ivThumb);
            this.ivRemove = (ImageView) parent.findViewById(R.id.ivRemove);
            this.ivEdit = (ImageView) parent.findViewById(R.id.ivEdit);
            this.clickableView = parent.findViewById(R.id.clickableView);
        }

        public void onItemClick(final View view, final Object o) {
            if (ImageEditAdapter.this.clickListner != null) {
                ImageEditAdapter.this.clickListner.onItemClick(view, o);
            }
        }
    }
}
