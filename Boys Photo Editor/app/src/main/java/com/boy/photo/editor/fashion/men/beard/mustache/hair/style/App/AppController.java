package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.App;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity.Activity_Creation;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity.Activity_Share;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity.MainActivity;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.OpenAppAds.AppOpenAdManager;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import java.io.IOException;


public class AppController extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {


    public static Activity activity;

    public static int Position;

    public static InterstitialAd mInterstitialAd;
    public static int AdsId;

    private static AppController mInstance;
    private static Context context;

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();
        this.registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();
        interstitialAd();
    }

    public static Context getContext() {
        return context;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
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
                                        switch (AdsId) {
                                            case 1:
                                                activity.startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 123);
                                                break;
                                            case 2:
                                                activity.startActivity(new Intent(activity, Activity_Creation.class));
                                                activity.finish();
                                                break;
                                            case 3:
                                                activity.startActivity(new Intent(activity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                activity.finish();
                                                break;
                                            case 4:
                                                ShowBackDialog();
                                                break;
                                            case 5:
                                                activity.startActivity(new Intent(activity, Activity_Share.class));
                                                activity.finish();
                                                break;
                                            case 6:
                                                activity.startActivity(new Intent(activity, Activity_Creation.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                activity.finish();
                                                break;
                                            case 7:
                                                Intent intent = new Intent(activity, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("ToHome", true);
                                                activity.startActivity(intent);
                                                activity.finish();
                                                break;
                                            case 8:
                                                activity.startActivity(new Intent(activity, Activity_Creation.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                break;
                                            case 9:
                                                setWallpaper("", (String) Utils.Constant.IMAGEALLARY.get(Position));
                                                break;
                                        }
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

    private void ShowBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message).setCancelable(false)
                .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    }
                })
                .setNegativeButton("Stay here", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setWallpaper(String diversity, String s) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
