package com.org.codechimp.apprater;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

public class AppRater {
    private static final int DAYS_UNTIL_PROMPT = 1;
    private static int DAYS_UNTIL_PROMPT_FOR_REMIND_LATER = 3;
    private static final int LAUNCHES_UNTIL_PROMPT = 3;
    private static int LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER = 7;
    private static final String PREF_APP_VERSION_CODE = "app_version_code";
    private static final String PREF_APP_VERSION_NAME = "app_version_name";
    private static final String PREF_DONT_SHOW_AGAIN = "dontshowagain";
    private static final String PREF_FIRST_LAUNCHED = "date_firstlaunch";
    private static final String PREF_LAUNCH_COUNT = "launch_count";
    private static final String PREF_NAME = "apprater";
    private static final String PREF_REMIND_LATER = "remindmelater";
    private static boolean hideNoButton = false;
    private static boolean isCancelable = true;
    private static boolean isDark;
    private static boolean isVersionCodeCheckEnabled;
    private static boolean isVersionNameCheckEnabled;
    private static Market market = new GoogleMarket();
    private static String packageName;
    private static Callback sCallback;
    private static boolean themeSet;

    public interface Callback {
        void onCancelClicked();

        void onNoClicked();

        void onYesClicked();
    }

    public static void setVersionNameCheckEnabled(boolean z) {
        isVersionNameCheckEnabled = z;
    }

    public static void setVersionCodeCheckEnabled(boolean z) {
        isVersionCodeCheckEnabled = z;
    }

    public static void setNumDaysForRemindLater(int i) {
        DAYS_UNTIL_PROMPT_FOR_REMIND_LATER = i;
    }

    public static void setNumLaunchesForRemindLater(int i) {
        LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER = i;
    }

    public static void setDontRemindButtonVisible(boolean z) {
        hideNoButton = z;
    }

    public static void setCancelable(boolean z) {
        isCancelable = z;
    }

    public static void app_launched(Context context) {
        app_launched(context, 1, 3);
    }

    public static void app_launched(Context context, int i, int i2, int i3, int i4) {
        setNumDaysForRemindLater(i3);
        setNumLaunchesForRemindLater(i4);
        app_launched(context, i, i2);
    }

    public static void app_launched(Context context, int i, int i2) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        Editor edit = sharedPreferences.edit();
        ApplicationRatingInfo createApplicationInfo = ApplicationRatingInfo.createApplicationInfo(context);
        if (isVersionNameCheckEnabled && !createApplicationInfo.getApplicationVersionName().equals(sharedPreferences.getString(PREF_APP_VERSION_NAME, "none"))) {
            edit.putString(PREF_APP_VERSION_NAME, createApplicationInfo.getApplicationVersionName());
            resetData(context);
            commitOrApply(edit);
        }
        if (isVersionCodeCheckEnabled && createApplicationInfo.getApplicationVersionCode() != sharedPreferences.getInt(PREF_APP_VERSION_CODE, -1)) {
            edit.putInt(PREF_APP_VERSION_CODE, createApplicationInfo.getApplicationVersionCode());
            resetData(context);
            commitOrApply(edit);
        }
        if (!sharedPreferences.getBoolean(PREF_DONT_SHOW_AGAIN, false)) {
            if (sharedPreferences.getBoolean(PREF_REMIND_LATER, false)) {
                i = DAYS_UNTIL_PROMPT_FOR_REMIND_LATER;
                i2 = LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER;
            }
            long j = sharedPreferences.getLong(PREF_LAUNCH_COUNT, 0) + 1;
            edit.putLong(PREF_LAUNCH_COUNT, j);
            Long valueOf = Long.valueOf(sharedPreferences.getLong(PREF_FIRST_LAUNCHED, 0));
            if (valueOf.longValue() == 0) {
                valueOf = Long.valueOf(System.currentTimeMillis());
                edit.putLong(PREF_FIRST_LAUNCHED, valueOf.longValue());
            }
            if (j >= ((long) i2) || System.currentTimeMillis() >= valueOf.longValue() + ((long) ((((i * 24) * 60) * 60) * 1000))) {
                showRateAlertDialog(context, edit);
            }
            commitOrApply(edit);
        }
    }

    public static void showRateDialog(Context context) {
        showRateAlertDialog(context, null);
    }

    public static void showRateDialog(Context context, OnClickListener onClickListener) {
        showRateAlertDialog(context, null);
    }

    public static void rateNow(final Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, market.getMarketURI(context)));
        } catch (ActivityNotFoundException activityNotFoundException1) {
        }
    }

    public static void setPackageName(String str) {
        market.overridePackageName(str);
    }

    public static void setMarket(Market market) {
        market = market;
    }

    public static Market getMarket() {
        return market;
    }

    @TargetApi(11)
    public static void setDarkTheme() {
        isDark = true;
        themeSet = true;
    }

    @TargetApi(11)
    public static void setLightTheme() {
        isDark = false;
        themeSet = true;
    }

    private static void showRateAlertDialog(final Context context, final Editor sharedPreferences$Editor) {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(context, R.style.AppAlertDialog);
        ApplicationRatingInfo.createApplicationInfo(context);
        alertDialog$Builder.setTitle((CharSequence) context.getString(R.string.apprater_dialog_title));
        alertDialog$Builder.setMessage((CharSequence) context.getString(R.string.apprater_rate_message));
        alertDialog$Builder.setCancelable(AppRater.isCancelable);
        alertDialog$Builder.setPositiveButton((CharSequence) context.getString(R.string.apprater_rate), (OnClickListener) new OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                AppRater.rateNow(context);
                if (sharedPreferences$Editor != null) {
                    sharedPreferences$Editor.putBoolean("dontshowagain", true);
                    commitOrApply(sharedPreferences$Editor);
                }
                dialogInterface.dismiss();
            }
        });
        alertDialog$Builder.setNeutralButton((CharSequence) context.getString(R.string.apprater_later), (OnClickListener) new OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                if (AppRater.sCallback != null) {
                    AppRater.sCallback.onCancelClicked();
                }
                if (sharedPreferences$Editor != null) {
                    sharedPreferences$Editor.putLong("date_firstlaunch", (long) Long.valueOf(System.currentTimeMillis()));
                    sharedPreferences$Editor.putLong("launch_count", 0L);
                    sharedPreferences$Editor.putBoolean("remindmelater", true);
                    sharedPreferences$Editor.putBoolean("dontshowagain", false);
                    commitOrApply(sharedPreferences$Editor);
                }
                dialogInterface.dismiss();
            }
        });
        if (!AppRater.hideNoButton) {
            alertDialog$Builder.setNegativeButton((CharSequence) context.getString(R.string.apprater_no_thanks), (OnClickListener) new OnClickListener() {
                public void onClick(final DialogInterface dialogInterface, final int n) {
                    if (AppRater.sCallback != null) {
                        AppRater.sCallback.onCancelClicked();
                    }
                    if (sharedPreferences$Editor != null) {
                        sharedPreferences$Editor.putBoolean("dontshowagain", true);
                        sharedPreferences$Editor.putBoolean("remindmelater", false);
                        sharedPreferences$Editor.putLong("date_firstlaunch", System.currentTimeMillis());
                        sharedPreferences$Editor.putLong("launch_count", 0L);
                        commitOrApply(sharedPreferences$Editor);
                    }
                    dialogInterface.dismiss();
                }
            });
        }
        final AlertDialog create = alertDialog$Builder.create();
        create.setOnShowListener((DialogInterface.OnShowListener) new DialogInterface.OnShowListener() {
            public void onShow(final DialogInterface dialogInterface) {
                try {
                    final Button button = create.getButton(-1);
                    if (button == null) {
                        return;
                    }
                    final LinearLayout linearLayout = (LinearLayout) button.getParent();
                    if (linearLayout == null) {
                        return;
                    }
                    boolean b = false;
                    if (button.getLeft() + button.getWidth() > linearLayout.getWidth()) {
                        b = true;
                    }
                    if (b) {
                        linearLayout.setOrientation(1);
                        linearLayout.setGravity(8388613);
                    }
                } catch (Exception ex) {
                }
            }
        });
        create.show();
    }

    public static void setCallback(Callback callback) {
        sCallback = callback;
    }

    @SuppressLint({"NewApi"})
    private static void commitOrApply(Editor editor) {
        if (VERSION.SDK_INT > 8) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static void resetData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(PREF_DONT_SHOW_AGAIN, false);
        editor.putBoolean(PREF_REMIND_LATER, false);
        editor.putLong(PREF_LAUNCH_COUNT, 0);
        long date_firstLaunch = System.currentTimeMillis();
        editor.putLong(PREF_FIRST_LAUNCHED, date_firstLaunch);
        commitOrApply(editor);
    }
}
