package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.movienaker.movie.themes.THEMES;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.PreviewActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.service.ImageCreatorService;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
import java.util.ArrayList;
import java.util.Arrays;

public class MoviewThemeAdapter extends RecyclerView.Adapter<MoviewThemeAdapter.Holder> {
    private MyApplication application;
    EPreferences ePref;
    private LayoutInflater inflater;
    private ArrayList<THEMES> list;
    private PreviewActivity previewActivity;

    public MoviewThemeAdapter(final PreviewActivity previewActivity) {
        this.application = MyApplication.getInstance();
        this.previewActivity = previewActivity;
        this.list = new ArrayList<THEMES>(Arrays.asList(THEMES.values()));
        this.inflater = LayoutInflater.from((Context) previewActivity);
        this.ePref = EPreferences.getInstance((Context) this.previewActivity);
    }

    private void deleteThemeDir(final String s) {
        new Thread() {
            @Override
            public void run() {
                FileUtils.deleteThemeDir(s);
            }
        }.start();
    }

    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<THEMES> getList() {
        return this.list;
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        final THEMES themes = this.list.get(n);
        Glide.with((Context) this.application).load(themes.getThemeDrawable()).into(holder.ivThumb);
        final TextView access$100 = holder.tvThemeName;
        boolean checked = true;
        access$100.setSelected(true);
        holder.tvThemeName.setText((CharSequence) themes.toString());
        holder.tvThemeName.setVisibility(View.GONE);
        final CheckBox cbSelect = holder.cbSelect;
        if (themes != this.application.selectedTheme) {
            checked = false;
        }
        cbSelect.setChecked(checked);
        if (holder.cbSelect.isChecked()) {
            holder.cbSelect.setVisibility(View.VISIBLE);
        } else {
            holder.cbSelect.setVisibility(View.GONE);
        }
        holder.clickableView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (MoviewThemeAdapter.this.list.get(n) == MoviewThemeAdapter.this.application.selectedTheme) {
                    return;
                }
                MoviewThemeAdapter.this.deleteThemeDir(MoviewThemeAdapter.this.application.selectedTheme.toString());
                MoviewThemeAdapter.this.application.videoImages.clear();
                if (MoviewThemeAdapter.this.application.isSelectSYS) {
                    MoviewThemeAdapter.this.application.isFristTimeTheme = false;
                } else {
                    MoviewThemeAdapter.this.application.isFristTimeTheme = true;
                }
                MoviewThemeAdapter.this.application.selectedTheme = MoviewThemeAdapter.this.list.get(n);
                MoviewThemeAdapter.this.application.setCurrentTheme(MoviewThemeAdapter.this.application.selectedTheme.toString());
                MoviewThemeAdapter.this.previewActivity.reset();
                final Intent intent = new Intent((Context) MoviewThemeAdapter.this.application, (Class) ImageCreatorService.class);
                intent.putExtra("selected_theme", MoviewThemeAdapter.this.application.getCurrentTheme());
                MoviewThemeAdapter.this.application.startService(intent);
                MoviewThemeAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new Holder(this.inflater.inflate(R.layout.movie_theme_items, viewGroup, false));
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
