package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ActivityAnimUtil;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.ScrollableViewPager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

public class NewTitleActivity extends AppCompatActivity implements View.OnClickListener {
    Activity activity = NewTitleActivity.this;
    public static ImageView imgCamera;
    public static ImageView imgGallery;
    public static ImageView imgSticker;
    public static ImageView imgText;
    public static boolean isFilterApplied;
    public static boolean isFrameChanged;
    public static boolean isFromPreview;
    public static boolean isStickerAdded;
    public static boolean isTextAdded;
    public static int selectedFramePos;
    private PagerAdapter adapter;
    private MyApplication application;
    private int currentTabPos;
    boolean isDone;
    boolean isSavePress;
    boolean isStartSaveTemp;
    DialogInterface.OnDismissListener onDismissListener;
    private ScrollableViewPager pager;
    public TabLayout.OnTabSelectedListener tabChangeListener;
    private TabLayout tbLayout;
    private Toolbar toolbar;


    public NewTitleActivity() {
        this.currentTabPos = 0;
        this.isStartSaveTemp = true;
        this.isDone = false;
        this.onDismissListener = (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
            public void onDismiss(final DialogInterface dialogInterface) {
                NewTitleActivity.this.tbLayout.setOnTabSelectedListener((TabLayout.OnTabSelectedListener) null);
                NewTitleActivity.this.tbLayout.getTabAt(NewTitleActivity.this.currentTabPos).select();
                NewTitleActivity.this.tbLayout.setOnTabSelectedListener(NewTitleActivity.this.tabChangeListener);
            }
        };
        this.isSavePress = true;
        this.tabChangeListener = (TabLayout.OnTabSelectedListener) new TabLayout.OnTabSelectedListener() {
            public void onTabReselected(final TabLayout.Tab tabLayout$Tab) {
            }

            public void onTabSelected(final TabLayout.Tab tabLayout$Tab) {
                isDone = false;
                NewTitleActivity.this.pager.setCurrentItem(tabLayout$Tab.getPosition());
                NewTitleActivity.this.currentTabPos = tabLayout$Tab.getPosition();
                NewTitleActivity.setStartFrameState(R.id.imgEditGallery);
                if (currentTabPos == 0) {
                    if (StartFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                        StartFrameFrag.bottomSheet.setVisibility(View.GONE);
                    }
                    StartFrameFrag.getOnSaveStoryTitle().setFrameVisibility(0);
                    return;
                }
                if (EndFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    EndFrameFrag.bottomSheet.setVisibility(View.GONE);
                }
                EndFrameFrag.getOnSaveStoryTitle().setFrameVisibility(0);
            }

            public void onTabUnselected(final TabLayout.Tab tabLayout$Tab) {
            }
        };
    }

    private void addListener() {
        this.tbLayout.setOnTabSelectedListener(this.tabChangeListener);
        findViewById(R.id.imgEditCamera).setOnClickListener(this);
        imgGallery.setOnClickListener(this);
        findViewById(R.id.imgEditText).setOnClickListener(this);
        findViewById(R.id.imgEditSticker).setOnClickListener(this);
    }


    private void bindView() {
        this.pager = (ScrollableViewPager) findViewById(R.id.vpPager);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.tbLayout = (TabLayout) findViewById(R.id.tblFrames);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NewTitleActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void init() {
        NewTitleActivity.isFromPreview = this.getIntent().getExtras().getBoolean("ISFROMPREVIEW");
        if (MyApplication.isStoryAdded) {
            MyApplication.isStartSave = false;
            MyApplication.isEndSave = false;
            if (StartFrameFrag.lastsaveTempPath != null) {
                MyApplication.isStartRemoved = (this.isStartFrameExist() ^ true);
            } else {
                MyApplication.isStartRemoved = true;
            }
            if (EndFrameFrag.lastsaveTempPath != null) {
                MyApplication.isLastRemoved = (this.isEndFrameExist() ^ true);
            } else {
                MyApplication.isLastRemoved = true;
            }
        }
        this.setSupportActionBar(this.toolbar);
        TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText(getString(R.string.title_activity_add_title));
        Utils.setFont(this, textView);
        this.application = MyApplication.getInstance();
        this.pager.setCanScroll(false);
        this.tbLayout.addTab(this.tbLayout.newTab().setText(R.string.start));
        this.tbLayout.addTab(this.tbLayout.newTab().setText(R.string.end));
        this.adapter = new PagerAdapter(getSupportFragmentManager());
        this.pager.setAdapter(this.adapter);
        imgGallery = (ImageView) findViewById(R.id.imgEditGallery);
        imgCamera = (ImageView) findViewById(R.id.imgEditCamera);
        imgText = (ImageView) findViewById(R.id.imgEditText);
        imgSticker = (ImageView) findViewById(R.id.imgEditSticker);
        imgGallery.setSelected(true);
    }

    private boolean isEndFrameExist() {
        return EndFrameFrag.lastsaveTempPath != null && new File(EndFrameFrag.lastsaveTempPath).exists();
    }

    private boolean isStartFrameExist() {
        return StartFrameFrag.lastsaveTempPath != null && new File(StartFrameFrag.lastsaveTempPath).exists();
    }


    private void resetBool() {
        NewTitleActivity.isFilterApplied = false;
        NewTitleActivity.isStickerAdded = false;
        NewTitleActivity.isFrameChanged = false;
        NewTitleActivity.isTextAdded = false;
    }

    private void saveBothStartAndEndFrame() {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder((Context) this, R.style.AppAlertDialog);
        alertDialog$Builder.setTitle((CharSequence) this.getString(R.string.save_frame_dialog));
        alertDialog$Builder.setMessage((CharSequence) this.getString(R.string.save_msg_start));
        alertDialog$Builder.setOnDismissListener(this.onDismissListener);
        alertDialog$Builder.setPositiveButton(R.string.apply, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                final ProgressDialog progressDialog = new ProgressDialog((Context) NewTitleActivity.this, R.style.Theme_MovieMaker_AlertDialog);
                progressDialog.setCancelable(false);
                progressDialog.setMessage((CharSequence) NewTitleActivity.this.getString(R.string.creating_your_preview_));
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.TEMP_IMG_DIRECTORY.mkdirs();
                        final long currentTimeMillis = System.currentTimeMillis();
                        final StringBuilder sb = new StringBuilder();
                        sb.append("NewTitle save start frame ");
                        sb.append(currentTimeMillis);
                        StartFrameFrag.isSavingDone = false;
                        StartFrameFrag.getOnSaveStoryTitle().onSaveImageNew();
                        MyApplication.isStartSave = true;
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("NewTitle save start frame ");
                        sb2.append(System.currentTimeMillis() - currentTimeMillis);
                        EndFrameFrag.isSavingDone = false;
                        EndFrameFrag.getOnSaveStoryTitle().onSaveImageNew();
                        MyApplication.isEndSave = true;
                        final StringBuilder sb3 = new StringBuilder();
                        sb3.append("NewTitle save end frame ");
                        sb3.append(System.currentTimeMillis() - currentTimeMillis);
                        NewTitleActivity.this.updateVideoFramesNew(MyApplication.isStartSave, MyApplication.isEndSave);
                        final StringBuilder sb4 = new StringBuilder();
                        sb4.append("NewTitle save dismiss ");
                        sb4.append(System.currentTimeMillis() - currentTimeMillis);
                        NewTitleActivity.this.resetBool();
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });
        alertDialog$Builder.setNegativeButton(R.string.dialog_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                MyApplication.isStartSave = false;
                NewTitleActivity.this.currentTabPos = 1;
                NewTitleActivity.this.tbLayout.setOnTabSelectedListener((TabLayout.OnTabSelectedListener) null);
                NewTitleActivity.this.pager.setCurrentItem(NewTitleActivity.this.currentTabPos);
                NewTitleActivity.this.tbLayout.getTabAt(NewTitleActivity.this.currentTabPos).select();
                NewTitleActivity.this.tbLayout.setOnTabSelectedListener(NewTitleActivity.this.tabChangeListener);
                NewTitleActivity.this.resetBool();
                dialogInterface.dismiss();
            }
        });
        alertDialog$Builder.show();
    }

    private void setEndFrameVisility() {
        if (EndFrameFrag.getOnSaveStoryTitle().getFrameVisibility() != 4 && EndFrameFrag.getOnSaveStoryTitle().getFrameVisibility() != 8) {
            EndFrameFrag.getOnSaveStoryTitle().setFrameVisibility(R.id.imgEditGallery);
            setStartFrameState(0);
            return;
        }
        EndFrameFrag.getOnSaveStoryTitle().setFrameVisibility(R.id.imgEditGallery);
        setStartFrameState(R.id.imgEditGallery);
    }

    public static void setStartFrameState(final int n) {
        NewTitleActivity.imgGallery.setSelected(false);
        NewTitleActivity.imgCamera.setSelected(false);
        NewTitleActivity.imgText.setSelected(false);
        NewTitleActivity.imgSticker.setSelected(false);
        switch (n) {
            default: {
            }
            case R.id.imgEditText: {
                NewTitleActivity.imgText.setSelected(true);
                NewTitleActivity.imgText.invalidate();
            }
            case R.id.imgEditSticker: {
                NewTitleActivity.imgSticker.setSelected(true);
                NewTitleActivity.imgSticker.invalidate();
            }
            case R.id.imgEditGallery: {
                NewTitleActivity.imgGallery.setSelected(true);
                NewTitleActivity.imgGallery.invalidate();
            }
            case R.id.imgEditCamera: {
                NewTitleActivity.imgCamera.setSelected(true);
                NewTitleActivity.imgCamera.invalidate();
            }
        }
    }

    private void setStartFrameVisibility() {
        if (StartFrameFrag.getOnSaveStoryTitle().getFrameVisibility() != 4 && StartFrameFrag.getOnSaveStoryTitle().getFrameVisibility() != 8) {
            StartFrameFrag.getOnSaveStoryTitle().setFrameVisibility(R.id.imgEditGallery);
            setStartFrameState(0);
            return;
        }
        StartFrameFrag.getOnSaveStoryTitle().setFrameVisibility(R.id.imgEditGallery);
        setStartFrameState(R.id.imgEditGallery);
    }

    private void updateVideoFramesNew(final boolean b, final boolean b2) {
        final ArrayList<ImageData> list = new ArrayList<ImageData>();
        if (MyApplication.isStoryAdded) {
            if (!MyApplication.isStartRemoved && !MyApplication.isLastRemoved) {
                this.application.removeSelectedImage(0);
                MyApplication.isStartRemoved = true;
                this.application.removeSelectedImage(this.application.selectedImages.size() - 1);
                MyApplication.isLastRemoved = true;
                MyApplication.isStoryAdded = false;
            } else {
                if (!MyApplication.isStartRemoved) {
                    this.application.removeSelectedImage(0);
                    MyApplication.isStartRemoved = true;
                }
                if (!MyApplication.isLastRemoved) {
                    this.application.removeSelectedImage(this.application.selectedImages.size() - 1);
                    MyApplication.isLastRemoved = true;
                }
                MyApplication.isStoryAdded = false;
            }
        }
        final ImageData imageData = new ImageData();
        final ImageData imageData2 = new ImageData();
        imageData.imagePath = new File(StartFrameFrag.lastsaveTempPath).getAbsolutePath();
        list.add(imageData);
        list.addAll(this.application.getSelectedImages());
        imageData2.imagePath = new File(EndFrameFrag.lastsaveTempPath).getAbsolutePath();
        list.add(imageData2);
        this.application.selectedImages.removeAll(this.application.selectedImages);
        this.application.selectedImages.addAll(list);
        MyApplication.isStoryAdded = true;
        this.application.isEditModeEnable = false;
        if (NewTitleActivity.isFromPreview) {
            PreviewActivity.getmActivity().finish();
        }
        this.runOnUiThread((Runnable) new Runnable() {
            @Override
            public void run() {
                ActivityAnimUtil.startActivitySafely((View) NewTitleActivity.this.toolbar, new Intent((Context) NewTitleActivity.this, (Class) PreviewActivity.class));
                NewTitleActivity.this.finish();

            }
        });
    }


    public void onBackPressed() {
        if (StartFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
            Utils.slideDown((Context) this, StartFrameFrag.bottomSheet);
            this.setStartFrameVisibility();
            return;
        }
        if (EndFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
            Utils.slideDown((Context) this, EndFrameFrag.bottomSheet);
            this.setEndFrameVisility();
            return;
        }
        super.onBackPressed();
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            default: {
            }
            case R.id.imgEditText: {
                if (StartFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    Utils.slideDown((Context) this, StartFrameFrag.bottomSheet);
                } else if (EndFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    Utils.slideDown((Context) this, EndFrameFrag.bottomSheet);
                }
                setStartFrameState(R.id.imgEditText);
                switch (this.currentTabPos) {
                    default: {
                        return;
                    }
                    case 1: {
                        EndFrameFrag.getOnSaveStoryTitle().onAddTextSticker();
                        return;
                    }
                    case 0: {
                        StartFrameFrag.getOnSaveStoryTitle().onAddTextSticker();
                        return;
                    }
                }
            }
            case R.id.imgEditSticker: {
                switch (this.currentTabPos) {
                    default: {
                        return;
                    }
                    case 1: {
                        EndFrameFrag.getOnSaveStoryTitle().onAddImageSticker();
                        return;
                    }
                    case 0: {
                        StartFrameFrag.getOnSaveStoryTitle().onAddImageSticker();
                        return;
                    }
                }
            }
            case R.id.imgEditGallery: {
                if (StartFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    Utils.slideDown((Context) this, StartFrameFrag.bottomSheet);
                } else if (EndFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    Utils.slideDown((Context) this, EndFrameFrag.bottomSheet);
                }
                switch (this.currentTabPos) {
                    default: {
                        return;
                    }
                    case 1: {
                        this.setEndFrameVisility();
                        return;
                    }
                    case 0: {
                        this.setStartFrameVisibility();
                        return;
                    }
                }
            }
            case R.id.imgEditCamera: {
                setStartFrameState(0);
                if (StartFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    Utils.slideDown((Context) this, StartFrameFrag.bottomSheet);
                } else if (EndFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
                    Utils.slideDown((Context) this, EndFrameFrag.bottomSheet);
                }
                switch (this.currentTabPos) {
                    default: {
                        return;
                    }
                    case 1: {
                        EndFrameFrag.getOnSaveStoryTitle().onAddCameraImage();
                        EndFrameFrag.getOnSaveStoryTitle().setFrameVisibility(R.id.imgEditCamera);
                        return;
                    }
                    case 0: {
                        StartFrameFrag.getOnSaveStoryTitle().onAddCameraImage();
                        StartFrameFrag.getOnSaveStoryTitle().setFrameVisibility(R.id.imgEditCamera);
                        return;
                    }
                }
            }
        }
    }

    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_title);
        if (Utils.checkPermission((Context) this)) {
            bindView();
            init();
            addListener();
            return;
        }
        PutAnalyticsEvent();
        final Intent intent = new Intent((Context) this, (Class) LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.story_title, menu);
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);
            final SubMenu subMenu = item.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); ++j) {
                    Utils.applyFontToMenuItem(this.getApplicationContext(), subMenu.getItem(j));
                }
            }
            Utils.applyFontToMenuItem(this.getApplicationContext(), item);
        }
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId != android.R.id.home) {
            if (itemId != R.id.action_done) {
                if (itemId == R.id.action_skip) {
                    if (MyApplication.isShowAd == 1) {
                        application.isEditModeEnable = false;
                        ActivityAnimUtil.startActivitySafely((View) this.toolbar, new Intent((Context) this, (Class) PreviewActivity.class));
                        finish();
                        MyApplication.isShowAd = 0;
                    } else {
                        if (MyApplication.mInterstitialAd != null) {
                            MyApplication.activity = activity;
                            MyApplication.AdsId = 109;
                            MyApplication.mInterstitialAd.show(activity);
                            MyApplication.isShowAd = 1;

                        } else {
                            application.isEditModeEnable = false;
                            ActivityAnimUtil.startActivitySafely((View) this.toolbar, new Intent((Context) this, (Class) PreviewActivity.class));
                            finish();
                        }
                    }
                }
            } else {
                isDone = true;
                saveBothStartAndEndFrame();

            }
        } else {
            if (MyApplication.isShowAd == 1) {
                application.isEditModeEnable = true;
                onBackPressed();
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.AdsId = 108;
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;

                } else {
                    application.isEditModeEnable = true;
                    onBackPressed();
                }
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public int getCount() {
            return 2;
        }

        public Fragment getItem(final int n) {
            switch (n) {
                default: {
                    return null;
                }
                case 1: {
                    return new EndFrameFrag();
                }
                case 0: {
                    return new StartFrameFrag();
                }
            }
        }
    }
}
