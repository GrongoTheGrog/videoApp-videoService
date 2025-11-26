package com.hugo.video_service.videos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WatchVideoDtoResponse {
    private String manifestUrl;
    private String folderUrl;
    private LocalDateTime expiresAt;
}
