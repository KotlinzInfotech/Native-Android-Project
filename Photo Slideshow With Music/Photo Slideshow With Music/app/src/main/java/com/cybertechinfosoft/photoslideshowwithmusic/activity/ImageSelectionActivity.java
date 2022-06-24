package com.cybertechinfosoft.photoslideshowwithmusic.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.AlbumAdapterById;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.ImageByAlbumAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.OnItemClickListner;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.SelectedImageAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.EmptyRecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.view.ExpandIconView;
import com.cybertechinfosoft.photoslideshowwithmusic.view.VerticalSlidingPanel;
import com.cybertechinfosoft.photoslideshowwithmusic.view.VerticalSlidingPanel.PanelSlideListener;
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
import static com.cybertechinfosoft.photoslideshowwithmusic.NativeAds.nativeads.populateUnifiedNativeAdView;

public class ImageSelectionActivity extends AppCompatActivity implements PanelSlideListener {

    Activity activity = ImageSelectionActivity.this;
    public static final String EXTRA_FROM_PREVIEW = "extra_from_preview";
    public static boolean isForFirst = false;
    public static ArrayList<ImageData> tempImage = new ArrayList();
    private AlbumAdapterById albumAdapter;
    private ImageByAlbumAdapter albumImagesAdapter;
    private MyApplication application;
    private Button btnClear;
    private ExpandIconView expandIcon;
    public boolean isFromCameraNotification = false;
    public boolean isFromPreview = false;
    boolean isPause = false;
    private VerticalSlidingPanel panel;
    private View parent;
    private RecyclerView rvAlbum;
    private RecyclerView rvAlbumImages;
    private EmptyRecyclerView rvSelectedImage;
    private SelectedImageAdapter selectedImageAdapter;
    private Toolbar toolbar;
    private TextView tvImageCount;

    private NativeAd nativeAd;


    public void onPanelAnchored(View view) {
    }

    public void onPanelShown(View view) {
    }

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.image_select_activity);
        this.application = MyApplication.getInstance();
        this.isFromPreview = getIntent().hasExtra("extra_from_preview");
        this.isFromCameraNotification = getIntent().hasExtra("isFromCameraNotification");
        MyApplication.isFromPreviewImageSelection = isFromPreview;
        bindView();
        init();
        addListner();
        loadAd();
        PutAnalyticsEvent();
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageSelectionActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    public void scrollToPostion(final int i) {
        this.rvAlbum.postDelayed(new Runnable() {
            public void run() {
                ImageSelectionActivity.this.rvAlbum.scrollToPosition(i);
            }
        }, 300);
    }

    private void init() {
        setSupportActionBar(this.toolbar);
        TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText(getString(R.string.select_images));
        Utils.setFont(this, textView);
        if (this.isFromCameraNotification) {
            this.application.getFolderList();
        }
        this.albumAdapter = new AlbumAdapterById(this);
        this.albumImagesAdapter = new ImageByAlbumAdapter(this);
        this.selectedImageAdapter = new SelectedImageAdapter(this);
        this.rvAlbum.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        this.rvAlbum.setHasFixedSize(true);
        this.rvAlbum.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.rvAlbum.setItemAnimator(new DefaultItemAnimator());
        this.rvAlbum.setAdapter(this.albumAdapter);
        this.rvAlbumImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        this.rvAlbumImages.setItemAnimator(new DefaultItemAnimator());
        this.rvAlbumImages.setAdapter(this.albumImagesAdapter);
        this.rvSelectedImage.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        this.rvSelectedImage.setItemAnimator(new DefaultItemAnimator());
        this.rvSelectedImage.setAdapter(this.selectedImageAdapter);
        this.rvSelectedImage.setEmptyView(findViewById(R.id.list_empty));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.tvImageCount.setText(String.valueOf(this.application.getSelectedImages().size()));
    }

    private void bindView() {
        this.tvImageCount = (TextView) findViewById(R.id.tvImageCount);
        this.expandIcon = (ExpandIconView) findViewById(R.id.settings_drag_arrow);
        this.rvAlbum = (RecyclerView) findViewById(R.id.rvAlbum);
        this.rvAlbumImages = (RecyclerView) findViewById(R.id.rvImageAlbum);
        this.rvSelectedImage = (EmptyRecyclerView) findViewById(R.id.rvSelectedImagesList);
        this.panel = (VerticalSlidingPanel) findViewById(R.id.overview_panel);
        this.panel.setEnableDragViewTouchEvents(true);
        this.panel.setDragView(findViewById(R.id.settings_pane_header));
        this.panel.setPanelSlideListener(this);
        this.parent = findViewById(R.id.default_home_screen_panel);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.btnClear = (Button) findViewById(R.id.btnClear);
        if (this.isFromPreview) {
            btnClear.setVisibility(View.GONE);
        }
    }

    private void addListner() {
        this.btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelectionActivity.this.clearData();
            }
        });
        this.albumAdapter.setOnItemClickListner(new OnItemClickListner() {
            @Override
            public void onItemClick(View view, Object o) {
                ImageSelectionActivity.this.albumImagesAdapter.notifyDataSetChanged();
            }
        });
        this.albumImagesAdapter.setOnItemClickListner(new OnItemClickListner() {
            @Override
            public void onItemClick(View view, Object obj) {
                ImageSelectionActivity.this.tvImageCount.setText(String.valueOf(ImageSelectionActivity.this.application.getSelectedImages().size()));
                ImageSelectionActivity.this.selectedImageAdapter.notifyDataSetChanged();
            }
        });
        this.selectedImageAdapter.setOnItemClickListner(new OnItemClickListner() {
            @Override
            public void onItemClick(View view, Object obj) {
                ImageSelectionActivity.this.tvImageCount.setText(String.valueOf(ImageSelectionActivity.this.application.getSelectedImages().size()));
                ImageSelectionActivity.this.albumImagesAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 999 && i2 == -1) {
            this.application.selectedImages.remove(MyApplication.TEMP_POSITION);
            ImageData imgData = new ImageData();
            imgData.setImagePath(intent.getExtras().getString("ImgPath"));
            this.application.selectedImages.add(MyApplication.TEMP_POSITION, imgData);
            setupAdapter();
        }
    }

    private void setupAdapter() {
        this.selectedImageAdapter = new SelectedImageAdapter(this);
        this.rvSelectedImage.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        this.rvSelectedImage.setItemAnimator(new DefaultItemAnimator());
        this.rvSelectedImage.setAdapter(this.selectedImageAdapter);
        this.rvSelectedImage.setEmptyView(findViewById(R.id.list_empty));
    }

    protected void onResume() {
        super.onResume();
        if (this.isPause) {
            this.isPause = false;
            this.tvImageCount.setText(String.valueOf(this.application.getSelectedImages().size()));
            this.albumImagesAdapter.notifyDataSetChanged();
            this.selectedImageAdapter.notifyDataSetChanged();
        }
    }

    protected void onPause() {
        super.onPause();
        this.isPause = true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_selection, menu);
        if (this.isFromPreview) {
            menu.removeItem(R.id.menu_clear);
        }
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

    private boolean isEndFrameExist() {
        if (EndFrameFrag.lastsaveTempPath == null) {
            return false;
        }
        return new File(EndFrameFrag.lastsaveTempPath).exists();
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId != android.R.id.home) {
            switch (itemId) {
                case R.id.menu_done: {
                    if (this.application.getSelectedImages().size() > 2) {
                        if (MyApplication.isShowAd == 1) {
                            loadDone();
                            MyApplication.isShowAd = 0;
                        } else {
                            if (MyApplication.mInterstitialAd != null) {
                                MyApplication.activity = activity;
                                MyApplication.AdsId = 107;
                                int i = 0;
                                if (this.isEndFrameExist()) {
                                    ImageData imageData = null;
                                    final ArrayList<ImageData> list = new ArrayList<ImageData>();
                                    list.addAll(this.application.selectedImages);
                                    this.application.selectedImages.clear();
                                    while (i < list.size()) {
                                        if (list.get(i).imagePath.equals(EndFrameFrag.lastsaveTempPath)) {
                                            imageData = list.get(i);
                                        } else {
                                            this.application.selectedImages.add(list.get(i));
                                        }
                                        ++i;
                                    }
                                    if (imageData != null) {
                                        this.application.selectedImages.add(imageData);
                                    }
                                }
                                MyApplication.mInterstitialAd.show(activity);
                                MyApplication.isShowAd = 1;

                            } else {
                                loadDone();
                            }
                        }
                        break;
                    }
                    Toast.makeText(this, R.string.select_more_than_2_images_for_create_video, Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.menu_clear: {
                    this.clearData();
                    break;
                }
            }
        } else {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private boolean loadDone() {
        final boolean isFromPreview = this.isFromPreview;
        int i = 0;
        if (isFromPreview) {
            if (this.isEndFrameExist()) {
                ImageData imageData = null;
                final ArrayList<ImageData> list = new ArrayList<ImageData>();
                list.addAll(this.application.selectedImages);
                this.application.selectedImages.clear();
                while (i < list.size()) {
                    if (list.get(i).imagePath.equals(EndFrameFrag.lastsaveTempPath)) {
                        imageData = list.get(i);
                    } else {
                        this.application.selectedImages.add(list.get(i));
                    }
                    ++i;
                }
                if (imageData != null) {
                    this.application.selectedImages.add(imageData);
                }
            }
            this.setResult(-1);
            this.finish();
            return true;
        } else {
            final Intent intent = new Intent(ImageSelectionActivity.this, (Class) ImageEditActivity.class);
            intent.putExtra("isFromCameraNotification", false);
            intent.putExtra("KEY", "FromImageSelection");
            startActivity(intent);
        }
        return false;
    }


    public void onBackPressed() {
        if (this.panel.isExpanded()) {
            this.panel.collapsePane();
        } else if (this.isFromCameraNotification) {
            startActivity(new Intent(this, LauncherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            this.application.clearAllSelection();
            finish();
        } else if (this.isFromPreview) {
            setResult(-1);
            finish();
        } else {
            this.application.videoImages.clear();
            this.application.clearAllSelection();
            startActivity(new Intent(this, LauncherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            super.onBackPressed();
        }
    }

    public void onPanelSlide(final View view, final float n) {
        if (this.expandIcon != null) {
            this.expandIcon.setFraction(n, false);
        }
        if (n >= 0.005f) {
            if (this.parent != null && this.parent.getVisibility() != View.VISIBLE) {
                this.parent.setVisibility(View.VISIBLE);
            }
        } else if (this.parent != null && this.parent.getVisibility() == View.VISIBLE) {
            this.parent.setVisibility(View.GONE);
        }
    }

    public void onPanelCollapsed(View view) {
        if (this.parent != null) {
            this.parent.setVisibility(View.VISIBLE);
        }
        this.selectedImageAdapter.isExpanded = false;
        this.selectedImageAdapter.notifyDataSetChanged();
    }

    public void onPanelExpanded(View view) {
        if (this.parent != null) {
            this.parent.setVisibility(View.GONE);
        }
        this.selectedImageAdapter.isExpanded = true;
        this.selectedImageAdapter.notifyDataSetChanged();
    }

    private void clearData() {
        for (int size = this.application.getSelectedImages().size() - 1; size >= 0; size--) {
            this.application.removeSelectedImage(size);
        }
        this.tvImageCount.setText("0");
        this.selectedImageAdapter.notifyDataSetChanged();
        this.albumImagesAdapter.notifyDataSetChanged();
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
                        if (ImageSelectionActivity.this.nativeAd != null) {
                            ImageSelectionActivity.this.nativeAd.destroy();
                        }
                        ImageSelectionActivity.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView =
                                (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified_small, null);
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
