package com.hugo.video_service.service;


import com.hugo.video_service.domain.Video;
import com.hugo.video_service.dto.UploadVideoDto;
import com.hugo.video_service.dto.WatchVideoDtoResponse;
import com.hugo.video_service.exceptions.ForbiddenException;
import com.hugo.video_service.exceptions.NotFoundException;
import com.hugo.video_service.exceptions.VideoException;
import com.hugo.video_service.repository.VideoRepository;
import com.hugo.video_service.utils.S3PathBuilder;
import com.hugo.video_service.utils.TempFileManager;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${aws.cloudfront.domain}")
    private String domain;

    private static final Logger log = LogManager.getLogger(VideoService.class);
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final QueueService queueService;
    private final CloudfrontService cloudfrontService;

    @Transactional
    public Video postVideo(
            UploadVideoDto uploadVideoDto,
            String userId
    ) throws IOException {

        Path path = TempFileManager.createFile(".mp4");
        TempFileManager.copyFromTo(uploadVideoDto.getFile().getInputStream(), path);

        float videoDuration = TempFileManager.getVideoDuration(path);

        Video video = Video.builder()
                    .userId(userId)
                    .sizeInBytes(uploadVideoDto.getFile().getSize())
                    .title(uploadVideoDto.getTitle())
                    .views(0L)
                    .durationInSeconds(videoDuration)
                    .description(uploadVideoDto.getDescription())
                    .build();


        videoRepository.save(video);
        s3Service.saveFile(path, userId + "/" + video.getId() + "/mp4");
        queueService.postUploadVideoEvent(userId, uploadVideoDto, userId + "/" + video.getId());

        TempFileManager.deleteFile(path);

        return video;
    }

    public Video getVideo(String videoId){
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) throw new NotFoundException("Video not found with id: " + videoId);

        return video.get();
    }

    public void deleteVideo(String videoId, String userId){
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) return;

        if (!video.get().getUserId().equals(userId))
            throw new ForbiddenException("User must be the owner of the resource.");

        videoRepository.delete(video.get());


        log.info("Video deleted.");
    }


    public WatchVideoDtoResponse watchVideo(String videoId, String userId){
        Optional<Video> optionalVideo = videoRepository.findById(videoId);

        if(optionalVideo.isEmpty()){
            throw new NotFoundException("Could not found video with id: " + videoId);
        }

        Video video = optionalVideo.get();
        video.addViews(1);
        videoRepository.save(video);

        String folderPath = S3PathBuilder.buildPath(video.getUserId(), videoId, "dash");
        String manifestUrl = "https://" + domain + folderPath + "/manifest.mpd";
        String folderUrl = "https://" + domain + folderPath + "/*";

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(video.getDurationInSeconds().intValue() + 1000);

        return WatchVideoDtoResponse.builder()
                .manifestUrl(manifestUrl)
                .expiresAt(expiresAt)
                .folderUrl(folderUrl)
                .build();
    }

}
