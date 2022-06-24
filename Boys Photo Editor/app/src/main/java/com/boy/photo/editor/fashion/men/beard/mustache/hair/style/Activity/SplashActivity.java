package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;

public class SplashActivity extends AppCompatActivity {

    Activity activity = SplashActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}