package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageSelectionActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlbumAdapterById extends RecyclerView.Adapter<AlbumAdapterById.Holder> {
    private ImageSelectionActivity activity;
    private MyApplication application;
    private OnItemClickListner<Object> clickListner;
    private ArrayList<String> folderId;
    RequestManager glide;
    private LayoutInflater inflater;

    public AlbumAdapterById(final Context context) {
        this.glide = Glide.with(context);
        this.application = MyApplication.getInstance();
        this.folderId = new ArrayList<String>(this.application.getAllAlbum().keySet());
        this.activity = (ImageSelectionActivity) context;
        Collections.sort(this.folderId, new Comparator<String>() {
            @Override
            public int compare(final String s, final String s2) {
                return s.compareToIgnoreCase(s2);
            }
        });
        if (this.activity.isFromCameraNotification) {
            this.application.setSelectedFolderId("-1739773001");
            this.activity.scrollToPostion(this.scrollToPos());
        } else {
            this.application.setSelectedFolderId((String) this.folderId.get(0));
        }
        this.inflater = LayoutInflater.from(context);
    }

    public String getItem(final int n) {
        return this.folderId.get(n);
    }

    public int getItemCount() {
        return this.folderId.size();
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        final String item = this.getItem(n);
        final ImageData imageData = this.application.getImageByAlbum(item).get(0);
        holder.textView.setSelected(true);
        holder.textView.setText((CharSequence) imageData.folderName);
        this.glide.load(imageData.imagePath).into(holder.imageView);
        holder.cbSelect.setChecked(item.equals(this.application.getSelectedFolderId()));
        if (holder.cbSelect.isChecked()) {
            holder.rlItemParent.setBackgroundColor(this.activity.getResources().getColor(R.color.app_theme_color));
            holder.cbSelect.setVisibility(View.VISIBLE);
        } else {
            holder.rlItemParent.setBackgroundColor(this.activity.getResources().getColor(R.color.transparent));
            holder.cbSelect.setVisibility(View.GONE);
        }
        holder.clickableView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                AlbumAdapterById.this.application.setSelectedFolderId(item);
                if (AlbumAdapterById.this.clickListner != null) {
                    AlbumAdapterById.this.clickListner.onItemClick(view, imageData);
                }
                AlbumAdapterById.this.notifyDataSetChanged();
            }
        });
    }

    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new Holder(this.inflater.inflate(R.layout.items, viewGroup, false));
    }

    public int scrollToPos() {
        for (int i = 0; i < this.folderId.size(); ++i) {
            if (this.folderId.get(i).equals("-1739773001")) {
                return i;
            }
        }
        return 0;
    }

    public void setOnItemClickListner(final OnItemClickListner<Object> clickListner) {
        this.clickListner = clickListner;
    }

    public class Holder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        private View clickableView;
        ImageView imageView;
        View parent;
        RelativeLayout rlItemParent;
        TextView textView;

        public Holder(final View parent) {
            super(parent);
            this.parent = parent;

            this.cbSelect = (CheckBox) parent.findViewById(R.id.cbSelect);
            this.imageView = (ImageView) parent.findViewById(R.id.imageView1);
            this.textView = (TextView) parent.findViewById(R.id.textView1);
            this.clickableView = parent.findViewById(R.id.clickableView);
            this.rlItemParent = (RelativeLayout) parent.findViewById(R.id.rlItemParent);
        }

        public void onItemClick(final View view, final Object o) {
            if (AlbumAdapterById.this.clickListner != null) {
                AlbumAdapterById.this.clickListner.onItemClick(view, o);
            }
        }
    }
}
