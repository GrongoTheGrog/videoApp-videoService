package com.hugo.video_service.videos.repositories;

import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.VideoProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoProgressRepository extends MongoRepository<VideoProgress, String> {

    Optional<VideoProgress> findByUserIdAndVideoId(
            String userId,
            String videoId
    );
}
