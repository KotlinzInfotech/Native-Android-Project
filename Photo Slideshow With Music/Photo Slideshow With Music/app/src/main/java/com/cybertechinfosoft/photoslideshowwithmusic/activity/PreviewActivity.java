package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.FrameAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.MoviewThemeAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.data.MusicData;
import com.cybertechinfosoft.photoslideshowwithmusic.service.CreateVideoService;
import com.cybertechinfosoft.photoslideshowwithmusic.service.ImageCreatorService;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.ScaleCardLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.movienaker.movie.themes.THEMES;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    Activity activity = PreviewActivity.this;
    public static PreviewActivity mActivity;
    private final int REQUEST_PICK_EDIT;
    private final int REQUEST_PICK_IMAGES;
    private final int REQUEST_PICK_SYS_AUDIO;
    private MyApplication application;
    private ArrayList<ImageData> arrayList;
    private BottomSheetBehavior<View> behavior;
    ScaleCardLayout cardLayout;
    private CardView cardViewFrames;
    private Float[] duration;
    EPreferences ePref;
    private View flLoader;
    int frame;
    private FrameAdapter frameAdapter;
    private RequestManager glide;
    public static Handler handler;
    int i;
    private ImageView imgAddImage;
    private ImageView imgAddMusic;
    private ImageView imgEditImage;
    private ImageView imgSetFrame;
    private ImageView imgSetTime;
    LayoutInflater inflater;
    boolean isFromTouch;
    private ImageView ivFrame;
    private View ivPlayPause;
    private ImageView ivPreview;
    ArrayList<ImageData> lastData;
    public static LockRunnable lockRunnable;
    private MediaPlayer mPlayer;
    String path;
    int posForAddMusicDialog;
    private RecyclerView rvDuration;
    private RecyclerView rvFrame;
    private RecyclerView rvThemes;
    private float seconds;
    private SeekBar seekBar;
    String strVideoName;
    private MoviewThemeAdapter themeAdapter;
    private Toolbar toolbar;
    private TextView tvEndTime;
    private TextView tvTime;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;


    public PreviewActivity() {
        this.handler = new Handler();
        this.lockRunnable = new LockRunnable();
        this.seconds = 2.0f;
        this.posForAddMusicDialog = 0;
        this.duration = new Float[]{1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f};
        this.i = 0;
        this.isFromTouch = false;
        this.REQUEST_PICK_SYS_AUDIO = 104;
        this.REQUEST_PICK_IMAGES = 102;
        this.REQUEST_PICK_EDIT = 103;
        this.lastData = new ArrayList<ImageData>();
    }

    private void ShowLowSpaceAlert() {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        alertDialog$Builder.setTitle(this.getString(R.string.low_storage_alert));
        alertDialog$Builder.setMessage(this.getString(R.string.low_storage_alert_disc));
        alertDialog$Builder.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                PreviewActivity.this.startActivityForResult(new Intent("android.settings.SETTINGS"), 0);
            }
        });
        alertDialog$Builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
            }
        });
        alertDialog$Builder.show();
    }


    private void addListner() {
        this.findViewById(R.id.video_clicker).setOnClickListener(this);
        this.seekBar.setOnSeekBarChangeListener(this);
        this.seekBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                return true;
            }
        });
        this.imgAddImage.setOnClickListener(this);
        this.imgEditImage.setOnClickListener(this);
        this.imgAddMusic.setOnClickListener(this);
        this.imgSetTime.setOnClickListener(this);
        this.imgSetFrame.setOnClickListener(this);
        this.imgSetFrame.setOnClickListener(this);
    }

    private void bindView() {
        this.cardLayout = findViewById(R.id.scaleCard);
        this.flLoader = findViewById(R.id.flLoader);
        this.ivPreview = findViewById(R.id.previewImageView1);
        this.ivFrame = findViewById(R.id.ivFrame);
        this.seekBar = findViewById(R.id.sbPlayTime);
        this.tvEndTime = findViewById(R.id.tvEndTime);
        this.tvTime = findViewById(R.id.tvTime);
        this.ivPlayPause = findViewById(R.id.ivPlayPause);
        this.toolbar = findViewById(R.id.toolbar);
        this.rvThemes = findViewById(R.id.rvThemes);
        this.rvDuration = findViewById(R.id.rvDuration);
        this.rvFrame = findViewById(R.id.rvFrame);
        this.cardViewFrames = findViewById(R.id.cardViewFrame);
        this.imgAddImage = findViewById(R.id.imgEditAddImage);
        this.imgEditImage = findViewById(R.id.imgEditImage);
        this.imgAddMusic = findViewById(R.id.imgEditAddMusic);
        this.imgSetTime = findViewById(R.id.imgEditSetTime);
        this.imgSetFrame = findViewById(R.id.imgSetFrame);
        this.imgSetFrame.setSelected(true);
        this.imgSetFrame.setVisibility(View.GONE);
        this.cardViewFrames.setVisibility(View.VISIBLE);
    }

    private void displayImage() {
        try {
            this.glide = Glide.with(this);
            if (this.i >= this.seekBar.getMax()) {
                this.i = 0;
                this.lockRunnable.stop();
                this.tvTime.setText(String.format("%02d:%02d", 0, 0));
                return;
            }
            if (this.i > 0 && this.flLoader.getVisibility() == View.VISIBLE) {
                this.flLoader.setVisibility(View.GONE);
                if (this.mPlayer != null && !this.mPlayer.isPlaying()) {
                    this.mPlayer.start();
                }
            }
            this.seekBar.setSecondaryProgress(this.application.videoImages.size());
            if (this.seekBar.getProgress() >= this.seekBar.getSecondaryProgress()) {
                return;
            }
            this.i %= this.application.videoImages.size();
            this.glide.load(this.application.videoImages.get(this.i)).asBitmap().signature(new MediaStoreSignature("image/*", System.currentTimeMillis(), 0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<Bitmap>() {
                public void onResourceReady(final Bitmap imageBitmap, final GlideAnimation glideAnimation) {
                    PreviewActivity.this.ivPreview.setImageBitmap(imageBitmap);
                }
            });
            ++this.i;
            if (!this.isFromTouch) {
                this.seekBar.setProgress(this.i);
            }
            final int n = (int) (this.i / 30.0f * this.seconds);
            this.tvTime.setText(String.format("%02d:%02d", n / 60, n % 60));
            final int n2 = (int) ((this.arrayList.size() - 1) * this.seconds);
            this.tvEndTime.setText(String.format("%02d:%02d", n2 / 60, n2 % 60));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static PreviewActivity getmActivity() {
        return PreviewActivity.mActivity;
    }

    private static long getAvailableInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return (((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) / 1048576L;
    }


    private void init() {
        this.setSupportActionBar(this.toolbar);
        final TextView textView = this.toolbar.findViewById(R.id.toolbar_title);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText(this.getString(R.string.preview));
        Utils.setFont(this, textView);
        this.ePref = EPreferences.getInstance(this);
        PreviewActivity.mActivity = this;
        this.seconds = this.application.getSecond();
        this.inflater = LayoutInflater.from(this);
        this.glide = Glide.with(this);
        this.application = MyApplication.getInstance();
        this.arrayList = this.application.getSelectedImages();
        this.seekBar.setMax((this.arrayList.size() - 1) * 30);
        final int n = (int) ((this.arrayList.size() - 1) * this.seconds);
        this.tvEndTime.setText(String.format("%02d:%02d", n / 60, n % 60));
        this.setUpThemeAdapter();
        if (this.application.getSelectedImages().size() == 0) {
            final Intent intent = new Intent(this, LauncherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
        if (this.application.getSelectedImages().size() > 0) {
            this.glide.load(this.application.getSelectedImages().get(0).imagePath).into(this.ivPreview);
        }
        final View viewById = this.findViewById(R.id.bottom_sheet);
        Utils.setFont(this, R.id.tvBottomSheetTitle);
        (this.behavior = BottomSheetBehavior.from(viewById)).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            public void onSlide(@NonNull final View view, final float n) {

            }

            public void onStateChanged(@NonNull final View view, final int n) {
                if (n == 3 && !PreviewActivity.this.lockRunnable.isPause()) {
                    PreviewActivity.this.lockRunnable.pause();
                }
            }
        });
        this.setTheme();
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PreviewActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private boolean isNeedRestart() {
        if (this.lastData.size() > this.application.getSelectedImages().size()) {
            return MyApplication.isBreak = true;
        }
        for (int i = 0; i < this.lastData.size(); ++i) {
            if (!this.lastData.get(i).imagePath.equals(this.application.getSelectedImages().get(i).imagePath)) {
                return MyApplication.isBreak = true;
            }
        }
        return false;
    }

    private void loadProgressActivity() {
        final Intent intent = new Intent(this, CreateVideoService.class);
        if (!this.strVideoName.endsWith(".mp4")) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.strVideoName);
            sb.append(".mp4");
            this.strVideoName = sb.toString();
        }
        application.isFromSdCardAudio = false;
        intent.putExtra("VideoName", this.strVideoName);
        startService(intent);

        final Intent intent2 = new Intent(PreviewActivity.this, ProgressActivity.class);
        intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent2);
        finish();
    }


    private void onBackDialog() {
        new AlertDialog.Builder(this, R.style.Theme_MovieMaker_AlertDialog).setTitle(R.string.app_name).setMessage(R.string.are_you_sure_your_video_is_not_prepared_yet_).setPositiveButton(R.string.yes_cap, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PreviewActivity.this.application.videoImages.clear();
                MyApplication.isBreak = true;
                ((NotificationManager) PreviewActivity.this.getSystemService(NOTIFICATION_SERVICE)).cancel(1001);
                finish();
            }
        }).setNegativeButton(R.string.stay_here, null).create().show();
    }


    private void pauseMusic() {
        if (this.mPlayer != null && this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
        }
    }

    private void playMusic() {
        if (this.flLoader.getVisibility() != View.VISIBLE && this.mPlayer != null && !this.mPlayer.isPlaying()) {
            this.mPlayer.start();
        }
    }

    private void reinitMusic() {
        final MusicData musicData = this.application.getMusicData();
        if (musicData != null) {
            final File file = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
            final Uri fromFile = Uri.fromFile(file);
            musicData.track_data = file.getAbsolutePath();
            this.application.setMusicData(musicData);
            this.mPlayer = MediaPlayer.create(this, fromFile);
            if (this.mPlayer == null) {
                return;
            }
            try {
                this.mPlayer.setLooping(true);
                this.mPlayer.prepare();
                return;
            } catch (IllegalStateException | IOException ex) {
                ex.printStackTrace();
                return;
            }
        }
        final MusicData musicData2 = new MusicData();
        final File file2 = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
        final Uri fromFile2 = Uri.fromFile(file2);
        musicData2.track_data = file2.getAbsolutePath();
        this.application.setMusicData(musicData2);
        this.mPlayer = MediaPlayer.create(this, fromFile2);
        if (this.mPlayer != null) {
            try {
                this.mPlayer.setLooping(true);
                this.mPlayer.prepare();
            } catch (IllegalStateException | IOException ex2) {
                final Throwable t2;
            }
        }
    }


    private void seekMediaPlayer() {
        if (this.mPlayer != null) {
            try {
                this.mPlayer.seekTo((int) (this.i / 30.0f * this.seconds * 1000.0f) % this.mPlayer.getDuration());
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setFocus(final ImageView imageView) {
        this.imgAddImage.setSelected(false);
        this.imgEditImage.setSelected(false);
        this.imgAddMusic.setSelected(false);
        this.imgSetTime.setSelected(false);
        this.imgSetFrame.setSelected(false);
        imageView.setSelected(true);
        this.cardViewFrames.setVisibility(View.VISIBLE);
    }

    private void setUpThemeAdapter() {
        this.themeAdapter = new MoviewThemeAdapter(this);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false);
        final GridLayoutManager layoutManager2 = new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false);
        this.rvThemes.setLayoutManager(layoutManager);
        this.rvThemes.setItemAnimator(new DefaultItemAnimator());
        this.rvThemes.setAdapter(this.themeAdapter);
        this.frameAdapter = new FrameAdapter(this);
        this.rvFrame.setLayoutManager(layoutManager2);
        this.rvFrame.setItemAnimator(new DefaultItemAnimator());
        this.rvFrame.setAdapter(this.frameAdapter);
        this.rvDuration.setHasFixedSize(true);
        this.rvDuration.setLayoutManager(new LinearLayoutManager(this));
        this.rvDuration.setAdapter(new DurationAdapter());
    }


    private void showSaveDialog() {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder((Context) this, R.style.Theme_MovieMaker_AlertDialog);
        final EditText view = new EditText((Context) this);
        view.setHint(R.string.enter_story_name);
        view.setTextColor(-1);
        alertDialog$Builder.setTitle(R.string.enter_video_name);
        alertDialog$Builder.setView((View) view);
        alertDialog$Builder.setPositiveButton(R.string.save, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                final StringBuilder sb = new StringBuilder();
                sb.append((CharSequence) view.getText());
                strVideoName = sb.toString();
                if (PreviewActivity.this.strVideoName.equals("")) {
                    Toast.makeText((Context) PreviewActivity.this, R.string.please_enter_story_name_, Toast.LENGTH_SHORT).show();
                    view.setFocusable(true);
                    view.setSelected(true);
                    return;
                }
                final File file = new File(FileUtils.APP_DIRECTORY.getAbsolutePath());
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(String.valueOf(PreviewActivity.this.strVideoName));
                sb2.append(".mp4");
                if (new File(file, sb2.toString()).exists()) {
                    Toast.makeText((Context) PreviewActivity.this, R.string.video_name_already_exist_, Toast.LENGTH_SHORT).show();
                    view.setFocusable(true);
                    view.setSelected(true);
                    return;
                }
                PreviewActivity.this.handler.removeCallbacks((Runnable) PreviewActivity.this.lockRunnable);
                ((Dialog) dialogInterface).getWindow().setSoftInputMode(2);
                PreviewActivity.this.loadProgressActivity();
                dialogInterface.dismiss();

            }
        });
        alertDialog$Builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog create = alertDialog$Builder.create();
        ((Dialog) create).getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimationForVideoTitleDailog;
        final long currentTimeMillis = System.currentTimeMillis();
        final Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        final StringBuilder sb = new StringBuilder();
        sb.append("PhotoSlideshow_");
        sb.append(new SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(instance.getTime()));
        sb.append(".mp4");
        view.setText((CharSequence) sb.toString());
        view.selectAll();
        view.requestFocus();
        ((Dialog) create).getWindow().setSoftInputMode(4);
        ((Dialog) create).show();
    }


    public int getFrame() {
        return this.application.getFrame();
    }

    protected void onActivityResult(int n, final int n2, Intent intent) {
        super.onActivityResult(n, n2, intent);
        this.application.isEditModeEnable = false;
        if (n2 == -1) {
            switch (n) {
                default: {
                }
                case 104: {
                    this.lockRunnable.stop();
                    if (ImageCreatorService.isImageComplate || !MyApplication.isMyServiceRunning(this.application, ImageCreatorService.class)) {
                        MyApplication.isBreak = false;
                        this.application.videoImages.clear();
                        this.application.min_pos = Integer.MAX_VALUE;
                        intent = new Intent(this.getApplicationContext(), ImageCreatorService.class);
                        intent.putExtra("selected_theme", this.application.getCurrentTheme());
                        this.startService(intent);
                    }
                    this.application.isFristTimeTheme = false;
                    this.application.isFromSdCardAudio = true;
                    this.application.isSelectSYS = true;
                    this.i = 0;
                    this.reinitMusic();
                }
                break;
                case 103: {
                    this.lockRunnable.stop();
                    if (ImageCreatorService.isImageComplate || !MyApplication.isMyServiceRunning(this.application, ImageCreatorService.class)) {
                        MyApplication.isBreak = false;
                        this.application.videoImages.clear();
                        this.application.min_pos = Integer.MAX_VALUE;
                        intent = new Intent(this.getApplicationContext(), ImageCreatorService.class);
                        intent.putExtra("selected_theme", this.application.getCurrentTheme());
                        this.startService(intent);
                    }
                    this.i = 0;
                    this.seekBar.setProgress(this.i);
                    this.arrayList = this.application.getSelectedImages();
                    n = (int) ((this.arrayList.size() - 1) * this.seconds);
                    this.seekBar.setMax((this.application.getSelectedImages().size() - 1) * 30);
                    this.tvEndTime.setText(String.format("%02d:%02d", n / 60, n % 60));
                }
                break;
                case 102: {
                    if (this.isNeedRestart()) {
                        this.stopService(new Intent(this.getApplicationContext(), ImageCreatorService.class));
                        this.lockRunnable.stop();
                        this.seekBar.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyApplication.isBreak = false;
                                PreviewActivity.this.application.videoImages.clear();
                                PreviewActivity.this.application.min_pos = Integer.MAX_VALUE;
                                final Intent intent = new Intent(PreviewActivity.this.getApplicationContext(), ImageCreatorService.class);
                                intent.putExtra("selected_theme", PreviewActivity.this.application.getCurrentTheme());
                                PreviewActivity.this.startService(intent);
                            }
                        }, 1000L);
                        n = (int) ((this.arrayList.size() - 1) * this.seconds);
                        this.arrayList = this.application.getSelectedImages();
                        this.seekBar.setMax((this.application.getSelectedImages().size() - 1) * 30);
                        this.tvEndTime.setText(String.format("%02d:%02d", n / 60, n % 60));
                        return;
                    }
                    if (ImageCreatorService.isImageComplate) {
                        MyApplication.isBreak = false;
                        this.application.videoImages.clear();
                        this.application.min_pos = Integer.MAX_VALUE;
                        intent = new Intent(this.getApplicationContext(), ImageCreatorService.class);
                        intent.putExtra("selected_theme", this.application.getCurrentTheme());
                        this.startService(intent);
                        this.i = 0;
                        this.seekBar.setProgress(0);
                    }
                    n = (int) ((this.arrayList.size() - 1) * this.seconds);
                    this.arrayList = this.application.getSelectedImages();
                    this.seekBar.setMax((this.application.getSelectedImages().size() - 1) * 30);
                    this.tvEndTime.setText(String.format("%02d:%02d", n / 60, n % 60));
                    break;
                }
            }
        }
    }

    public void onBackPressed() {
        if (MyApplication.isShowAd == 1) {
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {
                onBackDialog();
            }
            MyApplication.isShowAd = 0;
        } else {
            if (MyApplication.mInterstitialAd != null) {
                MyApplication.activity = activity;
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                MyApplication.AdsId = 113;
                MyApplication.mInterstitialAd.show(activity);
                MyApplication.isShowAd = 1;

            } else {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    onBackDialog();
                }
            }
        }
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            default: {
            }
            case R.id.video_clicker: {
                if (this.lockRunnable.isPause()) {
                    this.lockRunnable.play();
                    return;
                }
                this.lockRunnable.pause();
            }
            break;
            case R.id.imgSetFrame: {
                if (this.cardViewFrames.getVisibility() == View.VISIBLE) {
                    this.cardViewFrames.setVisibility(View.INVISIBLE);
                    this.imgSetFrame.setSelected(false);
                    return;
                }
                this.setFocus(this.imgSetFrame);
                this.cardViewFrames.setVisibility(View.VISIBLE);
            }
            case R.id.imgEditSetTime: {
                this.behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            break;
            case R.id.imgEditImage: {
                this.flLoader.setVisibility(View.GONE);
                this.application.isEditModeEnable = true;
                MyApplication.isBreak = true;
                this.lockRunnable.pause();
                this.startActivityForResult(new Intent(this, ImageEditActivity.class).putExtra("extra_from_preview", true).putExtra("KEY", "FromPreview"), 103);
            }
            break;
            case R.id.imgEditAddMusic: {
                this.flLoader.setVisibility(View.GONE);
                this.lockRunnable.pause();
                MyApplication.isBreak = true;
                this.startActivityForResult(new Intent(this, SongEditActivity.class), 104);
            }
            break;
            case R.id.imgEditAddImage: {
                this.flLoader.setVisibility(View.GONE);
                MyApplication.isBreak = true;
                this.application.isEditModeEnable = true;
                this.lastData.clear();
                this.lastData.addAll(this.arrayList);
                this.lockRunnable.pause();
                final Intent intent = new Intent(this, ImageSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("extra_from_preview", true);
                this.startActivityForResult(intent, 102);
            }
            break;
        }
    }

    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        this.application = MyApplication.getInstance();
        this.application.videoImages.clear();
        MyApplication.isBreak = false;
        final Intent intent = new Intent(this.getApplicationContext(), ImageCreatorService.class);
        intent.putExtra("selected_theme", this.application.getCurrentTheme());
        this.startService(intent);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_preview);
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PreviewActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        BannerAds();
        this.getWindow().addFlags(128);
        if (Utils.checkPermission(this)) {
            this.bindView();
            this.init();
            this.addListner();
            this.behavior.setHideable(true);
            this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        PutAnalyticsEvent();
        final Intent intent2 = new Intent(this, LauncherActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
    }

    private void BannerAds() {
        try {
            adContainerView = findViewById(R.id.banner_ad_view_container);
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            float f = displayMetrics.density;
            float width = (float) adContainerView.getWidth();
            if (width == 0.0f) {
                width = (float) displayMetrics.widthPixels;
            }
            adSize = AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, (int) (width / f));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adContainerView.getLayoutParams();
            layoutParams.height = adSize.getHeightInPixels(this);
            adContainerView.setLayoutParams(layoutParams);
            adContainerView.post(new Runnable() {
                public final void run() {
                    ShowAds();
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void ShowAds() {
        try {
            adView = new AdView(activity);
            adView.setAdUnitId(getString(R.string.Banner_ad_id));
            adContainerView.removeAllViews();
            adContainerView.addView(adView);
            adView.setAdSize(adSize);
            adView.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_selection, menu);
        menu.findItem(R.id.menu_done).setTitle((CharSequence) "NEXT");
        menu.removeItem(R.id.menu_clear);
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
                Long l = 100L;
                int i = l.intValue();
                if (MyApplication.isShowAd == 1) {
                    if (getAvailableInternalMemorySize() <= 100L) {
                        ShowLowSpaceAlert();
                    } else {
                        lockRunnable.pause();
                        showSaveDialog();
                    }
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.AdsId = 118;
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {
                        if (getAvailableInternalMemorySize() <= 100L) {
                            ShowLowSpaceAlert();
                        } else {
                            lockRunnable.pause();
                            showSaveDialog();
                        }
                    }
                }
            }
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected void onPause() {
        super.onPause();
        this.lockRunnable.pause();
    }

    public void onProgressChanged(final SeekBar seekBar, final int i, final boolean b) {
        this.i = i;
        if (this.isFromTouch) {
            seekBar.setProgress(Math.min(i, seekBar.getSecondaryProgress()));
            this.displayImage();
            this.seekMediaPlayer();
        }
    }

    protected void onResume() {
        super.onResume();
        if (SongEditActivity.isFromSdcard) {
            this.application.isFromSdCardAudio = true;
            this.i = 0;
            this.reinitMusic();
            this.lockRunnable.play();
            SongEditActivity.isFromSdcard = false;
        }
    }

    public void onStartTrackingTouch(final SeekBar seekBar) {
        this.isFromTouch = true;
    }

    public void onStopTrackingTouch(final SeekBar seekBar) {
        this.isFromTouch = false;
    }

    public void reset() {
        MyApplication.isBreak = false;
        this.application.videoImages.clear();
        this.handler.removeCallbacks(this.lockRunnable);
        this.lockRunnable.stop();
        Glide.get(this).clearMemory();
        new Thread() {
            @Override
            public void run() {
                Glide.get(PreviewActivity.this).clearDiskCache();
            }
        }.start();
        FileUtils.deleteTempDir();
        this.glide = Glide.with(this);
        this.flLoader.setVisibility(View.VISIBLE);
        this.setTheme();
    }

    public void setFrame(final int n) {
        this.frame = n;
        if (n == -1) {
            this.ivFrame.setImageDrawable(null);
        } else {
            this.ivFrame.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), n), MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, false));
        }
        this.application.setFrame(n);
    }

    public void setTheme() {
        if (!this.application.isFromSdCardAudio) {
            new Thread() {
                @Override
                public void run() {
                    final THEMES selectedTheme = PreviewActivity.this.application.selectedTheme;
                    if (PreviewActivity.this.application.isFristTimeTheme) {
                        try {
                            FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
                            final File file = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
                            if (file.exists()) {
                                FileUtils.deleteFile(file);
                            }
                            file.createNewFile();
                            final InputStream openRawResource = PreviewActivity.this.getResources().openRawResource(selectedTheme.getThemeMusic());
                            final FileOutputStream fileOutputStream = new FileOutputStream(file);
                            final byte[] array = new byte[1024];
                            while (true) {
                                final int read = openRawResource.read(array);
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
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                public void onPrepared(final MediaPlayer mediaPlayer) {
                                    musicData.track_duration = mediaPlayer.getDuration();
                                    mediaPlayer.stop();
                                }
                            });
                            musicData.track_Title = "temp";
                            PreviewActivity.this.application.setMusicData(musicData);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        try {
                            final MusicData musicData2 = PreviewActivity.this.application.getMusicData();
                            final MediaPlayer mediaPlayer2 = new MediaPlayer();
                            mediaPlayer2.setDataSource(musicData2.getTrack_data());
                            mediaPlayer2.setAudioStreamType(3);
                            mediaPlayer2.prepare();
                            musicData2.track_data = musicData2.getTrack_data();
                            mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                public void onPrepared(final MediaPlayer mediaPlayer) {
                                    musicData2.track_duration = mediaPlayer.getDuration();
                                    mediaPlayer.stop();
                                }
                            });
                            musicData2.track_Title = "temp";
                            PreviewActivity.this.application.setMusicData(musicData2);
                        } catch (Exception ex2) {
                        }
                    }
                    PreviewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PreviewActivity.this.reinitMusic();
                            PreviewActivity.this.lockRunnable.play();
                        }
                    });
                }
            }.start();
            return;
        }
        try {
            final MusicData musicData = this.application.getMusicData();
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(musicData.getTrack_data());
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.prepare();
            musicData.track_data = musicData.getTrack_data();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    musicData.track_duration = mediaPlayer.getDuration();
                    mediaPlayer.stop();
                }
            });
            musicData.track_Title = "temp";
            this.application.setMusicData(musicData);
        } catch (Exception ex) {
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PreviewActivity.this.reinitMusic();
                PreviewActivity.this.lockRunnable.play();
            }
        });
    }

    private class DurationAdapter extends RecyclerView.Adapter<DurationAdapter.ViewHolder> {
        public int getItemCount() {
            return PreviewActivity.this.duration.length;
        }

        public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
            final float floatValue = PreviewActivity.this.duration[n];
            final CheckedTextView checkedTextView = viewHolder.checkedTextView;
            boolean checked = true;
            checkedTextView.setText(String.format("%.1f Second", floatValue));
            if (floatValue != PreviewActivity.this.seconds) {
                checked = false;
            }
            Utils.setFont(activity, viewHolder.checkedTextView);
            checkedTextView.setChecked(checked);
            viewHolder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    PreviewActivity.this.seconds = floatValue;
                    PreviewActivity.this.application.setSecond(PreviewActivity.this.seconds);
                    DurationAdapter.this.notifyDataSetChanged();
                    PreviewActivity.this.lockRunnable.play();
                }
            });
        }

        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int n) {
            return new ViewHolder(PreviewActivity.this.inflater.inflate(R.layout.duration_list_item, viewGroup, false));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckedTextView checkedTextView;

            public ViewHolder(final View view) {
                super(view);
                this.checkedTextView = view.findViewById(R.id.text1);
            }
        }
    }

    public class LockRunnable implements Runnable {
        ArrayList<ImageData> appList;
        boolean isPause;

        public LockRunnable() {
            this.isPause = false;
            this.appList = new ArrayList<ImageData>();
        }

        public boolean isPause() {
            return this.isPause;
        }

        public void pause() {
            this.isPause = true;
            PreviewActivity.this.pauseMusic();
            final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(500L);
            alphaAnimation.setFillAfter(true);
            PreviewActivity.this.ivPlayPause.startAnimation(alphaAnimation);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(final Animation animation) {
                }

                public void onAnimationRepeat(final Animation animation) {
                }

                public void onAnimationStart(final Animation animation) {
                    PreviewActivity.this.ivPlayPause.setVisibility(View.VISIBLE);
                }
            });
        }

        public void play() {
            this.isPause = false;
            PreviewActivity.this.playMusic();
            PreviewActivity.this.handler.postDelayed(PreviewActivity.this.lockRunnable, (long) Math.round(PreviewActivity.this.seconds * 50.0f));
            final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(500L);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(final Animation animation) {
                    PreviewActivity.this.ivPlayPause.setVisibility(View.INVISIBLE);
                }

                public void onAnimationRepeat(final Animation animation) {
                }

                public void onAnimationStart(final Animation animation) {
                    PreviewActivity.this.ivPlayPause.setVisibility(View.VISIBLE);
                    PreviewActivity.this.tvTime.setText(String.format("%02d:%02d", 0, 0));
                }
            });
            PreviewActivity.this.ivPlayPause.startAnimation(alphaAnimation);
            if (PreviewActivity.this.behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                PreviewActivity.this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }

        @Override
        public void run() {
            PreviewActivity.this.displayImage();
            if (!this.isPause) {
                PreviewActivity.this.handler.postDelayed(PreviewActivity.this.lockRunnable, (long) Math.round(PreviewActivity.this.seconds * 50.0f));
            }
        }

        void setAppList(final ArrayList<ImageData> list) {
            this.appList.clear();
            this.appList.addAll(list);
        }

        public void stop() {
            this.pause();
            PreviewActivity.this.i = 0;
            if (PreviewActivity.this.mPlayer != null) {
                PreviewActivity.this.mPlayer.stop();
                PreviewActivity.this.mPlayer.release();
            }
            PreviewActivity.this.reinitMusic();
            PreviewActivity.this.seekBar.setProgress(0);
        }
    }

}
