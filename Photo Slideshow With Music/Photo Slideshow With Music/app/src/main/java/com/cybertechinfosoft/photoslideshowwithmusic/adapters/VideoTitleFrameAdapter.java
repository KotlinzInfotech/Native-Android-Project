package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.EndFrameFrag;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.NewTitleActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.StartFrameFrag;
import java.util.ArrayList;

public class VideoTitleFrameAdapter extends RecyclerView.Adapter<VideoTitleFrameAdapter.Holder> {
    private MyApplication application;
    private boolean b;
    private Fragment fragment;
    private RequestManager glide;
    private Context mContext;
    private ArrayList<Integer> titleFrames;

    public VideoTitleFrameAdapter() {
    }

    public VideoTitleFrameAdapter(final ArrayList<Integer> titleFrames, final Fragment fragment, final boolean b) {
        this.titleFrames = titleFrames;
        this.mContext = (Context) fragment.getActivity();
        this.b = b;
        this.fragment = fragment;
        this.application = MyApplication.getInstance();
        this.glide = Glide.with(this.mContext);
    }

    public int getItemCount() {
        return this.titleFrames.size();
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        final boolean b = false;
        boolean checked = false;
        if (n == 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("MyApplication.isStartFirst ");
            sb.append(MyApplication.isStoryAdded);
            sb.append(" b ");
            sb.append(this.b);
            sb.append(" pos ");
            sb.append(n);
            if (this.b) {
                if (MyApplication.isStoryAdded) {
                    holder.ivThumb.setImageURI(Uri.parse(this.application.getSelectedImages().get(0).imagePath));
                } else {
                    this.glide.load((Integer) this.titleFrames.get(n)).into(holder.ivThumb);
                }
            } else if (MyApplication.isStoryAdded) {
                holder.ivThumb.setImageURI(Uri.parse(this.application.getSelectedImages().get(this.application.getSelectedImages().size() - 1).imagePath));
            } else {
                this.glide.load((Integer) this.titleFrames.get(n)).into(holder.ivThumb);
            }
        } else {
            this.glide.load((Integer) this.titleFrames.get(n)).into(holder.ivThumb);
        }
        holder.tvThemeName.setVisibility(View.INVISIBLE);
        if (this.b) {
            final CheckBox cbSelect = holder.cbSelect;
            if (this.titleFrames.get(n) == this.application.startFrame) {
                checked = true;
            }
            cbSelect.setChecked(checked);
        } else {
            final CheckBox cbSelect2 = holder.cbSelect;
            boolean checked2 = b;
            if (this.titleFrames.get(n) == this.application.endFrame) {
                checked2 = true;
            }
            cbSelect2.setChecked(checked2);
        }
        if (holder.cbSelect.isChecked()) {
            holder.rlItemParent.setBackgroundColor(this.mContext.getResources().getColor(R.color.app_theme_color));
        } else {
            holder.rlItemParent.setBackgroundColor(this.mContext.getResources().getColor(R.color.transparent));
        }
        holder.clickableView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                NewTitleActivity.selectedFramePos = n;
                if (VideoTitleFrameAdapter.this.b) {
                    if (MyApplication.isStoryAdded && n == 1) {
                        VideoTitleFrameAdapter.this.application.startFrame = 0;
                    } else {
                        VideoTitleFrameAdapter.this.application.startFrame = VideoTitleFrameAdapter.this.titleFrames.get(n);
                    }
                    ((StartFrameFrag) VideoTitleFrameAdapter.this.fragment).setSelectedFrame();
                } else {
                    if (MyApplication.isStoryAdded && n == 1) {
                        VideoTitleFrameAdapter.this.application.endFrame = 0;
                    } else {
                        VideoTitleFrameAdapter.this.application.endFrame = VideoTitleFrameAdapter.this.titleFrames.get(n);
                    }
                    ((EndFrameFrag) VideoTitleFrameAdapter.this.fragment).setSelectedFrame();
                }
                VideoTitleFrameAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new Holder(LayoutInflater.from(this.mContext).inflate(R.layout.layout_story_theme_item, viewGroup, false));
    }

    public class Holder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        private View clickableView;
        private ImageView ivThumb;
        RelativeLayout rlItemParent;
        private TextView tvThemeName;

        public Holder(final View view) {
            super(view);
            this.cbSelect = (CheckBox) view.findViewById(R.id.cbSelect);
            this.ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
            this.tvThemeName = (TextView) view.findViewById(R.id.tvThemeName);
            this.clickableView = view.findViewById(R.id.clickableView);
            this.rlItemParent = (RelativeLayout) view.findViewById(R.id.rlItemParent);
        }
    }
}
