package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.main_utils.Constant;
import org.bouncycastle.i18n.TextBundle;


public class ImageToTextActivity extends BaseActivity implements View.OnClickListener {
    Activity activity = ImageToTextActivity.this;
    private static final String TAG = "ImageToTextActivity";
    protected ImageView iv_back;
    protected ImageView iv_copy_txt;
    private ImageView iv_preview_img;
    protected ImageView iv_rescan_img;
    protected ImageView iv_share_txt;

    public ProgressDialog progressDialog;

    public TextView tv_ocr_txt;
    private TextView tv_title;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_img_to_text);
        init();
        bindView();
        BannerAds();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageToTextActivity");
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

    private void init() {
        iv_back = findViewById(R.id.iv_back);
        iv_rescan_img = findViewById(R.id.iv_rescan_img);
        iv_preview_img = findViewById(R.id.iv_preview_img);
        tv_title = findViewById(R.id.tv_title);
        iv_share_txt = findViewById(R.id.iv_share_txt);
        iv_copy_txt = findViewById(R.id.iv_copy_txt);
        tv_ocr_txt = findViewById(R.id.tv_ocr_txt);
    }

    private void bindView() {
        tv_title.setText(getIntent().getStringExtra("group_name"));
        iv_preview_img.setImageBitmap(Constant.original);
        doOCR(Constant.original);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_copy_txt:
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(TextBundle.TEXT_ENTRY, tv_ocr_txt.getText().toString()));
                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return;
            case R.id.iv_rescan_img:
                tv_ocr_txt.setText("");
                doOCR(Constant.original);
                return;
            case R.id.iv_share_txt:
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/*");
                intent.putExtra("android.intent.extra.SUBJECT", "OCR Text");
                intent.putExtra("android.intent.extra.TEXT", tv_ocr_txt.getText().toString());
                startActivity(Intent.createChooser(intent, "Share text using"));
                return;
            default:
                return;
        }
    }

    private void doOCR(final Bitmap bitmap) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, "Processing", "Doing OCR...", true);
        } else {
            progressDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextRecognizer build = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!build.isOperational()) {
                    Log.e(ImageToTextActivity.TAG, "Detector dependencies not loaded yet");
                    return;
                }
                final SparseArray<TextBlock> detect = build.detect(new Frame.Builder().setBitmap(bitmap).build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (detect.size() != 0) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < detect.size(); i++) {
                                sb.append(detect.valueAt(i).getValue());
                                sb.append(" ");
                            }
                            tv_ocr_txt.setText(sb.toString());
                        } else {
                            tv_ocr_txt.setText("No Text Found...");
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
}
