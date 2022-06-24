package com.cybertechinfosoft.photoslideshowwithmusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class MyVideoView extends VideoView {
    PlayPauseListner listner;

    public interface PlayPauseListner {
        void onVideoPause();

        void onVideoPlay();
    }

    public void setOnPlayPauseListner(PlayPauseListner playPauseListner) {
        this.listner = playPauseListner;
    }

    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void start() {
        super.start();
        if (this.listner != null) {
            this.listner.onVideoPlay();
        }
    }

    public void pause() {
        super.pause();
        if (this.listner != null) {
            this.listner.onVideoPause();
        }
    }
}
