package com.cybertechinfosoft.photoslideshowwithmusic.activity;


import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.OnProgressReceiver;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.service.CreateVideoService;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable;
import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdViewbig;

public class ProgressActivity extends AppCompatActivity implements OnProgressReceiver {
    private MyApplication application;
    final float[] from;
    final float[] hsv;
    boolean isComplate;
    float lastProg;
    final float[] to;
    private TextView tvProgress;

    ProgressBar mProgressBar;

    private NativeAd nativeAd;

    public ProgressActivity() {
        this.from = new float[3];
        this.to = new float[3];
        this.hsv = new float[3];
        this.lastProg = 0.0f;
        this.isComplate = true;
    }


    private void bindView() {
        this.tvProgress = (TextView) this.findViewById(R.id.tvProgress);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ProgressActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void changePercentageOnText(final float lastProg) {
        synchronized (this) {
            if (this.isComplate) {
                final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.lastProg, lastProg});
                ofFloat.setDuration(300L);
                ofFloat.setInterpolator((TimeInterpolator) new LinearInterpolator());
                ofFloat.addUpdateListener((ValueAnimator.AnimatorUpdateListener) new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                        ProgressActivity.this.hsv[0] = ProgressActivity.this.from[0] + (ProgressActivity.this.to[0] - ProgressActivity.this.from[0]) * (float) valueAnimator.getAnimatedValue() / 100.0f;
                        ProgressActivity.this.hsv[1] = ProgressActivity.this.from[1] + (ProgressActivity.this.to[1] - ProgressActivity.this.from[1]) * (float) valueAnimator.getAnimatedValue() / 100.0f;
                        ProgressActivity.this.hsv[2] = ProgressActivity.this.from[2] + (ProgressActivity.this.to[2] - ProgressActivity.this.from[2]) * (float) valueAnimator.getAnimatedValue() / 100.0f;
                        if (lastProg >= 200) {
                            mProgressBar.setProgress(0);
                            tvProgress.setText("0.00 %");
                        } else {
                            if (lastProg >= 100) {
                                tvProgress.setText("100.00 %");
                                mProgressBar.setProgress(100);
                            } else {
                                mProgressBar.setProgress(Math.round(lastProg));
                                tvProgress.setText((String.valueOf(Math.round(lastProg)) + " %"));
                            }
                        }
                    }
                });
                ofFloat.addListener((Animator.AnimatorListener) new Animator.AnimatorListener() {
                    public void onAnimationCancel(final Animator animator) {
                        ProgressActivity.this.isComplate = true;
                    }

                    public void onAnimationEnd(final Animator animator) {
                        ProgressActivity.this.isComplate = true;
                    }

                    public void onAnimationRepeat(final Animator animator) {
                    }

                    public void onAnimationStart(final Animator animator) {
                        ProgressActivity.this.isComplate = false;
                    }
                });
                ofFloat.start();
                this.lastProg = lastProg;
            }
        }
    }


    public void onBackPressed() {
    }


    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_progress);
        this.getWindow().addFlags(128);
        this.application = MyApplication.getInstance();
        this.bindView();
        loadAd();
        PutAnalyticsEvent();
        mProgressBar = (ProgressBar) findViewById(R.id.google_progress);
        Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
        mProgressBar.setIndeterminateDrawable(getProgressDrawable());
        mProgressBar.getIndeterminateDrawable().setBounds(bounds);
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
                        if (ProgressActivity.this.nativeAd != null) {
                            ProgressActivity.this.nativeAd.destroy();
                        }
                        ProgressActivity.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_native, null);
                        populateUnifiedNativeAdViewbig(nativeAd, adView);
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



    private Drawable getProgressDrawable() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int value = Integer.parseInt(prefs.getString(getString(R.string.progressBar_pref_key), getString(R.string.progressBar_pref_defValue)));
        Drawable progressDrawable = null;
        progressDrawable = new ChromeFloatingCirclesDrawable.Builder(this)
                .colors(getProgressDrawableColors())
                .build();


        return progressDrawable;
    }


    private int[] getProgressDrawableColors() {
        int[] colors = new int[4];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        colors[0] = prefs.getInt(getString(R.string.firstcolor_pref_key), getResources().getColor(R.color.red));
        colors[1] = prefs.getInt(getString(R.string.secondcolor_pref_key), getResources().getColor(R.color.blue));
        colors[2] = prefs.getInt(getString(R.string.thirdcolor_pref_key), getResources().getColor(R.color.yellow));
        colors[3] = prefs.getInt(getString(R.string.fourthcolor_pref_key), getResources().getColor(R.color.green));
        return colors;
    }


    public void onImageProgressFrameUpdate(final float n) {
        this.runOnUiThread((Runnable) new Runnable() {
            @Override
            public void run() {
                ProgressActivity.this.changePercentageOnText(n * 25.0f / 100.0f);
            }
        });
    }

    public void onOverlayingFinish(final String s) {
    }

    public void onProgressFinish(final String s) {
        if (MyApplication.IsVideo) {
            Utils.isVideoCreationRunning = false;
            final Intent intent = new Intent((Context) this, (Class) Activity_VideoPlay.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("android.intent.extra.TEXT", s);
            intent.putExtra("KEY", "FromProgress");
            this.application.isFristTimeTheme = true;
            ActivityAnimUtil.startActivitySafely((View) this.tvProgress, intent);
            super.onBackPressed();
        }
    }

    protected void onResume() {
        super.onResume();
        this.application.setOnProgressReceiver((OnProgressReceiver) this);
    }

    protected void onStop() {
        super.onStop();
        this.application.setOnProgressReceiver((OnProgressReceiver) null);
        if (MyApplication.isMyServiceRunning((Context) this, (Class) CreateVideoService.class)) {
//            this.finish();
        }
    }

    public void onVideoProgressFrameUpdate(final float n) {
        this.runOnUiThread((Runnable) new Runnable() {
            @Override
            public void run() {
                ProgressActivity.this.changePercentageOnText(n * 75.0f / 100.0f + 25.0f);
            }
        });
    }
}
