package com.hugo.video_service.videos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

@Repository
@Document("videos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {

    @Id
    private String id;
    private String userId;
    private Long sizeInBytes;
    private String title;
    private Float durationInSeconds;
    private String description;
    private Long views;

    public void addViews(int views){
        this.views += views;
    }
}

