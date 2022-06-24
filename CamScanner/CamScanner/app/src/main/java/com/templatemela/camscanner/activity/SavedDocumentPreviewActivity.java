package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.itextpdf.text.pdf.PdfObject;
import com.templatemela.camscanner.MyApplication;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.adapter.PreviewPagerAdapter;
import com.templatemela.camscanner.adapter.SavedToolsAdapter;
import com.templatemela.camscanner.db.DBHelper;
import com.templatemela.camscanner.document_view.ViewPagerFixed;
import com.templatemela.camscanner.main_utils.BitmapUtils;
import com.templatemela.camscanner.main_utils.Constant;
import com.templatemela.camscanner.models.DBModel;
import com.templatemela.camscanner.models.SavedToolType;
import com.templatemela.camscanner.utils.AdmobAds;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SavedDocumentPreviewActivity extends BaseActivity implements View.OnClickListener, SavedToolsAdapter.OnSavedToolSelected {
    Activity activity = SavedDocumentPreviewActivity.this;
    private static final String TAG = "SavedDocumentPreviewAct";
    public static ArrayList<DBModel> currentDocList = new ArrayList<>();
    public static SavedDocumentPreviewActivity savedDocumentPreviewActivity;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyApplication.IdentifyActivity.equals("NoteActivity_Preview")) {
                Intent intent2 = new Intent(SavedDocumentPreviewActivity.this, NoteActivity.class);
                intent2.putExtra("group_name", edit_doc_grp_name);
                intent2.putExtra("current_doc_name", current_doc_name);
                intent2.putExtra("note", noteTxt);
                startActivity(intent2);
                MyApplication.IdentifyActivity = "";
            } else if (MyApplication.IdentifyActivity.equals("ImageToTextActivity_Preview")) {
                Intent intent3 = new Intent(SavedDocumentPreviewActivity.this, ImageToTextActivity.class);
                intent3.putExtra("group_name", edit_doc_grp_name);
                startActivity(intent3);
                MyApplication.IdentifyActivity = "";
            } else if (MyApplication.IdentifyActivity.equals("PDFViewerActivity_Preview")) {
                Intent intent4 = new Intent(SavedDocumentPreviewActivity.this, PDFViewerActivity.class);
                intent4.putExtra("title", selected_group_name + ".pdf");
                intent4.putExtra("pdf_path", pdfUri.toString());
                startActivity(intent4);
                MyApplication.IdentifyActivity = "";
            } else if (MyApplication.IdentifyActivity.equals("DocumentEditorActivity_SavedPreview")) {
                Intent intent5 = new Intent(SavedDocumentPreviewActivity.this, DocumentEditorActivity.class);
                intent5.putExtra("TAG", "ScannerActivity");
                intent5.putExtra("position", position);
                intent5.putExtra("edited_doc_grp_name", edit_doc_grp_name);
                intent5.putExtra("current_doc_name", current_doc_name);
                startActivityForResult(intent5, 21);
                MyApplication.IdentifyActivity = "";
            }
        }
    };

    public String current_doc_name;

    public DBHelper dataBaseHelper;

    public String edit_doc_grp_name;
    protected ImageView iv_back;
    protected ImageView iv_home;
    protected String noteTxt;
    protected Uri pdfUri;

    public int position;

    public PreviewPagerAdapter previewPagerAdapter;
    private RecyclerView rv_saved_tools;
    protected SavedToolsAdapter savedToolsAdapter;
    protected String selected_group_name;

    public Uri shareUri;

    public TextView tv_page;

    public ViewPagerFixed viewPager;


    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_SavedPreview"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".PDFViewerActivity_Preview"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".NoteActivity_Preview"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ImageToTextActivity_Preview"));
    }

    @Override
    public void onDestroy() {
        if (shareUri != null) {
            getContentResolver().delete(shareUri, null, null);
        }
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
        setContentView(R.layout.activity_saved_document_preview);
        savedDocumentPreviewActivity = this;
        dataBaseHelper = new DBHelper(this);
        BannerAds();
        init();
        bindView();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SavedDocumentPreviewActivity");
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
        iv_home = findViewById(R.id.iv_home);
        viewPager = findViewById(R.id.viewPager);
        tv_page = findViewById(R.id.tv_page);
        rv_saved_tools = findViewById(R.id.rv_saved_tools);
    }

    private void bindView() {
        edit_doc_grp_name = getIntent().getStringExtra("edit_doc_group_name");
        current_doc_name = getIntent().getStringExtra("current_doc_name");
        position = getIntent().getIntExtra("position", -1);
        new getAllDoc().execute();

    }

    public class getAllDoc extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        public getAllDoc() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SavedDocumentPreviewActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            currentDocList.clear();
            currentDocList.addAll(dataBaseHelper.getGroupDocs(edit_doc_grp_name.replace(" ", "")));
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            if (currentDocList.size() > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        previewPagerAdapter = new PreviewPagerAdapter(SavedDocumentPreviewActivity.this, currentDocList);
                        viewPager.setAdapter(previewPagerAdapter);
                        viewPager.setCurrentItem(position);
                        tv_page.setText(String.format("%s / %s", Integer.valueOf(position + 1), Integer.valueOf(currentDocList.size())));
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrollStateChanged(int i) {
                            }

                            @Override
                            public void onPageScrolled(int i, float f, int i2) {
                            }

                            @Override
                            public void onPageSelected(int i) {
                                Log.e(TAG, "onPageSelected: " + i);
                                position = i;
                                current_doc_name = currentDocList.get(position).getGroup_doc_name();
                                tv_page.setText(String.format("%s / %s", Integer.valueOf(position + 1), Integer.valueOf(currentDocList.size())));
                            }
                        });
                        setSavedToolsAdapter();
                    }
                });

            } else {
//                Toast.makeText(getApplicationContext(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private void setSavedToolsAdapter() {
        rv_saved_tools.setHasFixedSize(true);
        rv_saved_tools.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        savedToolsAdapter = new SavedToolsAdapter(this);
        rv_saved_tools.setAdapter(savedToolsAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.iv_home) {
            if (GroupDocumentActivity.groupDocumentActivity != null) {
                GroupDocumentActivity.groupDocumentActivity.finish();
            }
            finish();
        }
    }

    @Override
    public void onSavedToolSelected(SavedToolType savedToolType) {
        switch (savedToolType) {
            case EDIT:
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Constant.original = BitmapFactory.decodeStream(new FileInputStream(currentDocList.get(position).getGroup_doc_img()), null, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (MyApplication.isShowAd == 1) {

                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "DocumentEditorActivity_SavedPreview";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {

                    }


                }
                return;
            case OPENPDF:
                try {
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Constant.original = BitmapFactory.decodeStream(new FileInputStream(currentDocList.get(position).getGroup_doc_img()), null, options2);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                ArrayList arrayList = new ArrayList();
                arrayList.add(Constant.original);
                new openPDF(arrayList, edit_doc_grp_name).execute();
                return;
            case NAME:
                updateGroupName(edit_doc_grp_name);
                return;
            case ROTATE:
                try {
                    BitmapFactory.Options options3 = new BitmapFactory.Options();
                    options3.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Constant.original = BitmapFactory.decodeStream(new FileInputStream(currentDocList.get(position).getGroup_doc_img()), null, options3);
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                new rotateDoc().execute();
                return;
            case NOTE:
                noteTxt = dataBaseHelper.getSingleNote(edit_doc_grp_name, current_doc_name);
                if (MyApplication.isShowAd == 1) {
                    Intent intent2 = new Intent(SavedDocumentPreviewActivity.this, NoteActivity.class);
                    intent2.putExtra("group_name", edit_doc_grp_name);
                    intent2.putExtra("current_doc_name", current_doc_name);
                    intent2.putExtra("note", noteTxt);
                    startActivity(intent2);
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "NoteActivity_Preview";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {
                        Intent intent2 = new Intent(SavedDocumentPreviewActivity.this, NoteActivity.class);
                        intent2.putExtra("group_name", edit_doc_grp_name);
                        intent2.putExtra("current_doc_name", current_doc_name);
                        intent2.putExtra("note", noteTxt);
                        startActivity(intent2);
                    }
                }
                return;
            case ImageToText:
                try {
                    BitmapFactory.Options options4 = new BitmapFactory.Options();
                    options4.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Constant.original = BitmapFactory.decodeStream(new FileInputStream(currentDocList.get(position).getGroup_doc_img()), null, options4);
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                if (MyApplication.isShowAd == 1) {
                    Intent intent3 = new Intent(SavedDocumentPreviewActivity.this, ImageToTextActivity.class);
                    intent3.putExtra("group_name", edit_doc_grp_name);
                    startActivity(intent3);
                    MyApplication.isShowAd = 0;
                } else {
                    if (MyApplication.mInterstitialAd != null) {
                        MyApplication.activity = activity;
                        MyApplication.IdentifyActivity = "ImageToTextActivity_Preview";
                        MyApplication.mInterstitialAd.show(activity);
                        MyApplication.isShowAd = 1;

                    } else {
                        Intent intent3 = new Intent(SavedDocumentPreviewActivity.this, ImageToTextActivity.class);
                        intent3.putExtra("group_name", edit_doc_grp_name);
                        startActivity(intent3);
                    }
                }
                return;
            case SHARE:
                try {
                    BitmapFactory.Options options5 = new BitmapFactory.Options();
                    options5.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Constant.original = BitmapFactory.decodeStream(new FileInputStream(currentDocList.get(position).getGroup_doc_img()), null, options5);
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                shareGroupDoc();
                return;
            case DELETE:
                final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.delete_document_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                if (AdmobAds.SHOW_ADS) {
                    AdmobAds.loadNativeAds(SavedDocumentPreviewActivity.this, null, dialog.findViewById(R.id.admob_native_container), dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }
                dialog.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentDocList.size() == 1) {
                            dataBaseHelper.deleteGroup(edit_doc_grp_name);
                            if (GroupDocumentActivity.groupDocumentActivity != null) {
                                GroupDocumentActivity.groupDocumentActivity.finish();
                            }
                            dialog.dismiss();
                            finish();
                            return;
                        }
                        dataBaseHelper.deleteSingleDoc(edit_doc_grp_name, current_doc_name);
                        if (position == 0) {
                            dataBaseHelper.updateGroupFirstImg(edit_doc_grp_name, currentDocList.get(position + 1).getGroup_doc_img());
                            viewPager.setAdapter(null);
                            currentDocList.remove(position);
                            previewPagerAdapter.notifyDataSetChanged();
                            previewPagerAdapter = new PreviewPagerAdapter(savedDocumentPreviewActivity, currentDocList);
                            viewPager.setAdapter(previewPagerAdapter);
                            viewPager.setCurrentItem(position);
                            position = viewPager.getCurrentItem();
                            current_doc_name = currentDocList.get(position).getGroup_doc_name();
                        } else {
                            viewPager.setAdapter(null);
                            currentDocList.remove(position);
                            previewPagerAdapter.notifyDataSetChanged();
                            previewPagerAdapter = new PreviewPagerAdapter(savedDocumentPreviewActivity, currentDocList);
                            viewPager.setAdapter(previewPagerAdapter);
                            viewPager.setCurrentItem(position - 1);

                            position = viewPager.getCurrentItem();
                            current_doc_name = currentDocList.get(position).getGroup_doc_name();
                        }
                        tv_page.setText(String.format("%s / %s", Integer.valueOf(position + 1), Integer.valueOf(currentDocList.size())));
                        dialog.dismiss();
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
            default:
                return;
        }
    }

    public class openPDF extends AsyncTask<String, Void, String> {
        ArrayList<Bitmap> current_bitmap;
        String group_name;
        ProgressDialog progressDialog;

        public openPDF(ArrayList<Bitmap> arrayList, String str) {
            current_bitmap = arrayList;
            group_name = str;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SavedDocumentPreviewActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            createPDFfromBitmap(group_name, current_bitmap, "temp");
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            pdfUri = BaseActivity.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + group_name + ".pdf", SavedDocumentPreviewActivity.this);
            progressDialog.dismiss();
            selected_group_name = group_name;
            if (MyApplication.isShowAd == 1) {
                Intent intent4 = new Intent(SavedDocumentPreviewActivity.this, PDFViewerActivity.class);
                intent4.putExtra("title", selected_group_name + ".pdf");
                intent4.putExtra("pdf_path", pdfUri.toString());
                startActivity(intent4);
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.IdentifyActivity = "PDFViewerActivity_Preview";
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;

                } else {
                    Intent intent4 = new Intent(SavedDocumentPreviewActivity.this, PDFViewerActivity.class);
                    intent4.putExtra("title", selected_group_name + ".pdf");
                    intent4.putExtra("pdf_path", pdfUri.toString());
                    startActivity(intent4);
                }
            }
        }
    }

    private void updateGroupName(final String str) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.update_group_name);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(SavedDocumentPreviewActivity.this, null, dialog.findViewById(R.id.admob_native_container), dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText editText = dialog.findViewById(R.id.et_group_name);
        editText.setText(str);
        editText.setSelection(editText.length());
        dialog.findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                    Toast.makeText(SavedDocumentPreviewActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dataBaseHelper.updateGroupName(SavedDocumentPreviewActivity.this, str, editText.getText().toString().trim());
                dialog.dismiss();
                edit_doc_grp_name = editText.getText().toString();
                GroupDocumentActivity.current_group = editText.getText().toString();
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class rotateDoc extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        private rotateDoc() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SavedDocumentPreviewActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            Bitmap bitmap = Constant.original;
            Matrix matrix = new Matrix();
            matrix.postRotate(90.0f);
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Constant.original.recycle();
            System.gc();
            Constant.original = createBitmap;
            byte[] bytes = BitmapUtils.getBytes(Constant.original);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                Log.w(TAG, "Cannot write to " + file, e);
            }
            if (position == 0) {
                dataBaseHelper.updateGroupFirstImg(edit_doc_grp_name, file.getPath());
                dataBaseHelper.updateGroupListDoc(edit_doc_grp_name, current_doc_name, file.getPath());
            } else {
                dataBaseHelper.updateGroupListDoc(edit_doc_grp_name, current_doc_name, file.getPath());
            }
            currentDocList.clear();
            currentDocList = dataBaseHelper.getGroupDocs(edit_doc_grp_name.replace(" ", ""));
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            previewPagerAdapter = new PreviewPagerAdapter(savedDocumentPreviewActivity, currentDocList);
            viewPager.setAdapter(previewPagerAdapter);
            viewPager.setCurrentItem(position);
            progressDialog.dismiss();
        }
    }

    private void shareGroupDoc() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.share_group_doc);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(this, null, dialog.findViewById(R.id.admob_native_container), dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        dialog.findViewById(R.id.rl_share_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Constant.original);
                new shareAsPDF(arrayList, PdfObject.TEXT_PDFDOCENCODING, "").execute();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.rl_share_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareUri = BitmapUtils.getUri(savedDocumentPreviewActivity, Constant.original);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", shareUri);
                intent.putExtra("android.intent.extra.TEXT", "");
                intent.putExtra("android.intent.extra.SUBJECT", "");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, null));
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.rl_share_pdf_pswrd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePDFWithPswrd();
                dialog.dismiss();
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


    public void sharePDFWithPswrd() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.set_pdf_pswrd);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(this, null, dialog.findViewById(R.id.admob_native_container), dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText et_enter_pswrd = dialog.findViewById(R.id.et_enter_pswrd);
        final ImageView iv_enter_pswrd_show = dialog.findViewById(R.id.iv_enter_pswrd_show);
        final ImageView iv_enter_pswrd_hide = dialog.findViewById(R.id.iv_enter_pswrd_hide);
        final EditText et_confirm_pswrd = dialog.findViewById(R.id.et_confirm_pswrd);
        final ImageView iv_confirm_pswrd_show = dialog.findViewById(R.id.iv_confirm_pswrd_show);
        final ImageView iv_confirm_pswrd_hide = dialog.findViewById(R.id.iv_confirm_pswrd_hide);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/inter_medium.ttf");
        et_enter_pswrd.setTypeface(typeface);
        et_confirm_pswrd.setTypeface(typeface);
        et_enter_pswrd.setInputType(129);
        et_confirm_pswrd.setInputType(129);

        iv_enter_pswrd_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_enter_pswrd_show.setVisibility(View.GONE);
                iv_enter_pswrd_hide.setVisibility(View.VISIBLE);
                et_enter_pswrd.setTransformationMethod(new HideReturnsTransformationMethod());
                et_enter_pswrd.setSelection(et_enter_pswrd.getText().length());
            }
        });
        iv_enter_pswrd_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_enter_pswrd_show.setVisibility(View.VISIBLE);
                iv_enter_pswrd_hide.setVisibility(View.GONE);
                et_enter_pswrd.setTransformationMethod(new PasswordTransformationMethod());
                et_enter_pswrd.setSelection(et_enter_pswrd.getText().length());
            }
        });
        iv_confirm_pswrd_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_confirm_pswrd_show.setVisibility(View.GONE);
                iv_confirm_pswrd_hide.setVisibility(View.VISIBLE);
                et_confirm_pswrd.setTransformationMethod(new HideReturnsTransformationMethod());
                et_confirm_pswrd.setSelection(et_confirm_pswrd.getText().length());
            }
        });
        iv_confirm_pswrd_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_confirm_pswrd_show.setVisibility(View.VISIBLE);
                iv_confirm_pswrd_hide.setVisibility(View.GONE);
                et_confirm_pswrd.setTransformationMethod(new PasswordTransformationMethod());
                et_confirm_pswrd.setSelection(et_confirm_pswrd.getText().length());
            }
        });
        dialog.findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_enter_pswrd.getText().toString().equals("") || et_confirm_pswrd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!et_enter_pswrd.getText().toString().equals(et_confirm_pswrd.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Your password & Confirm password do not match.", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Constant.original);
                    new shareAsPDF(arrayList, "PDF With Password", et_enter_pswrd.getText().toString()).execute();
                    dialog.dismiss();
                }
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class shareAsPDF extends AsyncTask<String, Void, String> {
        private final ArrayList<Bitmap> bitmap;
        private final String inputType;
        private final String password;
        private ProgressDialog progressDialog;

        private shareAsPDF(ArrayList<Bitmap> arrayList, String str, String str2) {
            bitmap = arrayList;
            inputType = str;
            password = str2;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SavedDocumentPreviewActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            if (inputType.equals(PdfObject.TEXT_PDFDOCENCODING)) {
                createPDFfromBitmap(edit_doc_grp_name, bitmap, "temp");
                return null;
            }
            createProtectedPDFfromBitmap(edit_doc_grp_name, bitmap, password, "temp");
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            Uri uRIFromFile = BaseActivity.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + edit_doc_grp_name + ".pdf", SavedDocumentPreviewActivity.this);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("application/pdf");
            intent.putExtra("android.intent.extra.STREAM", uRIFromFile);
            intent.putExtra("android.intent.extra.TEXT", "");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent createChooser = Intent.createChooser(intent, null);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startActivity(createChooser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 21) {
            new updateEditedImg().execute();
        }
    }

    private class updateEditedImg extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        private updateEditedImg() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SavedDocumentPreviewActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            if (Constant.original == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(Constant.original);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                Log.w(TAG, "Cannot write to " + file, e);
            }
            if (position == 0) {
                dataBaseHelper.updateGroupFirstImg(edit_doc_grp_name, file.getPath());
                dataBaseHelper.updateGroupListDoc(edit_doc_grp_name, current_doc_name, file.getPath());
            } else {
                dataBaseHelper.updateGroupListDoc(edit_doc_grp_name, current_doc_name, file.getPath());
            }
            currentDocList.clear();
            currentDocList = dataBaseHelper.getGroupDocs(edit_doc_grp_name.replace(" ", ""));
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            previewPagerAdapter = new PreviewPagerAdapter(savedDocumentPreviewActivity, currentDocList);
            viewPager.setAdapter(previewPagerAdapter);
            viewPager.setCurrentItem(position);
            progressDialog.dismiss();
        }
    }
}
