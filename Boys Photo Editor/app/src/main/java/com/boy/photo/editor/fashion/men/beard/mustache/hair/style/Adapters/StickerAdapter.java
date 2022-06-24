package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity.Activity_imageEdit;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.StickerView;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.ViewGroupUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.MyViewHolder> {
    int selected_pos = 0;
    private final Activity_imageEdit activity;
    ArrayList<Integer> stickers;


    public StickerAdapter(Context context, ArrayList<Integer> stickers) {
        this.activity = (Activity_imageEdit) context;
        this.stickers = stickers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StickerAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Glide.with(activity).load(stickers.get(position)).into(holder.ivSticker);
        if (selected_pos == position) {
            holder.llStickerBorder.setBackgroundResource(R.drawable.bg_border_press);
        } else {
            holder.llStickerBorder.setBackgroundResource(R.drawable.bg_border);
        }
        holder.llStickerMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_pos = position;
                final StickerView stickerView = new StickerView(activity);
                activity.stickerId = stickers.get(position);
                stickerView.setImageResource(activity.stickerId);
                stickerView.setOperationListener(new StickerView.OperationListener() {
                    public void onDeleteClick() {
                        if (activity.stickers != null) {
                            activity.stickers.remove(stickerView);
                            activity.frameLayout.removeView(stickerView);
                        }
                    }

                    public void onEdit(StickerView stickerView) {
                        if (Activity_imageEdit.currentView != null) {
                            Activity_imageEdit.currentView.setInEdit(false);
                        }
                        Activity_imageEdit.currentView = stickerView;
                        Activity_imageEdit.currentView.setInEdit(true);
                    }

                    public void onTop(StickerView stickerView) {
                    }
                });
                if (position == ((LinearLayoutManager) activity.rvSticker.getLayoutManager()).findLastCompletelyVisibleItemPosition()) {
                    activity.rvSticker.smoothScrollToPosition(position + 1);
                } else {
                    if (position == 0) {
                    } else {
                        activity.rvSticker.smoothScrollToPosition(position - 1);
                    }
                }
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -1, 17);
                stickerView.setLayoutParams(new FrameLayout.LayoutParams(100, 100, 17));
                if (activity.IsSticker) {
                    activity.IsSticker = false;
                    activity.frameLayout.addView(stickerView, 0, lp);
                    activity.stickers.add(stickerView);
                    activity.setCurrentEdit(stickerView);
                } else {
                    if (activity.frameLayout.getChildCount() == 0) {
                        activity.frameLayout.addView(stickerView, 0, lp);
                        activity.stickers.add(stickerView);
                        activity.setCurrentEdit(stickerView);
                    } else {
                        activity.frameLayout.removeViewAt(0);
                        activity.stickers.remove(stickerView);
                        activity.frameLayout.addView(stickerView, 0, lp);
                        activity.stickers.add(stickerView);
                        activity.setCurrentEdit(stickerView);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.stickers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llStickerMain;
        LinearLayout llStickerBorder;
        ImageView ivSticker;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llStickerMain = itemView.findViewById(R.id.ll_sticker_main);
            llStickerBorder = itemView.findViewById(R.id.ll_sticker_border);
            ivSticker = itemView.findViewById(R.id.img_sticker_item);
        }
    }
}
