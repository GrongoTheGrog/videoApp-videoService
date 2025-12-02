package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.repositories.ReplyRepository;
import com.hugo.video_service.common.dto.Role;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        return replyRepository.save(reply);
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
    }
}
