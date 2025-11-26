package com.hugo.video_service.comments.controllers;


import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(
            @Valid CreateCommentDto createCommentDto
            ){

    }
}
