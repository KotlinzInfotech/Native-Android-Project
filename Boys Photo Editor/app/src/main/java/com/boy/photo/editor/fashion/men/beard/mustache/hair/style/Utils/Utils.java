package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import java.io.File;
import java.util.ArrayList;

public class Utils {
    public static final Utils INSTANCE;
    public static long mDeleteFileCount;

    static {
        INSTANCE = new Utils();
        Utils.mDeleteFileCount = 0L;
    }

    public static class Constant {
        public static ArrayList<String> IMAGEALLARY = new ArrayList<>();

        public static void listAllImages(File filepath) {
            File[] files = filepath.listFiles();
            if (files != null) {
                for (int j = files.length - 1; j >= 0; j--) {
                    String ss = files[j].toString();
                    File check = new File(ss);
                    if (check.toString().contains(".jpg")
                            || check.toString().contains(".png")
                            || check.toString().contains(".jpeg")) {
                        IMAGEALLARY.add(ss);
                    }
                    System.out.println(ss);
                }
                return;
            }
        }
    }

    public static boolean deleteFile(final File mFile) {
        boolean idDelete = false;
        if (mFile == null) {
            return idDelete;
        }
        if (mFile.exists()) {
            if (mFile.isDirectory()) {
                final File[] children = mFile.listFiles();
                if (children != null && children.length > 0) {
                    File[] array;
                    for (int length = (array = children).length, i = 0; i < length; ++i) {
                        final File child = array[i];
                        Utils.mDeleteFileCount += child.length();
                        idDelete = deleteFile(child);
                    }
                }
                Utils.mDeleteFileCount += mFile.length();
                idDelete = mFile.delete();
            } else {
                Utils.mDeleteFileCount += mFile.length();
                idDelete = mFile.delete();
            }
        }
        return idDelete;
    }
}
