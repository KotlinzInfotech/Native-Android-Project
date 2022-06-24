package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageSelectionActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import java.util.ArrayList;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.Holder> {
    final int TYPE_BLANK;
    final int TYPE_IMAGE;
    ImageSelectionActivity activity;
    private MyApplication application;
    private OnItemClickListner<Object> clickListner;
    private RequestManager glide;
    private LayoutInflater inflater;
    public boolean isExpanded;
    Context mContext;

    public SelectedImageAdapter(final Context mContext) {
        this.TYPE_IMAGE = 0;
        this.TYPE_BLANK = 1;
        this.isExpanded = false;
        this.mContext = mContext;
        this.activity = (ImageSelectionActivity) mContext;
        this.application = MyApplication.getInstance();
        this.inflater = LayoutInflater.from(mContext);
        this.glide = Glide.with(mContext);
    }

    private boolean hideRemoveBtn() {
        return this.application.getSelectedImages().size() <= 3 && this.activity.isFromPreview;
    }


    public ImageData getItem(int i) {
        ArrayList selectedImages = this.application.getSelectedImages();
        if (selectedImages.size() <= i) {
            return new ImageData();
        }
        return (ImageData) selectedImages.get(i);
    }

    public int getItemCount() {
        final ArrayList selectedImages = this.application.getSelectedImages();
        if (!this.isExpanded) {
            return selectedImages.size() + 20;
        }
        return selectedImages.size();
    }

    public int getItemViewType(final int n) {
        super.getItemViewType(n);
        if (this.isExpanded) {
            return 0;
        }
        if (n >= this.application.getSelectedImages().size()) {
            return 1;
        }
        return 0;
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        if (this.getItemViewType(n) == 1) {
            holder.parent.setVisibility(View.INVISIBLE);
            return;
        }
        holder.parent.setVisibility(View.VISIBLE);
        final ImageData item = this.getItem(n);
        this.glide.load(item.imagePath).into(holder.ivThumb);
        if (this.hideRemoveBtn()) {
            holder.ivRemove.setVisibility(View.GONE);
        } else {
            holder.ivRemove.setVisibility(View.VISIBLE);
        }
        holder.ivRemove.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (SelectedImageAdapter.this.activity.isFromPreview) {
                    SelectedImageAdapter.this.application.min_pos = Math.min(SelectedImageAdapter.this.application.min_pos, Math.max(0, n - 1));
                }
                if (ImageSelectionActivity.isForFirst && n <= ImageSelectionActivity.tempImage.size() && SelectedImageAdapter.this.application.getSelectedImages().contains(ImageSelectionActivity.tempImage.get(n).imagePath)) {
                    ImageSelectionActivity.tempImage.remove(n);
                }
                SelectedImageAdapter.this.application.removeSelectedImage(n);
                if (SelectedImageAdapter.this.clickListner != null) {
                    SelectedImageAdapter.this.clickListner.onItemClick(view, item);
                }
                if (SelectedImageAdapter.this.hideRemoveBtn()) {
                    Toast.makeText((Context) SelectedImageAdapter.this.activity, R.string.at_least_3_images_require_if_you_want_to_remove_this_images_than_add_more_images_, Toast.LENGTH_SHORT).show();
                }
                SelectedImageAdapter.this.notifyDataSetChanged();
            }
        });
        holder.ivEdit.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
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

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView ivEdit;
        private ImageView ivRemove;
        private ImageView ivThumb;
        View parent;

        public Holder(final View parent) {
            super(parent);
            this.parent = parent;
            this.ivThumb = (ImageView) parent.findViewById(R.id.ivThumb);
            this.ivRemove = (ImageView) parent.findViewById(R.id.ivRemove);
            this.ivEdit = (ImageView) parent.findViewById(R.id.ivEdit);
        }

        public void onItemClick(final View view, final Object o) {
            if (SelectedImageAdapter.this.clickListner != null) {
                SelectedImageAdapter.this.clickListner.onItemClick(view, o);
            }
        }
    }
}
