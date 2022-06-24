package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.scanlibrary.ScanActivity;
import com.templatemela.camscanner.MyApplication;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.db.DBHelper;
import com.templatemela.camscanner.main_utils.BitmapUtils;
import com.templatemela.camscanner.main_utils.Constant;
import com.templatemela.camscanner.models.DBModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CurrentFilterActivity extends BaseActivity implements View.OnClickListener {

    Activity activity = CurrentFilterActivity.this;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyApplication.IdentifyActivity.equals("SavedDocumentActivity")) {
                Intent intent2 = new Intent(CurrentFilterActivity.this, SavedDocumentActivity.class);
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                MyApplication.IdentifyActivity = "";
                finish();
            } else if (MyApplication.IdentifyActivity.equals("IDCardPreviewActivity2")) {
                startActivity(new Intent(CurrentFilterActivity.this, IDCardPreviewActivity.class));
                MyApplication.IdentifyActivity = "";
                finish();
            }
        }
    };

    public String current_docs_name;

    public DBHelper dataBaseHelper;
    protected ImageView iv_back;

    protected ImageView iv_done;
    protected TextView iv_ocv_black;
    protected TextView iv_original;
    protected TextView iv_color;
    protected TextView iv_sharp_black;

    public PhotoView iv_preview_filter;


    public Bitmap original;
    private ProgressDialog progressDialog;

    public String selected_group_name;

    public Bitmap tempBitmap;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".SavedDocumentActivity"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".IDCardPreviewActivity2"));
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
        setContentView(R.layout.activity_current_filter);
        dataBaseHelper = new DBHelper(this);
        init();
        BannerAds();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CurrentFilterActivity");
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
        iv_done = findViewById(R.id.iv_done);
        iv_preview_filter = findViewById(R.id.iv_preview_filter);
        iv_original = findViewById(R.id.iv_original);
        iv_color = findViewById(R.id.iv_color);
        iv_sharp_black = findViewById(R.id.iv_sharp_black);
        iv_ocv_black = findViewById(R.id.iv_ocv_black);
        if (Constant.original != null) {
            original = Constant.original;
            iv_preview_filter.setImageBitmap(original);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_color:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getMagicColorBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_filter.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_filter.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_color.setTextColor(getResources().getColor(R.color.white));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_done:
                if (!Constant.current_camera_view.equals("ID Card") || !Constant.card_type.equals("Single")) {
                    Constant.original = tempBitmap;
                    if (Constant.original == null) {
                        Constant.original = original;
                    }
                    new insertGroup().execute(Constant.original);
                    return;
                }
                Constant.singleSideBitmap = tempBitmap;
                if (Constant.singleSideBitmap == null) {
                    Constant.singleSideBitmap = original;
                }
                if (MyApplication.isShowAd == 1) {
                    startActivity(new Intent(CurrentFilterActivity.this, IDCardPreviewActivity.class));
                    finish();
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "IDCardPreviewActivity2";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;
                    } else {
                        startActivity(new Intent(CurrentFilterActivity.this, IDCardPreviewActivity.class));
                        finish();
                    }
                }
                return;
            case R.id.iv_ocv_black:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getBWBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_filter.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_filter.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });

                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.white));

                return;
            case R.id.iv_original:
                try {
                    showProgressDialog();
                    tempBitmap = original;
                    iv_preview_filter.setImageBitmap(original);
                    dismissProgressDialog();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }

                iv_original.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_original.setTextColor(getResources().getColor(R.color.white));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_sharp_black:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getGrayBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_filter.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_filter.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.white));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            default:
                return;
        }
    }

    private class insertGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private insertGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CurrentFilterActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = Constant.original;
            if (bitmap == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(bitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "CamScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dataBaseHelper.createDocTable(group_name);
                dataBaseHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dataBaseHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = GroupDocumentActivity.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dataBaseHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = this.current_doc_name;
            if (MyApplication.isShowAd == 1) {
                Intent intent2 = new Intent(CurrentFilterActivity.this, SavedDocumentActivity.class);
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                finish();
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.IdentifyActivity = "SavedDocumentActivity";
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;

                } else {
                    Intent intent2 = new Intent(CurrentFilterActivity.this, SavedDocumentActivity.class);
                    intent2.putExtra("scan_doc_group_name", selected_group_name);
                    intent2.putExtra("current_doc_name", current_docs_name);
                    startActivity(intent2);
                    finish();
                }
            }
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Applying Filter...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
