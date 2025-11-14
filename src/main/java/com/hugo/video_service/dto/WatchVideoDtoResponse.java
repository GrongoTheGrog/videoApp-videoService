package com.hugo.video_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WatchVideoDtoResponse {
    private String manifestUrl;
    private String folderUrl;
    private LocalDateTime expiresAt;
}
