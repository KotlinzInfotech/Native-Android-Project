package com.omsvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.omsvision.model.Account_get_set;
import com.omsvision.retrofit.ServiceHelper;
import com.omsvision.retrofit.ServiceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class FetchActivity extends BaseActivity {
    TextView txtstartdate, txtenddate;
    LinearLayout lstartdate, lenddate;
    EditText spinaccount;
    ImageView btnFrts, btnFreturn, btnFcancel, btnFPending, btnFpayment;
    Calendar cldr;
    int day, month, year;
    DatePickerDialog picker;
    ArrayList<Account_get_set> accountList = new ArrayList<>();
    MyListAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<String> Emaillist = new ArrayList<>();
    ArrayList<String> SIdList = new ArrayList<>();
    SimpleDateFormat inputFormat, outputFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        myApp ma = (myApp) getApplication();
        ma.fetchActivity = this;
        inputFormat = new SimpleDateFormat("d/M/yyyy");
        outputFormat = new SimpleDateFormat("dd/MM/yy");
        androidx.appcompat.widget.Toolbar toolbarTop = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_top);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setText("Fetch Orders");
        cldr = Calendar.getInstance();
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);
        txtstartdate = (TextView) findViewById(R.id.txtstartdate);
        txtenddate = (TextView) findViewById(R.id.txtenddate);

        lstartdate = (LinearLayout) findViewById(R.id.lstartdate);
        lenddate = (LinearLayout) findViewById(R.id.lenddate);

        spinaccount = (EditText) findViewById(R.id.spinaccount);

        btnFrts = (ImageView) findViewById(R.id.btnFRts);
        btnFreturn = (ImageView) findViewById(R.id.btnFreturnRts);
        btnFcancel = (ImageView) findViewById(R.id.btnFcancelorder);
        btnFpayment = (ImageView) findViewById(R.id.btnFPayment);
        btnFPending = (ImageView) findViewById(R.id.btnFPending);

        lstartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = new DatePickerDialog(FetchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Date date = null;
                                try {
                                    date = inputFormat.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String formattedDate = outputFormat.format(date);
                                txtstartdate.setText(formattedDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        lenddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = new DatePickerDialog(FetchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                                Date date = null;
                                try {
                                    date = inputFormat.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String formattedDate = outputFormat.format(date);
                                txtenddate.setText(formattedDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        GetAccount();
        btnFrts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Emaillist.size() <= 0 || SIdList.size() <= 0) {
                    Toast.makeText(FetchActivity.this, "Select Account", Toast.LENGTH_LONG).show();
                } else {
                    FetchRTs();
                }
            }
        });
        btnFcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Emaillist.size() <= 0 || SIdList.size() <= 0) {
                    Toast.makeText(FetchActivity.this, "Select Account", Toast.LENGTH_LONG).show();
                } else {
                    FetchCancel();
                }
            }
        });
        btnFPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Emaillist.size() <= 0 || SIdList.size() <= 0) {
                    Toast.makeText(FetchActivity.this, "Select Account", Toast.LENGTH_LONG).show();
                } else {
                    FetchPending();
                }
            }
        });
        btnFpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Emaillist.size() <= 0 || SIdList.size() <= 0) {
                    Toast.makeText(FetchActivity.this, "Select Account", Toast.LENGTH_LONG).show();
                } else {
                    FetchPayment();
                }
            }
        });
        btnFreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Emaillist.size() <= 0 || SIdList.size() <= 0) {
                    Toast.makeText(FetchActivity.this, "Select Account", Toast.LENGTH_LONG).show();
                } else if (txtstartdate.getText().toString().equals("Start Date")||txtstartdate.getText().toString().equals("")) {
                    Toast.makeText(FetchActivity.this, "Select Start Date", Toast.LENGTH_LONG).show();

                } else if (txtenddate.getText().toString().equals("End Date")||txtenddate.getText().toString().equals("")) {
                    Toast.makeText(FetchActivity.this, "Select End Date", Toast.LENGTH_LONG).show();
                } else {
                    FetchReturn();
                }
            }
        });
        spinaccount.setText(Emaillist.size() + " items selected");

        spinaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(FetchActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.multi_selection_spinner);
                if (accountList.size() == 0) {
                    GetAccount();
                }
                recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerspin);
                adapter = new MyListAdapter(accountList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(FetchActivity.this));
                recyclerView.setAdapter(adapter);
                dialog.show();
            }
        });
    }

    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private ArrayList<Account_get_set> listdata;

        public MyListAdapter(ArrayList<Account_get_set> listdata) {
            this.listdata = listdata;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.layout_text_check, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Account_get_set myListData = listdata.get(position);
            holder.textView.setText(myListData.getEmailid());
            if (myListData.getIsselect()) {
                holder.textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                holder.textView.setTextColor(getResources().getColor(R.color.maintext));
            }
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myListData.getIsselect()) {
                        myListData.setIsselect(false);
                        holder.textView.setTextColor(getResources().getColor(R.color.maintext));
                        Emaillist.remove(myListData.getEmailid());
                        SIdList.remove(String.valueOf(myListData.getSupplier_id()));

                    } else {
                        myListData.setIsselect(true);
                        holder.textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        Emaillist.add(myListData.getEmailid());
                        SIdList.add(String.valueOf(myListData.getSupplier_id()));
                    }
                    spinaccount.setText(Emaillist.size() + " items selected");

                }
            });
            spinaccount.setText(Emaillist.size() + " items selected");
        }


        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public LinearLayout linearLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                this.imageView = (ImageView) itemView.findViewById(R.id.imgtrue);
                this.textView = (TextView) itemView.findViewById(R.id.txtemail);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.lraw);
            }
        }
    }

    public void GetAccount() {
        accountList.clear();
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("onlydetails", true);

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.GET_ACCOUNT, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {

                        if (serviceResponse.Message != null) {

                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    if (obj.getInt("statuscode") == 200) {
                                        JSONObject body = obj.getJSONObject("body");
                                        JSONArray items = body.getJSONArray("Items");
                                        for (int i = 0; i < items.length(); i++) {
                                            JSONObject objdata = items.getJSONObject(i);
                                            Account_get_set account = new Account_get_set();
                                            account.setEmailid(objdata.getString("tp_emailid"));
                                            account.setSupplier_id(objdata.getInt("supplier_id"));
                                            account.setIsselect(false);
                                            accountList.add(account);
                                        }
                                        cancel_dialog();
                                        adapter.notifyDataSetChanged();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        try {
                            JSONObject obj = new JSONObject(str);

                            if (obj.getString("message").equals("The incoming token has expired")) {
                                showDialog(FetchActivity.this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void FetchRTs() {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();


        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("tp_emailid", new JSONArray(Emaillist));
            jSONObject2.put("supplier_id", new JSONArray(SIdList));

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.FETCH_RTS, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    if (obj.getInt("statusCode") == 200) {
                                        cancel_dialog();
                                        Toast.makeText(FetchActivity.this, "RTS Order Fetch Successfully", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getString("message").equals("The incoming token has expired")) {
                                showDialog(FetchActivity.this);
                            } else {
                                Toast.makeText(FetchActivity.this, obj.getString("body"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancel_dialog();
                        }
                    }
                });

    }

    public void FetchReturn() {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("tp_emailid", new JSONArray(Emaillist));
            jSONObject2.put("supplier_id", new JSONArray(SIdList));
            jSONObject2.put("start_date", txtstartdate.getText().toString());
            jSONObject2.put("end_date", txtenddate.getText().toString());

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.FETCH_RETURN, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {

                        if (serviceResponse.Message != null) {

                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    if (obj.getInt("statusCode") == 200) {
                                        Toast.makeText(FetchActivity.this, "Return RTS Order Fetch Successfully", Toast.LENGTH_LONG).show();
                                        cancel_dialog();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                cancel_dialog();

                            } catch (Exception e) {
                                e.printStackTrace();
                                cancel_dialog();

                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getString("message").equals("The incoming token has expired")) {
                                showDialog(FetchActivity.this);
                            } else {
                                Toast.makeText(FetchActivity.this, obj.getString("body"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void FetchCancel() {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("tp_emailid", new JSONArray(Emaillist));
            jSONObject2.put("supplier_id", new JSONArray(SIdList));

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.FETCH_CANCEL, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    if (obj.getInt("statusCode") == 200) {
                                        Toast.makeText(FetchActivity.this, "Cancel Order Fetch Successfully", Toast.LENGTH_LONG).show();
                                        cancel_dialog();
                                    }
                                } catch (JSONException e) {
                                    cancel_dialog();
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                cancel_dialog();
                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getString("message").equals("The incoming token has expired")) {
                                showDialog(FetchActivity.this);
                            } else {
                                Toast.makeText(FetchActivity.this, obj.getString("body"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            cancel_dialog();
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void FetchPending() {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("tp_emailid", new JSONArray(Emaillist));
            jSONObject2.put("supplier_id", new JSONArray(SIdList));

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.FETCH_PENDING, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())
                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    if (obj.getInt("statusCode") == 200) {
                                        cancel_dialog();
                                        Toast.makeText(FetchActivity.this, "Pending Data Fetch Successfully", Toast.LENGTH_LONG).show();

                                    }
                                } catch (JSONException e) {
                                    cancel_dialog();
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                cancel_dialog();
                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getString("message").equals("The incoming token has expired")) {
                                cancel_dialog();
                                //cognito.RefreshToken(FetchActivity.this,sessionManager.getUserid(), "fetchpending");
                                showDialog(FetchActivity.this);
                            } else {
                                cancel_dialog();
                                Toast.makeText(FetchActivity.this, obj.getString("body"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            cancel_dialog();
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void FetchPayment() {
        showProgressDialog("Please Wait");

        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("username", sessionManager.getUserid());
            jSONObject2.put("tp_emailid", new JSONArray(Emaillist));
            jSONObject2.put("supplier_id", new JSONArray(SIdList));

        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            jSONObject.put("body", jSONObject2);

        } catch (Exception e4) {
            jSONObject = null;
        }
        new ServiceHelper(ServiceHelper.FETCH_PAYMENT, ServiceHelper.RequestMethod.POST, jSONObject.toString(), sessionManager.getJWTToken())

                .call(new ServiceHelper.ServiceHelperDelegate() {
                    public void CallFinish(ServiceResponse serviceResponse) {
                        if (serviceResponse.Message != null) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(serviceResponse.RawResponse);
                                    if (obj.getInt("statusCode") == 200) {
                                        Toast.makeText(FetchActivity.this, "Payment Fetch Successfully", Toast.LENGTH_LONG).show();
                                        cancel_dialog();
                                    }
                                } catch (JSONException e) {
                                    cancel_dialog();
                                    e.printStackTrace();
                                }
                                cancel_dialog();
                            } catch (Exception e) {
                                cancel_dialog();
                                e.printStackTrace();
                            }
                        }
                    }

                    public void CallFailure(String str) {
                        cancel_dialog();
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getString("message").equals("The incoming token has expired")) {
                                showDialog(FetchActivity.this);
                            } else {
                                Toast.makeText(FetchActivity.this, obj.getString("body"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            cancel_dialog();
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showDialog(Activity activity) {
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
                myApp.cognito.RefreshToken(FetchActivity.this, sessionManager.getUserid(), "");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}