package com.hugo.video_service.comments.repositories;

import com.hugo.video_service.comments.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    public Page<Comment> findByVideoId(String video, Pageable pageable);
}
