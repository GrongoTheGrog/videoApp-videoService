package com.hugo.video_service.comments.repositories;

import com.hugo.video_service.comments.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {
    public List<Reply> findByCommentId(String commentId);
}
