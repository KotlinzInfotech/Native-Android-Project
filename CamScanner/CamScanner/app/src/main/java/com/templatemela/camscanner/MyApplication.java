package com.templatemela.camscanner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.templatemela.camscanner.OpenAppAds.AppOpenAdManager;
import com.templatemela.camscanner.main_utils.Constant;
import me.pqpo.smartcropperlib.SmartCropper;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static MyApplication instance;
    private boolean showAds = true;

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    public static Activity activity;
    public static InterstitialAd mInterstitialAd;
    public static String IdentifyActivity = "IdentifyActivity";
    public static int isShowAd = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SmartCropper.buildImageDetector(this);
        this.registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();
        interstitialAd();
        initTheme();
    }

    private void interstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.InterstitialAd_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        requestNewInterstitial();
                                        jumpNextActivity(activity);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    }
                });

    }

    private void requestNewInterstitial() {
        interstitialAd();
    }

    public static void jumpNextActivity(Activity activity) {
        if (IdentifyActivity.equals("MainActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".MainActivity"));
        } else if (IdentifyActivity.equals("PrivacyPolicyActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".PrivacyPolicyActivity"));
        } else if (IdentifyActivity.equals("QRGenerateActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".QRGenerateActivity"));
        } else if (IdentifyActivity.equals("QRReaderActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".QRReaderActivity"));
        } else if (IdentifyActivity.equals("MainGalleryActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".MainGalleryActivity"));
        } else if (IdentifyActivity.equals("ScannerActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ScannerActivity"));
        } else if (IdentifyActivity.equals("GroupDocumentActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".GroupDocumentActivity"));
        } else if (IdentifyActivity.equals("CropDocumentActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".CropDocumentActivity"));
        } else if (IdentifyActivity.equals("ScannerGalleryActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ScannerGalleryActivity"));
        } else if (IdentifyActivity.equals("CropDocumentActivity2")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".CropDocumentActivity2"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_Crop")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_Crop"));
        } else if (IdentifyActivity.equals("CurrentFilterActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".CurrentFilterActivity"));
        } else if (IdentifyActivity.equals("ScannerActivity_Retake")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ScannerActivity_Retake"));
        } else if (IdentifyActivity.equals("SavedDocumentActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".SavedDocumentActivity"));
        } else if (IdentifyActivity.equals("ScannerActivity_Retake2")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ScannerActivity_Retake2"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_Saved")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_Saved"));
        } else if (IdentifyActivity.equals("SavedEditDocumentActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".SavedEditDocumentActivity"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_SavedEdit")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_SavedEdit"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_SavedEdit2")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_SavedEdit2"));
        } else if (IdentifyActivity.equals("PDFViewerActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".PDFViewerActivity"));
        } else if (IdentifyActivity.equals("NoteActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".NoteActivity"));
        } else if (IdentifyActivity.equals("ImageToTextActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ImageToTextActivity"));
        } else if (IdentifyActivity.equals("PDFViewerActivity2")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".PDFViewerActivity2"));
        } else if (IdentifyActivity.equals("DocumentGalleryActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentGalleryActivity"));
        } else if (IdentifyActivity.equals("CropDocumentActivity4")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".CropDocumentActivity4"));
        } else if (IdentifyActivity.equals("ScannerActivity2")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ScannerActivity2"));
        } else if (IdentifyActivity.equals("SavedDocumentPreviewActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".SavedDocumentPreviewActivity"));
        } else if (IdentifyActivity.equals("IDCardPreviewActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".IDCardPreviewActivity"));
        } else if (IdentifyActivity.equals("SavedEditDocumentActivity3")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".SavedEditDocumentActivity3"));
        } else if (IdentifyActivity.equals("UcropActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".UcropActivity"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_Scanner")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_Scanner"));
        } else if (IdentifyActivity.equals("IDCardPreviewActivity2")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".IDCardPreviewActivity2"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_IDCard")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_IDCard"));
        } else if (IdentifyActivity.equals("IDCardGalleryActivity")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".IDCardGalleryActivity"));
        } else if (IdentifyActivity.equals("DocumentEditorActivity_SavedPreview")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".DocumentEditorActivity_SavedPreview"));
        } else if (IdentifyActivity.equals("PDFViewerActivity_Preview")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".PDFViewerActivity_Preview"));
        } else if (IdentifyActivity.equals("NoteActivity_Preview")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".NoteActivity_Preview"));
        } else if (IdentifyActivity.equals("ImageToTextActivity_Preview")) {
            activity.sendBroadcast(new Intent(activity.getPackageName() + ".ImageToTextActivity_Preview"));
        }
    }

    private void initTheme() {
        int savedTheme = getSavedTheme();
        if (savedTheme == Constant.THEME_LIGHT) {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO, Constant.THEME_LIGHT);
        } else if (savedTheme == Constant.THEME_DARK) {
            setTheme(AppCompatDelegate.MODE_NIGHT_YES, Constant.THEME_DARK);
        }
    }

    private int getSavedTheme() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constant.KEY_THEME, Constant.THEME_UNDEFINED);
    }


    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constant.KEY_THEME, theme).apply();
    }

    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }

    /*AppOpenAds Start*/
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    /**
     * Shows an app open ad.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    /*AppOpenAds End*/
}
