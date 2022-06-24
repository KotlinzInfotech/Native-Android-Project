package com.omsvision;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {

    CardView btnscan, btnfetch;
    LinearLayout btnlogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnscan = (CardView) findViewById(R.id.btnscan);
        btnfetch = (CardView) findViewById(R.id.btnFetch);

        btnlogout = (LinearLayout) findViewById(R.id.logout);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(HomeActivity.this, ScanDataActivity.class);
                    startActivity(intent);

            }
        });
        btnfetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FetchActivity.class);
                startActivity(intent);
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager.logoutUser();
            }
        });

        cognito.RefreshToken(this,sessionManager.getUserid(), "");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cognito.RefreshToken(this,sessionManager.getUserid(), "");
    }
}