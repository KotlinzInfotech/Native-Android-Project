package com.cybertechinfosoft.photoslideshowwithmusic;

import android.view.View;

public interface OnTextStickerListeners {
    void onRemoveHold(View view);

    void onTextFocusChanged(View view);

    void onTextHoldAndMove(View view);

    void onTextRemoved(View view);

    void onTextRemovedAnimation(boolean z);
}
