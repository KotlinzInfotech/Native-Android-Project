package com.omsvision;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken;
import com.amazonaws.regions.Regions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.service.controls.ControlsProviderService.TAG;

public class Cognito {
    private String poolID = DataManager.poolid;
    private String clientID = DataManager.client_id;
    private String clientSecret = DataManager.client_secret;
    private Regions awsRegion = Regions.AP_SOUTH_1;

    private CognitoUserPool userPool;
    private String userPassword = "", userEmail, type, scanresult, condition;                        // Used for Login
    SimpleDateFormat inputFormat;
    SimpleDateFormat outputFormat;
    Context context;
    public static Dialog dialog;

    public Cognito(Context mcontext) {
        dialog = new Dialog(mcontext);
        context = mcontext;
        userPool = new CognitoUserPool(context, this.poolID, this.clientID, this.clientSecret, this.awsRegion);
    }

    public void userLogin(String userId, String password) {
        showProgressDialog("Verifing User");
        CognitoUser cognitoUser = userPool.getUser(userId);
        this.userPassword = password;
        this.userEmail = userId;
        cognitoUser.getSessionInBackground(authenticationHandler);
    }

    public void RefreshToken(Context context, String userId, String type) {

        this.context = context;
        CognitoUser cognitoUser = userPool.getUser(userId);
        this.userEmail = userId;
        this.type = type;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = sdf.format(c.getTime());
        Date todaydate = null;
        Date tokendate = null;
        try {
            todaydate = sdf.parse(formattedDate);
            tokendate = sdf.parse(myApp.sessionManager.getTokenExpiry());

            int compareResult = todaydate.compareTo(tokendate);

            if (compareResult >= 0) {
                cognitoUser.getSessionInBackground(authenticationHandler);
            } else {
                Log.e("Refresh Token", "No Need To Refresh");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void RefreshTokenTryAgain(Context context, String userId, String type, String condition, String scanresult) {

        this.context = context;
        CognitoUser cognitoUser = userPool.getUser(userId);
        this.userEmail = userId;
        this.type = type;
        this.scanresult = scanresult;
        this.condition = condition;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = sdf.format(c.getTime());
        Date todaydate = null;
        Date tokendate = null;
        try {
            todaydate = sdf.parse(formattedDate);
            tokendate = sdf.parse(myApp.sessionManager.getTokenExpiry());

            int compareResult = todaydate.compareTo(tokendate);

            if (compareResult >= 0) {
                showProgressDialog("Verifing User");
                cognitoUser.getSessionInBackground(authenticationHandler);
            } else {
                Log.e("Refresh Token", "No Need To Refresh");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {

            String idToken = userSession.getAccessToken().getJWTToken();

            inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            Date tokenexpirydate = null;

            try {
                tokenexpirydate = inputFormat.parse(String.valueOf(userSession.getAccessToken().getExpiration()));
                String formattedDate = outputFormat.format(tokenexpirydate);
                myApp.sessionManager.setTokenExpiry(formattedDate);
                myApp.setalarm(context);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            myApp.sessionManager.setJWTToken("Bearer " + idToken);
            myApp.sessionManager.createLoginSession(userEmail);
            Log.e("Token...............", myApp.sessionManager.getJWTToken());
            if (!userPassword.equals("")) {
                if (myApp.sessionManager.isLoggedIn()) {
                    dialog.dismiss();
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            } else if (type.equalsIgnoreCase(context.getResources().getString(R.string.scanrts))) {
                dialog.dismiss();
                myApp.barcodeScanner.Addscanrts(scanresult);
            } else if (type.equalsIgnoreCase(context.getResources().getString(R.string.getdetail))) {
                dialog.dismiss();
                myApp.barcodeScanner.GetDetail(scanresult);
            } else if (type.equalsIgnoreCase(context.getResources().getString(R.string.scanreturn))) {
                dialog.dismiss();
                myApp.barcodeScanner.Addscanreturn(scanresult, condition);
            } else {

            }

        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, userPassword, null);
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

        }

        @Override
        public void onFailure(Exception exception) {
Log.e("Fail","......");
            myApp.sessionManager.logoutUser();
        }
    };

    public void showProgressDialog(String msg) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_dialog_layout);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        dialog.show();

    }

}
