package com.cybertechinfosoft.photoslideshowwithmusic.util;

import java.util.*;
import android.preference.*;
import android.content.*;
import android.os.*;
import android.annotation.*;
import android.content.res.*;

public class LanguageHelper
{
    private static final String SELECTED_LANGUAGE = "Language.Helper.Selected.Language";

    public static String getLanguage(final Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static String getPersistedData(final Context context, final String s) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("Language.Helper.Selected.Language", s);
    }

    public static Context onAttach(final Context context) {
        return setLanguage(context, getPersistedData(context, Locale.getDefault().getLanguage()));
    }

    public static Context onAttach(final Context context, final String s) {
        return setLanguage(context, getPersistedData(context, s));
    }

    private static void persist(final Context context, final String s) {
        final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("Language.Helper.Selected.Language", s);
        edit.apply();
    }

    public static Context setLanguage(final Context context, final String s) {
        persist(context, s);
        if (Build.VERSION.SDK_INT >= 24) {
            return updateResources(context, s);
        }
        return updateResourcesLegacy(context, s);
    }

    @TargetApi(24)
    private static Context updateResources(final Context context, final String s) {
        final Locale locale = new Locale(s);
        final Configuration configuration = context.getResources().getConfiguration();
        final LocaleList list = new LocaleList(new Locale[] { locale });
        LocaleList.setDefault(list);
        configuration.setLocales(list);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(final Context context, final String s) {
        final Locale locale = new Locale(s);
        Locale.setDefault(locale);
        final Resources resources = context.getResources();
        final Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
