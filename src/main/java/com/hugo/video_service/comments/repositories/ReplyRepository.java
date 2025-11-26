package com.hugo.video_service.comments.repositories;

import com.hugo.video_service.comments.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {
}
