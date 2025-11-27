package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.repositories.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    public List<Reply> getByCommentId(String commentId) {
        return replyRepository.findByCommentId(commentId);
    }
}
