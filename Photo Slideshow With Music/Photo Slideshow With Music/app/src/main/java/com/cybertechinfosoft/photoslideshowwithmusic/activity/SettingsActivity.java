package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdViewbig;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
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


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    EPreferences ePref;
    private Toolbar toolbar;
    private NativeAd nativeAd;

    private void addListener() {
        this.findViewById(R.id.rlLikeUs).setOnClickListener((View.OnClickListener) this);
        this.findViewById(R.id.rlRateUs).setOnClickListener((View.OnClickListener) this);
        this.findViewById(R.id.rlPrivacypolicy).setOnClickListener((View.OnClickListener) this);
    }


    private void bindView() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SettingsActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void init() {
        this.setSupportActionBar(this.toolbar);
        final TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText((CharSequence) this.getString(R.string.settings));
        Utils.setFont(this, textView);
        this.ePref = EPreferences.getInstance(this);
        Utils.setFont(this, R.id.tvLikeus);
        Utils.setFont(this, R.id.tvRateus);
        Utils.setFont(this, R.id.tvPrivacyPolicy);
    }

    private void loadRate() {
        final Intent intent = new Intent("android.intent.action.VIEW");
        final StringBuilder sb = new StringBuilder();
        sb.append("market://details?id=");
        sb.append(this.getPackageName());
        intent.setData(Uri.parse(sb.toString()));
        this.startActivity(intent);
    }

    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        super.onActivityResult(n, n2, intent);
    }

    public void onBackPressed() {
        this.getWindow().clearFlags(128);
        super.onBackPressed();
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            default: {
            }
            case R.id.rlRateUs: {
                this.loadRate();
            }
            break;
            case R.id.rlLikeUs: {
                this.loadRate();
            }
            break;
            case R.id.rlPrivacypolicy: {
                privacypolicy();
            }
            break;

        }
    }

    private void privacypolicy() {
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse("https://sites.google.com/site/cybertechinfosoft02/"));
        startActivity(intent1);
    }

    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);
        bindView();
        init();
        addListener();
        loadAd();
        PutAnalyticsEvent();
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
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
                        if (SettingsActivity.this.nativeAd != null) {
                            SettingsActivity.this.nativeAd.destroy();
                        }
                        SettingsActivity.this.nativeAd = nativeAd;
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
}
