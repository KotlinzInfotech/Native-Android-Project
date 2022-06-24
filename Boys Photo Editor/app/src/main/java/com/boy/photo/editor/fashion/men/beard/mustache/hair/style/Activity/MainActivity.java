package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.AppUpdate.InAppUpdateManager;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.AppUpdate.InAppUpdateStatus;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.App.AppController;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.EPreferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;

import static com.boy.photo.editor.fashion.men.beard.mustache.hair.style.AppUpdate.Constants.UpdateMode.FLEXIBLE;
import static com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.NativeAdMethod.populateUnifiedNativeAdView;

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {
    Activity activity = MainActivity.this;
    public static Bitmap bitmap;
    public static Uri image_uri;
    private int IMG_RESULT = 123;
    AlphaAnimation alpha_Animation;
    ImageView btn_mycreation;
    ImageView button;
    ImageView ivOther;
    EPreferences ePreferences;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private NativeAd nativeAd;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;


    //App Update
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private InAppUpdateManager inAppUpdateManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_start);
        ivOther = findViewById(R.id.iv_Other);
        btn_mycreation = findViewById(R.id.btn_mycreation);
        alpha_Animation = new AlphaAnimation(0.0f, 1.0f);
        alpha_Animation.setDuration(1000);
        ePreferences = EPreferences.getInstance(this);
        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE).resumeUpdates(true).mode(FLEXIBLE).handler(this);
        BannerAds();
        ivOther.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(activity, ivOther);
                popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.Privacy_policy:
                                PrivacyPolicy();
                                break;
                            case R.id.rate_us:
                                RateApp();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
                    } else {
                        if (AppController.mInterstitialAd != null) {
                            AppController.activity = activity;
                            AppController.AdsId = 1;
                            AppController.mInterstitialAd.show(activity);

                        } else {
                            openGallery();
                        }
                    }
                } else {
                    if (AppController.mInterstitialAd != null) {
                        AppController.activity = activity;
                        AppController.AdsId = 1;
                        AppController.mInterstitialAd.show(activity);

                    } else {
                        openGallery();
                    }
                }
            }
        });
        this.btn_mycreation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.mInterstitialAd != null) {
                    AppController.activity = activity;
                    AppController.AdsId = 2;
                    AppController.mInterstitialAd.show(activity);
                } else {
                    startActivity(new Intent(activity, Activity_Creation.class));
                    finish();
                }
            }
        });
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "MainActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void PrivacyPolicy() {
        try {
            Intent intent1 = new Intent("android.intent.action.VIEW");
            intent1.setData(Uri.parse(getResources().getString(R.string.privacy_link)));
            startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RateApp() {
        try {
            startActivity(new Intent("android.intent.action.VIEW",
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "You don't have Google Play installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    @Override
    public void onBackPressed() {
        if (ePreferences.getBoolean("pref_key_rate", false)) {
            ExitDialog();
        } else {
            RateDialog();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void RateDialog() {
        final boolean[] isRate = {false, false};
        final Dialog dialog = new Dialog(MainActivity.this);
        final ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCanceledOnTouchOutside(false);

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
                        if (MainActivity.this.nativeAd != null) {
                            MainActivity.this.nativeAd.destroy();
                        }
                        MainActivity.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = dialog.findViewById(R.id.fl_adplaceholder);
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

        ivStar1 = dialog.findViewById(R.id.ivStar1);
        ivStar2 = dialog.findViewById(R.id.ivStar2);
        ivStar3 = dialog.findViewById(R.id.ivStar3);
        ivStar4 = dialog.findViewById(R.id.ivStar4);
        ivStar5 = dialog.findViewById(R.id.ivStar5);
        ivStar1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_empty);
                ivStar3.setImageResource(R.drawable.star_empty);
                ivStar4.setImageResource(R.drawable.star_empty);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = false;
                isRate[1] = true;
                return false;
            }
        });
        ivStar2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_empty);
                ivStar4.setImageResource(R.drawable.star_empty);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = false;
                isRate[1] = true;
                return false;
            }
        });
        ivStar3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_fill);
                ivStar4.setImageResource(R.drawable.star_empty);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = false;
                isRate[1] = true;
                return false;
            }
        });
        ivStar4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_fill);
                ivStar4.setImageResource(R.drawable.star_fill);
                ivStar5.setImageResource(R.drawable.star_empty);
                isRate[0] = true;
                isRate[1] = true;
                return false;
            }
        });
        ivStar5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivStar1.setImageResource(R.drawable.star_fill);
                ivStar2.setImageResource(R.drawable.star_fill);
                ivStar3.setImageResource(R.drawable.star_fill);
                ivStar4.setImageResource(R.drawable.star_fill);
                ivStar5.setImageResource(R.drawable.star_fill);
                isRate[0] = true;
                isRate[1] = true;
                return false;
            }
        });
        dialog.findViewById(R.id.btnLater).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finishAffinity();
            }
        });
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRate[1]) {
                    dialog.dismiss();
                    if (isRate[0]) {
                        ePreferences.putBoolean("pref_key_rate", true);
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                        } catch (ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                    }
                    finishAffinity();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Your Review Star", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void ExitDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_layout_exit);
        dialog.setCanceledOnTouchOutside(false);

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
                        if (MainActivity.this.nativeAd != null) {
                            MainActivity.this.nativeAd.destroy();
                        }
                        MainActivity.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = dialog.findViewById(R.id.fl_adplaceholder);
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

        dialog.findViewById(R.id.btnLater).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        dialog.show();
    }

    private void openGallery() {
        startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), 123);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Sorry We cant process ahed due to denied pemission", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                MainActivity.this.openGallery();
                return;


        }
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == -1 && data != null && data.getData() != null) {
            image_uri = data.getData();
            try {
                bitmap = Media.getBitmap(getContentResolver(), data.getData());
                startActivity(new Intent(this, Activity_Crop.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onInAppUpdateError(int code, Throwable error) {
        Log.e("TAG", "code: " + code, error);
    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        Log.e("TAG", "OnInAppUpdateStatus" + status.toString());
    }

}
