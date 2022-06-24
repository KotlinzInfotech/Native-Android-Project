package com.omsvision;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class myApp extends Application {
    public static FetchActivity fetchActivity;
    public static Barcode_Scanner barcodeScanner;
    public static SessionManager sessionManager;
    public static Cognito cognito;
    @Override
    public void onCreate() {
        super.onCreate();
        cognito = new Cognito(this);
        sessionManager = new SessionManager(this);
        setalarm(this);
    }

    public static void setalarm(Context context) {
        if (!sessionManager.getTokenExpiry().equalsIgnoreCase("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Date d2 = null;
            try {
                d2 = sdf.parse(sessionManager.getTokenExpiry());
                cal.setTime(d2);
                Intent intent = new Intent(context, RefreshToken.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, 1, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        pendingIntent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

}
