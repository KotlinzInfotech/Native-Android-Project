package com.cybertechinfosoft.photoslideshowwithmusic.activity;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.VideoAlbumAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.data.VideoData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.PermissionModelUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.EmptyRecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.view.SpacesItemDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yalantis.com.sidemenu.model.SlideMenuItem;

import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdView;

public class VideoAlbumActivity extends AppCompatActivity {
    Activity activity = VideoAlbumActivity.this;
    public static final String EXTRA_FROM_VIDEO = "EXTRA_FROM_VIDEO";
    private boolean isFromVideo = false;
    private List<SlideMenuItem> list = new ArrayList();
    private VideoAlbumAdapter mVideoAlbumAdapter;
    private ArrayList<VideoData> mVideoDatas;
    PermissionModelUtil modelUtil;
    private EmptyRecyclerView rvVideoAlbum;
    private Toolbar toolbar;
    ImageView ivback;


    public View view;
    private NativeAd nativeAd;

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, int i2) {
    }

    protected void onCreate(@Nullable Bundle bundle) {
        try {
            sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(FileUtils.APP_DIRECTORY)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(bundle);
        this.isFromVideo = getIntent().hasExtra(EXTRA_FROM_VIDEO);
        setContentView(R.layout.activity_movie_album);
        bindView();
        setActionBar();
        init();
        setUpRecyclerView();
        addListener();
        loadAd();
        PutAnalyticsEvent();
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "VideoAlbumActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
                        if (VideoAlbumActivity.this.nativeAd != null) {
                            VideoAlbumActivity.this.nativeAd.destroy();
                        }
                        VideoAlbumActivity.this.nativeAd = nativeAd;
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


    protected void onResume() {
        super.onResume();
        if (this.mVideoAlbumAdapter != null) {
            this.mVideoAlbumAdapter.notifyDataSetChanged();
        }
    }

    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivback = findViewById(R.id.ivback);
        rvVideoAlbum = (EmptyRecyclerView) findViewById(R.id.rvVideoAlbum);
    }

    private void init() {
        getVideoList();
        this.modelUtil = new PermissionModelUtil(this);
        if (this.modelUtil.needPermissionCheck()) {
            this.modelUtil.showPermissionExplanationThenAuthorization();
        } else {
            MyApplication.getInstance().getFolderList();
        }
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(1001);
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(getString(R.string.my_creation));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Utils.setFont(this, textView);
        ivback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        this.mVideoAlbumAdapter = new VideoAlbumAdapter(this, this.mVideoDatas);
        this.rvVideoAlbum.setLayoutManager(gridLayoutManager);
        this.rvVideoAlbum.setItemAnimator(new DefaultItemAnimator());
        this.rvVideoAlbum.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.spacing)));
        this.rvVideoAlbum.setEmptyView(findViewById(R.id.list_empty));
        this.rvVideoAlbum.setAdapter(this.mVideoAlbumAdapter);
    }

    private void getVideoList() {
        this.mVideoDatas = new ArrayList();
        String[] strArr = new String[]{"_data", "_id", "bucket_display_name", "duration", "title", "datetaken", "_display_name"};
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_data like '%");
        stringBuilder.append(FileUtils.APP_DIRECTORY.getAbsolutePath());
        stringBuilder.append("%'");
        Cursor query = contentResolver.query(uri, strArr, stringBuilder.toString(), null, "datetaken DESC");
        if (query.moveToFirst()) {
            int columnIndex = query.getColumnIndex("duration");
            int columnIndex2 = query.getColumnIndex("_data");
            int columnIndex3 = query.getColumnIndex("title");
            int columnIndex4 = query.getColumnIndex("datetaken");
            do {
                VideoData videoData = new VideoData();
                videoData.videoDuration = query.getLong(columnIndex);
                videoData.videoFullPath = query.getString(columnIndex2);
                videoData.videoName = query.getString(columnIndex3);
                videoData.dateTaken = query.getLong(columnIndex4);
                if (new File(videoData.videoFullPath).exists()) {
                    this.mVideoDatas.add(videoData);
                }
            } while (query.moveToNext());
        }
    }


    private void addListener() {
        findViewById(R.id.btnCreateVideo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getInstance().getAllFolder().size() > 0) {
                    MyApplication.isStoryAdded = false;
                    MyApplication.isBreak = false;
                    MyApplication.getInstance().setMusicData(null);
                    ActivityAnimUtil.startActivitySafely(view, new Intent(VideoAlbumActivity.this, ImageSelectionActivity.class));
                    VideoAlbumActivity.this.finish();
                    return;
                }
                Toast.makeText(VideoAlbumActivity.this.getApplicationContext(), R.string.no_images_found_in_device_please_add_images_in_sdcard, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.list_empty).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getInstance().getAllFolder().size() > 0) {
                    MyApplication.isStoryAdded = false;
                    MyApplication.isBreak = false;
                    MyApplication.getInstance().setMusicData(null);
                    ActivityAnimUtil.startActivitySafely(view, new Intent(VideoAlbumActivity.this, ImageSelectionActivity.class));
                    VideoAlbumActivity.this.finish();
                    return;
                }
                Toast.makeText(VideoAlbumActivity.this.getApplicationContext(), R.string.no_images_found_in_device_please_add_images_in_sdcard, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        if (MyApplication.isShowAd == 1) {
            MyApplication.getInstance().setFrame(0);
            Intent intent = new Intent(activity, LauncherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            ActivityAnimUtil.startActivitySafely(toolbar, intent);
            finish();
            MyApplication.isShowAd = 0;
        } else {
            if (MyApplication.mInterstitialAd != null) {
                MyApplication.activity = activity;
                MyApplication.AdsId = 116;
                MyApplication.mInterstitialAd.show(activity);
                MyApplication.isShowAd = 1;

            } else {
                MyApplication.getInstance().setFrame(0);
                Intent intent = new Intent(activity, LauncherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ActivityAnimUtil.startActivitySafely(toolbar, intent);
                finish();
            }
        }
    }


    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

}
