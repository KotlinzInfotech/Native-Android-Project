package com.omsvision;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.Result;

import com.omsvision.barcodereader.ZXingScannerView;
import com.omsvision.model.Detail_get_set;
import com.omsvision.retrofit.ServiceHelper;
import com.omsvision.retrofit.ServiceResponse;

import org.bouncycastle.i18n.TextBundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.omsvision.myApp.cognito;

public class Barcode_Scanner extends BaseActivity implements ZXingScannerView.ResultHandler {

    ArrayList<Detail_get_set> Detaillist = new ArrayList<>();
    MyListAdapter adapter;
    public static Dialog dialog;

    String sacnresult;

    private static final String TAG = "QRReaderActivity";

    public String barcode_result;
    protected int camera_id = -1;
    private ArrayList<Integer> selected_indices;

    public ViewGroup viewGroup;

    public ZXingScannerView zXingScannerView;

    @Override
    public void onCreate(Bundle bundlestate) {
        super.onCreate(bundlestate);
        setContentView(R.layout.activity_barcode_scan);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);

        mTitle.setText(getIntent().getExtras().getString("type"));
        init();

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void setupBarcodeFormats() {
        ArrayList arrayList = new ArrayList();

        if (selected_indices == null || selected_indices.isEmpty()) {
            selected_indices = new ArrayList<>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                selected_indices.add(Integer.valueOf(i));
            }
        }
        Iterator<Integer> it = selected_indices.iterator();
        while (it.hasNext()) {
            arrayList.add(ZXingScannerView.ALL_FORMATS.get(it.next().intValue()));
        }

        if (zXingScannerView != null) {
            zXingScannerView.setFormats(arrayList);
        }
    }

    @Override
    public void handleResult(Result result) {
        sacnresult = result.getText();
        Log.e(TAG, result.getText());
        Log.e(TAG, result.getBarcodeFormat().toString());
        new ToneGenerator(5, 100).startTone(24);
        if (isNetworkConnected()) {
            if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.scanrts))) {
                Addscanrts(sacnresult);
            }
            if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.getdetail))) {
                GetDetail(sacnresult);
            }
            if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.scanreturn))) {
                Addscanreturn(sacnresult, getIntent().getExtras().getString("condition"));
            }
        } else {
            Toast.makeText(Barcode_Scanner.this, "Connet Internet ", Toast.LENGTH_LONG).show();
            restartPreviewAndDecode();
        }
    }

    @Override
    public void onResume() {
        if (zXingScannerView == null) {
            zXingScannerView = new ZXingScannerView(this);
            viewGroup.addView(zXingScannerView);
        }
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera(camera_id);
        setupBarcodeFormats();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        zXingScannerView.stopCamera();
        super.onDestroy();
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    private void init() {
        viewGroup = (ViewGroup) findViewById(R.id.fl_camera);
        zXingScannerView = new ZXingScannerView(this);
        viewGroup.addView(zXingScannerView);

    }

    public void Addscanrts(String awd) {

        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("awb", awd);

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.ADD_SCANRTS, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            String str = serviceResponse.Message;
                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    cancel_dialog();

                                    if (obj.getString("statuscode").equalsIgnoreCase("200")) {
                                        vibration();
                                    } else {
                                        showDialog(Barcode_Scanner.this, obj.getString("body"), obj.getString("statuscode"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    cancel_dialog();
                                    showDialog(Barcode_Scanner.this, "Error", "");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                cancel_dialog();
                                showDialog(Barcode_Scanner.this, "Error", "");
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            cancel_dialog();
                            try {
                                if (obj.getString("message").equals("The incoming token has expired")) {
                                    showfailDialog(Barcode_Scanner.this);
                                }
                            } catch (JSONException e) {
                                showDialog(Barcode_Scanner.this, obj.getString("body"), "");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancel_dialog();
                            showDialog(Barcode_Scanner.this, "Error", "");

                        }
                    }
                });
    }

    public void vibration() {

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
        restartPreviewAndDecode();
    }

    public void restartPreviewAndDecode() {
        if (zXingScannerView == null) {

            zXingScannerView = new ZXingScannerView(Barcode_Scanner.this);
            viewGroup.addView(zXingScannerView);
        }
        zXingScannerView.setResultHandler(Barcode_Scanner.this);
        zXingScannerView.startCamera(camera_id);
        setupBarcodeFormats();
    }

    public void GetDetail(String awd) {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("awb", awd);

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.GET_DETAIL, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            String str = serviceResponse.Message;
                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    JSONArray bodyarray = obj.getJSONArray("body");
                                    if (bodyarray.length() != 0) {
                                        Detaillist.clear();
                                        for (int i = 0; i < bodyarray.length(); i++) {
                                            JSONObject bodyobj = bodyarray.getJSONObject(i);
                                            Iterator<String> iter = bodyobj.keys();
                                            while (iter.hasNext()) {
                                                Detail_get_set detail = new Detail_get_set();
                                                String key = iter.next();
                                                detail.setKey(key);
                                                detail.setKeyDetail(bodyobj.getString(key));
                                                Detaillist.add(detail);
                                            }
                                        }
                                        cancel_dialog();
                                        showDetailDialog(Barcode_Scanner.this);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    cancel_dialog();
                                    showDialog(Barcode_Scanner.this, "Error", "");

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                cancel_dialog();
                                showDialog(Barcode_Scanner.this, "Error", "");

                            }
                        }
                    }

                    public void CallFailure(String str) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            cancel_dialog();
                            try {
                                if (obj.getString("message").equals("The incoming token has expired")) {
                                    showfailDialog(Barcode_Scanner.this);
                                }
                            } catch (JSONException e) {
                                showDialog(Barcode_Scanner.this, obj.getString("body"), "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancel_dialog();
                            showDialog(Barcode_Scanner.this, "Error", "");

                        }
                    }
                });
    }

    public void Addscanreturn(String awd, String condition) {
        showProgressDialog("Please Wait");
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("awb", awd);
            jSONObject2.put("condition", condition);

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.ADD_SCANRETURN, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            String str = serviceResponse.Message;
                            try {

                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    cancel_dialog();
                                    Log.e("finish", ".....");

                                    if (obj.getString("statuscode").equalsIgnoreCase("200")) {
                                        vibration();
                                    } else {
                                        showDialog(Barcode_Scanner.this, obj.getString("body"), obj.getString("statuscode"));
                                    }
                                    //mScannerView.resumeCameraPreview(BarcodeScanner.this);


                                } catch (JSONException e) {
                                    cancel_dialog();
                                    showDialog(Barcode_Scanner.this, "Error", "");

                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                cancel_dialog();
                                showDialog(Barcode_Scanner.this, "Error", "");

                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        Log.e("Failure", ".....");
                        try {
                            JSONObject obj = new JSONObject(str);

                            try {
                                if (obj.getString("message").equals("The incoming token has expired")) {
                                    showfailDialog(Barcode_Scanner.this);
                                }
                            } catch (JSONException e) {
                                showDialog(Barcode_Scanner.this, obj.getString("body"), "");
                            }

                        } catch (JSONException e) {
                            cancel_dialog();
                            showDialog(Barcode_Scanner.this, "Error", "");

                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showfailDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText("Try Again");
        ImageView img = (ImageView) dialog.findViewById(R.id.image);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Result", "........." + sacnresult);
                cognito.RefreshTokenTryAgain(Barcode_Scanner.this, sessionManager.getUserid(), getIntent().getExtras().getString("type"), getIntent().getExtras().getString("condition"), sacnresult);
                restartPreviewAndDecode();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialog(Activity activity, String msg, String status) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        ImageView img = (ImageView) dialog.findViewById(R.id.image);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);

        if (msg.equals("Error")) {
            cognito.RefreshToken(this, sessionManager.getUserid(), "");
        }

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                restartPreviewAndDecode();
            }
        });

        dialog.show();
    }

    public void showDetailDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.detail_dialog);
        RecyclerView recyclerView;

        recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerdetail);
        Log.e("Size", "....." + Detaillist.size());
        adapter = new MyListAdapter(Detaillist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sacnresult = "";
                restartPreviewAndDecode();
            }
        });
        dialog.show();
    }

    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private ArrayList<Detail_get_set> listdata;

        public MyListAdapter(ArrayList<Detail_get_set> listdata) {
            this.listdata = listdata;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.detail_content_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Detail_get_set myListData = listdata.get(position);
            holder.textkey.setText(myListData.getKey());
            holder.txtdata.setText(myListData.getKeyDetail());

        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textkey, txtdata;

            public ViewHolder(View itemView) {
                super(itemView);
                this.txtdata = (TextView) itemView.findViewById(R.id.txtdata);
                this.textkey = (TextView) itemView.findViewById(R.id.txtkey);
            }
        }
    }

}