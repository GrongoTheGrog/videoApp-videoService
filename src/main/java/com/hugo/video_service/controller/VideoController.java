package com.hugo.video_service.controller;

import com.hugo.video_service.domain.Video;
import com.hugo.video_service.dto.UploadVideoDto;
import com.hugo.video_service.dto.WatchVideoDtoResponse;
import com.hugo.video_service.service.CloudfrontService;
import com.hugo.video_service.service.VideoService;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final CloudfrontService cloudfrontService;

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

    @GetMapping(value = "/{videoId}/watch", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<WatchVideoDtoResponse> watchVideo(
            @PathVariable String videoId,
            @RequestHeader(name = "user-id", required = true) String userId,
            HttpServletResponse res
    ){

        WatchVideoDtoResponse watchVideoDtoResponse = videoService.watchVideo(videoId, userId);

        List<String> cookieHeaders = cloudfrontService.getCookieHeaders(
                watchVideoDtoResponse.getFolderUrl(),
                watchVideoDtoResponse.getExpiresAt()
        );

        for (String cookieHeader : cookieHeaders){
            res.addHeader(HttpHeaders.SET_COOKIE, cookieHeader);
        }

        return ResponseEntity.ok(watchVideoDtoResponse);
    }

}
