package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.cybertechinfosoft.photoslideshowwithmusic.util.LanguageHelper;

public class BaseActivity extends AppCompatActivity {
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageHelper.onAttach(context));
    }
}
