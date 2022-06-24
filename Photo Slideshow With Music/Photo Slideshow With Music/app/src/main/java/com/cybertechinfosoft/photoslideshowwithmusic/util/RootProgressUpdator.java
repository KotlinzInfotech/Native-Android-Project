package com.cybertechinfosoft.photoslideshowwithmusic.util;

public class RootProgressUpdator {
    GloblePrgListener mListner;

    public void addListener(GloblePrgListener utilsGloblePrgListener) {
        this.mListner = utilsGloblePrgListener;
    }

    public void broadCastPrg(String str, float f) {
        if (this.mListner != null) {
            this.mListner.updateProgress(str, f);
        }
    }

    public void notifyDataChange(String str, String str2) {
        if (this.mListner != null) {
            this.mListner.notifyAdaptor(str, str2);
        }
    }
}
