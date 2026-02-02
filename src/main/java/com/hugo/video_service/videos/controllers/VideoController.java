package com.hugo.video_service.videos.controllers;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.services.CommentFacade;
import com.hugo.video_service.comments.services.CommentService;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.evaluations.dto.LikeRequestDto;
import com.hugo.video_service.videos.VideoProgress;
import com.hugo.video_service.videos.dto.UploadVideoDto;
import com.hugo.video_service.videos.dto.VideoProgressDto;
import com.hugo.video_service.videos.dto.VideoProgressRequest;
import com.hugo.video_service.videos.dto.WatchVideoDtoResponse;
import com.hugo.video_service.videos.services.CloudfrontService;
import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.services.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.OutputKeys;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final CloudfrontService cloudfrontService;
    private final CommentFacade commentFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Video uploadVideo(
            @ModelAttribute UploadVideoDto uploadVideoDto,
            @RequestHeader(name = "user_id", required = true) String userId
            ) throws IOException {
        return videoService.postVideo(uploadVideoDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Video> getVideos(
            Pageable pageable
    ){
        return videoService.getVideos(pageable);
    }

    @GetMapping("/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public Video getVideo(
            @PathVariable String videoId
    ){
        return videoService.getVideo(videoId);
    }

    @DeleteMapping("/{videoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideo(
            @PathVariable String videoId,
            @RequestHeader(name = "user_id", required = true) String userId
    ){
        videoService.deleteVideo(videoId, userId);
    }

    @GetMapping(value = "/{videoId}/watch", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public WatchVideoDtoResponse watchVideo(
            @PathVariable String videoId,
            @RequestHeader(name = "user_id", required = true) String userId
    ){
        return videoService.watchVideo(videoId, userId);
    }

    @GetMapping(value = "/{videoId}/comments", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Page<Comment> getVideoComments (
            @PathVariable String videoId,
            Pageable pageable
            ){
        return commentFacade.getCommentsByVideoId(videoId, pageable);
    }

    @PatchMapping("/{videoId}/progress")
    @ResponseStatus(HttpStatus.OK)
    public VideoProgressDto patchVideoProgress(
            @RequestBody VideoProgressRequest videoProgressRequest,
            @PathVariable String videoId,
            @RequestHeader(name = "user_id", required = true) String userId
            ){

        return videoService.patchVideoProgress(videoProgressRequest, userId, videoId);
    }

    @GetMapping("/{videoId}/progress")
    @ResponseStatus(HttpStatus.OK)
    public VideoProgressDto getVideoProgress(
            @PathVariable String videoId,
            @RequestHeader(name = "user_id", required = true) String userId
    ){
        return videoService.getVideoProgress(userId, videoId);
    }

    @PutMapping("/{videoId}/evaluation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeVideo(
            @PathVariable String videoId,
            @RequestBody LikeRequestDto likeRequestDto,
            @RequestHeader(name = "user_id", required = true) String userId
    ){
        videoService.LikeVideo(likeRequestDto, userId, videoId);
    }

}
