package com.cybertechinfosoft.photoslideshowwithmusic;

public interface OnProgressReceiver {
    void onImageProgressFrameUpdate(float f);

    void onOverlayingFinish(String str);

    void onProgressFinish(String str);

    void onVideoProgressFrameUpdate(float f);
}
