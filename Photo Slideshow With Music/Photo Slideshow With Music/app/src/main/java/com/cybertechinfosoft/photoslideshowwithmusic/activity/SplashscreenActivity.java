package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SplashscreenActivity extends Activity {
    Activity activity = SplashscreenActivity.this;
    Animation animFadeIn1;
    private Animation animFadeIn2;
    Animation animFadeInBg;
    Animation animFadeInImageAlbm;
    Animation animFadeInTitle;
    Animation animFadeOutTitle;
    Animation animMoveTitle;
    Animation animRotate1;
    private Animation animRotate2;
    private Animation animZoomInOut;
    Button btnStart;
    ImageView imgAddMusic;
    ImageView imgArrow1;
    ImageView imgArrow2;
    ImageView imgLoadImage;
    ImageView imgSplashBg;
    ImageView imgTitle;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splashscreen);
        bindView();
        init();
        addListener();
        PutAnalyticsEvent();
        this.imgTitle.startAnimation(this.animFadeInTitle);
        this.animFadeInTitle.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadAnimation();
                SplashscreenActivity.this.imgSplashBg.setVisibility(View.VISIBLE);
                SplashscreenActivity.this.imgSplashBg.startAnimation(SplashscreenActivity.this.animFadeInBg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.animMoveTitle.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.animFadeInBg.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void bindView() {
        this.imgTitle = (ImageView) findViewById(R.id.imgTitle);
        this.imgSplashBg = (ImageView) findViewById(R.id.imgBackground);
        this.imgLoadImage = (ImageView) findViewById(R.id.imgSelectPhoto);
        this.imgArrow1 = (ImageView) findViewById(R.id.imgArrow1);
        this.imgArrow2 = (ImageView) findViewById(R.id.imgArrow2);
        this.imgAddMusic = (ImageView) findViewById(R.id.imgAddMusic);
        this.btnStart = (Button) findViewById(R.id.btnStartCreate);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SplashscreenActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void init() {
        Utils.setFont(this, this.btnStart);
        Utils.setFont(this, R.id.tvCreateAccurateFastVideo);
        this.animFadeInTitle = AnimationUtils.loadAnimation(this, R.anim.fadein);
        this.animFadeOutTitle = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        this.animFadeInBg = AnimationUtils.loadAnimation(this, R.anim.fadein);
        this.animMoveTitle = AnimationUtils.loadAnimation(this, R.anim.move);
        this.animRotate1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        this.animRotate2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        this.animFadeIn1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        this.animFadeIn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        this.animZoomInOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_zoom_out);
    }

    private void addListener() {
        this.animRotate1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override

            public void onAnimationEnd(Animation animation) {
                SplashscreenActivity.this.imgArrow1.setVisibility(View.VISIBLE);
                SplashscreenActivity.this.imgArrow1.startAnimation(SplashscreenActivity.this.animFadeIn1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.animFadeIn1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SplashscreenActivity.this.imgAddMusic.setVisibility(View.VISIBLE);
                SplashscreenActivity.this.imgAddMusic.startAnimation(SplashscreenActivity.this.animRotate2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.animRotate2.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SplashscreenActivity.this.imgArrow2.setVisibility(View.VISIBLE);
                SplashscreenActivity.this.imgArrow2.startAnimation(SplashscreenActivity.this.animFadeIn2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animFadeIn2.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SplashscreenActivity.this.btnStart.startAnimation(SplashscreenActivity.this.animZoomInOut);
                SplashscreenActivity.this.btnStart.setVisibility(View.VISIBLE);
                SplashscreenActivity.this.findViewById(R.id.tvCreateAccurateFastVideo).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.isShowAd == 0) {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.AdsId = 117;
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;
                    } else {
                        startActivity(new Intent(SplashscreenActivity.this.getApplicationContext(), LauncherActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashscreenActivity.this.getApplicationContext(), LauncherActivity.class));
                    finish();
                    MyApplication.isShowAd = 0;
                }
            }
        });
    }

    private void loadAnimation() {
        this.imgLoadImage.setVisibility(View.VISIBLE);
        this.imgLoadImage.startAnimation(this.animRotate1);
    }
}
