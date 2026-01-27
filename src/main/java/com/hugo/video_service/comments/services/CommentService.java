package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import com.hugo.video_service.comments.repositories.CommentRepository;
import com.hugo.video_service.common.User;
import com.hugo.video_service.common.dto.Role;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.common.exceptions.HttpException;
import com.hugo.video_service.common.exceptions.NotFoundException;
import com.hugo.video_service.common.repositories.UserRepository;
import com.hugo.video_service.videos.repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public Page<Comment> getCommentsByVideoId(String videoId, Pageable pageable){
        return commentRepository.findByVideoId(videoId, pageable);
    }

    public Comment createComment(CreateCommentDto createCommentDto, String userId){

        videoRepository.findById(createCommentDto.getVideoId())
                .orElseThrow(() -> new NotFoundException("Unable to find video."));

        User user = userRepository.findById(userId).orElseThrow(() ->
            new HttpException("User making the request does not exist.", HttpStatus.NOT_FOUND)
        );

        Comment comment = Comment.builder()
                .user(user)
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

        if (!userId.equals(comment.getUser().getId()) && !roles.contains(Role.ADMIN)){
            throw new ForbiddenException("An user can't delete another users comment.");
        }

        commentRepository.delete(comment);
    }

    public Comment updateContent(String commentId, String content, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment could not be found."));

        if (!comment.getUser().getId().equals(userId)){
            throw new ForbiddenException("Only the comment's owner can update its contents.");
        }

        comment.setContent(content);
        commentRepository.save(comment);

        return comment;
    }
}
