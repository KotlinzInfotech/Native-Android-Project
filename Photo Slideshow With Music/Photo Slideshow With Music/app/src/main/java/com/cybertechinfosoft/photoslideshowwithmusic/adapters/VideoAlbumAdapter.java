package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.Activity_VideoPlay;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.VideoAlbumActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.data.VideoData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;

public class VideoAlbumAdapter extends RecyclerView.Adapter<VideoAlbumAdapter.Holder> {
    public static ArrayList<VideoData> mVideoDatas;
    private final Context mContext;
    VideoAlbumActivity activity;
    private int id;

    public class Holder extends RecyclerView.ViewHolder {
        private final View clickable;
        private final ImageView ivPreview;
        private final Toolbar toolbar;
        private final TextView tvDuration;
        private final TextView tvFileName;
        private final TextView tvVideoDate;

        public Holder(View view) {
            super(view);
            this.clickable = view.findViewById(R.id.list_item_video_clicker);
            this.ivPreview = view.findViewById(R.id.list_item_video_thumb);
            this.tvDuration = view.findViewById(R.id.list_item_video_duration);
            this.tvFileName = view.findViewById(R.id.list_item_video_title);
            this.tvVideoDate = view.findViewById(R.id.list_item_video_date);
            this.toolbar = view.findViewById(R.id.list_item_video_toolbar);
        }
    }

    private class MenuItemClickListener implements Toolbar.OnMenuItemClickListener {
        VideoData videoData;

        public MenuItemClickListener(VideoData videoData) {
            this.videoData = videoData;
        }


        public boolean onMenuItemClick(final MenuItem menuItem) {
            final int index = VideoAlbumAdapter.mVideoDatas.indexOf(this.videoData);
            final int itemId = menuItem.getItemId();
            if (itemId != R.id.action_delete) {
                if (itemId == R.id.action_share_native) {
                    final File file = new File(VideoAlbumAdapter.mVideoDatas.get(index).videoFullPath);
                    final Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("video/*");
                    intent.putExtra("android.intent.extra.SUBJECT", VideoAlbumAdapter.mVideoDatas.get(index).videoName);
                    intent.putExtra("android.intent.extra.TITLE", VideoAlbumAdapter.mVideoDatas.get(index).videoName);
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                    VideoAlbumAdapter.this.mContext.startActivity(Intent.createChooser(intent, "Share Video"));
                }
            } else {
                final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(VideoAlbumAdapter.this.mContext, R.style.Theme_MovieMaker_AlertDialog);
                alertDialog$Builder.setTitle(R.string.delete_video_);
                final StringBuilder sb = new StringBuilder();
                sb.append(VideoAlbumAdapter.this.mContext.getResources().getString(R.string.are_you_sure_to_delete_));
                sb.append(VideoAlbumAdapter.mVideoDatas.get(index).videoName);
                sb.append(".mp4 ?");
                alertDialog$Builder.setMessage(sb.toString());
                alertDialog$Builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        FileUtils.deleteFile(new File(VideoAlbumAdapter.mVideoDatas.remove(index).videoFullPath));
                        VideoAlbumAdapter.this.notifyDataSetChanged();
                    }
                });
                alertDialog$Builder.setNegativeButton("Cancel", null);
                alertDialog$Builder.show();
            }
            return false;
        }
    }

    public VideoAlbumAdapter(Context context, ArrayList<VideoData> arrayList) {
        mVideoDatas = arrayList;
        this.mContext = context;
        this.activity = (VideoAlbumActivity) mContext;
    }

    public int getItemCount() {
        return mVideoDatas.size();
    }

    public void onBindViewHolder(Holder holder, @SuppressLint("RecyclerView") final int i) {
        holder.tvDuration.setText(FileUtils.getDuration(mVideoDatas.get(i).videoDuration));
        Glide.with(this.mContext).load(mVideoDatas.get(i).videoFullPath).into(holder.ivPreview);
        TextView access$200 = holder.tvFileName;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mVideoDatas.get(i).videoName);
        stringBuilder.append(".mp4");
        access$200.setText(stringBuilder.toString());
        holder.tvVideoDate.setText(DateFormat.getDateInstance().format(Long.valueOf(mVideoDatas.get(i).dateTaken)));
        holder.tvDuration.setVisibility(View.INVISIBLE);
        holder.tvFileName.setVisibility(View.INVISIBLE);
        holder.tvVideoDate.setVisibility(View.INVISIBLE);
        holder.clickable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoAlbumAdapter.this.id = i;
                activity.view = view;
                VideoAlbumAdapter.this.loadVideoPlayer(activity.view);
            }
        });
        menu(holder.toolbar, R.menu.home_item_exported_video_local_menu, new MenuItemClickListener(mVideoDatas.get(i)));
    }


    private void loadVideoPlayer(View view) {
        Intent intent = new Intent(this.mContext, Activity_VideoPlay.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("KEY", "FromVideoAlbum");
        intent.putExtra("android.intent.extra.TEXT", mVideoDatas.get(id).videoFullPath);
        intent.putExtra(this.mContext.getResources().getString(R.string.video_position_key), id);
        ActivityAnimUtil.startActivitySafely(view, intent);
    }

    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(this.mContext).inflate(R.layout.list_item_published_video, viewGroup, false));
    }

    public static void menu(Toolbar toolbar, int i, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(i);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }
}
