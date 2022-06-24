package com.org.codechimp.apprater;

import android.content.Context;
import android.net.Uri;

public abstract class Market {
    protected static String packageName;

    protected abstract Uri getMarketURI(Context context);

    public void overridePackageName(String str) {
        packageName = str;
    }

    protected static String getPackageName(Context context) {
        if (packageName != null) {
            return packageName;
        }
        return context.getPackageName();
    }
}
