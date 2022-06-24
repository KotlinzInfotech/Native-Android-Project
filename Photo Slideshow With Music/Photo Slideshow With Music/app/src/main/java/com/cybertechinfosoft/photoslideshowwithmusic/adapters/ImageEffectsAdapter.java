package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.media.effect.Effect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageEditorActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.view.RoundedImageView;

public class ImageEffectsAdapter extends RecyclerView.Adapter<ImageEffectsAdapter.ViewHolder> {
    public Effect effect;
    LayoutInflater inflater;
    private Context mContext;
    private int selectedItem;

    public ImageEffectsAdapter(final Context mContext) {
        this.selectedItem = 0;
        this.inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
    }

    public Object getItem(final int n) {
        return null;
    }

    public int getItemCount() {
        return 12;
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
        viewHolder.bind(n);
    }

    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new ViewHolder(this.inflater.inflate(R.layout.gallery_image_layout, viewGroup, false));
    }

    public void setSelectedItem(final int selectedItem) {
        if (this.selectedItem != -1) {
            this.notifyItemChanged(this.selectedItem);
        }
        if (this.selectedItem != selectedItem) {
            this.selectedItem = selectedItem;
        }
        this.notifyItemChanged(selectedItem);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView roundedImageView;

        public ViewHolder(final View view) {
            super(view);
            this.roundedImageView = (RoundedImageView) view.findViewById(R.id.gallery_image_preview_item);
        }

        private void bind(final int n) {
            if (n < ImageEditorActivity.bitmapPreview.size()) {
                this.roundedImageView.setImageBitmap(ImageEditorActivity.bitmapPreview.get(n));
            }
            if (ImageEffectsAdapter.this.selectedItem == n) {
                this.roundedImageView.setBorderColor(ImageEffectsAdapter.this.mContext.getResources().getColor(R.color.app_theme_color));
                return;
            }
            this.roundedImageView.setBorderColor(0);
        }
    }
}
