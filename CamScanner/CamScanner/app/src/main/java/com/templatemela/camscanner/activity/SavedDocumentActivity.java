package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.templatemela.camscanner.MyApplication;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.db.DBHelper;
import com.templatemela.camscanner.main_utils.Constant;
import com.templatemela.camscanner.utils.AdmobAds;

public class SavedDocumentActivity extends BaseActivity implements View.OnClickListener {

    Activity activity = SavedDocumentActivity.this;
    private static final String TAG = "SavedDocumentActivity";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyApplication.IdentifyActivity.equals("ScannerActivity_Retake2")) {
                startActivity(new Intent(SavedDocumentActivity.this, ScannerActivity.class));
                MyApplication.IdentifyActivity = "";
                finish();
            } else if (MyApplication.IdentifyActivity.equals("DocumentEditorActivity_Saved")) {
                Intent intent2 = new Intent(SavedDocumentActivity.this, DocumentEditorActivity.class);
                intent2.putExtra("TAG", SavedDocumentActivity.TAG);
                intent2.putExtra("scan_doc_group_name", preview_doc_grp_name);
                intent2.putExtra("current_doc_name", current_doc_name);
                startActivity(intent2);
                MyApplication.IdentifyActivity = "";
                finish();
            }
        }
    };

    public String current_doc_name;

    public DBHelper dataBaseHelper;
    protected ImageView iv_back;
    protected ImageView iv_delete;
    protected ImageView iv_edit;
    private ImageView iv_preview_saved;
    protected ImageView iv_retake;
    protected ImageView iv_rotate;
    private LinearLayout llRetake, llEdit, llRotate, llDelete;

    public String preview_doc_grp_name;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity_Retake2"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_Saved"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_saved_document);
        dataBaseHelper = new DBHelper(this);
        BannerAds();
        init();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SavedDocumentActivity");
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
        iv_preview_saved = findViewById(R.id.iv_preview_saved);

        llRetake = findViewById(R.id.llRetake);
        llEdit = findViewById(R.id.llEdit);
        llRotate = findViewById(R.id.llRotate);
        llDelete = findViewById(R.id.llDelete);

        iv_retake = findViewById(R.id.iv_retake);
        iv_edit = findViewById(R.id.iv_edit);
        iv_rotate = findViewById(R.id.iv_rotate);
        iv_delete = findViewById(R.id.iv_delete);
        if (Constant.original != null) {
            iv_preview_saved.setImageBitmap(Constant.original);
        }
        preview_doc_grp_name = getIntent().getStringExtra("scan_doc_group_name");
        current_doc_name = getIntent().getStringExtra("current_doc_name");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.llDelete:
                final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.delete_document_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                if (AdmobAds.SHOW_ADS) {
                    AdmobAds.loadNativeAds(SavedDocumentActivity.this, null, dialog.findViewById(R.id.admob_native_container), dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }
                dialog.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.inputType.equals("Group")) {
                            dataBaseHelper.deleteGroup(preview_doc_grp_name);
                        } else {
                            dataBaseHelper.deleteSingleDoc(preview_doc_grp_name, current_doc_name);
                        }
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return;
            case R.id.llEdit:
                if (MyApplication.isShowAd == 1) {
                    Intent intent2 = new Intent(SavedDocumentActivity.this, DocumentEditorActivity.class);
                    intent2.putExtra("TAG", SavedDocumentActivity.TAG);
                    intent2.putExtra("scan_doc_group_name", preview_doc_grp_name);
                    intent2.putExtra("current_doc_name", current_doc_name);
                    startActivity(intent2);
                    finish();
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "DocumentEditorActivity_Saved";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {
                        Intent intent2 = new Intent(SavedDocumentActivity.this, DocumentEditorActivity.class);
                        intent2.putExtra("TAG", SavedDocumentActivity.TAG);
                        intent2.putExtra("scan_doc_group_name", preview_doc_grp_name);
                        intent2.putExtra("current_doc_name", current_doc_name);
                        startActivity(intent2);
                        finish();
                    }
                }
                return;
            case R.id.llRetake:
                if (MyApplication.isShowAd == 1) {
                    startActivity(new Intent(SavedDocumentActivity.this, ScannerActivity.class));
                    finish();
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "ScannerActivity_Retake2";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {
                        startActivity(new Intent(SavedDocumentActivity.this, ScannerActivity.class));
                        finish();
                    }
                }
                return;
            case R.id.llRotate:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                iv_preview_saved.setImageBitmap(Constant.original);
                return;
            default:
                return;
        }
    }
}
