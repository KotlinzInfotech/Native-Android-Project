package com.cybertechinfosoft.photoslideshowwithmusic;

import static com.cybertechinfosoft.photoslideshowwithmusic.activity.PreviewActivity.handler;
import static com.cybertechinfosoft.photoslideshowwithmusic.activity.PreviewActivity.lockRunnable;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.cybertechinfosoft.photoslideshowwithmusic.OpenAppAds.AppOpenAdManager;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.EndFrameFrag;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageEditActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageSelectionActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.LauncherActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.NewTitleActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.OnlineMusicActivtiy;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.PreviewActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ProgressActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.SongEditActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.SplashscreenActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.StartFrameFrag;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.VideoAlbumActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.data.MusicData;
import com.cybertechinfosoft.photoslideshowwithmusic.service.CreateVideoService;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
import com.cybertechinfosoft.photoslideshowwithmusic.util.PermissionModelUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.tabs.TabLayout;
import com.movienaker.movie.themes.THEMES;
import com.org.codechimp.apprater.AppRater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    public static int TEMP_POSITION = -1;
    public static int VIDEO_HEIGHT = 480;
    public static int VIDEO_WIDTH = 720;
    private static MyApplication instance;
    public static boolean isBreak = false;
    public static boolean isEndSave = false;
    public static boolean isLastRemoved = false;
    public static boolean isStartRemoved = false;
    public static boolean isStartSave = false;
    public static boolean isStoryAdded = false;
    public float EXTRA_FRAME_TIME;
    public HashMap<String, ArrayList<ImageData>> allAlbum;
    private ArrayList<String> allFolder;
    public int endFrame;
    int frame;
    public boolean isEditModeEnable;
    public boolean isFristTimeTheme;
    public boolean isFromSdCardAudio;
    public boolean isSelectSYS;
    String mAudioDirPath;
    public int min_pos;
    private MusicData musicData;
    private OnProgressReceiver onProgressReceiver;
    public int posForAddMusicDialog;
    private float second;
    private String selectedFolderId;
    public final ArrayList<ImageData> selectedImages;
    public THEMES selectedTheme;
    public int startFrame;
    public ArrayList<String> videoImages;
    public ArrayList<String> welcomeImages;

    public static boolean IsVideo = false;

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    public static InterstitialAd mInterstitialAd;
    public static Activity activity;
    public static int AdsId;
    public static int isShowAd = 0;

    public static boolean isFromCameraNotification;
    public static boolean isFromPreview;

    public static boolean isFromPreviewImageSelection;

    public String strVideoName;

    public MyApplication() {
        this.posForAddMusicDialog = 0;
        this.videoImages = new ArrayList<String>();
        this.welcomeImages = new ArrayList<String>();
        this.selectedImages = new ArrayList<ImageData>();
        this.selectedFolderId = "";
        this.second = 2.0f;
        this.EXTRA_FRAME_TIME = 0.9f;
        this.selectedTheme = THEMES.Shine;
        this.isEditModeEnable = false;
        this.isFromSdCardAudio = false;
        this.min_pos = Integer.MAX_VALUE;
        this.frame = 0;
        this.isFristTimeTheme = true;
        this.isSelectSYS = false;
        this.mAudioDirPath = "";
    }

    public static MyApplication getInstance() {
        return MyApplication.instance;
    }

    private void init() {
        if (!new PermissionModelUtil(this).needPermissionCheck()) {
            this.getFolderList();
            if (!FileUtils.APP_DIRECTORY.exists()) {
                FileUtils.APP_DIRECTORY.mkdirs();
            }
            this.copyAssets();
        }
//        this.loadLib();
        this.setVideoHeightWidth();
    }


    private boolean isAudioFile(final String s) {
        return !TextUtils.isEmpty(s) && s.endsWith(".mp3");
    }

    public static boolean isMyServiceRunning(final Context context, final Class<?> clazz) {
        final Iterator<ActivityManager.RunningServiceInfo> iterator = ((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE).iterator();
        while (iterator.hasNext()) {
            if (clazz.getName().equals(iterator.next().service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void setVideoHeightWidth() {
        final String s = this.getResources().getStringArray(R.array.video_height_width)[EPreferences.getInstance(this.getApplicationContext()).getInt("pref_key_video_quality", 2)];
        final StringBuilder sb = new StringBuilder();
        sb.append("MyApplication VideoQuality value is:- ");
        sb.append(s);
        MyApplication.VIDEO_WIDTH = Integer.parseInt(s.split(Pattern.quote("*"))[0]);
        MyApplication.VIDEO_HEIGHT = Integer.parseInt(s.split(Pattern.quote("*"))[1]);
    }

    public void addSelectedImage(final ImageData imageData) {
        if (MyApplication.isStoryAdded) {
            if (this.selectedImages.size() > 0) {
                this.selectedImages.add(this.selectedImages.size() - 1, imageData);
            } else {
                this.selectedImages.add(imageData);
            }
        } else {
            this.selectedImages.add(imageData);
        }
        ++imageData.imageCount;
    }

    public void clearAllSelection() {
        this.videoImages.clear();
        this.allAlbum = null;
        this.getSelectedImages().clear();
        System.gc();
        this.getFolderList();
    }

    public void copyAssets() {
        final File file = new File(FileUtils.APP_DIRECTORY, "watermark.png");
        if (!file.exists()) {
            final AssetManager assets = this.getAssets();
            try {
                final InputStream open = assets.open("watermark.png");
                final byte[] array = new byte[1024];
                final FileOutputStream fileOutputStream = new FileOutputStream(file);
                while (true) {
                    final int read = open.read(array, 0, 1024);
                    if (read < 0) {
                        break;
                    }
                    fileOutputStream.write(array, 0, read);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                open.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void copyList(final ArrayList<ImageData> list) {
        this.selectedImages.addAll(list);
    }

    public HashMap<String, ArrayList<ImageData>> getAllAlbum() {
        return this.allAlbum;
    }

    public ArrayList<String> getAllFolder() {
        return this.allFolder;
    }

    public Typeface getApplicationTypeFace() {
        return null;
    }

    public String getBucketNameFromURI(final Uri uri) {
        if (uri.toString().startsWith("/")) {
            return uri.toString();
        }
        final ContentResolver contentResolver = this.getContentResolver();
        final String s = null;
        final Cursor query = contentResolver.query(uri, new String[]{"bucket_display_name"}, s, null, s);
        String string;
        if (query.moveToFirst()) {
            string = query.getString(query.getColumnIndexOrThrow("_data"));
        } else {
            string = null;
        }
        query.close();
        return string;
    }

    public String getCurrentTheme() {
        return this.getSharedPreferences("theme", 0).getString("current_theme", THEMES.Shine.toString());
    }

    public void getFolderList() {
        this.allFolder = new ArrayList<String>();
        this.allAlbum = new HashMap<String, ArrayList<ImageData>>();
        final Cursor query = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "bucket_display_name", "bucket_id", "datetaken", "_data"}, null, null, "_data DESC");
        if (query.moveToFirst()) {
            final int columnIndex = query.getColumnIndex("bucket_display_name");
            final int columnIndex2 = query.getColumnIndex("bucket_id");
            this.setSelectedFolderId(query.getString(columnIndex2));
            do {
                final ImageData imageData = new ImageData();
                imageData.imagePath = query.getString(query.getColumnIndex("_data"));
                imageData.imageThumbnail = query.getString(query.getColumnIndex("_data"));
                if (!imageData.imagePath.endsWith(".gif")) {
                    final String string = query.getString(columnIndex);
                    final String string2 = query.getString(columnIndex2);
                    if (!this.allFolder.contains(string2)) {
                        this.allFolder.add(string2);
                    }
                    ArrayList<ImageData> list;
                    if ((list = this.allAlbum.get(string2)) == null) {
                        list = new ArrayList<ImageData>();
                    }
                    imageData.folderName = string;
                    list.add(imageData);
                    this.allAlbum.put(string2, list);
                }
            } while (query.moveToNext());
        }
    }

    public int getFrame() {
        return this.frame;
    }

    public ArrayList<ImageData> getImageByAlbum(final String s) {
        ArrayList<ImageData> list;
        if ((list = this.getAllAlbum().get(s)) == null) {
            list = new ArrayList<ImageData>();
        }
        return list;
    }

    public MusicData getMusicData() {
        return this.musicData;
    }

    public ArrayList<MusicData> getMusicFiles() {
        final ArrayList<MusicData> list = new ArrayList<MusicData>();
        final Cursor query = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, "is_music != 0", null, "title ASC");
        final int columnIndex = query.getColumnIndex("_id");
        final int columnIndex2 = query.getColumnIndex("title");
        final int columnIndex3 = query.getColumnIndex("_display_name");
        final int columnIndex4 = query.getColumnIndex("_data");
        final int columnIndex5 = query.getColumnIndex("duration");
        while (query.moveToNext()) {
            final String string = query.getString(columnIndex4);
            if (this.isAudioFile(string)) {
                final MusicData musicData = new MusicData();
                musicData.track_Id = query.getLong(columnIndex);
                musicData.track_Title = query.getString(columnIndex2);
                musicData.track_data = string;
                musicData.track_duration = query.getLong(columnIndex5);
                musicData.track_displayName = query.getString(columnIndex3);
                list.add(musicData);
            }
        }
        return list;
    }

    public ArrayList<MusicData> getMusicFiles(final boolean b) {
        final ArrayList<MusicData> list = new ArrayList<MusicData>();
        final Cursor query = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, "is_music != 0", null, "title ASC");
        final int columnIndex = query.getColumnIndex("_id");
        final int columnIndex2 = query.getColumnIndex("title");
        final int columnIndex3 = query.getColumnIndex("_display_name");
        final int columnIndex4 = query.getColumnIndex("_data");
        final int columnIndex5 = query.getColumnIndex("duration");
        while (query.moveToNext()) {
            final String string = query.getString(columnIndex4);
            final StringBuilder sb = new StringBuilder();
            sb.append("GLOB Fatch PAth = ");
            sb.append(string);
            final boolean b2 = false;
            int n = 0;


            if (b) {
                if (this.isAudioFile(string)) {
                    if (this.isAudioFilterPath(string)) {
                        n = 1;
                    }
                }
            } else {
                if (this.isAudioFile(string)) {
                    n = 1;
                }
            }

            if (n == 1) {
                final MusicData musicData = new MusicData();
                musicData.track_Id = query.getLong(columnIndex);
                musicData.track_Title = query.getString(columnIndex2);
                musicData.track_data = string;
                musicData.track_duration = query.getLong(columnIndex5);
                musicData.track_displayName = query.getString(columnIndex3);
                list.add(musicData);
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Fatch PAth = ");
                sb2.append(musicData.track_data);
            }
        }
        return list;
    }

    public OnProgressReceiver getOnProgressReceiver() {
        return this.onProgressReceiver;
    }

    public float getSecond() {
        return this.second;
    }

    public String getSelectedFolderId() {
        return this.selectedFolderId;
    }

    public ArrayList<ImageData> getSelectedImages() {
        return this.selectedImages;
    }

    public Uri getUriFromPath(final String s) {
        return Uri.fromFile(new File(s));
    }

    public void initArray() {
        this.videoImages = new ArrayList<String>();
    }

    public boolean isAudioFilterPath(final String s) {
        if (this.mAudioDirPath.equals("")) {
            this.mAudioDirPath = Utils.INSTANCE.getAudioFolderPath();
        }
        return s.contains(this.mAudioDirPath);
    }

    public void onCreate() {
        super.onCreate();
        (MyApplication.instance = this).init();
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

    public void removeSelectedImage(final int n) {
        if (n <= this.selectedImages.size()) {
            final ImageData imageData = this.selectedImages.remove(n);
            --imageData.imageCount;
        }
    }

    public boolean runApp(final String s, final int n) {
        try {
            final Intent intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            for (final ResolveInfo resolveInfo : this.getPackageManager().queryIntentActivities(intent, 0)) {
                if (resolveInfo.loadLabel(this.getPackageManager()).equals(s)) {
                    final Intent launchIntentForPackage = this.getPackageManager().getLaunchIntentForPackage(resolveInfo.activityInfo.applicationInfo.packageName);
                    if (n != 1) {
                        return launchIntentForPackage != null;
                    }
                    this.startActivity(launchIntentForPackage);
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public void setAutostartAppName() {
        if (Build.MANUFACTURER.equals("asus")) {
            Utils.autostart_app_name = "Auto-start Manager";
            return;
        }
        if (Build.MANUFACTURER.equals("Xiaomi")) {
            Utils.autostart_app_name = "Security";
        }
    }

    public void setCurrentTheme(final String s) {
        final SharedPreferences.Editor edit = this.getSharedPreferences("theme", 0).edit();
        edit.putString("current_theme", s);
        edit.commit();
    }

    public void setFrame(final int frame) {
        this.frame = frame;
    }

    public void setMusicData(final MusicData musicData) {
        this.musicData = musicData;
    }

    public void setOnProgressReceiver(final OnProgressReceiver onProgressReceiver) {
        this.onProgressReceiver = onProgressReceiver;
    }

    public void setSecond(final float second) {
        this.second = second;
    }

    public void setSelectedFolderId(final String selectedFolderId) {
        this.selectedFolderId = selectedFolderId;
    }

    public ArrayList<ImageData> sortDescending() {
        Collections.sort(null, Collections.reverseOrder());
        return null;
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
                                            case 100:
                                                activity.startActivity(new Intent(activity, VideoAlbumActivity.class));
                                                activity.finish();
                                                break;

                                            case 101:
                                                activity.startActivity(new Intent(activity, ImageSelectionActivity.class));
                                                activity.finish();
                                                break;

                                            case 102:
                                                GoBack();
                                                break;
                                            case 103:
                                                Intent intent = new Intent(activity, VideoAlbumActivity.class);
                                                intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
                                                activity.startActivity(intent);
                                                activity.finish();
                                                break;
                                            case 105:
                                                GoBackFromImageEditActivity();
                                                break;
                                            case 106:
                                                Intent intenttitle = new Intent(activity, NewTitleActivity.class);
                                                intenttitle.putExtra("ISFROMPREVIEW", isFromPreview);
                                                activity.startActivity(intenttitle);
                                                if (isFromPreview) {
                                                    activity.finish();
                                                }
                                                break;
                                            case 107:
                                                loadDone();
                                                break;
                                            case 108:
                                                isEditModeEnable = true;
                                                activity.onBackPressed();
                                                break;
                                            case 109:
                                                isEditModeEnable = false;
                                                activity.startActivity(new Intent(activity, PreviewActivity.class));
                                                activity.finish();
                                                break;
                                            case 110:
                                                activity.onBackPressed();
                                                break;
                                            case 111:
                                                SongDone();
                                                break;
                                            case 112:
                                                activity.startActivityForResult(new Intent(activity, SongEditActivity.class), 101);
                                                break;

                                            case 113:
                                                onBackDialogFromPreviewActivity();
                                                break;

                                            case 115:
                                                activity.onBackPressed();
                                                break;

                                            case 116:
                                                MyApplication.getInstance().setFrame(0);
                                                Intent intentAlbum = new Intent(activity, LauncherActivity.class);
                                                intentAlbum.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                activity.startActivity(intentAlbum);
                                                activity.finish();
                                                break;
                                            case 117:
                                                activity.startActivity(new Intent(activity, LauncherActivity.class));
                                                activity.finish();
                                                break;
                                            case 118:
                                                if (getAvailableInternalMemorySize() <= 100L) {
                                                    ShowLowSpaceAlert();
                                                } else {
                                                    lockRunnable.pause();
                                                    showSaveDialog();
                                                }
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

    private void GoBack() {
        Intent intent;
        if (activity.getIntent().getExtras().get("KEY").equals("FromVideoAlbum")) {
            if (new Random().nextInt(100) > 50) {
                AppRater.showRateDialog(this);
                AppRater.setCallback(new AppRater.Callback() {
                    @Override
                    public void onCancelClicked() {
                        Intent intent = new Intent(activity, VideoAlbumActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXTRA_FROM_VIDEO", true);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onNoClicked() {
                    }

                    @Override
                    public void onYesClicked() {
                    }
                });
                return;
            }
            intent = new Intent(this, VideoAlbumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
            activity.startActivity(intent);
            activity.finish();
        } else if (activity.getIntent().getExtras().get("KEY").equals("FromProgress")) {
            intent = new Intent(this, VideoAlbumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
            intent.putExtra("KEY", VideoAlbumActivity.EXTRA_FROM_VIDEO);
            activity.startActivity(intent);
            activity.finish();
        } else if (activity.getIntent().getExtras().get("KEY").equals("FromNotify")) {
            intent = new Intent(this, LauncherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(VideoAlbumActivity.EXTRA_FROM_VIDEO, true);
            activity.startActivity(intent);
            activity.finish();
        } else {
            activity.onBackPressed();
        }
    }

    private void GoBackFromImageEditActivity() {
        ImageEditActivity.isEdit = false;
        if (isFromPreview && !isFromCameraNotification) {
            addTitleAlert();
        }
        if (isFromCameraNotification) {
            Intent intent = new Intent(this, ImageSelectionActivity.class);
            intent.putExtra("isFromImageEditActivity", true);
            activity.startActivity(intent);
            activity.finish();
            return;
        }
        onBackDialog();
    }

    private void addTitleAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setTitle(R.string.add_story_title);
        builder.setMessage(R.string.do_you_want_to_add_title_frame_);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int n) {
                isEditModeEnable = false;
                final Intent intent = new Intent(activity, NewTitleActivity.class);
                intent.putExtra("ISFROMPREVIEW", isFromPreview);
                activity.startActivity(intent);
                if (isFromPreview) {
                    activity.finish();
                }
            }
        });
        builder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                done();
            }
        });
        builder.show();
    }

    private void done() {
        isEditModeEnable = false;
        if (isFromPreview) {
            activity.setResult(-1);
            activity.finish();
            return;
        }
        activity.startActivity(new Intent(activity, PreviewActivity.class));
    }

    private void onBackDialog() {
        new AlertDialog.Builder(this, R.style.Theme_MovieMaker_AlertDialog).setTitle(R.string.app_name).setMessage(R.string.your_changes_on_images_will_be_removed_are_you_sure_to_go_back_).setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int n) {
                if (isFromPreview && !isFromCameraNotification) {
                    done();
                    return;
                }
                final Intent intent = new Intent(activity, ImageSelectionActivity.class);
                intent.putExtra("isFromImageEditActivity", true);
                activity.startActivity(intent);
                activity.finish();
            }
        }).setNegativeButton(R.string.stay_here, null).create().show();
    }


    private boolean loadDone() {
        if (isFromPreview) {
            activity.setResult(-1);
            activity.finish();
            return true;
        } else {
            Intent intent = new Intent(activity, ImageEditActivity.class);
            intent.putExtra("isFromCameraNotification", false);
            intent.putExtra("KEY", "FromImageSelection");
            activity.startActivity(intent);
        }
        return false;
    }

    private void SongDone() {
        setMusic();
        activity.setResult(-1, new Intent(activity, PreviewActivity.class));
        activity.finish();
    }

    private void setMusic() {
        try {
            FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
            final File file = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
            if (file.exists()) {
                FileUtils.deleteFile(file);
                file.createNewFile();
            }
            final FileInputStream fileInputStream = new FileInputStream(new File(OnlineMusicActivtiy.mMusicDatas.get(OnlineMusicActivtiy.mAdapter.checked).track_data));
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final byte[] array = new byte[1024];
            while (true) {
                final int read = fileInputStream.read(array);
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(array, 0, read);
            }
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.prepare();
            final MusicData musicData = new MusicData();
            musicData.track_data = file.getAbsolutePath();
            mediaPlayer.setOnPreparedListener((MediaPlayer.OnPreparedListener) new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    musicData.track_duration = mediaPlayer.getDuration();
                    mediaPlayer.stop();
                }
            });
            musicData.track_Title = "temp";
            setMusicData(musicData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onBackDialogFromPreviewActivity() {
        new AlertDialog.Builder(this, R.style.Theme_MovieMaker_AlertDialog).setTitle(R.string.app_name).setMessage(R.string.are_you_sure_your_video_is_not_prepared_yet_).setPositiveButton(R.string.yes_cap, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                videoImages.clear();
                MyApplication.isBreak = true;
                ((NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE)).cancel(1001);
                activity.finish();
            }
        }).setNegativeButton(R.string.stay_here, null).create().show();
    }


    private static long getAvailableInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return (((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) / 1048576L;
    }

    private void ShowLowSpaceAlert() {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        alertDialog$Builder.setTitle(this.getString(R.string.low_storage_alert));
        alertDialog$Builder.setMessage(this.getString(R.string.low_storage_alert_disc));
        alertDialog$Builder.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                activity.startActivityForResult(new Intent("android.settings.SETTINGS"), 0);
            }
        });
        alertDialog$Builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
            }
        });
        alertDialog$Builder.show();
    }

    private void showSaveDialog() {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder((Context) activity, R.style.Theme_MovieMaker_AlertDialog);
        final EditText view = new EditText((Context) this);
        view.setHint(R.string.enter_story_name);
        view.setTextColor(-1);
        alertDialog$Builder.setTitle(R.string.enter_video_name);
        alertDialog$Builder.setView((View) view);
        alertDialog$Builder.setPositiveButton(R.string.save, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                Log.e("TAG", "Save Video");
                final StringBuilder sb = new StringBuilder();
                sb.append((CharSequence) view.getText());
                strVideoName = sb.toString();
                if (strVideoName.equals("")) {
                    Toast.makeText((Context) activity, R.string.please_enter_story_name_, Toast.LENGTH_SHORT).show();
                    view.setFocusable(true);
                    view.setSelected(true);
                    return;
                }
                final File file = new File(FileUtils.APP_DIRECTORY.getAbsolutePath());
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(String.valueOf(strVideoName));
                sb2.append(".mp4");
                if (new File(file, sb2.toString()).exists()) {
                    Toast.makeText((Context) activity, R.string.video_name_already_exist_, Toast.LENGTH_SHORT).show();
                    view.setFocusable(true);
                    view.setSelected(true);
                    return;
                }
                handler.removeCallbacks((Runnable) lockRunnable);
                ((Dialog) dialogInterface).getWindow().setSoftInputMode(2);
                loadProgressActivity();
                dialogInterface.dismiss();

            }
        });
        alertDialog$Builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog create = alertDialog$Builder.create();
        ((Dialog) create).getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimationForVideoTitleDailog;
        final long currentTimeMillis = System.currentTimeMillis();
        final Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        final StringBuilder sb = new StringBuilder();
        sb.append("PhotoSlideshow_");
        sb.append(new SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(instance.getTime()));
        sb.append(".mp4");
        view.setText((CharSequence) sb.toString());
        view.selectAll();
        view.requestFocus();
        ((Dialog) create).getWindow().setSoftInputMode(4);
        ((Dialog) create).show();
    }

    private void loadProgressActivity() {
        final Intent intent = new Intent(this, CreateVideoService.class);
        if (!strVideoName.endsWith(".mp4")) {
            final StringBuilder sb = new StringBuilder();
            sb.append(strVideoName);
            sb.append(".mp4");
            strVideoName = sb.toString();
        }
        isFromSdCardAudio = false;
        Log.e("TAG", "Save Video ===");
        intent.putExtra("VideoName", strVideoName);
        activity.startService(intent);

        final Intent intent2 = new Intent(activity, ProgressActivity.class);
        intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent2);
        activity.finish();
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
