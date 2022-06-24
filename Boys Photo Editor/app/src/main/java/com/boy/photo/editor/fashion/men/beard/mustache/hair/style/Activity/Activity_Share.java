package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import static com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.NativeAdMethod.populateUnifiedNativeAdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.App.AppController;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;

public class Activity_Share extends AppCompatActivity implements OnClickListener {
    Activity activity = Activity_Share.this;
    private ImageView final_img;
    private ImageView img_home;
    private ImageView img_back;
    private ImageView img_Share_More;
    private ImageView img_facebook;
    private ImageView img_instagram;
    private ImageView img_whatsapp;

    private NativeAd nativeAd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        LoadNativeAds();
        bindview();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity_Share");
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
                        if (Activity_Share.this.nativeAd != null) {
                            Activity_Share.this.nativeAd.destroy();
                        }
                        Activity_Share.this.nativeAd = nativeAd;
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


    private void bindview() {
        this.img_back = findViewById(R.id.imgg_back);
        this.img_back.setOnClickListener(this);
        this.final_img = findViewById(R.id.final_img);
        this.final_img.setImageBitmap(Activity_imageEdit.final_bitmap);
        this.img_home = findViewById(R.id.img_home);
        this.img_home.setOnClickListener(this);
        this.img_whatsapp = findViewById(R.id.img_whatsapp);
        this.img_whatsapp.setOnClickListener(this);
        this.img_instagram = findViewById(R.id.img_instagram);
        this.img_instagram.setOnClickListener(this);
        this.img_facebook = findViewById(R.id.img_facebook);
        this.img_facebook.setOnClickListener(this);
        this.img_Share_More = findViewById(R.id.img_Share_More);
        this.img_Share_More.setOnClickListener(this);

    }

    public void onClick(View view) {
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("image/*");
        shareIntent.putExtra("android.intent.extra.TEXT", getApplicationContext().getString(R.string.app_name) + " Created By : " + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
        switch (view.getId()) {
            case R.id.img_home:
                if (AppController.mInterstitialAd != null) {
                    AppController.activity = activity;
                    AppController.AdsId = 7;
                    AppController.mInterstitialAd.show(activity);
                } else {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("ToHome", true);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.imgg_back:
                onBackPressed();
                return;
            case R.id.img_whatsapp:
                try {
                    shareImageAll("com.whatsapp", "WhatsApp");
                    return;
                } catch (Exception e) {
                    Toast.makeText(this, "WhatsApp doesn't installed", Toast.LENGTH_SHORT).show();
                    return;
                }
            case R.id.img_facebook:
                try {
                    shareImageAll("com.facebook.katana", "Facebook");
                    return;
                } catch (Exception e2) {
                    Toast.makeText(this, "Facebook doesn't installed", Toast.LENGTH_SHORT).show();
                    return;
                }
            case R.id.img_instagram:
                try {
                    shareImageAll("com.instagram.android", "Instagram");
                    return;
                } catch (Exception e3) {
                    Toast.makeText(this, "Instagram doesn't installed", Toast.LENGTH_SHORT).show();
                    return;
                }
            case R.id.img_Share_More:
                Intent sharingIntent;
                if (VERSION.SDK_INT < 23) {
                    Parcelable fromFile;
                    if (VERSION.SDK_INT <= 19) {
                        fromFile = Uri.fromFile(new File(Activity_imageEdit.str_url));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getPackageName());
                        stringBuilder.append(".provider");
                        fromFile = FileProvider.getUriForFile(this, stringBuilder.toString(), new File(Activity_imageEdit.str_url));
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/*");
                    intent.putExtra("android.intent.extra.TEXT",
                            getApplicationContext().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());

                    intent.putExtra("android.intent.extra.STREAM", fromFile);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_your_story)));
                } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                    Parcelable fromFile;
                    if (VERSION.SDK_INT <= 19) {
                        fromFile = Uri.fromFile(new File(Activity_imageEdit.str_url));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getPackageName());
                        stringBuilder.append(".provider");
                        fromFile = FileProvider.getUriForFile(this, stringBuilder.toString(), new File(Activity_imageEdit.str_url));
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/*");
                    intent.putExtra("android.intent.extra.TEXT",
                            getApplicationContext().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());

                    intent.putExtra("android.intent.extra.STREAM", fromFile);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_your_story)));
                } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
                }
                break;
            default:
                return;
        }
    }

    public void shareImageAll(String str, String str2) {
        Parcelable fromFile;
        if (VERSION.SDK_INT <= 19) {
            fromFile = Uri.fromFile(new File(Activity_imageEdit.str_url));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getPackageName());
            stringBuilder.append(".provider");
            fromFile = FileProvider.getUriForFile(this, stringBuilder.toString(), new File(Activity_imageEdit.str_url));
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.STREAM", fromFile);
        if (isPackageInstalled(str, getApplicationContext())) {
            intent.setPackage(str);
            startActivity(Intent.createChooser(intent, getString(R.string.share_your_story)));
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Please Install ");
        stringBuilder2.append(str2);
        Toast.makeText(this, stringBuilder2.toString(), Toast.LENGTH_LONG).show();
    }


    private boolean isPackageInstalled(final String s, final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ex) {
            return false;
        }
    }

    public void onBackPressed() {
        if (AppController.mInterstitialAd != null) {
            AppController.activity = activity;
            AppController.AdsId = 6;
            AppController.mInterstitialAd.show(activity);
        } else {
            startActivity(new Intent(activity, Activity_Creation.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }
}
