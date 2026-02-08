package com.hugo.video_service.videos.dto;

import com.hugo.video_service.videos.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WatchVideoDtoResponse {
    private String manifestUrl;
    private String folderUrl;
    private List<String> cookies;
    private LocalDateTime expiresAt;
    private Video video;
}
