package com.templatemela.camscanner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.templatemela.camscanner.R;

import static com.templatemela.camscanner.CustomNativeAd.populateUnifiedNativeAdViewbig;

public class ExitActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity = ExitActivity.this;

    ImageView ivClose;
    RatingBar ratingBar;
    ImageView ivExit, ivSubmit;
    private Float rating = 0.0f;

    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        loadAd();
        BindView();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ExitActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void loadAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.NativeAdvanceAd_id));
        builder.forNativeAd(
                nativeAd -> {
                    boolean isDestroyed = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        isDestroyed = isDestroyed();
                    }
                    if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                        nativeAd.destroy();
                        return;
                    }
                    if (ExitActivity.this.nativeAd != null) {
                        ExitActivity.this.nativeAd.destroy();
                    }
                    ExitActivity.this.nativeAd = nativeAd;
                    FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                    NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_native, null);
                    populateUnifiedNativeAdViewbig(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

    }


    private void BindView() {
        ivExit = findViewById(R.id.iv_exit);
        ivSubmit = findViewById(R.id.iv_submit);
        ratingBar = findViewById(R.id.rateBar);
        ivClose = findViewById(R.id.iv_close);
        ivExit.setOnClickListener(this);
        ivSubmit.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (!b) {
                    return;
                }
                rating = v;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                startActivity(new Intent(activity, MainActivity.class));
                finish();
                break;
            case R.id.iv_submit:
                if (rating < 4.0f) {
                    CloseApp();
                    ratingBar.setRating(0.0f);
                    return;
                }
                RateApp();
                ratingBar.setRating(0.0f);
                break;
            case R.id.iv_exit:
                CloseApp();
                break;
        }
    }


    private void CloseApp() {
        finishAffinity();
    }

    private void RateApp() {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(activity, MainActivity.class));
        finish();
    }
}