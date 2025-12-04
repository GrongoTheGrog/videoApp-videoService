package com.hugo.video_service.comments.services;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.repositories.ReplyRepository;
import com.hugo.video_service.common.User;
import com.hugo.video_service.common.dto.Role;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.common.exceptions.HttpException;
import com.hugo.video_service.common.exceptions.NotFoundException;
import com.hugo.video_service.common.repositories.UserRepository;
import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.services.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final VideoService videoService;
    private final UserRepository userRepository;

    public Page<Reply> getByCommentId(String commentId, Pageable pageable) {
        return replyRepository.findByCommentId(commentId, pageable);
    }

    public Reply createReply(CreateReplyDto createReplyDto, String userId){

        videoService.getVideo(createReplyDto.getVideoId());

        User user = userRepository.findById(userId).orElseThrow(() ->
                new HttpException("User making the request does not exist.", HttpStatus.NOT_FOUND)
        );

        Reply reply = Reply.builder()
                .videoId(createReplyDto.getVideoId())
                .content(createReplyDto.getContent())
                .commentId(createReplyDto.getCommentId())
                .user(user)
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

        if (!userId.equals(reply.getUser().getId()) && !userRoles.contains(Role.ADMIN)){
            throw new ForbiddenException("An user can't delete another user's comment.");
        }

        replyRepository.delete(reply);
    }

    public Reply updateContent(String replyId, String content, String userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException("Comment could not be found."));

        if (!reply.getUser().getId().equals(userId)){
            throw new ForbiddenException("Only the reply's owner can update its contents.");
        }

        reply.setContent(content);
        replyRepository.save(reply);

        return reply;
    }
}
