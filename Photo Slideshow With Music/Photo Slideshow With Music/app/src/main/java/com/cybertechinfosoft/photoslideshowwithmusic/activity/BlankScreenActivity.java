package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class BlankScreenActivity extends Activity {
    RelativeLayout rlLayout;
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.transperent_activity);
        this.rlLayout = (RelativeLayout)this.findViewById(R.id.rlblankscreen);
        if (this.getIntent().getStringExtra("dialogType").equals("NOTIFICATION")) {
            this.notificationHelp();
            return;
        }
        this.alertHelp();
        PutAnalyticsEvent();
    }
    private void notificationHelp() {
        final Dialog dialog = new Dialog(this, R.style.PauseDialogAnimation);
        dialog.setContentView(R.layout.setting_help);
        Utils.setFont(this, R.id.tvUsageTips);
        Utils.setFont(this, R.id.tvVibrate);
        Utils.setFont(this, R.id.tvFlash);
        Utils.setFont(this, R.id.tvRingtone);
        Utils.setFont(this, R.id.btnGotit);
        LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.height = -2;
        attributes.width = -1;
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ((Button) dialog.findViewById(R.id.btnGotit)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BlankScreenActivity.this.rlLayout.setBackgroundColor(Color.parseColor("#00000000"));
                BlankScreenActivity.this.finish();
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BlankScreenActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }




    private void alertHelp() {
        final Dialog dialog = new Dialog(this, R.style.PauseDialogAnimation);
        dialog.setContentView(R.layout.setting_help);
        Utils.setFont(this, R.id.tvUsageTips);
        Utils.setFont(this, R.id.tvVibrate);
        Utils.setFont(this, R.id.tvFlash);
        Utils.setFont(this, R.id.tvRingtone);
        Utils.setFont(this, R.id.btnGotit);
        ((ImageView) dialog.findViewById(R.id.ivVibrate)).setImageResource(R.drawable.notification);
        ((TextView) dialog.findViewById(R.id.tvVibrate)).setText(R.string.set_show_daily_alert_on_to_receive_daily_notification);
        ((LinearLayout) dialog.findViewById(R.id.llFlash)).setVisibility(View.GONE);
        ((LinearLayout) dialog.findViewById(R.id.llRing)).setVisibility(View.GONE);
        LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.height = -2;
        attributes.width = -1;
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ((Button) dialog.findViewById(R.id.btnGotit)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BlankScreenActivity.this.rlLayout.setBackgroundColor(Color.parseColor("#00000000"));
                BlankScreenActivity.this.finish();
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }
}
