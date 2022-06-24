package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.content.*;
import android.util.Log;

import java.util.*;

public class EPreferences {
    public static final String DURATION_VALUE = "duration_value";
    public static final String IS_TITLE = "false";
    public static final String LANG_POSITION = "lang_position";
    private static final int MODE_PRIVATE = 0;
    public static final String PREF_AUDIO_PATH = "pref_audio_path";
    public static final String PREF_CB_MUSIC_FILE_POSITION = "pref_cb_music_file_position";
    public static final String PREF_KEY_ANIMATION_INDEX = "pref_key_animation_index";
    public static final String PREF_KEY_FILTER_INDEX = "pref_key_filter_index";
    public static final String PREF_KEY_IS_FIRST_TIME = "is_first_time";
    public static final String PREF_KEY_NOTIFICATION_FLESH = "notification_flesh";
    public static final String PREF_KEY_NOTIFICATION_RING = "notification_ring";
    public static final String PREF_KEY_NOTIFICATION_VIBRATE = "notification_vibrate";
    public static final String PREF_KEY_SAVEING_CAM_IMG = "pref_key_saveing_cam_img";
    public static final String PREF_KEY_VIDEONAME = "videoname";
    public static final String PREF_KEY_VIDEO_FRAME_PATH = "pref_key_video_frame_path";
    public static final String PREF_KEY_VIDEO_QUALITY = "pref_key_video_quality";
    public static final String PREF_KEY_WANT_CAPTURED_ALERT = "pref_key_captured_alert";
    public static final String PREF_KEY_WANT_DAILY_ALERT = "pref_key_daily_alert";
    private static final String PREF_NAME = "slideshow_pref";
    public static final String TITLE_INDEX = "title_index";
    public static final String TITLE_STRING = "title_string";
    String PrefKeyUrl;
    private SharedPreferences m_csPref;

    private EPreferences(final Context context, final String s, final int n) {
        this.PrefKeyUrl = "all_url";
        this.m_csPref = context.getSharedPreferences(s, n);
    }

    public static EPreferences getInstance(final Context context) {
        return new EPreferences(context, "slideshow_pref", 0);
    }
    public static EPreferences getInstance1(final Context context) {
        return new EPreferences(context, "policy_check", 0);
    }
    public boolean checkUrlAvailable(final String s) {
        return this.getCsvURL().contains(s);
    }

    public void clear(final String s) {
        this.m_csPref.edit().remove(s).commit();
    }

    public void clearPref() {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.clear();
        edit.commit();
        final StringBuilder sb = new StringBuilder();
        sb.append("Pref=>");
        sb.append(this.getCsvURL());
    }

    public void clearURLPref() {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.putString(this.PrefKeyUrl, "");
        edit.commit();
    }

    public ArrayList<String> getAllURL() {
        final String csvURL = this.getCsvURL();
        if (csvURL != null && !csvURL.equals("") && csvURL.length() > 0) {
            return new ArrayList<String>(Arrays.asList(csvURL.split(",")));
        }
        return null;
    }

    public boolean getBoolean(final String s, final boolean b) {
        Log.e("getBool S = ", "" + s);
        return this.m_csPref.getBoolean(s, b);
    }

    public String getCsvURL() {
        return this.m_csPref.getString(this.PrefKeyUrl, "");
    }

    public int getInt(final String s, final int n) {
        return this.m_csPref.getInt(s, n);
    }

    public String getString(final String s, final String s2) {
        return this.m_csPref.getString(s, s2);
    }

    public int putBoolean(final String s, final boolean b) {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.putBoolean(s, b);
        edit.commit();
        Log.e("putBool S = ", "" + s);
        return 0;
    }

    public int putInt(final String s, final int n) {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.putInt(s, n);
        edit.commit();
        return 0;
    }

    public void putMultipleURLPref(final ArrayList<String> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.putURLValue(list.get(i));
        }
    }

    public int putString(final String s, final String s2) {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.putString(s, s2);
        edit.commit();
        return 0;
    }

    public void putURLValue(final String s) {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        final String csvURL = this.getCsvURL();
        if (csvURL != null && !csvURL.equals("") && csvURL.length() > 0) {
            final String prefKeyUrl = this.PrefKeyUrl;
            final StringBuilder sb = new StringBuilder();
            sb.append(csvURL);
            sb.append(",");
            sb.append(s);
            edit.putString(prefKeyUrl, sb.toString());
        } else {
            edit.putString(this.PrefKeyUrl, s);
        }
        edit.commit();
    }

    public void setString(final String s, final String s2) {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.putString(s, s2);
        edit.commit();
    }
}
