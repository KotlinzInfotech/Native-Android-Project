package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity.Activity_imageEdit;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.FrameModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.MyViewHolder> {
    int selected_pos = 0;
    public ArrayList<FrameModel> frames = new ArrayList();
    private Activity_imageEdit EditActivity;

    public FrameAdapter(Context context, ArrayList<FrameModel> frames) {
        this.EditActivity = (Activity_imageEdit) context;
        this.frames = frames;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.framelayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (selected_pos == position) {
            Glide.with(EditActivity).load(frames.get(position).getThumbId()).centerCrop().crossFade().into(holder.imageView);
        } else {
            Glide.with(EditActivity).load(frames.get(position).getFrameID()).centerCrop().crossFade().into(holder.imageView);
        }
        holder.llFrameMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_pos = position;
                if (position == 0) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame5);
                } else if (position == 1) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame8);
                } else if (position == 2) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame3);
                } else if (position == 3) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame4);
                } else if (position == 4) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame6);
                } else if (position == 5) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame2);
                } else if (position == 6) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.list_frame7);
                } else if (position == 7) {
                    EditActivity.IsSticker = true;
                    EditActivity.SetSticker(EditActivity.sticker_list);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.frames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llFrameMain;
        LinearLayout llBorder;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llFrameMain = itemView.findViewById(R.id.ll_frame_main);
            llBorder = itemView.findViewById(R.id.ll_border);
            imageView = itemView.findViewById(R.id.img);
        }
    }
}
