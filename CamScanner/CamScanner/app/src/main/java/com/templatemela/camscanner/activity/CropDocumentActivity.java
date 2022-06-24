package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.scanlibrary.ScanActivity;
import com.templatemela.camscanner.MyApplication;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.db.DBHelper;
import com.templatemela.camscanner.main_utils.AdjustUtil;
import com.templatemela.camscanner.main_utils.BitmapUtils;
import com.templatemela.camscanner.main_utils.Constant;
import com.templatemela.camscanner.models.DBModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import me.pqpo.smartcropperlib.view.CropImageView;

public class CropDocumentActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    Activity activity = CropDocumentActivity.this;
    private static final String TAG = "CropDocumentActivity";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyApplication.IdentifyActivity.equals("DocumentEditorActivity_Crop")) {
                Intent intent2 = new Intent(CropDocumentActivity.this, DocumentEditorActivity.class);
                intent2.putExtra("TAG", "SavedDocumentActivity");
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                MyApplication.IdentifyActivity = "";
                finish();
            } else if (MyApplication.IdentifyActivity.equals("CurrentFilterActivity")) {
                startActivity(new Intent(CropDocumentActivity.this, CurrentFilterActivity.class));
                MyApplication.IdentifyActivity = "";
                finish();
            } else if (MyApplication.IdentifyActivity.equals("ScannerActivity_Retake")) {
                startActivity(new Intent(CropDocumentActivity.this, ScannerActivity.class));
                MyApplication.IdentifyActivity = "";
                finish();
            }
        }
    };

    public String current_docs_name;

    public DBHelper dbHelper;
    protected ImageView iv_back;
    protected TextView iv_Rotate_Doc;
    protected ImageView iv_edit;
    protected ImageView iv_done;
    protected ImageView iv_full_crop;
    private CropImageView iv_preview_crop;
    protected TextView iv_retake;
    protected LinearLayout ly_current_filter;
    protected LinearLayout ly_rotate_doc;

    public String selected_group_name;

    private TextView iv_ocv_black;
    private TextView iv_original;
    private TextView iv_color;
    private TextView iv_sharp_black;
    private Bitmap tempBitmap;
    public Bitmap original;
    private ProgressDialog progressDialog;

    private ImageView iv_add_new_scan;
    private SeekBar seekBarBrightness;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_Crop"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".CurrentFilterActivity"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity_Retake"));
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
        setContentView(R.layout.activity_crop_document);
        dbHelper = new DBHelper(this);
        PutAnalyticsEvent();
        init();
        BannerAds();
    }


    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CropDocumentActivity");
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
        iv_add_new_scan = findViewById(R.id.iv_add_new_scan);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);

        iv_back = findViewById(R.id.iv_back);
        iv_edit = findViewById(R.id.iv_edit);
        iv_done = findViewById(R.id.iv_done);
        iv_preview_crop = findViewById(R.id.iv_preview_crop);
        iv_full_crop = findViewById(R.id.iv_full_crop);
        ly_rotate_doc = findViewById(R.id.ly_rotate_doc);
        ly_current_filter = findViewById(R.id.ly_current_filter);
        iv_retake = findViewById(R.id.iv_retake);
        iv_Rotate_Doc = findViewById(R.id.iv_Rotate_Doc);
        iv_original = findViewById(R.id.iv_original);
        iv_color = findViewById(R.id.iv_color);
        iv_sharp_black = findViewById(R.id.iv_sharp_black);
        iv_ocv_black = findViewById(R.id.iv_ocv_black);


        if (Constant.original != null) {
            iv_preview_crop.setImageToCrop(Constant.original);
            original = Constant.original;
            changeBrightness(20);
        }
        seekBarBrightness.setOnSeekBarChangeListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_new_scan:
                return;
            case R.id.iv_original:
                try {
                    showProgressDialog();
                    tempBitmap = original;
                    iv_preview_crop.setImageBitmap(original);
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
                                    iv_preview_crop.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_crop.setImageBitmap(tempBitmap);
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
                                    iv_preview_crop.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_crop.setImageBitmap(tempBitmap);
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
                                    iv_preview_crop.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_crop.setImageBitmap(tempBitmap);
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
            case R.id.iv_done:
                if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    new addDocGroup().execute(Constant.original);
                    return;
                }
                return;
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_Rotate_Doc:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                original = createBitmap;
                iv_preview_crop.setImageToCrop(Constant.original);
                iv_preview_crop.setFullImgCrop();
                return;
            case R.id.iv_edit:
                if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    new addGroup().execute(Constant.original);
                    return;
                }
                return;
            case R.id.iv_full_crop:
                iv_preview_crop.setFullImgCrop();
                return;
            case R.id.iv_retake:
                if (MyApplication.isShowAd == 1) {
                    startActivity(new Intent(CropDocumentActivity.this, ScannerActivity.class));
                    finish();
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "ScannerActivity_Retake";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;
                    } else {
                        startActivity(new Intent(CropDocumentActivity.this, ScannerActivity.class));
                        finish();
                    }
                }
                return;
            case R.id.ly_current_filter:
                if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    if (MyApplication.isShowAd == 1) {
                        startActivity(new Intent(CropDocumentActivity.this, CurrentFilterActivity.class));
                        MyApplication.isShowAd = 0;
                    } else {
                        if (MyApplication.mInterstitialAd != null) {
                            MyApplication.activity = activity;
                            MyApplication.IdentifyActivity = "CurrentFilterActivity";
                            MyApplication.mInterstitialAd.show(activity);
                            MyApplication.isShowAd = 1;

                        } else {
                            startActivity(new Intent(CropDocumentActivity.this, CurrentFilterActivity.class));
                        }
                    }
                    return;
                }
                return;
          /*  case R.id.ly_rotate_doc:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                iv_preview_crop.setImageToCrop(Constant.original);
                iv_preview_crop.setFullImgCrop();
                return;*/
            default:
                return;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBarBrightness:
                changeBrightness(progress);
                break;
        }
    }

    private void changeBrightness(float brightness) {
        iv_preview_crop.setImageBitmap(AdjustUtil.changeBitmapContrastBrightness(original, 1.0f, brightness));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class addGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CropDocumentActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please Wait...");
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
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = GroupDocumentActivity.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = current_doc_name;
            if (MyApplication.isShowAd == 1) {
                Intent intent2 = new Intent(CropDocumentActivity.this, DocumentEditorActivity.class);
                intent2.putExtra("TAG", "SavedDocumentActivity");
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                finish();
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.IdentifyActivity = "DocumentEditorActivity_Crop";
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;
                } else {
                    Intent intent2 = new Intent(CropDocumentActivity.this, DocumentEditorActivity.class);
                    intent2.putExtra("TAG", "SavedDocumentActivity");
                    intent2.putExtra("scan_doc_group_name", selected_group_name);
                    intent2.putExtra("current_doc_name", current_docs_name);
                    startActivity(intent2);
                    finish();
                }
            }
        }
    }

    private class addDocGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addDocGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CropDocumentActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please Wait...");
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
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = GroupDocumentActivity.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = current_doc_name;

            Intent intent2 = new Intent(CropDocumentActivity.this, GroupDocumentActivity.class);
            intent2.putExtra("current_group", selected_group_name);
            startActivity(intent2);
            MyApplication.IdentifyActivity = "";
            finish();
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
