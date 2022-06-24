package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.OnlineMusicActivtiy;
import com.cybertechinfosoft.photoslideshowwithmusic.data.MusicData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ProgressModel;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.DonutProgress;

public class DownloadMusicAdapter extends BaseAdapter {
    OnlineMusicActivtiy ActivityOfAdaptor;
    public int checked;
    LayoutInflater li;
    Context mContext;
    MyViewHolder mHolder;

    public DownloadMusicAdapter(final Context mContext) {
        this.li = null;
        this.checked = -1;
        this.mContext = mContext;
        this.ActivityOfAdaptor = (OnlineMusicActivtiy) mContext;
        this.li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return OnlineMusicActivtiy.mMusicDatas.size();
    }

    public Object getItem(final int n) {
        return null;
    }

    public long getItemId(final int n) {
        return 0L;
    }

    public View getView(final int n, View inflate, final ViewGroup viewGroup) {
        if (inflate == null) {
            inflate = this.li.inflate(R.layout.row_music_item, viewGroup, false);
            this.mHolder = new MyViewHolder();
            this.mHolder.cbx = (CheckBox) inflate.findViewById(R.id.cb_music);
            this.mHolder.ll_download = (LinearLayout) inflate.findViewById(R.id.ll_download);
            this.mHolder.tv_download_name = (TextView) inflate.findViewById(R.id.tv_download_name);
            this.mHolder.iv_dowload = (ImageView) inflate.findViewById(R.id.iv_dowload);
            (this.mHolder.donut_progress = (DonutProgress) inflate.findViewById(R.id.donut_progress)).setMax(100);
            inflate.setTag((Object) this.mHolder);
            Utils.setFont((Activity) this.mContext, this.mHolder.tv_download_name);
            Utils.setFont((Activity) this.mContext, (TextView) this.mHolder.cbx);
        } else {
            this.mHolder = (MyViewHolder) inflate.getTag();
        }
        this.mHolder.cbx.setTag((Object) n);
        this.mHolder.cbx.setText((CharSequence) OnlineMusicActivtiy.mMusicDatas.get(n).track_displayName);
        if (!OnlineMusicActivtiy.mMusicDatas.get(n).isAvailableOffline) {
            this.mHolder.ll_download.setVisibility(View.VISIBLE);
            this.mHolder.tv_download_name.setText((CharSequence) OnlineMusicActivtiy.mMusicDatas.get(n).track_displayName);
            this.mHolder.cbx.setVisibility(View.GONE);
        } else {
            this.mHolder.ll_download.setVisibility(View.GONE);
            this.mHolder.cbx.setVisibility(View.VISIBLE);
        }
        if (this.checked != -1) {
            if (this.checked == n) {
                this.mHolder.cbx.setChecked(true);
                final StringBuilder sb = new StringBuilder();
                sb.append("T checked=");
                sb.append(this.checked);
                sb.append("  Position=");
                sb.append(n);
            } else {
                this.mHolder.cbx.setChecked(false);
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("F checked=");
                sb2.append(this.checked);
                sb2.append("  Position=");
                sb2.append(n);
            }
        }
        if (!OnlineMusicActivtiy.mMusicDatas.get(n).isAvailableOffline) {
            if (OnlineMusicActivtiy.mMusicDatas.get(n).isDownloading) {
                this.mHolder.iv_dowload.setVisibility(View.GONE);
                this.mHolder.donut_progress.setVisibility(View.VISIBLE);
                this.mHolder.donut_progress.setProgress(OnlineMusicActivtiy.mMusicDatas.get(n).down_prg);
            } else {
                this.mHolder.iv_dowload.setVisibility(View.VISIBLE);
                this.mHolder.donut_progress.setVisibility(View.GONE);
            }
        }
        this.mHolder.cbx.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (((CheckBox) view).isChecked()) {
                    DownloadMusicAdapter.this.checked = n;
                    DownloadMusicAdapter.this.ActivityOfAdaptor.rePlayAudio(DownloadMusicAdapter.this.checked, true);
                } else {
                    DownloadMusicAdapter.this.checked = -1;
                    DownloadMusicAdapter.this.ActivityOfAdaptor.rePlayAudio(DownloadMusicAdapter.this.checked, false);
                }
                DownloadMusicAdapter.this.notifyDataSetChanged();
            }
        });
        this.mHolder.iv_dowload.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                final Utils instance = Utils.INSTANCE;
                boolean z = true;
                if (Utils.checkConnectivity(DownloadMusicAdapter.this.mContext, true)) {
                    final String songDownloadUri = ((MusicData) OnlineMusicActivtiy.mMusicDatas.get(n)).SongDownloadUri;
                    for (int i = 0; i < Utils.mPrgModel.size(); i++) {
                        if (((ProgressModel) Utils.mPrgModel.get(i)).Uri.equals(view)) {
                            break;
                        }
                    }
                    z = false;
                    if (!z) {
                        DownloadMusicAdapter.this.notifyDataSetChanged();
                        DownloadMusicAdapter.this.ActivityOfAdaptor.downloadAudio(((MusicData) OnlineMusicActivtiy.mMusicDatas.get(n)).SongDownloadUri, ((MusicData) OnlineMusicActivtiy.mMusicDatas.get(n)).track_displayName, n);
                    }
                }
            }
        });
        return inflate;
    }

    private static class MyViewHolder {
        protected CheckBox cbx;
        protected DonutProgress donut_progress;
        protected ImageView iv_dowload;
        protected LinearLayout ll_download;
        protected TextView tv_download_name;
    }
}
