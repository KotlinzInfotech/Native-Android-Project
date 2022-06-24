package com.cybertechinfosoft.photoslideshowwithmusic.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.OnProgressReceiver;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.Activity_VideoPlay;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ProgressActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.util.ScalingUtilities;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;


public class CreateVideoService extends IntentService {

    public static final int NOTIFICATION_ID = 1001;
    MyApplication application;
    private File audioFile;
    private File audioIp;
    int last;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    String timeRe;
    private float toatalSecond;
    private String videoName;

    String VideoPath;
    public int Progress;
    private long StartTime;

    String completePercentage;
    int VideoSecond;

    PendingIntent activity;

    public CreateVideoService() {
        this(CreateVideoService.class.getName());
    }

    public CreateVideoService(final String s) {
        super(s);
        this.timeRe = "\\btime=\\b\\d\\d:\\d\\d:\\d\\d.\\d\\d";
        this.last = 0;


    }

    public static void appendAudioLog(final String s) {
        if (!FileUtils.TEMP_DIRECTORY.exists()) {
            FileUtils.TEMP_DIRECTORY.mkdirs();
        }
        final File file = new File(FileUtils.TEMP_DIRECTORY, "audio.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.append((CharSequence) s);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

    public static void appendVideoLog(final String s) {
        if (!FileUtils.TEMP_DIRECTORY.exists()) {
            FileUtils.TEMP_DIRECTORY.mkdirs();
        }
        final File file = new File(FileUtils.TEMP_DIRECTORY, "video.txt");
        final StringBuilder sb = new StringBuilder();
        sb.append("File append ");
        sb.append(s);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.append((CharSequence) s);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

    private void buildNotification(final String s) {
        final Intent intent = new Intent(this, (Class) Activity_VideoPlay.class);
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("KEY", "FromNotify");
        intent.putExtra("android.intent.extra.TEXT", s);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        final Resources resources = this.getResources();
        final NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this);
        notificationCompatBuilder.setContentIntent(activity).setSmallIcon(R.drawable.notification_icon_48).setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.icon)).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle((CharSequence) this.getResources().getString(R.string.app_name)).setContentText((CharSequence) this.getString(R.string.video_created));
        final Notification build = notificationCompatBuilder.build();
        build.defaults |= -1;
        this.mNotifyManager.notify(1001, build);
    }

/*

    private int durationToprogtess(String group) {
        final Matcher matcher = Pattern.compile(this.timeRe).matcher(group);
        if (!TextUtils.isEmpty((CharSequence) group) && group.contains("time=")) {
            int last = 0;
            while (matcher.find()) {
                group = matcher.group();
                final String[] split = group.substring(group.lastIndexOf(61) + 1).split(":");
                final float n = Float.valueOf(split[0]) * 3600 + Float.valueOf(split[1]) * 60 + Float.valueOf(split[2]);
                final StringBuilder sb = new StringBuilder();
                sb.append("totalSecond:");
                sb.append(n);
                last = (int) (100.0f * n / this.toatalSecond);
                this.updateInMili(n);
            }
            return this.last = last;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("not contain time ");
        sb2.append(group);
        return this.last;
    }
*/


    private String getVideoName() {
        if (this.videoName != null) {
            if (!this.videoName.endsWith(".mp4")) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this.videoName);
                sb.append(".mp4");
                this.videoName = sb.toString();
            }
            return this.videoName;
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MMM_dd_HH_mm_ss", Locale.ENGLISH);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("video_");
        sb2.append(simpleDateFormat.format(new Date()));
        sb2.append(".mp4");
        return sb2.toString();
    }


    private void updateInMili(final float n) {
        new Handler(Looper.getMainLooper()).post((Runnable) new Runnable() {
            final double val$progress = n * 100.0 / CreateVideoService.this.toatalSecond;

            @Override
            public void run() {
                final OnProgressReceiver onProgressReceiver = CreateVideoService.this.application.getOnProgressReceiver();
                if (onProgressReceiver != null) {
                    onProgressReceiver.onVideoProgressFrameUpdate((float) this.val$progress);
                }
            }
        });
    }

    private void createVideo() {
        String[] inputCode;
        StartTime = System.currentTimeMillis();
        Looper mainLooper;
        MyApplication myApplication = this.application;
        this.toatalSecond = (myApplication.getSecond() * ((float) this.application.getSelectedImages().size())) - 1.0f;
        VideoSecond = (int) ((myApplication.getSecond() * ((float) this.application.getSelectedImages().size())) - 1.0f) * 1000;
        joinAudio();
        while (true) {
            if (ImageCreatorService.isImageComplate) {
                break;
            }
        }
        File file = new File(FileUtils.TEMP_DIRECTORY, "video.txt");
        File file_watermark = new File(FileUtils.APP_DIRECTORY, "watermark.png");
        file.delete();
        int i = 0;
        while (true) {
            if (i >= this.application.videoImages.size()) {
                break;
            } else {
                String[] objArr = new String[1];
                objArr[0] = this.application.videoImages.get(i);
                appendVideoLog(String.format("file '%s'", objArr));

                if (MyApplication.isStoryAdded) {
                    if (i >= 30) {
                        if (i < (this.application.videoImages.size() - 30)) {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("duration ");
                            sb2.append(this.application.getSecond() / 30.0f);
                            appendVideoLog(sb2.toString());
                            i++;
                        } else {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("duration ");
                            sb2.append((this.application.getSecond() + this.application.EXTRA_FRAME_TIME) / 30.0f);
                            appendVideoLog(sb2.toString());
                            i++;
                        }
                    } else {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("duration ");
                        sb2.append((this.application.getSecond() + this.application.EXTRA_FRAME_TIME) / 30.0f);
                        appendVideoLog(sb2.toString());
                        i++;
                    }
                } else {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("duration ");
                    sb2.append(this.application.getSecond() / 30.0f);
                    appendVideoLog(sb2.toString());
                    i++;
                }
            }
        }

        VideoPath = new File(FileUtils.APP_DIRECTORY, getVideoName()).getAbsolutePath();
        if (this.application.getMusicData() == null) {
            Log.e("TAG", "IF");
            inputCode = new String[15];
            inputCode[0] = "-r";
            inputCode[1] = String.valueOf(30.0f / this.application.getSecond());
            inputCode[2] = "-f";
            inputCode[3] = "concat";
            FileUtils.frameFile.getAbsolutePath();
            inputCode[4] = "-i";
            inputCode[5] = file.getAbsolutePath();
            inputCode[6] = "-r";
            inputCode[7] = "30";
            inputCode[8] = "-c:v";
            inputCode[9] = "libx264";
            inputCode[10] = "-preset";
            inputCode[11] = "ultrafast";
            inputCode[12] = "-pix_fmt";
            inputCode[13] = "yuv420p";
            inputCode[14] = VideoPath;
            Log.e("TAG", "IF");
        } else if (this.application.getFrame() != 0) {
            if (!FileUtils.frameFile.exists()) {
                try {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(),
                            this.application.getFrame());
                    if (!(bm.getWidth() == MyApplication.VIDEO_WIDTH && bm
                            .getHeight() == MyApplication.VIDEO_HEIGHT)) {
                        bm = ScalingUtilities.scaleCenterCrop(bm,
                                MyApplication.VIDEO_WIDTH,
                                MyApplication.VIDEO_HEIGHT);
                    }
                    FileOutputStream outStream = new FileOutputStream(
                            FileUtils.frameFile);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    bm.recycle();
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String str = String.valueOf(this.toatalSecond);
            Log.e("TAG", "Else IF");
            inputCode = new String[]{"-r",
                    String.valueOf(30.0f / this.application.getSecond()), "-f",
                    "concat", "-safe", "0", "-i", file.getAbsolutePath(), "-i",
                    FileUtils.frameFile.getAbsolutePath(),
                    "-i", file_watermark.getAbsolutePath(), "-i",
                    this.audioFile.getAbsolutePath(), "-filter_complex",
                    "overlay [tmp]; [tmp] overlay=W-w:H-h:enable='between(t,1," + this.toatalSecond + ")'", "-strict", "experimental", "-r",
                    String.valueOf(30.0f / this.application.getSecond()), "-t",
                    "30", "-t", str, "-c:v", "libx264",
                    "-preset", "ultrafast", "-pix_fmt", "yuv420p", "-ac", "2", VideoPath};
        } else {
            Log.e("TAG", "Else Part");
            inputCode = new String[25];
            inputCode[0] = "-f";
            inputCode[1] = "concat";
            inputCode[2] = "-safe";
            inputCode[3] = "0";
            inputCode[4] = "-i";
            inputCode[5] = file.getAbsolutePath();
            inputCode[6] = "-i";
            inputCode[7] = file_watermark.getAbsolutePath();
            inputCode[8] = "-i";
            inputCode[9] = this.audioFile.getAbsolutePath();
            inputCode[10] = "-filter_complex";
            inputCode[11] = "[0:v][1:v] overlay=main_w-overlay_w-10:main_h-overlay_h-10:enable='between(t,1," + this.toatalSecond + ")'";
            inputCode[12] = "-strict";
            inputCode[13] = "experimental";
            inputCode[14] = "-t";
            inputCode[15] = String.valueOf(this.toatalSecond);
            inputCode[16] = "-c:v";
            inputCode[17] = "libx264";
            inputCode[18] = "-preset";
            inputCode[19] = "ultrafast";
            inputCode[20] = "-pix_fmt";
            inputCode[21] = "yuv420p";
            inputCode[22] = "-ac";
            inputCode[23] = "2";
            inputCode[24] = VideoPath;
        }
        System.gc();
        ExcuteFFmpeg(inputCode, true);
    }

    private void joinAudio() {
        audioIp = new File(FileUtils.TEMP_DIRECTORY, "audio.txt");
        audioFile = new File(FileUtils.APP_DIRECTORY, "audio.mp3");
        audioFile.delete();
        audioIp.delete();
        int d = 0;
        while (true) {
            appendAudioLog(String.format("file '%s'",
                    new Object[]{this.application.getMusicData().track_data}));
            if (this.toatalSecond * 1000.0f <= ((float) (this.application
                    .getMusicData().track_duration * ((long) d)))) {
                break;
            } else {
                d++;
            }
        }
        String[] AudioMearge = new String[]{"-f", "concat", "-safe", "0", "-i", audioIp.getAbsolutePath(), "-c", "copy", "-preset", "ultrafast", "-ac", "2", audioFile.getAbsolutePath()};
        ExcuteFFmpeg(AudioMearge, false);
    }

    public void ExcuteFFmpeg(final String[] command, final boolean IsFrom) {
        Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {
                if (IsFrom) {
                    if (newStatistics == null) {
                        return;
                    }
                    int timeInMilliseconds = newStatistics.getTime();
                    if (timeInMilliseconds > 0) {
                        int totalVideoDuration = VideoSecond;
                        completePercentage = new BigDecimal(timeInMilliseconds).multiply(new BigDecimal(100)).divide(new BigDecimal(totalVideoDuration), 0, BigDecimal.ROUND_HALF_UP).toString();
                    }
                    Looper mainLooper;
                    mainLooper = Looper.getMainLooper();
                    new Handler(mainLooper).post(new Runnable() {
                        @Override
                        public void run() {
                            OnProgressReceiver receiver = CreateVideoService.this.application.getOnProgressReceiver();
                            if (receiver != null) {
                                receiver.onVideoProgressFrameUpdate(Float.parseFloat(completePercentage));
                            }
                        }
                    });
                    mBuilder.setProgress(100, Integer.parseInt(completePercentage), false);
                }
            }
        });
        FFmpeg.executeAsync(command, new ExecuteCallback() {
            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.e("TAG", "Async command execution completed successfully.");
                    if (IsFrom) {
                        Log.e("TAG", "Async command execution completed successfully Mearge");
                        MyApplication.IsVideo = true;
                        mBuilder.setContentText("Video created :" + FileUtils.getDuration(System.currentTimeMillis() - StartTime)).setProgress(0, 0, false);
                        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
                        try {
                            long fileSize = new File(VideoPath).length();
                            String artist = getResources().getString(R.string.artist_name);
                            ContentValues values = new ContentValues();
                            values.put("_data", VideoPath);
                            values.put("_size", Long.valueOf(fileSize));
                            values.put("mime_type", "video/mp4");
                            values.put("artist", artist);
                            values.put("duration", Float.valueOf(toatalSecond * 1000.0f));
                            Uri uri = MediaStore.Audio.Media.getContentUriForPath(VideoPath);
                            getContentResolver().insert(uri, values);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        try {
                            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(VideoPath))));
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                        application.clearAllSelection();
                        FileUtils.deleteFile(FileUtils.TEMP_IMG_DIRECTORY);
                        buildNotification(VideoPath);
                        Looper mainLooper;
                        mainLooper = Looper.getMainLooper();
                        new Handler(mainLooper).post(new Runnable() {
                            @Override
                            public void run() {
                                OnProgressReceiver receiver = CreateVideoService.this.application
                                        .getOnProgressReceiver();
                                if (receiver != null) {
                                    receiver.onVideoProgressFrameUpdate(100);
                                    Utils.watchadCheck = true;
                                    receiver.onProgressFinish(VideoPath);
                                }
                            }
                        });
                        FileUtils.deleteTempDir();
                        FileUtils.TEMP_IMG_DIRECTORY.delete();
                        application.setFrame(0);
                        stopSelf();
                    }
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.e("TAG", "Async command execution cancelled by user.");
                } else {
                    Log.e("TAG", String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }


    protected void onHandleIntent(Intent intent) {
        final String string = this.getString(R.string.create_amazing_video_);
        final String string2 = this.getString(R.string.create_stunning_video_);
        final String string3 = this.getString(R.string.someone_waiting_for_your_video);
        videoName = intent.getExtras().getString("VideoName");
        application = MyApplication.getInstance();
        mNotifyManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this, "M_CH_ID");
        this.mBuilder.setContentTitle((CharSequence) (new String[]{string, string2, string3})[new Random().nextInt(3)]).setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.icon)).setContentText((CharSequence) this.getString(R.string.making_in_progress)).setSmallIcon(R.drawable.notification_icon_48);
        intent = new Intent(this, (Class) ProgressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            this.mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE));
        } else {
            this.mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, 1207959552));
        }
        this.application.copyAssets();
        this.createVideo();
    }
}
