package com.omsvision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.REGION;


public class LoginActivity extends BaseActivity {

    private EditText userLoginEditText, passwordLoginEditText;
    ImageButton btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userLoginEditText = findViewById(R.id.edtEmail);
        passwordLoginEditText = findViewById(R.id.edtpass);
        btnlogin = findViewById(R.id.loginButton);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    login(userLoginEditText.getText().toString(), passwordLoginEditText.getText().toString());
                }else
                {
                    Toast.makeText(LoginActivity.this,"Connet Internet ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void login(String email, String password) {
        cognito.userLogin(email, password);

    }


}