package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters.CreationAdapter;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.App.AppController;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;

import static com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.NativeAdMethod.populateUnifiedNativeAdView;

public class Activity_Creation extends AppCompatActivity {
    Activity activity = Activity_Creation.this;
    private static final int MY_REQUEST_CODE = 5;
    private CreationAdapter creation_adapter;
    ImageView img_back;
    GridView gridview;

    private NativeAd nativeAd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_creation);
        this.img_back = findViewById(R.id.img_back);
        this.gridview = findViewById(R.id.gridview);
        getImages();
        LoadNativeAds();
        init();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity_Creation");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void init() {
        this.creation_adapter = new CreationAdapter(this, Utils.Constant.IMAGEALLARY);
        Activity_Creation.this.creation_adapter.notifyDataSetChanged();
        this.gridview.setAdapter(this.creation_adapter);
        this.gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    final int position, long id) {
                Intent i = new Intent(activity, DisplayImageActivity.class);
                i.putExtra("position", position);
                startActivity(i);

            }
        });

        this.img_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getImages() {
        if (VERSION.SDK_INT < 23) {
            fetchImage();
        } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            fetchImage();
        } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 5);
        }
    }


    private void fetchImage() {
        Utils.Constant.IMAGEALLARY.clear();
        Utils.Constant.listAllImages(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getString(R.string.app_name) + "/"));
    }

    public void onBackPressed() {
        if (AppController.mInterstitialAd != null) {
            AppController.activity = activity;
            AppController.AdsId = 3;
            AppController.mInterstitialAd.show(activity);

        } else {
            startActivity(new Intent(activity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5:
                if (grantResults[0] == 0) {
                    fetchImage();
                    return;
                } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                            5);
                    return;
                } else {
                    getImages();
                    return;
                }
            default:
                return;
        }
    }


    private void LoadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.admob_Native));
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
                        if (Activity_Creation.this.nativeAd != null) {
                            Activity_Creation.this.nativeAd.destroy();
                        }
                        Activity_Creation.this.nativeAd = nativeAd;
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


    @Override
    public void onResume() {
        Activity_Creation.this.creation_adapter.notifyDataSetChanged();
        super.onResume();
    }
}
