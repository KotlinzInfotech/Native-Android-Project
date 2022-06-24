package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import static com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.NativeAdMethod.populateUnifiedNativeAdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.App.AppController;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;

public class DisplayImageActivity extends AppCompatActivity {
    Activity activity = DisplayImageActivity.this;
    ImageView ivMainImage;
    ImageView ivDelete;
    int pos;
    ImageView ivSetas;
    ImageView ivShare;

    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        PutAnalyticsEvent();
        LoadNativeAds();
        pos = getIntent().getIntExtra("position", 0);
        AppController.Position = pos;
        ivMainImage = (ImageView) findViewById(R.id.ivMainImage);
        try {
            ivMainImage.setImageURI(Uri.parse((String) Utils.Constant.IMAGEALLARY.get(pos)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dial = new Dialog(DisplayImageActivity.this,
                        android.R.style.Theme_Translucent);
                dial.requestWindowFeature(1);
                dial.setContentView(R.layout.delete_confirmation);
                dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dial.setCanceledOnTouchOutside(true);
                ((TextView) dial.findViewById(R.id.tvDelete))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                File fD = new File((String) Utils.Constant.IMAGEALLARY.get(pos));
                                if (fD.exists()) {
                                    fD.delete();
                                }
                                Utils.deleteFile(new File(Utils.Constant.IMAGEALLARY.get(pos)));
                                Utils.Constant.IMAGEALLARY.remove(pos);
                                DisplayImageActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(String.valueOf(fD)))));
                                if (Utils.Constant.IMAGEALLARY.size() == 0) {
                                    Toast.makeText(activity, "No Image Found..", Toast.LENGTH_SHORT
                                    ).show();
                                }
                                dial.dismiss();
                                finish();

                            }
                        });
                ((TextView) dial.findViewById(R.id.tvCancle))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                dial.dismiss();
                            }
                        });
                dial.show();

            }
        });

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ivSetas = (ImageView) findViewById(R.id.ivSetas);
        ivSetas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppController.mInterstitialAd != null) {
                    AppController.activity = activity;
                    AppController.AdsId = 9;
                    AppController.mInterstitialAd.show(activity);

                } else {
                    setWallpaper("", (String) Utils.Constant.IMAGEALLARY.get(pos));
                }

            }
        });
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivShare.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Parcelable fromFile;
                if (Build.VERSION.SDK_INT <= 19) {
                    fromFile = Uri.fromFile(new File(Utils.Constant.IMAGEALLARY.get(pos)));
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(getPackageName());
                    stringBuilder.append(".provider");
                    fromFile = FileProvider.getUriForFile(DisplayImageActivity.this, stringBuilder.toString(), new File(Utils.Constant.IMAGEALLARY.get(pos)));
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.TEXT",
                        getApplicationContext().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                intent.putExtra("android.intent.extra.STREAM", fromFile);
                intent.addFlags(1);
                startActivity(Intent.createChooser(intent, getString(R.string.share_your_story)));

            }
        });
    }


    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "DisplayImageActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
                        if (DisplayImageActivity.this.nativeAd != null) {
                            DisplayImageActivity.this.nativeAd.destroy();
                        }
                        DisplayImageActivity.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_small, null);
                        populateUnifiedNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        com.google.android.gms.ads.nativead.NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void setWallpaper(String diversity, String s) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            wallpaperManager.setBitmap(BitmapFactory.decodeFile(s, options));
            wallpaperManager.suggestDesiredDimensions(width / 2, height / 2);
            Toast.makeText(this, "Wallpaper Set Sucessfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (AppController.mInterstitialAd != null) {
            AppController.activity = activity;
            AppController.AdsId = 8;
            AppController.mInterstitialAd.show(activity);

        } else {
            startActivity(new Intent(activity, Activity_Creation.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}
