package com.hugo.video_service.dto;

public enum VideoQuality {
    R4320p(4320),
    R2160p(2160),
    R1080p(1080),
    R720p(720),
    R480p(480);

    private final int resolution;

    VideoQuality(int resolution){
        this.resolution = resolution;
    }


}
