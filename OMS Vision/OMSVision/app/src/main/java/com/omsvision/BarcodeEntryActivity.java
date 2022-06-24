package com.omsvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omsvision.model.Detail_get_set;
import com.omsvision.retrofit.ServiceHelper;
import com.omsvision.retrofit.ServiceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static android.service.controls.ControlsProviderService.TAG;

public class BarcodeEntryActivity extends BaseActivity {
    EditText edtawd, edtsuborder;
    ImageView btnadd;
    ArrayList<Detail_get_set> Detaillist = new ArrayList<>();

    MyListAdapter adapter;

    boolean call=true;
    final int TYPING_TIMEOUT = 1000; // 1 seconds timeout
    final Handler timeoutHandler = new Handler();
    final Runnable typingTimeout = new Runnable() {
        public void run() {
            if (call){
           // call = false;
            btnadd.callOnClick();}
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_entry);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setText(getIntent().getExtras().getString("type"));

        edtawd = (EditText) findViewById(R.id.edtawd);
        edtsuborder = (EditText) findViewById(R.id.edtsuborder);
        btnadd = (ImageView) findViewById(R.id.btnadd);
        edtawd.requestFocus();
        edtawd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnadd.callOnClick();
                }
                return handled;
            }
        });
        edtsuborder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnadd.callOnClick();
                }
                return handled;
            }
        });
        edtawd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!edtsuborder.getText().toString().equalsIgnoreCase("")) {
                    edtsuborder.setText("");
                }
                return false;
            }
        });

        edtsuborder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!edtawd.getText().toString().equalsIgnoreCase("")) {

                    edtawd.setText("");
                }
                return false;
            }
        });

       /* edtawd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&

                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    btnadd.callOnClick();
                    return true;
                }
                return false;
            }
        });*/
        edtsuborder.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    btnadd.callOnClick();
                    return true;
                }
                return false;
            }
        });
        edtawd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timeoutHandler.removeCallbacks(typingTimeout);
                if (edtawd.getText().toString().trim().length() > 0) {
                    timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);
                    if (!call) {
                        call = true;
                    }
                } else {
                    call = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtsuborder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timeoutHandler.removeCallbacks(typingTimeout);
                if (edtsuborder.getText().toString().trim().length() > 0) {
                    timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);
                    if (!call) {
                        call = true;
                    }
                } else {
                    call = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtawd.getText().toString().equals("") && edtsuborder.getText().toString().equals("")) {
                    Toast.makeText(BarcodeEntryActivity.this, "Enter Any Number", Toast.LENGTH_LONG).show();
                } else {
                    call=false;
                    if (isNetworkConnected()) {
                        if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.scanrts))) {
                            Addscanrts(edtawd.getText().toString(), edtsuborder.getText().toString());
                        }
                        if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.getdetail))) {
                            GetDetail(edtawd.getText().toString(), edtsuborder.getText().toString());
                        }
                        if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.scanreturn))) {

                            Addscanreturn(edtawd.getText().toString(), edtsuborder.getText().toString(), getIntent().getExtras().getString("condition"));
                        }
                    } else {
                        Toast.makeText(BarcodeEntryActivity.this, "Connet Internet ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void Addscanrts(String awd, String suborder) {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();

        if (!awd.equals("")) {
            try {
                jSONObject2.put("username", sessionManager.getUserid());
                jSONObject2.put("awb", awd);

            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                jSONObject2.put("username", sessionManager.getUserid());
                jSONObject2.put("suborder_no", suborder);

            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        Log.e("json", "....." + jSONObject.toString());

        new ServiceHelper(ServiceHelper.ADD_SCANRTS, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            String str = serviceResponse.Message;
                            try {
                                cancel_dialog();
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    cancel_dialog();
                                    if (obj.getString("statuscode").equalsIgnoreCase("200")) {
                                        vibration();
                                    } else {
                                        showDialog(BarcodeEntryActivity.this, obj.getString("body"), obj.getString("statuscode"));
                                    }                                    //showDialog(BarcodeEntryActivity.this, obj.getString("body"), obj.getString("statuscode"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                cancel_dialog();

                            } catch (Exception e) {
                                cancel_dialog();
                                showDialog(BarcodeEntryActivity.this, "Error", "");
                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        try {
                            JSONObject obj = new JSONObject(str);
                            try {
                                if (obj.getString("message").equals("The incoming token has expired")) {
                                    showfailDialog(BarcodeEntryActivity.this);
                                }
                            } catch (JSONException e) {
                                showDialog(BarcodeEntryActivity.this, obj.getString("body"), "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancel_dialog();
                            showDialog(BarcodeEntryActivity.this, "Error", "");

                        }
                    }
                });
    }

    public void GetDetail(String awd, String suborder) {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        if (!awd.equals("")) {
            try {
                jSONObject2.put("username", sessionManager.getUserid());
                jSONObject2.put("awb", awd);

            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                jSONObject2.put("username", sessionManager.getUserid());
                jSONObject2.put("suborder_no", suborder);

            } catch (JSONException e2) {
                e2.printStackTrace();
            }
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
                                    showDetailDialog(BarcodeEntryActivity.this);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                cancel_dialog();
                                showDialog(BarcodeEntryActivity.this, "Error", "");

                            }

                        }
                    }

                    public void CallFailure(String str) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            cancel_dialog();
                            try {
                                if (obj.getString("message").equals("The incoming token has expired")) {
                                    showfailDialog(BarcodeEntryActivity.this);
                                }
                            } catch (JSONException e) {
                                showDialog(BarcodeEntryActivity.this, obj.getString("body"), "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancel_dialog();
                            showDialog(BarcodeEntryActivity.this, "Error", "");

                        }
                    }
                });
    }

    public void showDetailDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.detail_dialog);
        RecyclerView recyclerView;

        recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerdetail);
        adapter = new MyListAdapter(Detaillist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtawd.setText("");
                edtsuborder.setText("");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void Addscanreturn(String awd, String suborder, String condition) {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();

        if (!awd.equals("")) {
            try {
                jSONObject2.put("username", sessionManager.getUserid());
                jSONObject2.put("awb", awd);
                jSONObject2.put("condition", condition);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                jSONObject2.put("username", sessionManager.getUserid());
                jSONObject2.put("suborder_no", suborder);
                jSONObject2.put("condition", condition);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
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
                                    if (obj.getString("statuscode").equalsIgnoreCase("200")) {
                                        vibration();
                                    } else {
                                        showDialog(BarcodeEntryActivity.this, obj.getString("body"), obj.getString("statuscode"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    cancel_dialog();
                                    showDialog(BarcodeEntryActivity.this, "Error", "");

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                cancel_dialog();
                                showDialog(BarcodeEntryActivity.this, "Error", "");

                            }
                        }
                    }

                    public void CallFailure(String str) {
                        try {
                            JSONObject obj = new JSONObject(str);

                            cancel_dialog();
                            try {
                                if (obj.getString("message").equals("The incoming token has expired")) {
                                    showfailDialog(BarcodeEntryActivity.this);
                                }
                            } catch (JSONException e) {
                                showDialog(BarcodeEntryActivity.this, obj.getString("body"), "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancel_dialog();
                            showDialog(BarcodeEntryActivity.this, "Error", "");

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
                dialog.dismiss();
                if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.scanrts))) {
                    Addscanrts(edtawd.getText().toString(), edtsuborder.getText().toString());
                }
                if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.getdetail))) {
                    GetDetail(edtawd.getText().toString(), edtsuborder.getText().toString());
                }
                if (getIntent().getExtras().getString("type").equals(getResources().getString(R.string.scanreturn))) {
                    Addscanreturn(edtawd.getText().toString(), edtsuborder.getText().toString(), getIntent().getExtras().getString("condition"));
                }
            }
        });
        dialog.show();
    }

    public void vibration() {

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
        edtawd.setText("");
        edtsuborder.setText("");

    }

    public void showDialog(Activity activity, String msg, String status) {
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                edtawd.setText("");
                edtsuborder.setText("");
                dialog.dismiss();
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