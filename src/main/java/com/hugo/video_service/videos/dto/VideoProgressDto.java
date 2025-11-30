package com.hugo.video_service.videos.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoProgressDto {

    String id;
    String userId;
    String videoId;
    float lastPositionInSeconds;

}
