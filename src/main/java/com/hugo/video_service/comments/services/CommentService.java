package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import com.hugo.video_service.comments.repositories.CommentRepository;
import com.hugo.video_service.common.dto.Role;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Page<Comment> getCommentsByVideoId(String videoId, Pageable pageable){
        return commentRepository.findByVideoId(videoId, pageable);
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

    public void deleteComment(String commentId, String userId, List<Role> roles) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) return;

        Comment comment = optionalComment.get();

        if (!userId.equals(comment.getUserId()) && !roles.contains(Role.ADMIN)){
            throw new ForbiddenException("An user can't delete another user's comment.");
        }

        commentRepository.delete(comment);
    }
}
