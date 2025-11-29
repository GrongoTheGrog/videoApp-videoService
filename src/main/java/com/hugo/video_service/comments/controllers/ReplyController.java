package com.hugo.video_service.comments.controllers;


import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.services.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reply createReply(
            @RequestBody @Valid CreateReplyDto createReplyDto,
            @RequestHeader(name = "user-id", required = true) String userId
    ){
        return replyService.createReply(createReplyDto, userId);
    }

}
