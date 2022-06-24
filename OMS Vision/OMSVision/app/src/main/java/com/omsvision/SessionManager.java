package com.omsvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Omsvisionpref";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_EMAIL = "email";

    public static final String KEY_JWT_TOKEN = "jwttoken";

    public static final String KEY_TOKEN_EXPIRY = "tokenexpiry";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getUserid() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void setJWTToken(String jwttoken) {
        editor.putString(KEY_JWT_TOKEN, jwttoken);
        editor.commit();
    }

    public String getJWTToken() {
        return pref.getString(KEY_JWT_TOKEN, "");
    }

    public void setTokenExpiry(String expiry) {
        editor.putString(KEY_TOKEN_EXPIRY, expiry);
        editor.commit();
    }

    public String getTokenExpiry() {
        return pref.getString(KEY_TOKEN_EXPIRY, "");
    }

    public void createLoginSession(String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
        ((Activity) _context).finish();

    }

}
