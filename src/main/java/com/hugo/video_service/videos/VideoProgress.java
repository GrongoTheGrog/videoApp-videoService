package com.hugo.video_service.videos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("seekVideos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VideoProgress {

    @Id
    String id;
    String userId;
    String videoId;
    float lastPositionInSeconds;

}
