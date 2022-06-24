package com.cybertechinfosoft.photoslideshowwithmusic.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileUtils {
    public static File APP_DIRECTORY;
    public static final File TEMP_DIRECTORY;
    public static final File TEMP_DIRECTORY_AUDIO;
    public static final File TEMP_IMG_DIRECTORY;
    public static final File TEMP_VID_DIRECTORY;
    public static final File frameFile;
    public static long mDeleteFileCount = 0L;
    public static File mDownloadDir;
    public static File mSdCard;
    private static File[] mStorageList;
    public static final String rawExternalStorage;
    public static String rawSecondaryStoragesStr;
    public static String unlockDirectoryNameImage = "GalaryLock/Image/";
    public static String unlockDirectoryNameVideo = "GalaryLock/Video/";

    static {
        rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        FileUtils.rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        FileUtils.mSdCard = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        FileUtils.mDownloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        FileUtils.APP_DIRECTORY = new File(FileUtils.mSdCard, "Photo Slide Show With Music");
        TEMP_DIRECTORY = new File(FileUtils.APP_DIRECTORY, ".temp");
        TEMP_VID_DIRECTORY = new File(FileUtils.TEMP_DIRECTORY, ".temp_vid");
        TEMP_DIRECTORY_AUDIO = new File(FileUtils.APP_DIRECTORY, ".temp_audio");
        TEMP_IMG_DIRECTORY = new File(FileUtils.APP_DIRECTORY, ".temp_image");
        frameFile = new File(FileUtils.APP_DIRECTORY, ".frame.png");
        if (!FileUtils.TEMP_IMG_DIRECTORY.exists()) {
            FileUtils.TEMP_IMG_DIRECTORY.mkdirs();
        }
        if (!FileUtils.TEMP_DIRECTORY.exists()) {
            FileUtils.TEMP_DIRECTORY.mkdirs();
        }
        if (!FileUtils.TEMP_VID_DIRECTORY.exists()) {
            FileUtils.TEMP_VID_DIRECTORY.mkdirs();
        }
    }

    public FileUtils() {
        FileUtils.mDeleteFileCount = 0L;
    }


    public static boolean deleteFile(final File file) {
        boolean delete = false;
        int i = 0;
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                final File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    while (i < listFiles.length) {
                        final File file2 = listFiles[i];
                        FileUtils.mDeleteFileCount += file2.length();
                        deleteFile(file2);
                        ++i;
                    }
                }
                FileUtils.mDeleteFileCount += file.length();
                return file.delete();
            }
            FileUtils.mDeleteFileCount += file.length();
            delete = file.delete();
        }
        return delete;
    }

    public static boolean deleteFile(final String s) {
        return deleteFile(new File(s));
    }

    public static void deleteTempDir() {
        final File[] listFiles = FileUtils.TEMP_DIRECTORY.listFiles();
        for (int length = listFiles.length, i = 0; i < length; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.deleteFile(listFiles[finalI]);
                }
            }).start();
        }
    }

    public static boolean deleteThemeDir(final String s) {
        return deleteFile(getImageDirectory(s));
    }


    @SuppressLint({"DefaultLocale"})
    public static String getDuration(long n) {
        if (n < 1000L) {
            return String.format("%02d:%02d", 0, 0);
        }
        final long n2 = n / 1000L;
        n = n2 / 3600L;
        final long n3 = 3600L * n;
        final long n4 = (n2 - n3) / 60L;
        final long n5 = n2 - (n3 + 60L * n4);
        if (n == 0L) {
            return String.format("%02d:%02d", n4, n5);
        }
        return String.format("%02d:%02d:%02d", n, n4, n5);
    }

    public static File getHiddenAppDirectory(File file) {
        file = new File(file, ".MyGalaryLock/");
        if (file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getHiddenImageAppDirectory(File file) {
        file = new File(getHiddenAppDirectory(file), "Image/");
        if (file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getHiddenImageDirectory(final File file, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(".MyGalaryLock/Image/");
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File getHiddenVideoAppDirectory(File file) {
        file = new File(getHiddenAppDirectory(file), "Video/");
        if (file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getHiddenVideoDirectory(final File file, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(".MyGalaryLock/Video/");
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File getImageDirectory(final int n) {
        final File file = new File(FileUtils.TEMP_DIRECTORY, String.format("IMG_%03d", n));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getImageDirectory(final String s) {
        final File file = new File(FileUtils.TEMP_DIRECTORY, s);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getImageDirectory(final String s, final int n) {
        final File file = new File(getImageDirectory(s), String.format("IMG_%03d", n));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getMoveFolderpath(final File file, final String s) {
        final File parentFile = file.getParentFile().getParentFile();
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("/");
        sb.append(file.getName());
        return new File(parentFile, sb.toString());
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File getOutputImageFile() {
        if (!FileUtils.APP_DIRECTORY.exists() && !FileUtils.APP_DIRECTORY.mkdirs()) {
            return null;
        }
        final String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final File app_DIRECTORY = FileUtils.APP_DIRECTORY;
        final StringBuilder sb = new StringBuilder();
        sb.append("IMG_");
        sb.append(format);
        sb.append(".jpg");
        return new File(app_DIRECTORY, sb.toString());
    }

    public static Bitmap getPicFromBytes(final byte[] array) {
        if (array != null) {
            final BitmapFactory.Options bitmapFactory$Options = new BitmapFactory.Options();
            bitmapFactory$Options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(array, 0, array.length, bitmapFactory$Options);
            int round;
            if ((bitmapFactory$Options.outWidth < bitmapFactory$Options.outHeight) ? (bitmapFactory$Options.outHeight >= 1024) : (bitmapFactory$Options.outWidth >= 1024)) {
                round = Math.round(bitmapFactory$Options.outWidth / 1024.0f);
            } else {
                round = 1;
            }
            final BitmapFactory.Options bitmapFactory$Options2 = new BitmapFactory.Options();
            bitmapFactory$Options2.inSampleSize = round;
            return BitmapFactory.decodeByteArray(array, 0, array.length, bitmapFactory$Options2).copy(Bitmap.Config.RGB_565, true);
        }
        return null;
    }

    public static File getRestoreImageDirectory(final File file, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.unlockDirectoryNameImage);
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File getRestoreVideoDirectory(final File file, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.unlockDirectoryNameVideo);
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File[] getStorages() {
        if (FileUtils.mStorageList != null) {
            return FileUtils.mStorageList;
        }
        return getStorge();
    }

    private static File[] getStorge() {
        final ArrayList<File> list = new ArrayList<File>();
        if (FileUtils.rawExternalStorage != null) {
            list.add(new File(FileUtils.rawExternalStorage));
        } else if (FileUtils.mSdCard != null) {
            list.add(FileUtils.mSdCard);
        }
        if (FileUtils.rawSecondaryStoragesStr != null) {
            list.add(new File(FileUtils.rawSecondaryStoragesStr));
        }
        FileUtils.mStorageList = new File[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            FileUtils.mStorageList[i] = (File) list.get(i);
        }
        return FileUtils.mStorageList;
    }

    public static File getThumbnailDirectory(File file, final int n) {
        if (n == 0) {
            file = new File(getHiddenAppDirectory(file), ".thumb/Video/");
        } else {
            file = new File(getHiddenAppDirectory(file), ".thumb/Image/");
        }
        if (file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getVideoDirectory() {
        if (!FileUtils.TEMP_VID_DIRECTORY.exists()) {
            FileUtils.TEMP_VID_DIRECTORY.mkdirs();
        }
        return FileUtils.TEMP_VID_DIRECTORY;
    }

    public static File getVideoFile(final int n) {
        if (!FileUtils.TEMP_VID_DIRECTORY.exists()) {
            FileUtils.TEMP_VID_DIRECTORY.mkdirs();
        }
        return new File(FileUtils.TEMP_VID_DIRECTORY, String.format("vid_%03d.mp4", n));
    }

    public static String humanReadableByteCount(final long n, final boolean b) {
        int n2;
        if (b) {
            n2 = 1000;
        } else {
            n2 = 1024;
        }
        if (n < n2) {
            final StringBuilder sb = new StringBuilder();
            sb.append(n);
            sb.append(" B");
            return sb.toString();
        }
        final double n3 = n;
        final double log = Math.log(n3);
        final double n4 = n2;
        final int n5 = (int) (log / Math.log(n4));
        final StringBuilder sb2 = new StringBuilder();
        String s;
        if (b) {
            s = "kMGTPE";
        } else {
            s = "KMGTPE";
        }
        sb2.append(s.charAt(n5 - 1));
        String s2;
        if (b) {
            s2 = "";
        } else {
            s2 = "i";
        }
        sb2.append(s2);
        return String.format("%.1f %sB", n3 / Math.pow(n4, n5), sb2.toString());
    }

    public static void moveFile(final File file, final File file2) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.renameTo(file2)) {
            return;
        }
        if (!file2.getParentFile().exists()) {
            file2.getParentFile().mkdirs();
        }
        final FileInputStream fileInputStream = new FileInputStream(file);
        final FileOutputStream fileOutputStream = new FileOutputStream(file2);
        final byte[] array = new byte[1024];
        while (true) {
            final int read = fileInputStream.read(array);
            if (read <= 0) {
                break;
            }
            fileOutputStream.write(array, 0, read);
        }
        fileInputStream.close();
        fileOutputStream.close();
        if (file.exists()) {
            file.delete();
        }
    }

    public static void moveFile(final String s, final String s2) throws IOException {
        if (new File(s).renameTo(new File(s2))) {
            return;
        }
        final File file = new File(s);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final FileInputStream fileInputStream = new FileInputStream(s);
        final FileOutputStream fileOutputStream = new FileOutputStream(s2);
        final byte[] array = new byte[1024];
        while (true) {
            final int read = fileInputStream.read(array);
            if (read <= 0) {
                break;
            }
            fileOutputStream.write(array, 0, read);
        }
        fileInputStream.close();
        fileOutputStream.close();
        final File file2 = new File(s);
        if (file2.exists()) {
            file2.delete();
        }
    }

    private static String putPrefixZero(final long n) {
        final StringBuilder sb = new StringBuilder();
        sb.append(n);
        if (sb.toString().length() < 2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("0");
            sb2.append(n);
            return sb2.toString();
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(n);
        return sb3.toString();
    }

    public static char[] readPatternData(final Context context) {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(context.getFilesDir());
            sb.append("/pattern");
            if (new File(sb.toString()).exists()) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(context.getFilesDir());
                sb2.append("/pattern");
                final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(sb2.toString()));
                final char[] array = (char[]) objectInputStream.readObject();
                objectInputStream.close();
                return array;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
