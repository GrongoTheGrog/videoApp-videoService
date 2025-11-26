package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;

    public List<Comment> getCommentsByVideoId(String videoId){
        return commentService.getCommentsByVideoId(videoId);
    }
}
