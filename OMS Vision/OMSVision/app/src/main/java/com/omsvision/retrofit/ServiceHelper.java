package com.omsvision.retrofit;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceHelper {
    public static String URL = "https://api.omsvision.in/v1/";

    public static final String ADD_SCANRTS = "scan-rts";
    public static final String GET_DETAIL = "get-order-details";
    public static final String ADD_SCANRETURN = "add-return-entry";
    public static final String GET_ACCOUNT = "get-user-accounts";
    public static final String FETCH_RTS = "fetch-rts-orders";
    public static final String FETCH_RETURN = "fetch-return-orders";
    public static final String FETCH_CANCEL = "fetch-cancel-orders";
    public static final String FETCH_PENDING = "fetch-pending";
    public static final String FETCH_PAYMENT = "fetch-payment-data";

    public static final String COMMON_ERROR = "Data not found!\n Please try again.";

    public RequestMethod RequestMethodType = RequestMethod.GET;
    public ServiceHelperDelegate m_delegate = null;
    String m_methodName = null;
    private ArrayList<String> m_params = new ArrayList<>();
    String post_data;
    String token = "";
    String url = "";

    public enum RequestMethod {
        GET,
        POST,
        PUT
    }

    public interface ServiceHelperDelegate {
        void CallFailure(String str);

        void CallFinish(ServiceResponse serviceResponse);
    }

    public ServiceHelper(String str, RequestMethod requestMethod, String str2, String token) {
        this.m_methodName = str;
        this.RequestMethodType = requestMethod;
        this.post_data = str2;
        this.token = token;
    }


    public String getFinalUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append(this.m_methodName.toString());
        if (this.RequestMethodType == RequestMethod.GET && this.m_params.size() > 0) {
            String join =join(this.m_params, "&");
            sb.append("?");
            sb.append(join);
        }
        Log.i("URL==>", sb.toString());
        return sb.toString();
    }
    public String join(List<? extends CharSequence> list, String str) {
        int length = str.length();
        Iterator<? extends CharSequence> it = list.iterator();
        int i = 0;
        if (it.hasNext()) {
            i = 0 + ((CharSequence) it.next()).length() + length;
        }
        StringBuilder sb = new StringBuilder(i);
        Iterator<? extends CharSequence> it2 = list.iterator();
        if (it2.hasNext()) {
            sb.append((CharSequence) it2.next());
            while (it2.hasNext()) {
                sb.append(str);
                sb.append((CharSequence) it2.next());
            }
        }
        return sb.toString();
    }

    public void call(ServiceHelperDelegate serviceHelperDelegate) {
        this.m_delegate = serviceHelperDelegate;
        new CallServiceAsync((String) null).execute(new Void[0]);
    }

    public ServiceResponse call() {
        String str;
        Request request;
        ServiceResponse serviceResponse = new ServiceResponse();
        StringBuilder sb = new StringBuilder();
        OkHttpClient build = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        String str2 = this.url;
        if (str2 == null || str2.length() <= 0) {
            str = getFinalUrl();
        } else {
            str = this.url;
        }
        if (this.RequestMethodType == RequestMethod.GET) {
            request = new Request.Builder().url(str).addHeader("Content-Type", "application/json; charset=utf-8").addHeader("Authorization", this.token).build();
        } else if (this.RequestMethodType == RequestMethod.PUT) {
            MediaType parse = MediaType.parse("application/json; charset=UTF-8");
            if (this.post_data.length() > 0) {
                request = new Request.Builder().url(this.url).put(RequestBody.create(parse, this.post_data)).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("Accept", "application/json, text/plain, */*").addHeader("Authorization", this.token).build();
            } else {
                request = new Request.Builder().url(this.url).put(RequestBody.create(parse, "")).build();
            }
        } else {
            MediaType parse2 = MediaType.parse("application/json");
            if (this.post_data.length() > 0) {
                request = new Request.Builder().url(str).post(RequestBody.create(parse2, this.post_data)).addHeader("Content-Type", "application/json; charset=utf-8").addHeader("Authorization", this.token).build();
            } else {
                request = new Request.Builder().url(getFinalUrl()).post(RequestBody.create(parse2, "")).build();
            }
        }
        try {
            Response execute = build.newCall(request).execute();
            Log.i("RESPONSE CODE==>", execute.code() + "");
            if (execute.code() == 200) {
                serviceResponse.Message = "Loaded Successfully";
                serviceResponse.isSuccess = true;
                serviceResponse.statuscode = 200;
                sb.append(execute.body().string());
                serviceResponse.RawResponse = sb.toString();
                Log.i("RESPONSE==>", serviceResponse.RawResponse);
            } else {
                sb.append(execute.body().string());
                serviceResponse.Message = sb.toString();
                serviceResponse.isSuccess = false;
                serviceResponse.statuscode = execute.code();
                serviceResponse.RawResponse = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            serviceResponse.Message = "Internet connection problem.\nPlease try again.";
            serviceResponse.isSuccess = false;
            serviceResponse.statuscode = 1;
            serviceResponse.RawResponse = "";
        }
        return serviceResponse;
    }

    public ServiceResponse callurl(String str) {
        Request request = null;
        ServiceResponse serviceResponse = new ServiceResponse();
        StringBuilder sb = new StringBuilder();
        OkHttpClient okHttpClient = new OkHttpClient();
      if (this.RequestMethodType == RequestMethod.POST) {
            MediaType parse = MediaType.parse("application/json");
            if (this.post_data.length() > 0) {
                request = new Request.Builder().url(str).post(RequestBody.create(parse, this.post_data)).addHeader("Authorization", this.token).build();
            } else {
                request = new Request.Builder().url(str).post(RequestBody.create(parse, "")).build();
            }
        }
        try {
            Response execute = okHttpClient.newCall(request).execute();
            if (execute.code() == 200) {
                serviceResponse.isSuccess = true;
                serviceResponse.statuscode = 200;
                sb.append(execute.body().string());
                serviceResponse.RawResponse = sb.toString();
            } else {
                sb.append(execute.body().string());
                serviceResponse.Message = sb.toString();
                serviceResponse.isSuccess = false;
                serviceResponse.statuscode = execute.code();
                serviceResponse.RawResponse = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            serviceResponse.Message = "Internet connection problem.\nPlease try again.";
            serviceResponse.isSuccess = false;
            serviceResponse.statuscode = 1;
            serviceResponse.RawResponse = "";
        }
        return serviceResponse;
    }


    class CallServiceAsync extends AsyncTask<Void, Void, ServiceResponse> {
        String m_url = null;

        public CallServiceAsync(String str) {
            this.m_url = str;
        }

        public ServiceResponse doInBackground(Void... voidArr) {
            String str = this.m_url;
            if (str == null) {
                return ServiceHelper.this.call();
            }
            return ServiceHelper.this.callurl(str);
        }

        public void onPostExecute(ServiceResponse serviceResponse) {
            if (serviceResponse != null) {
                if (serviceResponse.statuscode == 200) {
                    if (ServiceHelper.this.m_delegate != null) {
                        ServiceHelper.this.m_delegate.CallFinish(serviceResponse);
                    }
                } else if (serviceResponse.statuscode == 201) {
                    if (ServiceHelper.this.m_delegate != null) {
                        ServiceHelper.this.m_delegate.CallFinish(serviceResponse);
                    }
                } else if (ServiceHelper.this.m_delegate != null) {
                    ServiceHelper.this.m_delegate.CallFailure(serviceResponse.Message);
                }
            } else if (ServiceHelper.this.m_delegate != null) {
                ServiceHelper.this.m_delegate.CallFailure(ServiceHelper.COMMON_ERROR);
            }
            super.onPostExecute(serviceResponse);
        }
    }
}
