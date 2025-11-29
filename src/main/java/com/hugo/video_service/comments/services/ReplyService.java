package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.repositories.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
