package com.hugo.video_service.comments.repositories;

import com.hugo.video_service.comments.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {
    public Page<Reply> findByCommentId(String commentId, Pageable pageable);

    void deleteByVideoId(String videoId);
}
