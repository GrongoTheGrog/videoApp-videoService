package com.hugo.video_service.comments.controllers;


import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.dto.UpdateCommentReply;
import com.hugo.video_service.comments.services.ReplyService;
import com.hugo.video_service.common.dto.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reply createReply(
            @RequestBody @Valid CreateReplyDto createReplyDto,
            @RequestHeader(name = "user_id", required = true) String userId
    ){
        return replyService.createReply(createReplyDto, userId);
    }

    @GetMapping("/{replyId}")
    @ResponseStatus(HttpStatus.OK)
    public Reply getReplyById(
            @PathVariable String replyId
    ){
        return replyService.getById(replyId);
    }

    @DeleteMapping("/{replyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReplyById(
            @PathVariable String replyId,
            @RequestHeader(name = "user_id", required = true) String userId,
            @RequestHeader(name = "user_roles", required = true) List<Role> userRoles
    ){
        replyService.deleteReply(replyId, userId, userRoles);
    }


    @PatchMapping("/{replyId}")
    @ResponseStatus(HttpStatus.OK)
    public Reply updateReply(
            @PathVariable String replyId,
            @RequestBody UpdateCommentReply updateCommentReply,
            @RequestHeader(name = "user_id", required = true) String userId
    ){
        return replyService.updateContent(replyId, updateCommentReply.getContent(), userId);
    }

}
