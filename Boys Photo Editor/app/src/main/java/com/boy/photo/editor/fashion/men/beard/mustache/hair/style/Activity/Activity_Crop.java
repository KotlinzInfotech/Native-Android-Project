package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.theartofdev.edmodo.cropper.CropImageView.CropShape;

import java.io.IOException;

public class Activity_Crop extends AppCompatActivity {

    Activity activity = Activity_Crop.this;
    private Bitmap bitmap;
    public CropImageView cropImageView;
    public static Bitmap bitmap_cropped;
    ImageView Img_backcreation;
    private int angle = 0;
    public ImageView img_rotate;
    ImageView img_save;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        try {
            this.bitmap = Media.getBitmap(getContentResolver(), MainActivity.image_uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
        PutAnalyticsEvent();
        BannerAds();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity_Crop");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) adContainerView.getLayoutParams();
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

    private void initView() {
        this.cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        this.cropImageView.setImageUriAsync(MainActivity.image_uri);
        this.cropImageView.setCropShape(CropShape.RECTANGLE);
        this.img_save = (ImageView) findViewById(R.id.img_save);
        this.Img_backcreation = (ImageView) findViewById(R.id.Img_back_creation);
        this.img_rotate = (ImageView) findViewById(R.id.img_Rotate);
        this.img_rotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Crop.this.angle = 90;
                Activity_Crop.this.bitmap = Activity_Crop.this.rotateImage(
                        Activity_Crop.this.bitmap, (float) Activity_Crop.this.angle);
                Activity_Crop.this.cropImageView
                        .setImageBitmap(Activity_Crop.this.bitmap);
            }
        });
        this.Img_backcreation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Crop.this.finish();
            }
        });
        this.img_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Crop.bitmap_cropped = Activity_Crop.this.cropImageView.getCroppedImage();
             /*   Activity_Crop.this.startActivity(new Intent(Activity_Crop.this, Activity_imageEdit.class));
                finish();*/
                Intent intent = new Intent(getApplicationContext(), Activity_imageEdit.class);
                intent.putExtra("IsFrom", false);
                startActivity(intent);
                finish();
            }
        });

    }

    public Bitmap rotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
