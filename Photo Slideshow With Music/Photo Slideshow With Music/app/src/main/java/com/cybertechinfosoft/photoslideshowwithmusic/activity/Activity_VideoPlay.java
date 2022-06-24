package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.MyVideoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.org.codechimp.apprater.AppRater;
import java.io.File;
import java.util.Random;
import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdView;

public class Activity_VideoPlay extends AppCompatActivity implements MyVideoView.PlayPauseListner, OnClickListener, OnSeekBarChangeListener {
    Activity activity = Activity_VideoPlay.this;
    private int currentVideoDuration;
    private ImageView ivPlayPause;
    private Handler Handler;
    private boolean Complate;
    private File videoFile;
    private String StrVideoPath;
    private Runnable runnable;
    private SeekBar SbVideo;
    private Long CaptureTime;
    private Toolbar toolbar;
    private MyVideoView myVideoView;
    private TextView tvDuration;
    private TextView tvEndDuration;
    private ImageView imgFacebook;
    private ImageView imgInstagram;
    private ImageView imgShare;
    //    private ImageView imgTwitter;
    private ImageView imgWhatsApp;

    private NativeAd nativeAd;

    public Activity_VideoPlay() {
        this.Handler = new Handler();
        this.CaptureTime = Long.valueOf(0);
        this.runnable = new Runnable() {
            @Override
            public void run() {
                if (!Activity_VideoPlay.this.Complate) {
                    Activity_VideoPlay.this.currentVideoDuration = Activity_VideoPlay.this.myVideoView
                            .getCurrentPosition();
                    Activity_VideoPlay videoPlayActivity = Activity_VideoPlay.this;
                    videoPlayActivity.CaptureTime = Long
                            .valueOf(videoPlayActivity.CaptureTime.longValue() + 100);
                    Activity_VideoPlay.this.tvDuration.setText(FileUtils
                            .getDuration(Activity_VideoPlay.this.myVideoView
                                    .getCurrentPosition()));
                    Activity_VideoPlay.this.tvEndDuration.setText(FileUtils
                            .getDuration(Activity_VideoPlay.this.myVideoView
                                    .getDuration()));
                    Activity_VideoPlay.this.SbVideo
                            .setProgress(Activity_VideoPlay.this.currentVideoDuration);
                    Activity_VideoPlay.this.Handler.postDelayed(this, 100);
                }
            }
        };
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_creationvideo);
        this.toolbar = findViewById(R.id.toolbar);
        this.myVideoView = findViewById(R.id.MyvideoView);
        this.tvDuration = findViewById(R.id.tvStartDuration);
        this.tvEndDuration = findViewById(R.id.tvEndDuration);
        this.ivPlayPause = findViewById(R.id.ivPlayPause);
        this.SbVideo = findViewById(R.id.VideoSeekbar);
        setSupportActionBar(this.toolbar);
        this.StrVideoPath = getIntent().getStringExtra("android.intent.extra.TEXT");
        try {
            if (this.StrVideoPath != null) {
                this.videoFile = new File(this.StrVideoPath);
                this.myVideoView.setVideoPath(this.StrVideoPath);
            } else {
                Toast.makeText(getApplicationContext(), "Try Again !!!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        TextView mTitle = this.toolbar.findViewById(R.id.tvtittleToolbar);
        mTitle.setText(getString(R.string.sharemycreation));
        Utils.setFont(this, mTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        PutAnalyticsEvent();
        this.myVideoView.setOnPlayPauseListner(this);
        this.myVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(100);
                Activity_VideoPlay.this.SbVideo.setMax(mp.getDuration());
                Activity_VideoPlay.this.progressToTimer(mp.getDuration(),
                        mp.getDuration());
                Activity_VideoPlay.this.tvDuration.setText(FileUtils
                        .getDuration(mp.getCurrentPosition()));
                Activity_VideoPlay.this.tvEndDuration.setText(FileUtils
                        .getDuration(mp.getDuration()));
            }
        });
        findViewById(R.id.ClickVideo).setOnClickListener(this);
        this.myVideoView.setOnClickListener(this);
        this.ivPlayPause.setOnClickListener(this);
        this.SbVideo.setOnSeekBarChangeListener(this);
        this.myVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Activity_VideoPlay.this.Complate = true;
                Activity_VideoPlay.this.Handler
                        .removeCallbacks(Activity_VideoPlay.this.runnable);
                Activity_VideoPlay.this.tvDuration.setText(FileUtils
                        .getDuration(mp.getDuration()));
                Activity_VideoPlay.this.tvEndDuration.setText(FileUtils
                        .getDuration(mp.getDuration()));
            }
        });
        loadAd();
        this.imgWhatsApp = findViewById(R.id.imgFacebook);
        this.imgFacebook = findViewById(R.id.imgWhatsApp);
        this.imgInstagram = findViewById(R.id.imgInstagram);
        this.imgShare = findViewById(R.id.imgShare);
//        this.imgTwitter = findViewById(R.id.imgTwitter);
        this.imgFacebook.setOnClickListener(this);
        this.imgInstagram.setOnClickListener(this);
        this.imgWhatsApp.setOnClickListener(this);
//        this.imgTwitter.setOnClickListener(this);
        this.imgShare.setOnClickListener(this);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity_VideoPlay");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    protected void onPause() {
        super.onPause();
        this.myVideoView.pause();
    }

    public View onCreateView(View view, String str, Context context,
                             AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public View onCreateView(String str, Context context,
                             AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    public void onVideoPause() {
        if (!(this.Handler == null || this.runnable == null)) {
            this.Handler.removeCallbacks(this.runnable);
        }
        Animation animation = new AlphaAnimation(0.0f,
                1.0f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        this.ivPlayPause.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Activity_VideoPlay.this.ivPlayPause.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
    }

    public void onVideoPlay() {
        updateProgressBar();
        Animation animation = new AlphaAnimation(
                1.0f, 0.0f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        this.ivPlayPause.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Activity_VideoPlay.this.ivPlayPause.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Activity_VideoPlay.this.ivPlayPause.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void updateProgressBar() {
        try {
            this.Handler.removeCallbacks(this.runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.Handler.postDelayed(this.runnable, 100);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.delete_video, menu);
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.MyvideoView:
            case R.id.ClickVideo:
            case R.id.ivPlayPause:
                if (this.myVideoView.isPlaying()) {
                    this.myVideoView.pause();
                    return;
                }
                this.myVideoView.start();
                this.Complate = false;
                break;
            case R.id.imgFacebook:
                try {
                    shareImageWhatsApp("com.facebook.katana", "Facebook");
                } catch (Exception e) {
                }
                return;
            case R.id.imgInstagram:
                try {
                    shareImageWhatsApp("com.instagram.android", "Instagram");
                } catch (Exception e) {
                }
                return;
            case R.id.imgShare:
                Uri uri;
                try {
                    if (this.StrVideoPath != null) {
                        if (Build.VERSION.SDK_INT <= 19) {
                            uri = Uri.fromFile(new File(StrVideoPath));
                        } else {
                            final StringBuilder sb = new StringBuilder();
                            sb.append(this.getPackageName());
                            sb.append(".provider");
                            File f = new File(StrVideoPath);
                            uri = FileProvider.getUriForFile(this, sb.toString(), f);
                        }
                        final Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("video/*");
                        intent.putExtra("android.intent.extra.STREAM", uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        this.startActivity(Intent.createChooser(intent, "SHARE"));
                    } else {
                        Toast.makeText(getApplicationContext(), "Try Again !!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case R.id.imgWhatsApp:
                try {
                    shareImageWhatsApp("com.whatsapp", "Whatsapp");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            default:
        }
    }

    private boolean a(final String s, final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ex) {
            return false;
        }
    }

    public void shareImageWhatsApp(final String package1, final String s) {
        Uri uri;
        try {
            if (this.StrVideoPath != null) {
                if (Build.VERSION.SDK_INT <= 19) {
                    uri = Uri.fromFile(new File(StrVideoPath));
                } else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(this.getPackageName());
                    sb.append(".provider");
                    File f = new File(StrVideoPath);
                    uri = FileProvider.getUriForFile(this, sb.toString(), f);
                }
                final Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("video/mp4");
                intent.putExtra("android.intent.extra.STREAM", uri);
                if (this.a(package1, this.getApplicationContext())) {
                    intent.setPackage(package1);
                    this.startActivity(Intent.createChooser(intent, "SHARE"));
                    return;
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Please Install ");
                sb2.append(s);
                Toast.makeText(this, sb2.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Try Again !!!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.Handler.removeCallbacks(this.runnable);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.Handler.removeCallbacks(this.runnable);
        this.currentVideoDuration = progressToTimer(seekBar.getProgress(),
                this.myVideoView.getDuration());
        this.myVideoView.seekTo(seekBar.getProgress());
        if (this.myVideoView.isPlaying()) {
            updateProgressBar();
        }
    }

    public int progressToTimer(int progress, int totalDuration) {
        return ((int) ((((double) progress) / 100.0d) * ((double) (totalDuration / 1000))))
                * 1000;
    }

    protected void onDestroy() {
        this.myVideoView.stopPlayback();
        this.Handler.removeCallbacks(this.runnable);
        super.onDestroy();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (MyApplication.isShowAd == 1) {
                    Intent intent = new Intent(this, VideoAlbumActivity.class);
                    intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
                    startActivity(intent);
                    finish();
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.AdsId = 103;
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;
                    } else {
                        Intent intent = new Intent(this, VideoAlbumActivity.class);
                        intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
            case R.id.action_delete:
                if (this.myVideoView.isPlaying()) {
                    this.myVideoView.pause();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_VideoPlay.this,
                        R.style.Theme_MovieMaker_AlertDialog);
                builder.setTitle("Delete Video !");
                builder.setMessage("Are you sure to delete?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (StrVideoPath != null) {
                                FileUtils.deleteFile(new File(
                                        StrVideoPath));
                                startActivity(new Intent(Activity_VideoPlay.this, VideoAlbumActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Video Not Found !!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (MyApplication.isShowAd == 1) {
            GoBack();
            MyApplication.isShowAd = 0;
        } else {
            if (MyApplication.mInterstitialAd != null) {
                MyApplication.activity = activity;
                MyApplication.AdsId = 102;
                MyApplication.mInterstitialAd.show(activity);
                MyApplication.isShowAd = 1;
            } else {
                GoBack();
            }
        }
    }

    private void GoBack() {
        Intent intent;
        if (getIntent().getExtras().get("KEY").equals("FromVideoAlbum")) {
            if (new Random().nextInt(100) > 50) {
                AppRater.showRateDialog(this);
                AppRater.setCallback(new AppRater.Callback() {
                    @Override
                    public void onCancelClicked() {
                        final Intent intent = new Intent(Activity_VideoPlay.this, VideoAlbumActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXTRA_FROM_VIDEO", true);
                        Activity_VideoPlay.this.startActivity(intent);
                        Activity_VideoPlay.this.finish();
                    }

                    @Override
                    public void onNoClicked() {
                    }

                    @Override
                    public void onYesClicked() {
                    }
                });
                return;
            }
            intent = new Intent(this, VideoAlbumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
            startActivity(intent);
            finish();
        } else if (getIntent().getExtras().get("KEY").equals("FromProgress")) {
            intent = new Intent(this, VideoAlbumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
            intent.putExtra("KEY", VideoAlbumActivity.EXTRA_FROM_VIDEO);
            startActivity(intent);
            finish();
        } else if (getIntent().getExtras().get("KEY").equals("FromNotify")) {
            intent = new Intent(this, LauncherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void loadAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.NativeAdvanceAd_id));
        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        boolean isDestroyed = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = isDestroyed();
                        }
                        if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                            nativeAd.destroy();
                            return;
                        }
                        if (Activity_VideoPlay.this.nativeAd != null) {
                            Activity_VideoPlay.this.nativeAd.destroy();
                        }
                        Activity_VideoPlay.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_small, null);
                        populateUnifiedNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());


    }
}
