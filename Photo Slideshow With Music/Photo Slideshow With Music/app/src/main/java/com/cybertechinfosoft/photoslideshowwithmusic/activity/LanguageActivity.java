package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.LanguageSelectionAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Language;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.Locale;

public class LanguageActivity extends BaseActivity {
    private ArrayList<Language> langAndLocalesList = new ArrayList();
    private LanguageSelectionAdapter mAdapter;
    RecyclerView settingsRecyclerView;
    private Toolbar toolbar;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_language);
        bindView();
        init();
        PutAnalyticsEvent();
    }


    private void bindView() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "LanguageActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void init() {
        setSupportActionBar(this.toolbar);
        TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText(getString(R.string.change_language));
        Utils.setFont(this, textView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        this.settingsRecyclerView = (RecyclerView) findViewById(R.id.settingsRecyclerView);
        this.settingsRecyclerView.setLayoutManager(linearLayoutManager);
        this.settingsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        loadLanguageList();
        this.mAdapter = new LanguageSelectionAdapter(this, this.langAndLocalesList);
        this.settingsRecyclerView.setAdapter(this.mAdapter);
    }

    private void loadLanguageList() {
        for (String str : getResources().getStringArray(R.array.ln)) {
            Locale locale = new Locale(str);
            this.langAndLocalesList.add(new Language(str, locale.getDisplayLanguage(locale), locale.getDisplayLanguage(new Locale(getString(R.string.en)))));
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }
}
