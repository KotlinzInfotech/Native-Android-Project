package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.cybertechinfosoft.photoslideshowwithmusic.AppUpdate.Constants;
import com.cybertechinfosoft.photoslideshowwithmusic.AppUpdate.InAppUpdateManager;
import com.cybertechinfosoft.photoslideshowwithmusic.AppUpdate.InAppUpdateStatus;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.service.CreateVideoService;
import com.cybertechinfosoft.photoslideshowwithmusic.service.ImageCreatorService;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
import com.cybertechinfosoft.photoslideshowwithmusic.util.PermissionModelUtil;
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
import com.org.codechimp.apprater.AppRater;
import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdView;

public class LauncherActivity extends AppCompatActivity implements OnClickListener, InAppUpdateManager.InAppUpdateHandler {
    Activity activity = LauncherActivity.this;
    private static final int RequestPermissionCode = 222;
    static int f37i;
    public static LauncherActivity MainActivity;
    ComponentName SecurityComponentName = null;
    EPreferences ePref;
    EPreferences ePref1;
    PermissionModelUtil modelUtil;
    View f38v;
    static Context ctx;

    //App Update
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private InAppUpdateManager inAppUpdateManager;

    private NativeAd nativeAd;


    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (this.isVideoInprocess()) {
            this.startActivity(new Intent(activity, ProgressActivity.class));
            this.overridePendingTransition(0, 0);
            this.finish();
            return;
        }
        this.setContentView(R.layout.activity_home_main);
        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE).resumeUpdates(true).mode(Constants.UpdateMode.FLEXIBLE).handler(this);
        ePref = EPreferences.getInstance(this);
        ePref1 = EPreferences.getInstance1(this);
        setActionBar();
        init();
        addListener();
        loadAd();
        MyApplication.getInstance().setAutostartAppName();
        if (MyApplication.getInstance().runApp(Utils.autostart_app_name, 0) && !this.check_permission()) {
            this.permissionDialog();
        }
        AppRater.setPackageName(this.getPackageName());
        AppRater.app_launched(this);
        if (!this.ePref1.getBoolean("Policy_check", false)) {
            PolicyDialog();
        }
        PutAnalyticsEvent();
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "LauncherActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void openApp(View v) {
        try {
            startActivity(new Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(v.getTag().toString())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "You don't have Google Play installed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean check_permission() {
        return this.ePref.getBoolean("HasAutoStartPermission", false);
    }


    private void permissionDialog() {
        String str = Build.MANUFACTURER;
        if (str.equals("Xiaomi")) {
            this.SecurityComponentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
            Utils.autostart_app_name = "Security";
        } else if (str.equals("asus")) {
            this.SecurityComponentName = new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity");
            Utils.autostart_app_name = "Auto-start Manager";
        }
        Intent intent = new Intent(this, CustomPermissionActivity.class);
        intent.putExtra("PACKAGE", this.SecurityComponentName);
        intent.putExtra("APPNAME", Utils.autostart_app_name);
        startActivity(intent);
    }


    private boolean isVideoInprocess() {
        return MyApplication.isMyServiceRunning(this, CreateVideoService.class) || MyApplication.isMyServiceRunning(this, ImageCreatorService.class);
    }

    private void init() {
        MainActivity = this;
        Utils.isVideoCreationRunning = true;
        if (Utils.checkPermission(this)) {
            MyApplication.getInstance().getFolderList();
        } else {
            Utils.requestPermission(this);
        }
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(307);
    }

    @Override
    protected void onStart() {
        super.onStart();
        inAppUpdateManager.checkForAppUpdate();
    }


    protected void onResume() {
        super.onResume();
        if (f37i == 1) {
            f37i = 0;
            if (Utils.checkPermission(this)) {
                onStart();
            } else {
                Utils.requestPermission(this);
            }
        }
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int n, @NonNull final String[] array, @NonNull final int[] array2) {
        if (n != 222) {
            return;
        }
        if (array2.length > 0) {
            boolean b = false;
            if (array2[0] == 0) {
                n = 1;
            } else {
                n = 0;
            }
            if (array2[1] == 0) {
                b = true;
            }
            if (n != 0 && b) {
                MyApplication.getInstance().getFolderList();
                return;
            }
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE") && !ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                Utils.permissionDailog(this);
                return;
            }
            Utils.requestPermission(this);
        }
    }

    private void addListener() {
        findViewById(R.id.btnCreateVideo).setOnClickListener(this);
        findViewById(R.id.btnViewVideo).setOnClickListener(this);
        findViewById(R.id.btnChangeLang).setOnClickListener(this);
    }

    public void onClick(View view) {
        this.f38v = view;
        int id = view.getId();
        if (id != R.id.btnChangeLang) {
            if (id != R.id.btnCreateVideo) {
                if (id == R.id.btnViewVideo) {
                    if (Utils.checkPermission(this)) {
                        if (MyApplication.isShowAd == 1) {
                            startActivity( new Intent(activity, VideoAlbumActivity.class));
                            finish();
                            MyApplication.isShowAd = 0;
                        } else {
                            if (MyApplication.mInterstitialAd != null) {
                                MyApplication.activity = activity;
                                MyApplication.AdsId = 100;
                                MyApplication.mInterstitialAd.show(activity);
                                MyApplication.isShowAd = 1;

                            } else {
                                startActivity( new Intent(activity, VideoAlbumActivity.class));
                                finish();
                            }


                        }
                    }
                    Utils.requestPermission(this);
                }
            } else if (Utils.checkPermission(this)) {
                init();
                MyApplication.getInstance().getFolderList();
                if (MyApplication.getInstance().getAllFolder().size() > 0) {
                    MyApplication.isStoryAdded = false;
                    MyApplication.isBreak = false;
                    MyApplication.getInstance().setMusicData(null);
                    if (MyApplication.isShowAd == 1) {
                        startActivity(new Intent(activity, ImageSelectionActivity.class));
                        finish();
                        MyApplication.isShowAd = 0;
                    } else {
                        if (MyApplication.mInterstitialAd != null) {
                            MyApplication.activity = activity;
                            MyApplication.AdsId = 101;
                            MyApplication.mInterstitialAd.show(activity);
                            MyApplication.isShowAd = 1;

                        } else {
                            startActivity(new Intent(activity, ImageSelectionActivity.class));
                            finish();
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.no_images_found_in_device_please_add_images_in_sdcard, Toast.LENGTH_SHORT).show();
                }
            } else {
                Utils.requestPermission(this);
            }
        } else if (Utils.checkPermission(this)) {
            loadLanguage(view);
        } else {
            this.modelUtil.showPermissionExplanationThenAuthorization();
        }
    }

    private void loadLanguage(View view) {
        ActivityAnimUtil.startActivitySafely(view, new Intent(this, LanguageActivity.class));
    }

    private void loadVideoList(View view) {
        ActivityAnimUtil.startActivitySafely(view, new Intent(this, VideoAlbumActivity.class));
    }

    private void loadCreateVideo(View view) {
        MyApplication.getInstance().getFolderList();
        if (MyApplication.getInstance().getAllFolder().size() > 0) {
            MyApplication.isStoryAdded = false;
            MyApplication.isBreak = false;
            MyApplication.getInstance().setMusicData(null);
            ActivityAnimUtil.startActivitySafely(view, new Intent(this, ImageSelectionActivity.class));
            return;
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_images_found_in_device_please_add_images_in_sdcard, Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        if (this.ePref.getBoolean("pref_key_rate", false)) {
            ExitDialog();
        } else {
            RateDialog();
        }

    }

    public void ExitDialog() {
        final Dialog dialog = new Dialog(LauncherActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_back_layout);
        dialog.setCanceledOnTouchOutside(false);
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
                        if (LauncherActivity.this.nativeAd != null) {
                            LauncherActivity.this.nativeAd.destroy();
                        }
                        LauncherActivity.this.nativeAd = nativeAd;
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
                dialog.dismiss();
                System.exit(0);
            }
        });
        dialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void RateDialog() {
        final boolean[] isRate = {false, false};
        final Dialog dialog = new Dialog(LauncherActivity.this);
        final ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        ivStar1 = dialog.findViewById(R.id.ivStar1);
        ivStar2 = dialog.findViewById(R.id.ivStar2);
        ivStar3 = dialog.findViewById(R.id.ivStar3);
        ivStar4 = dialog.findViewById(R.id.ivStar4);
        ivStar5 = dialog.findViewById(R.id.ivStar5);
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
                        if (LauncherActivity.this.nativeAd != null) {
                            LauncherActivity.this.nativeAd.destroy();
                        }
                        LauncherActivity.this.nativeAd = nativeAd;
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

        adLoader.loadAd(new AdRequest.Builder().build());
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
                    ePref.putBoolean("pref_key_rate", true);
                    dialog.dismiss();
                    if (isRate[0]) {
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

    public void PolicyDialog() {
        final Dialog dialog = new Dialog(LauncherActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation1);
        dialog.setContentView(R.layout.dialog_layout_policy);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.btnLater).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                System.exit(0);
            }
        });
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ePref1.putBoolean("Policy_check", true);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvReadMore).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://sites.google.com/site/cybertechinfosoft02/"));
                startActivity(intent1);
            }
        });
        dialog.show();
    }

    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setText(getString(R.string.app_name));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Utils.setFont(this, textView);
    }

    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public boolean onCreonCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SubMenu subMenu = item.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int i2 = 0; i2 < subMenu.size(); i2++) {
                    Utils.applyFontToMenuItem(getApplicationContext(), subMenu.getItem(i2));
                }
            }
            Utils.applyFontToMenuItem(getApplicationContext(), item);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                        if (LauncherActivity.this.nativeAd != null) {
                            LauncherActivity.this.nativeAd.destroy();
                        }
                        LauncherActivity.this.nativeAd = nativeAd;
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
    public void onInAppUpdateError(int code, Throwable error) {

    }


    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {

    }

}
