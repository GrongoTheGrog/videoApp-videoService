package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import com.hugo.video_service.comments.repositories.CommentRepository;
import com.hugo.video_service.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByVideoId(String videoId){
        return commentRepository.findByVideoId(videoId);
    }

    public Comment createComment(CreateCommentDto createCommentDto, String userId){
        Comment comment = Comment.builder()
                .userId(userId)
                .content(createCommentDto.getContent())
                .videoId(createCommentDto.getVideoId())
                .build();

        commentRepository.save(comment);

        return comment;
    }

    public Comment getCommentById(String commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Could not find comment with id: " + commentId));
    }
}
