package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shockwave.pdfium.PdfDocument;
import com.templatemela.camscanner.R;
import java.util.List;

public class PDFViewerActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    Activity activity = PDFViewerActivity.this;
    private static final String TAG = "PDFViewerActivity";
    private int page_no = 0;
    private PDFView pdfView;
    protected Uri pdf_uri;
    protected String title;
    protected TextView tv_page;
    protected TextView tv_title;

    private FrameLayout adContainerView;
    private AdView adView;
    private AdSize adSize;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pdfviewer);
        init();
        BannerAds();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PDFViewerActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void BannerAds() {
        try {
            adContainerView = findViewById(R.id.banner_ad_view_container);
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            float f = displayMetrics.density;
            float width = (float) adContainerView.getWidth();
            if (width == 0.0f) {
                width = (float) displayMetrics.widthPixels;
            }
            adSize = AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, (int) (width / f));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) adContainerView.getLayoutParams();
            layoutParams.height = adSize.getHeightInPixels(this);
            adContainerView.setLayoutParams(layoutParams);
            adContainerView.post(new Runnable() {
                public final void run() {
                    ShowAds();
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void ShowAds() {
        try {
            adView = new AdView(activity);
            adView.setAdUnitId(getString(R.string.Banner_ad_id));
            adContainerView.removeAllViews();
            adContainerView.addView(adView);
            adView.setAdSize(adSize);
            adView.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        tv_title = findViewById(R.id.tv_title);
        tv_page = findViewById(R.id.tv_page);
        pdfView = findViewById(R.id.pdfView);
        pdfView.setBackgroundColor(getResources().getColor(R.color.bg_color));
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        pdf_uri = Uri.parse(getIntent().getStringExtra("pdf_path"));
        loadPDF(pdf_uri);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPDF(Uri uri) {
        pdfView.fromUri(uri).defaultPage(page_no).onPageChange(this).enableAnnotationRendering(true).onLoad(this).scrollHandle(new DefaultScrollHandle(this)).spacing(12).onPageError(this).load();
    }

    @Override
    public void loadComplete(int i) {
        PdfDocument.Meta documentMeta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + documentMeta.getTitle());
        Log.e(TAG, "author = " + documentMeta.getAuthor());
        Log.e(TAG, "subject = " + documentMeta.getSubject());
        Log.e(TAG, "keywords = " + documentMeta.getKeywords());
        Log.e(TAG, "creator = " + documentMeta.getCreator());
        Log.e(TAG, "producer = " + documentMeta.getProducer());
        Log.e(TAG, "creationDate = " + documentMeta.getCreationDate());
        Log.e(TAG, "modDate = " + documentMeta.getModDate());
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> list, String str) {
        for (PdfDocument.Bookmark next : list) {
            Log.e(TAG, String.format("%s %s, p %d", str, next.getTitle(), Long.valueOf(next.getPageIdx())));
            if (next.hasChildren()) {
                List<PdfDocument.Bookmark> children = next.getChildren();
                printBookmarksTree(children, str + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int i, int i2) {
        page_no = i;
        tv_page.setText(String.format("%s / %s", Integer.valueOf(i + 1), Integer.valueOf(i2)));
    }

    @Override
    public void onPageError(int i, Throwable th) {
        Log.e(TAG, "Cannot load page " + i);
    }
}
