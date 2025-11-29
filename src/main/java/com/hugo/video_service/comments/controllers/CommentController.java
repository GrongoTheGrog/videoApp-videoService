package com.hugo.video_service.comments.controllers;


import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import com.hugo.video_service.comments.services.CommentService;
import com.hugo.video_service.comments.services.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ReplyService replyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @RequestHeader(name = "user-id", required = true) String userId
            ){
        return commentService.createComment(createCommentDto, userId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Comment getCommentById(
            @PathVariable String commentId
    ){
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/{commentId}/replies")
    @ResponseStatus(HttpStatus.OK)
    public Page<Reply> getReplies(
            @PathVariable String commentId,
            Pageable pageable
    ){
        return replyService.getByCommentId(commentId, pageable);
    }
}
