package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.templatemela.camscanner.MyApplication;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.db.DBHelper;
import com.templatemela.camscanner.models.DBModel;
import com.templatemela.camscanner.scrapbook.ImageStickerConfig;
import com.templatemela.camscanner.scrapbook.LocalDisplay;
import com.templatemela.camscanner.scrapbook.StickerConfigInterface;
import com.templatemela.camscanner.scrapbook.StickerHolderView;
import com.templatemela.camscanner.scrapbook.TextStickerConfig;
import com.templatemela.camscanner.main_utils.BitmapUtils;
import com.templatemela.camscanner.main_utils.Constant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class IDCardPreviewActivity extends BaseActivity implements View.OnClickListener, StickerHolderView.OnStickerSelectionCallback {
    Activity activity = IDCardPreviewActivity.this;
    private static final String TAG = "IDCardPreviewActivity";

    public AspectRatioLayout aspectRatioLayout;
    protected Bitmap backSide;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyApplication.IdentifyActivity.equals("DocumentEditorActivity_IDCard")) {
                Intent intent2 = new Intent(IDCardPreviewActivity.this, DocumentEditorActivity.class);
                intent2.putExtra("TAG", IDCardPreviewActivity.TAG);
                startActivityForResult(intent2, 14);
                MyApplication.IdentifyActivity = "";
            } else if (MyApplication.IdentifyActivity.equals("IDCardGalleryActivity")) {
                ImagePicker.with(IDCardPreviewActivity.this)
                        .setStatusBarColor("#25c4a4")
                        .setToolbarColor("#25c4a4")
                        .setBackgroundColor("#ffffff")
                        .setFolderMode(true)
                        .setFolderTitle("Gallery")
                        .setMultipleMode(true)
                        .setShowNumberIndicator(true)
                        .setAlwaysShowDoneButton(true)
                        .setMaxSize(7)
                        .setShowCamera(false)
                        .setLimitMessage("You can select up to 7 images")
                        .setRequestCode(100)
                        .start();
                MyApplication.IdentifyActivity = "";
            }
        }
    };

    public int color = SupportMenu.CATEGORY_MASK;

    public DBHelper dbHelper;
    protected Bitmap finalBitmap;
    protected Bitmap frontSide;
    protected ImageView iv_add_new;
    protected ImageView iv_back;
    private LinearLayout ly_add_new, ly_scrap, ly_edit, ly_left_rotate, ly_right_rotate, ly_horizontal, ly_vertical;

    public ImageView iv_bg_color;
    protected ImageView iv_done;
    protected ImageView iv_edit;
    protected ImageView iv_horizontal;
    protected ImageView iv_left;
    protected ImageView iv_right;
    protected ImageView iv_scrap;
    protected ImageView iv_vertical;
    protected LinearLayout ly_color;

    public LinearLayout ly_main;
    private LinearLayout ly_scrap_view;
    private TextView txtScrap;

    public RelativeLayout rl_main;

    public StickerHolderView stickerHolderView;

    @Override
    public void onTextStickerSelected(TextStickerConfig textStickerConfig, boolean z) {
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_IDCard"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".IDCardGalleryActivity"));
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
        LocalDisplay.init(this);
        setContentView(R.layout.activity_idcard_preview);
        dbHelper = new DBHelper(this);
        init();
        bindView();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IDCardPreviewActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void init() {
        txtScrap = findViewById(R.id.txtScrap);
        ly_vertical = findViewById(R.id.ly_vertical);
        ly_horizontal = findViewById(R.id.ly_horizontal);
        ly_right_rotate = findViewById(R.id.ly_right_rotate);
        ly_left_rotate = findViewById(R.id.ly_left_rotate);
        ly_edit = findViewById(R.id.ly_edit);
        ly_add_new = findViewById(R.id.ly_add_new);
        ly_main = findViewById(R.id.ly_main);
        iv_back = findViewById(R.id.iv_back);
        iv_done = findViewById(R.id.iv_done);
        rl_main = findViewById(R.id.rl_main);
        aspectRatioLayout = findViewById(R.id.aspectRatioLayout);
        stickerHolderView = findViewById(R.id.stickerHolderView);
        iv_bg_color = findViewById(R.id.iv_bg_color);
        iv_add_new = findViewById(R.id.iv_add_new);
        ly_color = findViewById(R.id.ly_color);
        iv_scrap = findViewById(R.id.iv_scrap);
        ly_scrap = findViewById(R.id.ly_scrap);
        ly_scrap_view = findViewById(R.id.ly_scrap_view);
        iv_edit = findViewById(R.id.iv_edit);
        iv_left = findViewById(R.id.iv_left);
        iv_right = findViewById(R.id.iv_right);
        iv_horizontal = findViewById(R.id.iv_horizontal);
        iv_vertical = findViewById(R.id.iv_vertical);
    }

    private void bindView() {
        aspectRatioLayout.setAspectRatio(3.0f, 4.0f);
        stickerHolderView.setTextStickerSelectionCallback(this);
        if (!Constant.current_camera_view.equals("ID Card") || !Constant.card_type.equals("Single")) {
            frontSide = ScannerActivity.idcardImgList.get(0);
            backSide = ScannerActivity.idcardImgList.get(1);
            stickerHolderView.addStickerView(new ImageStickerConfig(frontSide, StickerConfigInterface.STICKER_TYPE.IMAGE));
            stickerHolderView.addStickerView(new ImageStickerConfig(backSide, StickerConfigInterface.STICKER_TYPE.IMAGE));
        } else if (Constant.singleSideBitmap != null) {
            stickerHolderView.addStickerView(new ImageStickerConfig(Constant.singleSideBitmap, StickerConfigInterface.STICKER_TYPE.IMAGE));
        }
    }

    @Override
    public void onImageStickerSelected(ImageStickerConfig imageStickerConfig, boolean z) {
        iv_scrap.setImageResource(R.drawable.ic_scrap_selection);
        txtScrap.setTextColor(getResources().getColor(R.color.black));
        ly_scrap_view.setVisibility(View.VISIBLE);
        Constant.original = imageStickerConfig.getBitmapImage();
    }

    @Override
    public void onNoneStickerSelected() {
        iv_scrap.setImageResource(R.drawable.ic_scrap);
        txtScrap.setTextColor(getResources().getColor(R.color.white));
        ly_scrap_view.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_add_new:
                if (MyApplication.isShowAd == 1) {
                    ImagePicker.with(IDCardPreviewActivity.this)
                            .setStatusBarColor("#25c4a4")
                            .setToolbarColor("#25c4a4")
                            .setBackgroundColor("#ffffff")
                            .setFolderMode(true)
                            .setFolderTitle("Gallery")
                            .setMultipleMode(true)
                            .setShowNumberIndicator(true)
                            .setAlwaysShowDoneButton(true)
                            .setMaxSize(7)
                            .setShowCamera(false)
                            .setLimitMessage("You can select up to 7 images")
                            .setRequestCode(100)
                            .start();
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "IDCardGalleryActivity";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;
                    } else {
                        ImagePicker.with(IDCardPreviewActivity.this)
                                .setStatusBarColor("#25c4a4")
                                .setToolbarColor("#25c4a4")
                                .setBackgroundColor("#ffffff")
                                .setFolderMode(true)
                                .setFolderTitle("Gallery")
                                .setMultipleMode(true)
                                .setShowNumberIndicator(true)
                                .setAlwaysShowDoneButton(true)
                                .setMaxSize(7)
                                .setShowCamera(false)
                                .setLimitMessage("You can select up to 7 images")
                                .setRequestCode(100)
                                .start();
                    }
                }
                return;
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_done:
                aspectRatioLayout.setDrawingCacheEnabled(true);
                stickerHolderView.leaveSticker();
                iv_scrap.setImageResource(R.drawable.ic_scrap);
                txtScrap.setTextColor(getResources().getColor(R.color.white));
                ly_scrap_view.setVisibility(View.GONE);
                new saveIDCard().execute();
                return;
            case R.id.ly_edit:
                if (MyApplication.isShowAd == 1) {
                    Intent intent2 = new Intent(IDCardPreviewActivity.this, DocumentEditorActivity.class);
                    intent2.putExtra("TAG", IDCardPreviewActivity.TAG);
                    startActivityForResult(intent2, 14);
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "DocumentEditorActivity_IDCard";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {
                        Intent intent2 = new Intent(IDCardPreviewActivity.this, DocumentEditorActivity.class);
                        intent2.putExtra("TAG", IDCardPreviewActivity.TAG);
                        startActivityForResult(intent2, 14);
                    }
                }
                return;
            case R.id.ly_horizontal:
                stickerHolderView.flipStickerHorizontal(true);
                return;
            case R.id.ly_left_rotate:
                stickerHolderView.rightRotate(true);
                return;
            case R.id.ly_right_rotate:
                stickerHolderView.leftRotate(true);
                return;
            case R.id.ly_scrap:
                if (ly_scrap_view.getVisibility() == View.VISIBLE) {
                    iv_scrap.setImageResource(R.drawable.ic_scrap);
                    txtScrap.setTextColor(getResources().getColor(R.color.white));
                    ly_scrap_view.setVisibility(View.GONE);
                    return;
                } else if (ly_scrap_view.getVisibility() == View.GONE) {
                    iv_scrap.setImageResource(R.drawable.ic_scrap_selection);
                    txtScrap.setTextColor(getResources().getColor(R.color.black));
                    ly_scrap_view.setVisibility(View.VISIBLE);
                    return;
                } else {
                    return;
                }
            case R.id.ly_vertical:
                stickerHolderView.flipSticker(true);
                return;
            case R.id.ly_color:
                ColorPickerDialogBuilder.with(this).setTitle("Choose color").initialColor(color).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(10).setOnColorSelectedListener(new OnColorSelectedListener() {
                    public void onColorSelected(int i) {
                    }
                }).setPositiveButton("ok", new ColorPickerClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] numArr) {
                        color = i;
                        iv_bg_color.setBackgroundColor(i);
                        ly_main.setBackgroundColor(i);
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).build().show();
                return;
            default:
                return;
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (ImagePicker.shouldHandleResult(i, i2, intent, 100)) {
            Iterator<Image> it = ImagePicker.getImages(intent).iterator();
            while (it.hasNext()) {
                Image next = it.next();
                if (Build.VERSION.SDK_INT >= 29) {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getUri()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            Constant.original = bitmap;
                            stickerHolderView.addStickerView(new ImageStickerConfig(bitmap, StickerConfigInterface.STICKER_TYPE.IMAGE));
                        }
                    });
                } else {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getPath()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            Constant.original = bitmap;
                            stickerHolderView.addStickerView(new ImageStickerConfig(bitmap, StickerConfigInterface.STICKER_TYPE.IMAGE));
                        }
                    });
                }
            }
        }
        if (i2 == -1 && i == 14) {
            stickerHolderView.setEditImageOnSticker(Constant.original);
        }
    }

    private class saveIDCard extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private saveIDCard() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IDCardPreviewActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            finalBitmap = aspectRatioLayout.getDrawingCache();
            finalBitmap = getMainFrameBitmap(rl_main);
            if (finalBitmap == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(finalBitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                Log.w(IDCardPreviewActivity.TAG, "Cannot write to " + file, e);
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

            Intent intent2 = new Intent(IDCardPreviewActivity.this, GroupDocumentActivity.class);
            intent2.putExtra("current_group", group_name);
            startActivity(intent2);
            MyApplication.IdentifyActivity = "";
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.editor_screen_exit_dailog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.iv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }
}
