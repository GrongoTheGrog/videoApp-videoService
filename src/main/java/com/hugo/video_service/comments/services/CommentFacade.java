package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.repositories.CommentRepository;
import com.hugo.video_service.comments.repositories.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    public Page<Comment> getCommentsByVideoId(String videoId, Pageable pageable){
        return commentService.getCommentsByVideoId(videoId, pageable);
    }

    public void deleteCommentsAndRepliesByVideoId(String videoId){
        replyRepository.deleteByVideoId(videoId);
        commentRepository.deleteByVideoId(videoId);
    }
}
