package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.ResponseBody;

public class ConcDownloadTask extends CustomAsync<String, String, String> {
    File MusicOutFile;
    EPreferences MyPref;
    int index_down;
    boolean isFullFile;
    Context mContext;
    RootProgressUpdator mProgressUpdator;
    public MediaScannerConnection mScanner;
    URL url;

    public ConcDownloadTask(final Context mContext) {
        this.index_down = -1;
        this.MusicOutFile = null;
        this.isFullFile = false;
        this.mScanner = null;
        this.mContext = mContext;
        this.MyPref = EPreferences.getInstance(mContext);
        this.mProgressUpdator = new RootProgressUpdator();
        this.mProgressUpdator.addListener((GloblePrgListener) this.mContext);
    }

    public void broadCastAudioInsert(final String s, final String s2) {
        final Context mContext = this.mContext;
        final StringBuilder sb = new StringBuilder();
        sb.append("file://");
        sb.append(s);
        mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(sb.toString())));
        this.reScan(s2);
    }

    @Override
    protected String doInBackground(String... strArr) {
        Exception e;
        Exception exception;
        StringBuilder stringBuilder;
        long length;
        String absolutePath;
        String name;
        long length2;
        ContentValues contentValues;
        int i;
        this.MusicOutFile = null;
        long contentLength;
        long j;
        try {
            url = new URL(strArr[0]);
            URLConnection openConnection = url.openConnection();
            openConnection.connect();
            contentLength = (long) openConnection.getContentLength();
            try {
                OutputStream fileOutputStream;
                String audioFolderPath = Utils.INSTANCE.getAudioFolderPath();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(audioFolderPath);
                stringBuilder2.append(strArr[1]);
                MusicOutFile = new File(stringBuilder2.toString());
                long length3 = MusicOutFile.length();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("online_size = ");
                stringBuilder3.append(contentLength);
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("downloadedSize = ");
                stringBuilder3.append(length3);
                boolean z = length3 > 0 && length3 < contentLength;
                OkHttpClient okHttpClient = new OkHttpClient();
                Builder builder = new Builder();
                if (z) {
                    try {
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append("bytes=");
                        stringBuilder4.append(String.valueOf(length3));
                        stringBuilder4.append("-");
                        builder.addHeader("Range", stringBuilder4.toString());
                    } catch (Exception e2) {
                        e = e2;
                    }
                }
                ResponseBody body = okHttpClient.newCall(builder.url(url).build()).execute().body();
                body.source();
                File file = new File(MusicOutFile.getAbsolutePath());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(body.byteStream());
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("isResume = ");
                stringBuilder3.append(z);
                if (z) {
                    fileOutputStream = new FileOutputStream(file, true);
                } else {
                    fileOutputStream = new FileOutputStream(file, false);
                }
                long contentLength2 = body.contentLength();
                byte[] bArr = new byte[1024];
                long j2 = 0;
                while (true) {
                    int read = bufferedInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    if (index_down == -1) {
                        index_down = Integer.parseInt(strArr[2]);
                    }
                    j = contentLength;
                    long j3 = j2 + ((long) read);
                    try {
                        String[] strArr2 = new String[1];
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append("");
                        stringBuilder5.append((int) ((100 * j3) / contentLength2));
                        strArr2[0] = stringBuilder5.toString();
                        publishProgress(strArr2);
                        fileOutputStream.write(bArr, 0, read);
                        contentLength = j;
                        j2 = j3;
                    } catch (Exception e3) {
                        exception = e3;
                        contentLength = j;
                    }
                }
                j = contentLength;
                fileOutputStream.flush();
                fileOutputStream.close();
                bufferedInputStream.close();
            } catch (Exception e4) {
                Exception e3 = e4;
                j = contentLength;
                exception = e3;
                stringBuilder = new StringBuilder();
                stringBuilder.append("ERR = ");
                stringBuilder.append(exception.getMessage());
                j = contentLength;
                length = MusicOutFile.length();
                if (length > 0) {
                }
                isFullFile = true;
                absolutePath = MusicOutFile.getAbsolutePath();
                name = MusicOutFile.getName();
                length2 = MusicOutFile.length();
                contentValues = new ContentValues();
                contentValues.put("_data", absolutePath);
                contentValues.put("title", name);
                contentValues.put("_size", Long.valueOf(length2));
                contentValues.put("mime_type", "audio/mpeg");
                contentValues.put("artist", "ffmpegGame");
                contentValues.put("is_music", Boolean.valueOf(true));
                for (i = 0; i < Utils.mPrgModel.size(); i++) {
                    if (!((ProgressModel) Utils.mPrgModel.get(i)).Uri.equals(url)) {
                        ((ProgressModel) Utils.mPrgModel.get(i)).isDownloading = false;
                        ((ProgressModel) Utils.mPrgModel.get(i)).isAvailableOffline = true;
                    }
                }
                broadCastAudioInsert(absolutePath, name);
                return MusicOutFile.getAbsolutePath();
            }
        } catch (Exception e32) {
            exception = e32;
            contentLength = 0;
            stringBuilder = new StringBuilder();
            stringBuilder.append("ERR = ");
            stringBuilder.append(exception.getMessage());
            j = contentLength;
            length = MusicOutFile.length();
            if (length > 0) {
            }
            isFullFile = true;
            absolutePath = MusicOutFile.getAbsolutePath();
            name = MusicOutFile.getName();
            length2 = MusicOutFile.length();
            contentValues = new ContentValues();
            contentValues.put("_data", absolutePath);
            contentValues.put("title", name);
            contentValues.put("_size", Long.valueOf(length2));
            contentValues.put("mime_type", "audio/mpeg");
            contentValues.put("artist", "ffmpegGame");
            contentValues.put("is_music", Boolean.valueOf(true));
            for (i = 0; i < Utils.mPrgModel.size(); i++) {
                if (!((ProgressModel) Utils.mPrgModel.get(i)).Uri.equals(url)) {
                    ((ProgressModel) Utils.mPrgModel.get(i)).isDownloading = false;
                    ((ProgressModel) Utils.mPrgModel.get(i)).isAvailableOffline = true;
                }
            }
            broadCastAudioInsert(absolutePath, name);
            return MusicOutFile.getAbsolutePath();
        }
        length = MusicOutFile.length();
        if (length > 0 || length >= 16) {
            isFullFile = true;
            absolutePath = MusicOutFile.getAbsolutePath();
            name = MusicOutFile.getName();
            length2 = MusicOutFile.length();
            contentValues = new ContentValues();
            contentValues.put("_data", absolutePath);
            contentValues.put("title", name);
            contentValues.put("_size", Long.valueOf(length2));
            contentValues.put("mime_type", "audio/mpeg");
            contentValues.put("artist", "ffmpegGame");
            contentValues.put("is_music", Boolean.valueOf(true));
            for (i = 0; i < Utils.mPrgModel.size(); i++) {
                if (!((ProgressModel) Utils.mPrgModel.get(i)).Uri.equals(url)) {
                    ((ProgressModel) Utils.mPrgModel.get(i)).isDownloading = false;
                    ((ProgressModel) Utils.mPrgModel.get(i)).isAvailableOffline = true;
                }
            }
            broadCastAudioInsert(absolutePath, name);
            return MusicOutFile.getAbsolutePath();
        }
        isFullFile = false;
        return "";
    }

    @Override
    protected void onPostExecute(final String s) {
        final boolean isFullFile = this.isFullFile;
        final int n = 0;
        if (isFullFile) {
            for (int i = 0; i < Utils.mPrgModel.size(); ++i) {
                if (((ProgressModel) Utils.mPrgModel.get(i)).Uri.equals(this.url.toString())) {
                    Utils.mPrgModel.remove(i);
                } else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("URI =");
                    sb.append(this.url.toString());
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("ARR =");
                    sb2.append(Utils.mPrgModel.get(i).Uri);
                }
            }
            final ArrayList allURL = this.MyPref.getAllURL();
            for (int j = n; j < allURL.size(); ++j) {
                if (allURL.get(j).equals(this.url.toString())) {
                    allURL.remove(j);
                    break;
                }
            }
            this.MyPref.clearURLPref();
            this.MyPref.putMultipleURLPref(allURL);
            this.mProgressUpdator.notifyDataChange(this.url.toString(), this.MusicOutFile.getAbsolutePath());
            return;
        }
        Utils.mPrgModel.clear();
        Utils.mAsynkList.clear();
        Utils.TaskCount = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(final String... array) {
        final StringBuilder sb = new StringBuilder();
        sb.append("index_down =");
        sb.append(this.index_down);
        sb.append(" PRG = ");
        sb.append(Float.parseFloat(array[0]));
        for (int i = 0; i < Utils.mPrgModel.size(); ++i) {
            if (((ProgressModel) Utils.mPrgModel.get(i)).Uri.equals(this.url.toString())) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("P_UPD PRG = ");
                sb2.append(Utils.mPrgModel.get(i).Progress);
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("P_UPD URI = ");
                sb3.append(this.url.toString());
                ((ProgressModel) Utils.mPrgModel.get(i)).Progress = Float.parseFloat(array[0]);
                this.mProgressUpdator.broadCastPrg(this.url.toString(), ((ProgressModel) Utils.mPrgModel.get(i)).Progress);
                return;
            }
        }
    }

    public void reScan(final String s) {
        (this.mScanner = new MediaScannerConnection(this.mContext, (MediaScannerConnection.MediaScannerConnectionClient) new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                ConcDownloadTask.this.mScanner.scanFile(s, (String) null);
            }

            public void onScanCompleted(final String s, final Uri uri) {
                if (s.equals(s)) {
                    ConcDownloadTask.this.mScanner.disconnect();
                }
            }
        })).connect();
    }
}
