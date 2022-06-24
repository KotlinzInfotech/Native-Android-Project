package com.cybertechinfosoft.photoslideshowwithmusic.data;

import java.io.Serializable;

public class VideoData implements Serializable {
    private static final long serialVersionUID = 1797860026700764929L;
    public long dateTaken;
    public long videoDuration;
    public String videoFullPath;
    public String videoName;

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String str) {
        this.videoName = str;
    }

    public String getVideoFullPath() {
        return this.videoFullPath;
    }

    public void setVideoFullPath(String str) {
        this.videoFullPath = str;
    }

    public long getVideoDuration() {
        return this.videoDuration;
    }

    public void setVideoDuration(long j) {
        this.videoDuration = j;
    }
}
