package com.org.codechimp.apprater;

import android.content.Context;
import android.net.Uri;

public class GoogleMarket extends Market {
    private static String marketLink = "market://details?id=";

    public Uri getMarketURI(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(marketLink);
        stringBuilder.append(Market.getPackageName(context));
        return Uri.parse(stringBuilder.toString());
    }
}
