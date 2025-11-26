package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByVideoId(String videoId){
        return commentRepository.findByVideoId(videoId);
    }

}
