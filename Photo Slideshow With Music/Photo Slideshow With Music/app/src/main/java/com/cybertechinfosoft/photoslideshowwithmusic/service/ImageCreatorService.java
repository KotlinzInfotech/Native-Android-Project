package com.cybertechinfosoft.photoslideshowwithmusic.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.bumptech.glide.Glide;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.OnProgressReceiver;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.data.ImageData;
import com.cybertechinfosoft.photoslideshowwithmusic.mask.FinalMaskBitmap3D;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ScalingUtilities;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ImageCreatorService extends IntentService {
    public static final String ACTION_CREATE_NEW_THEME_IMAGES = "ACTION_CREATE_NEW_THEME_IMAGES";
    public static final String ACTION_UPDATE_THEME_IMAGES = "ACTION_UPDATE_THEME_IMAGES";
    public static final String EXTRA_SELECTED_THEME = "selected_theme";
    public static boolean isImageComplate;
    public static final Object mLock;
    MyApplication application;
    ArrayList<ImageData> arrayList;
    private NotificationCompat.Builder mBuilder;
    private String selectedTheme;
    int totalImages;

    static {
        mLock = new Object();
    }

    public ImageCreatorService() {
        this(ImageCreatorService.class.getName());
    }

    public ImageCreatorService(final String s) {
        super(s);
    }

    private void createImages() {
        Bitmap newSecondBmp2 = null;
        this.totalImages = this.arrayList.size();
        int i = 0;
        while (i < this.arrayList.size() - 1 && isSameTheme() && !MyApplication.isBreak) {
            Bitmap newFirstBmp;
            File imgDir = FileUtils.getImageDirectory(this.application.selectedTheme.toString(), i);
            Bitmap firstBitmap;
            Bitmap temp;
            if (i == 0) {
                firstBitmap = ScalingUtilities.checkBitmap(((ImageData) this.arrayList.get(i)).imagePath);
                temp = ScalingUtilities.scaleCenterCrop(firstBitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                newFirstBmp = ScalingUtilities.ConvetrSameSizeNew(firstBitmap, temp, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                temp.recycle();
                firstBitmap.recycle();
                System.gc();
            } else {
                if (newSecondBmp2 == null || newSecondBmp2.isRecycled()) {
                    firstBitmap = ScalingUtilities.checkBitmap(((ImageData) this.arrayList.get(i)).imagePath);
                    temp = ScalingUtilities.scaleCenterCrop(firstBitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                    newSecondBmp2 = ScalingUtilities.ConvetrSameSizeNew(firstBitmap, temp, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                    temp.recycle();
                    firstBitmap.recycle();
                }
                newFirstBmp = newSecondBmp2;
            }
            Bitmap secondBitmap = ScalingUtilities.checkBitmap(((ImageData) this.arrayList.get(i + 1)).imagePath);
            Bitmap temp2 = ScalingUtilities.scaleCenterCrop(secondBitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
            newSecondBmp2 = ScalingUtilities.ConvetrSameSizeNew(secondBitmap, temp2, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
            temp2.recycle();
            secondBitmap.recycle();
            System.gc();
            FinalMaskBitmap3D.EFFECT effect = (FinalMaskBitmap3D.EFFECT) this.application.selectedTheme.getTheme().get(i % this.application.selectedTheme.getTheme().size());
            effect.initBitmaps(newFirstBmp, newSecondBmp2);
            for (int j = 0; ((float) j) < FinalMaskBitmap3D.ANIMATED_FRAME; j++) {
                Bitmap bitmap3 = effect.getMask(newFirstBmp, newSecondBmp2, j);
                if (!isSameTheme()) {
                    continue;
                }
                File file = new File(imgDir, String.format("img%02d.jpg", new Object[]{Integer.valueOf(j)}));
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    OutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap3.compress(CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                boolean isBreak = false;
                while (this.application.isEditModeEnable) {
                    if (this.application.min_pos != Integer.MAX_VALUE) {
                        i = this.application.min_pos;
                        isBreak = true;
                    }
                    if (MyApplication.isBreak) {
                        isImageComplate = true;
                        stopSelf();
                        return;
                    }
                }
                if (isBreak) {
                    ArrayList<String> str = new ArrayList();
                    str.addAll(this.application.videoImages);
                    this.application.videoImages.clear();
                    int size = Math.min(str.size(), Math.max(0, i - i) * 30);
                    for (int p = 0; p < size; p++) {
                        this.application.videoImages.add((String) str.get(p));
                    }
                    this.application.min_pos = Integer.MAX_VALUE;
                }
                if (!isSameTheme() || MyApplication.isBreak) {
                    break;
                }
                this.application.videoImages.add(file.getAbsolutePath());
//                updateImageProgress();
                if (((float) j) == FinalMaskBitmap3D.ANIMATED_FRAME - 1.0f) {
                    for (int m = 0; m < 8 && isSameTheme() && !MyApplication.isBreak; m++) {
                        this.application.videoImages.add(file.getAbsolutePath());
//                        updateImageProgress();
                    }
                }
            }
            i++;
        }
        Glide.get(this).clearDiskCache();
        isImageComplate = true;
        stopSelf();
        isSameTheme();
    }

    private boolean isSameTheme() {
        return this.selectedTheme.equals(this.application.getCurrentTheme());
    }

    private void updateImageProgress() {
        new Handler(Looper.getMainLooper()).post((Runnable) new Runnable() {
            final float val$progress = ImageCreatorService.this.application.videoImages.size() * 100.0f / ((ImageCreatorService.this.totalImages - 1) * 30);

            @Override
            public void run() {
                final OnProgressReceiver onProgressReceiver = ImageCreatorService.this.application.getOnProgressReceiver();
                if (onProgressReceiver != null) {
                    onProgressReceiver.onImageProgressFrameUpdate(this.val$progress);
                }
            }
        });
    }

    public IBinder onBind(final Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.application = MyApplication.getInstance();
    }

    protected void onHandleIntent(final Intent intent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "M_CH_ID";
            CharSequence name = "M_CH_ID";
            String Description = this.getString(R.string.preparing_story);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            notificationManager.createNotificationChannel(mChannel);
        }
        this.mBuilder = new NotificationCompat.Builder(this, "M_CH_ID");
        this.mBuilder.setContentTitle((CharSequence) this.getString(R.string.preparing_story)).setContentText((CharSequence) this.getString(R.string.making_in_progress)).setSmallIcon(R.drawable.notification_icon_48);
        this.selectedTheme = intent.getStringExtra("selected_theme");
        this.arrayList = (ArrayList<ImageData>) this.application.getSelectedImages();
        this.application.initArray();
        ImageCreatorService.isImageComplate = false;
        this.createImages();
    }

    @Deprecated
    public void onStart(final Intent intent, final int n) {
        super.onStart(intent, n);
    }
}
