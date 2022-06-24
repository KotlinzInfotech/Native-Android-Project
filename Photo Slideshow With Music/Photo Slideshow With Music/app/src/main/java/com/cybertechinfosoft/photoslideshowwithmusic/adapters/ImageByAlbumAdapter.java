package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageSelectionActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import java.util.ArrayList;

public class ImageByAlbumAdapter extends RecyclerView.Adapter<ImageByAlbumAdapter.Holder> {
    private ImageSelectionActivity activity;
    private MyApplication application;
    private OnItemClickListner<Object> clickListner;
    private RequestManager glide;
    private LayoutInflater inflater;

    public ImageByAlbumAdapter(final Context context) {
        this.application = MyApplication.getInstance();
        this.inflater = LayoutInflater.from(context);
        this.glide = Glide.with(context);
        this.activity = (ImageSelectionActivity) context;
        if (this.activity.isFromCameraNotification) {
            ImageSelectionActivity.isForFirst = true;
            ImageSelectionActivity.tempImage = (ArrayList<ImageData>) this.application.getSelectedImages();
        }
    }

    public ImageData getItem(final int n) {
        return this.application.getImageByAlbum(this.application.getSelectedFolderId()).get(n);
    }

    public int getItemCount() {
        return this.application.getImageByAlbum(this.application.getSelectedFolderId()).size();
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        final ImageData item = this.getItem(n);
        holder.textView.setSelected(true);
        final TextView textView = holder.textView;
        String format;
        if (item.imageCount == 0) {
            format = "";
        } else {
            format = String.format("%02d", item.imageCount);
        }
        textView.setText((CharSequence) format);
        this.glide.load(item.imagePath).into(holder.imageView);
        final TextView textView2 = holder.textView;
        int color;
        if (item.imageCount == 0) {
            color = 0;
        } else {
            color = Color.parseColor("#AC000000");
        }
        textView2.setBackgroundColor(color);
        if (ImageSelectionActivity.tempImage.size() == 0) {
            ImageSelectionActivity.isForFirst = false;
        }
        if (this.activity.isFromCameraNotification && ImageSelectionActivity.isForFirst) {
            for (int i = 0; i < this.application.getSelectedImages().size(); ++i) {
                final ImageData imageData = this.application.getSelectedImages().get(i);
                if (imageData.imagePath.equals(item.imagePath)) {
                    holder.textView.setText((CharSequence) String.format("%02d", imageData.imageCount));
                }
            }
        }
        holder.clickableView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (holder.imageView.getDrawable() == null) {
                    Toast.makeText((Context) ImageByAlbumAdapter.this.application, R.string.image_currpted_or_not_support_, Toast.LENGTH_SHORT).show();
                    return;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Image data ");
                sb.append(item.imageCount);
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Image data path ");
                sb2.append(item.imagePath);
                if (ImageByAlbumAdapter.this.activity.isFromCameraNotification) {
                    for (int i = 0; i < ImageByAlbumAdapter.this.application.getSelectedImages().size(); ++i) {
                        final ImageData imageData = ImageByAlbumAdapter.this.application.getSelectedImages().get(i);
                        if (imageData.imagePath.equals(item.imagePath)) {
                            holder.textView.setText((CharSequence) String.format("%02d", imageData.imageCount + 1));
                            holder.textView.setBackgroundColor(1627373831);
                            ImageByAlbumAdapter.this.application.addSelectedImage(imageData);
                            ImageByAlbumAdapter.this.notifyItemChanged(n);
                            if (ImageByAlbumAdapter.this.clickListner != null) {
                                ImageByAlbumAdapter.this.clickListner.onItemClick(view, item);
                            }
                            return;
                        }
                    }
                }
                ImageByAlbumAdapter.this.application.addSelectedImage(item);
                ImageByAlbumAdapter.this.notifyItemChanged(n);
                if (ImageByAlbumAdapter.this.clickListner != null) {
                    ImageByAlbumAdapter.this.clickListner.onItemClick(view, item);
                }
            }
        });
    }

    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new Holder(this.inflater.inflate(R.layout.items_by_folder, viewGroup, false));
    }

    public void setOnItemClickListner(final OnItemClickListner<Object> clickListner) {
        this.clickListner = clickListner;
    }

    public class Holder extends RecyclerView.ViewHolder {
        View clickableView;
        ImageView imageView;
        View parent;
        TextView textView;

        public Holder(final View parent) {
            super(parent);
            this.parent = parent;
            this.imageView = (ImageView) parent.findViewById(R.id.imageView1);
            this.textView = (TextView) parent.findViewById(R.id.textView1);
            this.clickableView = parent.findViewById(R.id.clickableView);
        }

        public void onItemClick(final View view, final Object o) {
            if (ImageByAlbumAdapter.this.clickListner != null) {
                ImageByAlbumAdapter.this.clickListner.onItemClick(view, o);
            }
        }
    }
}
