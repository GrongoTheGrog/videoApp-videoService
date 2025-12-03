package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.repositories.ReplyRepository;
import com.hugo.video_service.common.dto.Role;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    public Page<Reply> getByCommentId(String commentId, Pageable pageable) {
        return replyRepository.findByCommentId(commentId, pageable);
    }

    public Reply createReply(CreateReplyDto createReplyDto, String userId){
        Reply reply = Reply.builder()
                .videoId(createReplyDto.getVideoId())
                .content(createReplyDto.getContent())
                .commentId(createReplyDto.getCommentId())
                .userId(userId)
                .build();

        replyRepository.save(reply);

        log.info("Reply {} created for comment {}", reply.getId(), createReplyDto.getCommentId());

        return reply;
    }

    public Reply getById(String replyId){
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException("Reply could not be found."));
    }

    public void deleteReply(String replyId, String userId, List<Role> userRoles) {
        Optional<Reply> optionalReply = replyRepository.findById(replyId);
        if (optionalReply.isEmpty()) return;

        Reply reply = optionalReply.get();

        if (!userId.equals(reply.getUserId()) && !userRoles.contains(Role.ADMIN)){
            throw new ForbiddenException("An user can't delete another user's comment.");
        }

        replyRepository.delete(reply);

        log.info("Reply {} deleted", reply.getId());
    }

    public Reply updateContent(String replyId, String content, String userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException("Comment could not be found."));

        if (!reply.getUserId().equals(userId)){
            throw new ForbiddenException("Only the reply's owner can update its contents.");
        }

        reply.setContent(content);
        replyRepository.save(reply);

        log.info("Reply {} updated", reply.getId());

        return reply;
    }
}
