package com.cybertechinfosoft.photoslideshowwithmusic.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.OnlineMusicActivtiy;
import com.cybertechinfosoft.photoslideshowwithmusic.util.AsynkModel;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;

public class FileDownloadService extends Service {
    boolean Keep;
    Context con;
    MyThread mt;

    public FileDownloadService() {
        this.Keep = true;
        this.mt = new MyThread();
    }

    public IBinder onBind(final Intent intent) {
        return null;
    }

    public void onCreate() {
        this.con = this.getApplicationContext();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(final Intent intent, final int n, final int n2) {
        this.startForeground(0, new Notification.Builder(this.getApplicationContext()).setSmallIcon(R.drawable.notification_icon_48).setOngoing(true).setContentTitle((CharSequence) "Title").setContentText((CharSequence) "Text").setContentIntent(PendingIntent.getActivity((Context) this, 0, new Intent(this.getApplicationContext(), (Class) OnlineMusicActivtiy.class), 0)).build());
        if (this.mt.getState() == Thread.State.NEW) {
            this.mt.start();
        }
        return 1;
    }

    @SuppressLint({"NewApi"})
    public void onTaskRemoved(final Intent intent) {
        final Intent intent2 = new Intent(this.getApplicationContext(), (Class) this.getClass());
        intent2.setPackage(this.getPackageName());
        ((AlarmManager) this.getApplicationContext().getSystemService(ALARM_SERVICE)).set(3, SystemClock.elapsedRealtime() + 1000L, PendingIntent.getService(this.getApplicationContext(), 1, intent2, 1073741824));
        super.onTaskRemoved(intent);
    }


    public ComponentName startService(final Intent intent) {
        return super.startService(intent);
    }

    public void stopMyService() {
        this.mt.interrupt();
        this.Keep = false;
        final PendingIntent activity = PendingIntent.getActivity(this.getApplicationContext(), 1, new Intent(this.getApplicationContext(), (Class) this.getClass()), 134217728);
        activity.cancel();
        ((AlarmManager) this.getApplicationContext().getSystemService(ALARM_SERVICE)).cancel(activity);
        this.stopSelf();
    }

    public boolean stopService(final Intent intent) {
        return super.stopService(intent);
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            while (FileDownloadService.this.Keep) {
                try {
                    Thread.sleep(1000L);
                    if (Utils.TaskCount >= 0 && Utils.mAsynkList.size() > Utils.TaskCount) {
                        final AsynkModel asynkModel = Utils.mAsynkList.get(Utils.TaskCount);
                        ++Utils.TaskCount;
                        (asynkModel.mTask).execute(asynkModel.Uri, asynkModel.DisplayName, asynkModel.pos);
                    } else {
                        if (Utils.mAsynkList.size() != 0 || Utils.TaskCount <= 0) {
                            Utils.TaskCount = 0;
                            FileDownloadService.this.stopMyService();
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
