package com.hugo.video_service.videos.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadVideoEvent {

    private String messageId;
    private String videoPath;
    private String videoId;
    private String videoDir;
    private String userId;
    private Integer segmentDuration;
    private List<VideoQuality> videoQualities;
    private LocalDate issuedAt = LocalDate.now();

}
