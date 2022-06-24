package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.DownloadMusicAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.data.MusicData;
import com.cybertechinfosoft.photoslideshowwithmusic.service.FileDownloadService;
import com.cybertechinfosoft.photoslideshowwithmusic.util.AsynkModel;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ConcDownloadTask;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
import com.cybertechinfosoft.photoslideshowwithmusic.util.GloblePrgListener;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ProgressModel;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.DonutProgress;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OnlineMusicActivtiy extends AppCompatActivity implements GloblePrgListener {
    Activity activity = OnlineMusicActivtiy.this;
    private static ListView lv_music_list;
    public static DownloadMusicAdapter mAdapter;
    public static ArrayList<MusicData> mMusicDatas;
    EPreferences MyPref;
    private final int REQUEST_PICK_AUDIO;
    private MyApplication application;
    private String avail_offline;
    Button btnFromStorage;
    FileDownloadService mDownloadService;
    private String mFilename;
    Handler mLocalLoadHandler;
    OfflineDialog mOfflineDialog;
    OnlineDialog mOnlineDialog;
    Handler mOnlineLoadHandler;
    MediaPlayer mediaPlayer;
    String path;
    private MusicData selectedMusicData;
    private Toolbar toolbar;

    static {
        OnlineMusicActivtiy.mMusicDatas = new ArrayList<MusicData>();
    }

    public OnlineMusicActivtiy() {
        this.REQUEST_PICK_AUDIO = 101;
        this.mFilename = "";
        this.avail_offline = "";
        this.mLocalLoadHandler = new Handler();
        this.mOnlineLoadHandler = new Handler();
    }

    private void addListener() {
        btnFromStorage.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (OnlineMusicActivtiy.this.application.getMusicFiles(false).size() > 0) {
                    if (MyApplication.isShowAd == 1) {
                        stopPlaying(mediaPlayer);
                        startActivityForResult(new Intent((Context) OnlineMusicActivtiy.this, (Class) SongEditActivity.class), 101);
                        MyApplication.isShowAd = 0;
                    } else {
                        if (MyApplication.mInterstitialAd != null) {
                            MyApplication.activity = activity;
                            stopPlaying(mediaPlayer);
                            MyApplication.AdsId = 112;
                            MyApplication.mInterstitialAd.show(activity);
                            MyApplication.isShowAd = 1;

                        } else {
                            stopPlaying(mediaPlayer);
                            startActivityForResult(new Intent((Context) OnlineMusicActivtiy.this, (Class) SongEditActivity.class), 101);
                        }


                    }
                    return;
                }
                Toast.makeText(OnlineMusicActivtiy.this.getApplicationContext(), (CharSequence) "No Music found in device\nPlease add music in sdCard", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindView() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        OnlineMusicActivtiy.lv_music_list = (ListView) this.findViewById(R.id.lv_music_list);
        this.btnFromStorage = (Button) this.findViewById(R.id.btnFromStorage);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OnlineMusicActivtiy");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void findAndNotify(final String s, final String track_data) {
        if (OnlineMusicActivtiy.mAdapter != null) {
            for (int i = 0; i < OnlineMusicActivtiy.mMusicDatas.size(); ++i) {
                if (OnlineMusicActivtiy.mMusicDatas.get(i).SongDownloadUri.equals(s)) {
                    OnlineMusicActivtiy.mMusicDatas.get(i).isDownloading = false;
                    OnlineMusicActivtiy.mMusicDatas.get(i).isAvailableOffline = true;
                    OnlineMusicActivtiy.mMusicDatas.get(i).track_data = track_data;
                    break;
                }
            }
            OnlineMusicActivtiy.mAdapter.notifyDataSetChanged();
        }
    }

    private void init() {
        this.setSupportActionBar(this.toolbar);
        final TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText((CharSequence) this.getString(R.string.select_music));
        Utils.setFont((Activity) this, textView);
        this.application = MyApplication.getInstance();
        OnlineMusicActivtiy.mMusicDatas.clear();
        this.mOnlineDialog = new OnlineDialog();
        this.mOfflineDialog = new OfflineDialog();
        this.mDownloadService = new FileDownloadService();
        new LoadMusics().start();
        Utils.setFont((Activity) this, (TextView) this.btnFromStorage);
    }

    private void manageAsynkList(final String s) {
        if (Utils.mAsynkList != null) {
            for (int i = 0; i < Utils.mAsynkList.size(); ++i) {
                if (((AsynkModel) Utils.mAsynkList.get(i)).Uri.equals(s)) {
                    Utils.mAsynkList.remove(i);
                    break;
                }
            }
        }
        if (Utils.TaskCount > 0) {
            --Utils.TaskCount;
        }
    }

    private void setMusic() {
        try {
            FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
            final File file = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
            if (file.exists()) {
                FileUtils.deleteFile(file);
                file.createNewFile();
            }
            final FileInputStream fileInputStream = new FileInputStream(new File(OnlineMusicActivtiy.mMusicDatas.get(OnlineMusicActivtiy.mAdapter.checked).track_data));
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final byte[] array = new byte[1024];
            while (true) {
                final int read = fileInputStream.read(array);
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(array, 0, read);
            }
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.prepare();
            final MusicData musicData = new MusicData();
            musicData.track_data = file.getAbsolutePath();
            mediaPlayer.setOnPreparedListener((MediaPlayer.OnPreparedListener) new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    musicData.track_duration = mediaPlayer.getDuration();
                    mediaPlayer.stop();
                }
            });
            musicData.track_Title = "temp";
            this.application.setMusicData(musicData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setUpListView() {
        OnlineMusicActivtiy.mAdapter = new DownloadMusicAdapter((Context) this);
        OnlineMusicActivtiy.lv_music_list.setAdapter((ListAdapter) OnlineMusicActivtiy.mAdapter);
    }

    private void stopPlaying(final MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("stopPlaying() = ");
                sb.append(ex.getMessage());
            }
        }
    }

    private void updateView(final int n, final float progress) {
        final View child = OnlineMusicActivtiy.lv_music_list.getChildAt(n - OnlineMusicActivtiy.lv_music_list.getFirstVisiblePosition());
        if (child == null) {
            return;
        }
        ((ImageView) child.findViewById(R.id.iv_dowload)).setVisibility(View.GONE);
        final DonutProgress donutProgress = (DonutProgress) child.findViewById(R.id.donut_progress);
        donutProgress.setVisibility(View.VISIBLE);
        donutProgress.setProgress(progress);
    }

    public void downloadAudio(final String s, final String displayName, final int n) {
        if (Utils.checkConnectivity((Context) this, true)) {
            OnlineMusicActivtiy.mMusicDatas.get(n).isDownloading = true;
            OnlineMusicActivtiy.mMusicDatas.get(n).isAvailableOffline = false;
            final AsynkModel asynkModel = new AsynkModel();
            asynkModel.mTask = new ConcDownloadTask((Context) this);
            asynkModel.DisplayName = displayName;
            asynkModel.Uri = s;
            final StringBuilder sb4 = new StringBuilder();
            sb4.append(n);
            sb4.append("");
            asynkModel.pos = sb4.toString();
            Utils.mAsynkList.add(asynkModel);
            final ProgressModel progressModel = new ProgressModel();
            progressModel.Uri = s;
            progressModel.isDownloading = true;
            progressModel.isAvailableOffline = false;
            progressModel.Progress = 0.0f;
            Utils.mPrgModel.add(progressModel);
            if (!this.MyPref.checkUrlAvailable(s)) {
                this.MyPref.putURLValue(s);
            }
            this.startService(new Intent((Context) this, (Class) FileDownloadService.class));
        }
    }

    public void notifyAdaptor(final String s, final String s2) {
        this.findAndNotify(s, s2);
        this.manageAsynkList(s);
    }

    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        super.onActivityResult(n, n2, intent);
        final StringBuilder sb = new StringBuilder();
        sb.append("onlinemusic act onact result requestcode ");
        sb.append(n);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("onlinemusic act onact result resultcode ");
        sb2.append(n2);
        if (n2 == -1) {
            if (n != 101) {
                return;
            }
            this.setResult(-1);
            this.finish();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_predefine_add_music1);
        this.MyPref = EPreferences.getInstance((Context) this);
        this.bindView();
        this.init();
        this.addListener();
        PutAnalyticsEvent();
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_selection, menu);
        menu.removeItem(R.id.menu_clear);
        menu.findItem(R.id.menu_done).setTitle(R.string.done);
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);
            final SubMenu subMenu = item.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); ++j) {
                    Utils.applyFontToMenuItem(this.getApplicationContext(), subMenu.getItem(j));
                }
            }
            Utils.applyFontToMenuItem(this.getApplicationContext(), item);
        }
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId != android.R.id.home) {
            if (itemId == R.id.menu_done) {
                this.stopPlaying(this.mediaPlayer);
                if (OnlineMusicActivtiy.mAdapter.checked != -1) {
                    if (MyApplication.isShowAd == 1) {
                        stopPlaying(this.mediaPlayer);
                        new ChangeMusic().start();
                        MyApplication.isShowAd = 0;
                    } else {
                        if (MyApplication.mInterstitialAd != null) {
                            MyApplication.activity = activity;
                            stopPlaying(this.mediaPlayer);
                            MyApplication.AdsId = 111;
                            MyApplication.mInterstitialAd.show(activity);
                            MyApplication.isShowAd = 1;

                        } else {
                            stopPlaying(this.mediaPlayer);
                            new ChangeMusic().start();
                        }
                    }
                } else {
                    Toast.makeText((Context) this, (CharSequence) "Must select audio", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (MyApplication.isShowAd == 1) {
                stopPlaying(this.mediaPlayer);
                onBackPressed();
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.AdsId = 110;
                    stopPlaying(this.mediaPlayer);
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;

                } else {
                    stopPlaying(this.mediaPlayer);
                    onBackPressed();
                }

            }

        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected void onPause() {
        super.onPause();
        if (this.mediaPlayer != null) {
            this.stopPlaying(this.mediaPlayer);
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    public void rePlayAudio(final int n, final boolean b) {
        if (b) {
            this.stopPlaying(this.mediaPlayer);
            this.mediaPlayer = new MediaPlayer();
            try {
                this.mediaPlayer.setDataSource(OnlineMusicActivtiy.mMusicDatas.get(n).track_data);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.mediaPlayer.setOnPreparedListener((MediaPlayer.OnPreparedListener) new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            this.mediaPlayer.prepareAsync();
            return;
        }
        this.mediaPlayer.pause();
    }

    public void updateProgress(final String s, final float n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("uri = ");
        sb.append(s);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("prg = ");
        sb2.append(n);
        for (int i = 0; i < OnlineMusicActivtiy.mMusicDatas.size(); ++i) {
            if (OnlineMusicActivtiy.mMusicDatas.get(i).SongDownloadUri.equals(s)) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("IDX = ");
                sb3.append(i);
                this.updateView(i, n);
                return;
            }
        }
    }

    public class ChangeMusic extends Thread {
        @Override
        public void run() {
            OnlineMusicActivtiy.this.runOnUiThread((Runnable) new Runnable() {
                @Override
                public void run() {
                    setMusic();
                    setResult(-1, new Intent((Context) OnlineMusicActivtiy.this, (Class) PreviewActivity.class));
                    finish();
                }
            });
        }
    }

    public class LoadMusics extends Thread {
        @Override
        public void run() {
            super.run();
            OnlineMusicActivtiy.this.mOfflineDialog.setState(true);
            OnlineMusicActivtiy.this.mLocalLoadHandler.post((Runnable) OnlineMusicActivtiy.this.mOfflineDialog);
            OnlineMusicActivtiy.mMusicDatas = (ArrayList<MusicData>) MyApplication.getInstance().getMusicFiles(true);
            if (OnlineMusicActivtiy.mMusicDatas.size() > 0) {
                OnlineMusicActivtiy.this.selectedMusicData = OnlineMusicActivtiy.mMusicDatas.get(0);
                OnlineMusicActivtiy.this.mFilename = OnlineMusicActivtiy.this.selectedMusicData.getTrack_data();
                OnlineMusicActivtiy.this.avail_offline = "avail_offline";
                for (int i = 0; i < OnlineMusicActivtiy.mMusicDatas.size(); ++i) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("PAth(");
                    sb.append(i);
                    sb.append(") = ");
                    sb.append(OnlineMusicActivtiy.mMusicDatas.get(0).track_data);
                }
            }
            OnlineMusicActivtiy.this.mOfflineDialog.setState(false);
            OnlineMusicActivtiy.this.mLocalLoadHandler.post((Runnable) OnlineMusicActivtiy.this.mOfflineDialog);
            new LoadOnlineMusics().start();
        }
    }

    public class LoadOnlineMusics extends Thread {
        @Override
        public void run() {
            super.run();
            if (OnlineMusicActivtiy.mMusicDatas.size() == 0) {
                OnlineMusicActivtiy.this.mOnlineDialog.setState(true);
                OnlineMusicActivtiy.this.mOnlineLoadHandler.post((Runnable) OnlineMusicActivtiy.this.mOnlineDialog);
            } else {
                final StringBuilder sb = new StringBuilder();
                sb.append("mMusicDatas.size() = ");
                sb.append(OnlineMusicActivtiy.mMusicDatas.size());
            }
            if (Utils.checkConnectivity((Context) OnlineMusicActivtiy.this, false)) {
                OnlineMusicActivtiy.mMusicDatas.addAll(Utils.INSTANCE.getOnlineAudioFiles((Context) OnlineMusicActivtiy.this));
                OnlineMusicActivtiy.this.mFilename = "net_is_on";
            } else {
                OnlineMusicActivtiy.this.mFilename = "ask_internet";
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Online sizs = ");
            sb2.append(OnlineMusicActivtiy.mMusicDatas.size());
            OnlineMusicActivtiy.this.mOnlineDialog.setState(false);
            OnlineMusicActivtiy.this.mOnlineLoadHandler.post((Runnable) OnlineMusicActivtiy.this.mOnlineDialog);
        }
    }

    class OfflineDialog extends Thread {
        boolean flagShow;
        ProgressDialog pDialog;

        public OfflineDialog() {
            this.flagShow = false;
            (this.pDialog = new ProgressDialog((Context) OnlineMusicActivtiy.this, R.style.Theme_MovieMaker_AlertDialog)).setMessage((CharSequence) "Loading music...");
            this.pDialog.setCancelable(false);
        }

        @Override
        public void run() {
            super.run();
            if (this.flagShow) {
                this.pDialog.show();
                return;
            }
            this.pDialog.dismiss();
            this.setUpView();
        }

        public void setState(final boolean flagShow) {
            this.flagShow = flagShow;
        }

        public void setUpView() {
            this.pDialog.dismiss();
            if (OnlineMusicActivtiy.this.avail_offline.equals("avail_offline")) {
                OnlineMusicActivtiy.this.setUpListView();
            }
        }
    }

    public class OnlineDialog extends Thread {
        boolean flagShow;
        ProgressDialog pDialog;

        public OnlineDialog() {
            this.flagShow = false;
            if (this.pDialog == null) {
                (this.pDialog = new ProgressDialog((Context) OnlineMusicActivtiy.this, R.style.Theme_MovieMaker_AlertDialog)).setMessage((CharSequence) OnlineMusicActivtiy.this.getString(R.string.loading_music));
                this.pDialog.setCancelable(false);
            }
        }

        @Override
        public void run() {
            super.run();
            if (this.flagShow) {
                this.pDialog.show();
                return;
            }
            this.pDialog.dismiss();
            this.setUpView();
        }

        public void setState(final boolean flagShow) {
            this.flagShow = flagShow;
        }

        public void setUpView() {
            if (OnlineMusicActivtiy.this.mFilename.equals("net_is_on")) {
                OnlineMusicActivtiy.this.setUpListView();
                return;
            }
            if (!OnlineMusicActivtiy.this.avail_offline.equals("avail_offline") && OnlineMusicActivtiy.this.mFilename.equals("ask_internet")) {
                Toast.makeText(OnlineMusicActivtiy.this.getApplicationContext(), (CharSequence) "No Music found in device\nPlease turn on internet", Toast.LENGTH_LONG).show();
                return;
            }
            if (OnlineMusicActivtiy.this.mFilename.equals("ask_internet")) {
                Toast.makeText(OnlineMusicActivtiy.this.getApplicationContext(), (CharSequence) "Get more Music online!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
