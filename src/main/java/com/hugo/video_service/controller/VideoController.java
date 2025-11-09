package com.hugo.video_service.controller;

import com.hugo.video_service.domain.Video;
import com.hugo.video_service.dto.UploadVideoDto;
import com.hugo.video_service.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Video uploadVideo(
            @ModelAttribute UploadVideoDto uploadVideoDto,
            @RequestHeader(name = "user-id", required = true) String userId
            ) throws IOException {
        return videoService.postVideo(uploadVideoDto, userId);
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
            @RequestHeader(name = "user-id", required = true) String userId
    ){
        videoService.deleteVideo(videoId, userId);
    }

}
