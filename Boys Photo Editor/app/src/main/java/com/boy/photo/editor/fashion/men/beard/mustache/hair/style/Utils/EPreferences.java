package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class EPreferences {
    String PrefKeyUrl;
    private SharedPreferences m_csPref;

    private EPreferences(final Context context, final String s, final int n) {
        this.PrefKeyUrl = "all_url";
        this.m_csPref = context.getSharedPreferences(s, n);
    }

    public static EPreferences getInstance(final Context context) {
        return new EPreferences(context, "slideshow_pref", 0);
    }


    public boolean getBoolean(final String s, final boolean b) {
        return this.m_csPref.getBoolean(s, b);
    }
    public int putBoolean(final String s, final boolean b) {
        final SharedPreferences.Editor edit = this.m_csPref.edit();
        edit.putBoolean(s, b);
        edit.commit();
        return 0;
    }


}
