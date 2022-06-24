package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cybertechinfosoft.photoslideshowwithmusic.OnStickerSelected;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageEditorActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.NewTitleActivity;
import java.util.ArrayList;

public class NewStickerAdapter extends RecyclerView.Adapter<NewStickerAdapter.Holder> {
    private Context mContext;
    private OnStickerSelected onStickerSelected;
    ArrayList<Integer> stickerListArray;

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView ivSticker;

        public Holder(View view) {
            super(view);
            this.ivSticker = (ImageView) view.findViewById(R.id.ivSticker);
        }
    }

    public OnStickerSelected getOnStickerSelected() {
        return this.onStickerSelected;
    }

    public void setOnStickerSelected(OnStickerSelected onStickerSelected) {
        this.onStickerSelected = onStickerSelected;
    }

    public NewStickerAdapter(Context context, ArrayList<Integer> arrayList) {
        this.mContext = context;
        this.stickerListArray = arrayList;
    }

    public int getItemCount() {
        return this.stickerListArray.size();
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        Glide.with(this.mContext).load((Integer) this.stickerListArray.get(n)).into(holder.ivSticker);
        holder.itemView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (NewStickerAdapter.this.mContext instanceof NewTitleActivity) {
                    NewStickerAdapter.this.onStickerSelected.onStickerTouch(NewStickerAdapter.this.stickerListArray.get(n));
                    return;
                }
                if (NewStickerAdapter.this.mContext instanceof ImageEditorActivity) {
                    ((ImageEditorActivity) NewStickerAdapter.this.mContext).addSticker(NewStickerAdapter.this.stickerListArray.get(n));
                }
            }
        });
    }

    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(this.mContext).inflate(R.layout.layout_stickers_raw, viewGroup, false));
    }
}
