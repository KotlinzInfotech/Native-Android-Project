package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;

public class CustomPermissionActivity extends AppCompatActivity {
    String AppName;
    ComponentName SecurityComponentName;
    Button btnGotIt;
    EPreferences ePref;
    private AnimatorSet g;
    ImageView imageHand;
    SwitchCompat switchTut;
    private TextView tvName;

    public CustomPermissionActivity() {
        this.SecurityComponentName = null;
        this.AppName = "";
    }

    public static int b(final Context context, final float n) {
        return (int) (context.getResources().getDisplayMetrics().density * n + 0.5f);
    }

    private void staranimation() {
        this.imageHand.setAlpha(0.0f);
        this.switchTut.setAlpha(0.0f);
        this.tvName.setAlpha(0.0f);
        final int b = b((Context) this, 125.0f);
        this.imageHand.setTranslationY((float) b);
        this.imageHand.setTranslationX((float) (-b / 2));
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) this.imageHand, "alpha", new float[]{1.0f});
        final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object) this.tvName, "alpha", new float[]{1.0f});
        final ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat((Object) this.switchTut, "alpha", new float[]{1.0f});
        ofFloat.setDuration(250L);
        ofFloat2.setDuration(250L);
        ofFloat3.setDuration(250L);
        final ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat((Object) this.imageHand, "translationY", new float[]{0.0f});
        final ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat((Object) this.imageHand, "translationX", new float[]{0.0f});
        final ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat((Object) this, "rotationX", new float[]{-60.0f, 0.0f});
        ofFloat4.setDuration(600L);
        ofFloat5.setDuration(600L);
        ofFloat6.setDuration(600L);
        ofFloat6.setInterpolator((TimeInterpolator) new AccelerateInterpolator());
        (this.g = new AnimatorSet()).playTogether(new Animator[]{ofFloat3, ofFloat2, ofFloat, ofFloat4, ofFloat5});
        this.g.setStartDelay(700L);
        this.g.addListener((Animator.AnimatorListener) new Animator.AnimatorListener() {
            public void onAnimationCancel(final Animator animator) {
            }

            public void onAnimationEnd(final Animator animator) {
                CustomPermissionActivity.this.switchTut.setChecked(true);
            }

            public void onAnimationRepeat(final Animator animator) {
                CustomPermissionActivity.this.switchTut.setChecked(false);
            }

            public void onAnimationStart(final Animator animator) {
                CustomPermissionActivity.this.switchTut.setChecked(false);
            }
        });
        this.g.start();
    }

    public void onBackPressed() {
    }

    @TargetApi(19)
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 19) {
            final Window window = this.getWindow();
            window.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            window.setFlags(134217728, 134217728);
        }
        this.setContentView(R.layout.tutorial_activity);
        this.ePref = EPreferences.getInstance((Context) this);
        this.switchTut = (SwitchCompat) this.findViewById(R.id.scSwitchTut);
        this.imageHand = (ImageView) this.findViewById(R.id.ivImageHand);
        this.btnGotIt = (Button) this.findViewById(R.id.btnGotIt);
        this.tvName = (TextView) this.findViewById(R.id.tvName);
        this.btnGotIt.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                final Intent intent = CustomPermissionActivity.this.getIntent();
                CustomPermissionActivity.this.SecurityComponentName = (ComponentName) intent.getParcelableExtra("PACKAGE");
                CustomPermissionActivity.this.AppName = intent.getStringExtra("APPNAME");
                if (CustomPermissionActivity.this.SecurityComponentName != null) {
                    final Intent intent2 = new Intent();
                    intent2.setComponent(CustomPermissionActivity.this.SecurityComponentName);
                    CustomPermissionActivity.this.startActivity(intent2);
                }
                CustomPermissionActivity.this.ePref.putBoolean("HasAutoStartPermission", true);
                CustomPermissionActivity.this.finish();
            }
        });
        this.staranimation();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        this.overridePendingTransition(0, 0);
    }
}
