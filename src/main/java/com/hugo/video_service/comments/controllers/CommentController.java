package com.hugo.video_service.comments.controllers;


import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import com.hugo.video_service.comments.services.CommentService;
import com.hugo.video_service.comments.services.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            @Valid CreateCommentDto createCommentDto
            ){
        return commentService.createComment(createCommentDto);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Comment getCommentById(
            @PathVariable String commentId
    ){
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/{commentId}/replies")
    public List<Reply> getReplies(
            @PathVariable String commentId
    ){
        return replyService.getByCommentId(commentId);
    }
}
