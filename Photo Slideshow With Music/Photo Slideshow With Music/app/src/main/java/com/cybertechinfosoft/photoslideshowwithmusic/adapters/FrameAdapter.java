package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.PreviewActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.Holder> {
    PreviewActivity activity;
    private MyApplication application;
    private int[] drawable;
    private LayoutInflater inflater;
    int lastPos;

    public FrameAdapter(final PreviewActivity activity) {
        drawable = new int[]{R.drawable.ic_trans, R.drawable.frame1, R.drawable.frame2, R.drawable.frame3, R.drawable.frame4, R.drawable.frame5, R.drawable.frame6, R.drawable.frame7, R.drawable.frame8, R.drawable.frame9, R.drawable.frame10, R.drawable.frame11, R.drawable.frame12, R.drawable.frame13, R.drawable.frame14, R.drawable.frame15, R.drawable.frame16, R.drawable.frame17, R.drawable.frame18, R.drawable.frame19, R.drawable.frame21, R.drawable.frame22, R.drawable.frame23, R.drawable.frame24, R.drawable.frame25, R.drawable.frame26, R.drawable.frame27, R.drawable.frame28, R.drawable.frame30, R.drawable.frame32, R.drawable.frame33};

        this.lastPos = 0;
        this.activity = activity;
        this.application = MyApplication.getInstance();
        this.inflater = LayoutInflater.from((Context) activity);
    }

    public int getItem(final int n) {
        return this.drawable[n];
    }

    public int getItemCount() {
        return this.drawable.length;
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        final int item = this.getItem(n);
        if (n != 0) {
            holder.tvThemeName.setVisibility(View.GONE);
            Glide.with((Context) this.application).load(item).into(holder.ivThumb);
        } else {
            holder.tvThemeName.setText(R.string.no_frame);
            holder.tvThemeName.setVisibility(View.GONE);
            Glide.with((Context) this.application).load(R.drawable.none_frame).into(holder.ivThumb);
        }
        holder.ivThumb.setScaleType(ImageView.ScaleType.FIT_XY);
        final int frame = this.activity.getFrame();
        boolean checked = true;
        if (frame == 0 && n == 0) {
            holder.cbSelect.setChecked(true);
        } else {
            final CheckBox cbSelect = holder.cbSelect;
            if (item != this.activity.getFrame()) {
                checked = false;
            }
            cbSelect.setChecked(checked);
            if (this.application.getFrame() != 0) {
                this.activity.setFrame(this.application.getFrame());
            }
        }
        if (holder.cbSelect.isChecked()) {
            holder.cbSelect.setVisibility(View.VISIBLE);
        } else {
            holder.cbSelect.setVisibility(View.GONE);
        }
        holder.clickableView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (item == FrameAdapter.this.activity.getFrame()) {
                    return;
                }
                if (item == -1) {
                    return;
                }
                FrameAdapter.this.activity.setFrame(item);
                FileUtils.deleteFile(FileUtils.frameFile);
                while (true) {
                    try {
                        Bitmap bitmap;
                        if (n == 0) {
                            bitmap = BitmapFactory.decodeResource(FrameAdapter.this.activity.getResources(), R.drawable.ic_trans);
                        } else {
                            bitmap = BitmapFactory.decodeResource(FrameAdapter.this.activity.getResources(), item);
                        }
                        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, false);
                        final FileOutputStream fileOutputStream = new FileOutputStream(FileUtils.frameFile);
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream) fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        scaledBitmap.recycle();
                        System.gc();
                        FrameAdapter.this.notifyDataSetChanged();
                    } catch (Exception ex) {
                        continue;
                    }
                    break;
                }
            }
        });
    }

    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new Holder(this.inflater.inflate(R.layout.movie_theme_items, viewGroup, false));
    }

    public void setOnItemClickListner(final OnItemClickListner<Object> onItemClickListner) {
    }

    public class Holder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        private View clickableView;
        private ImageView ivThumb;
        private TextView tvThemeName;

        public Holder(final View view) {
            super(view);
            this.cbSelect = (CheckBox) view.findViewById(R.id.cbSelect);
            this.ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
            this.tvThemeName = (TextView) view.findViewById(R.id.tvThemeName);
            this.clickableView = view.findViewById(R.id.clickableView);
        }
    }
}
