package com.cybertechinfosoft.photoslideshowwithmusic.activity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.ImageEditAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.OnItemClickListner;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ImageEditor;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.EmptyRecyclerView;
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
import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdView;

public class ImageEditActivity extends AppCompatActivity {
    public static final String EXTRA_FROM_PREVIEW = "extra_from_preview";
    public static boolean isEdit = false;
    Callback _ithCallback = new Callback() {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }


        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i == 0) {
                ImageEditActivity.this.imageEditAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onMoved(final RecyclerView recyclerView, final RecyclerView.ViewHolder recyclerView$ViewHolder, final int n, final RecyclerView.ViewHolder recyclerView$ViewHolder2, final int n2, final int n3, final int n4) {
            if (MyApplication.isStoryAdded && ImageEditActivity.this.isStartFrameExist() && n == 0) {
                return;
            }
            if (MyApplication.isStoryAdded && ImageEditActivity.this.isEndFrameExist() && n == ImageEditActivity.this.application.selectedImages.size() - 1) {
                return;
            }
            if (MyApplication.isStoryAdded && ImageEditActivity.this.isStartFrameExist() && n2 == 0) {
                return;
            }
            if (MyApplication.isStoryAdded && ImageEditActivity.this.isEndFrameExist() && n2 == ImageEditActivity.this.application.selectedImages.size() - 1) {
                return;
            }
            ImageEditActivity.this.imageEditAdapter.swap(recyclerView$ViewHolder.getAdapterPosition(), recyclerView$ViewHolder2.getAdapterPosition());
            ImageEditActivity.this.application.min_pos = Math.min(ImageEditActivity.this.application.min_pos, Math.min(n, n2));
            MyApplication.isBreak = true;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeFlag(2, 51);
        }
    };
    Activity activity = ImageEditActivity.this;
    private MyApplication application;
    private ImageEditAdapter imageEditAdapter;
    private boolean isFromCameraNotification;
    public boolean isFromPreview = false;
    private EmptyRecyclerView rvSelectedImages;
    String tempImgPath;
    private Toolbar toolbar;

    private NativeAd nativeAd;


    private void addListener() {
    }

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_arrange_images);
        if (Utils.checkPermission(this)) {
            this.isFromPreview = getIntent().hasExtra("extra_from_preview");
            MyApplication.isFromPreview = isFromPreview;
            this.application = MyApplication.getInstance();
            this.application.isEditModeEnable = true;
            bindView();
            init();
            addListener();
            loadAd();
            return;
        }
        PutAnalyticsEvent();
        Intent intent = new Intent(this, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void bindView() {
        this.rvSelectedImages = (EmptyRecyclerView) findViewById(R.id.rvVideoAlbum);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageEditActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void init() {
        setupAdapter();
//        new ItemTouchHelper(this._ithCallback).attachToRecyclerView(this.rvSelectedImages);
        setSupportActionBar(this.toolbar);
        TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText(getString(R.string.swap_images));
        Utils.setFont(this, textView);
        this.isFromCameraNotification = getIntent().getExtras().getBoolean("isFromCameraNotification");
        MyApplication.isFromCameraNotification = isFromCameraNotification;
        if (getIntent().getExtras().getString("KEY").equals("FromImageSelection") || getIntent().getExtras().getString("KEY").equals("FromCameraService") || getIntent().getExtras().getString("KEY").equals("FromPreview")) {
            isEdit = true;
        }
    }

    private void setupAdapter() {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        this.imageEditAdapter = new ImageEditAdapter(this);
        this.rvSelectedImages.setLayoutManager(gridLayoutManager);
        this.rvSelectedImages.setItemAnimator(new DefaultItemAnimator());
        this.rvSelectedImages.setEmptyView(findViewById(R.id.list_empty));
        this.rvSelectedImages.setAdapter(this.imageEditAdapter);
        this.imageEditAdapter.setOnItemClickListner(new OnItemClickListner() {
            @Override
            public void onItemClick(View view, Object obj) {
                Integer.parseInt((String) view.getTag());
            }
        });
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == ImageEditor.IMAGE_EDIT_ACTIVITY_TEXT_AND_STICKER && i2 == -1) {
            this.application.selectedImages.remove(MyApplication.TEMP_POSITION);
            ImageData idata = new ImageData();
            idata.setImagePath(intent.getExtras().getString("ImgPath"));
            this.application.selectedImages.add(MyApplication.TEMP_POSITION, idata);
            setupAdapter();
        }
    }

    protected void onResume() {
        super.onResume();
        if (Utils.checkPermission(this)) {
            this.application.isEditModeEnable = true;
            if (this.imageEditAdapter != null) {
                this.imageEditAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        Intent intent = new Intent(this, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private boolean isEndFrameExist() {
        return EndFrameFrag.lastsaveTempPath != null ? new File(EndFrameFrag.lastsaveTempPath).exists() : false;
    }

    private boolean isStartFrameExist() {
        return new File(StartFrameFrag.lastsaveTempPath).exists();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_selection, menu);
        menu.removeItem(R.id.menu_clear);
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
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.menu_done) {
            if (MyApplication.isShowAd == 1) {
                addTitle();
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.AdsId = 106;
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;

                } else {
                    addTitle();
                }
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void done() {
        this.application.isEditModeEnable = false;
        if (this.isFromPreview) {
            setResult(-1);
            finish();
            return;
        }
        ActivityAnimUtil.startActivitySafely(this.toolbar, new Intent(this, PreviewActivity.class));
    }

    private void addTitle() {
        Intent intent = new Intent(this, NewTitleActivity.class);
        intent.putExtra("ISFROMPREVIEW", this.isFromPreview);
        ActivityAnimUtil.startActivitySafely(this.toolbar, intent);
        if (this.isFromPreview) {
            finish();
        }
    }

    public void onBackPressed() {
        if (MyApplication.isShowAd == 1) {
            GoBack();
            MyApplication.isShowAd = 0;
        } else {
            if (MyApplication.mInterstitialAd != null) {
                MyApplication.activity = activity;
                MyApplication.AdsId = 105;
                MyApplication.mInterstitialAd.show(activity);
                MyApplication.isShowAd = 1;

            } else {
                GoBack();
            }
        }
    }

    private void GoBack() {
        isEdit = false;
        if (this.isFromPreview && !this.isFromCameraNotification) {
            addTitleAlert();
        }
        if (this.isFromCameraNotification) {
            Intent intent = new Intent(this, ImageSelectionActivity.class);
            intent.putExtra("isFromImageEditActivity", true);
            startActivity(intent);
            finish();
            return;
        }
        onBackDialog();
    }

    private void addTitleAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setTitle(R.string.add_story_title);
        builder.setMessage(R.string.do_you_want_to_add_title_frame_);
        builder.setPositiveButton(R.string.yes, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int n) {
                ImageEditActivity.this.application.isEditModeEnable = false;
                final Intent intent = new Intent((Context) ImageEditActivity.this, (Class) NewTitleActivity.class);
                intent.putExtra("ISFROMPREVIEW", ImageEditActivity.this.isFromPreview);
                ActivityAnimUtil.startActivitySafely((View) ImageEditActivity.this.toolbar, intent);
                if (ImageEditActivity.this.isFromPreview) {
                    ImageEditActivity.this.finish();
                }
            }
        });
        builder.setNegativeButton(R.string.skip, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ImageEditActivity.this.done();
            }
        });
        builder.show();
    }

    private void onBackDialog() {
        new AlertDialog.Builder(this, R.style.Theme_MovieMaker_AlertDialog).setTitle(R.string.app_name).setMessage(R.string.your_changes_on_images_will_be_removed_are_you_sure_to_go_back_).setPositiveButton(R.string.go_back, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int n) {
                if (ImageEditActivity.this.isFromPreview && !ImageEditActivity.this.isFromCameraNotification) {
                    ImageEditActivity.this.done();
                    return;
                }
                final Intent intent = new Intent((Context) ImageEditActivity.this, (Class) ImageSelectionActivity.class);
                intent.putExtra("isFromImageEditActivity", true);
                ImageEditActivity.this.startActivity(intent);
                ImageEditActivity.this.finish();
            }
        }).setNegativeButton(R.string.stay_here, null).create().show();
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
                        if (ImageEditActivity.this.nativeAd != null) {
                            ImageEditActivity.this.nativeAd.destroy();
                        }
                        ImageEditActivity.this.nativeAd = nativeAd;
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
}
