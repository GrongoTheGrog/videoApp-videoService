package com.hugo.video_service.comments.repositories;

import com.hugo.video_service.comments.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findByVideoId(String video, Pageable pageable);

    void deleteByVideoId(String videoId);
}
