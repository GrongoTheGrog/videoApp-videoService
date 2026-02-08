package com.hugo.video_service.videos.services;


import com.hugo.video_service.comments.services.CommentFacade;
import com.hugo.video_service.common.exceptions.HttpException;
import com.hugo.video_service.evaluations.EvaluationType;
import com.hugo.video_service.evaluations.dto.LikeRequestDto;
import com.hugo.video_service.evaluations.services.EvaluationService;
import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.VideoProgress;
import com.hugo.video_service.videos.dto.UploadVideoDto;
import com.hugo.video_service.videos.dto.VideoProgressDto;
import com.hugo.video_service.videos.dto.VideoProgressRequest;
import com.hugo.video_service.videos.dto.WatchVideoDtoResponse;
import com.hugo.video_service.common.exceptions.ForbiddenException;
import com.hugo.video_service.common.exceptions.NotFoundException;
import com.hugo.video_service.videos.repositories.VideoProgressRepository;
import com.hugo.video_service.videos.utils.S3PathBuilder;
import com.hugo.video_service.videos.utils.TempFileManager;
import com.hugo.video_service.videos.repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
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
    private final VideoProgressRepository videoProgressRepository;
    private final ModelMapper modelMapper;
    private final CloudfrontService cloudfrontService;
    private final CommentFacade commentFacade;
    private final EvaluationService evaluationService;

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
        video.setThumbnailUrl(s3Service.getThumbnailUrl(video.getId()));
        videoRepository.save(video);
        queueService.postUploadVideoEvent(userId, video.getId(), uploadVideoDto, userId + "/" + video.getId());

        TempFileManager.deleteFile(path);

        return video;
    }

    public Video getVideo(String videoId){
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) throw new NotFoundException("Video not found with id: " + videoId);

        return video.get();
    }

    public Page<Video> getVideos(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    public void deleteVideo(String videoId, String userId){
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) return;

        if (!video.get().getUserId().equals(userId))
            throw new ForbiddenException("User must be the owner of the resource.");

        videoRepository.delete(video.get());
        s3Service.deleteVideoByIdAndUserId(videoId, userId);

        commentFacade.deleteCommentsAndRepliesByVideoId(videoId);
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

        List<String> cookieHeaders = cloudfrontService.getCookieHeaders(
                folderPath,
                expiresAt
        );

        return WatchVideoDtoResponse.builder()
                .manifestUrl(manifestUrl)
                .expiresAt(expiresAt)
                .folderUrl(folderUrl)
                .cookies(cookieHeaders)
                .video(video)
                .build();
    }

    public VideoProgressDto patchVideoProgress(
            VideoProgressRequest videoProgressRequest,
            String userId,
            String videoId
    ){
        return videoProgressRepository.findByUserIdAndVideoId(userId, videoId)
                .map(videoProgress -> modelMapper.map(videoProgress, VideoProgressDto.class))
                .orElseGet(() -> {
                   VideoProgress videoProgress = VideoProgress.builder()
                           .lastPositionInSeconds(videoProgressRequest.getLastPositionInSeconds())
                           .userId(userId)
                           .videoId(videoId)
                           .build();

                   videoProgressRepository.save(videoProgress);

                   return modelMapper.map(videoProgress, VideoProgressDto.class);
                });
    }


    public VideoProgressDto getVideoProgress(
            String userId,
            String videoId
    ){
        return videoProgressRepository.findByUserIdAndVideoId(userId, videoId)
                .map(videoProgress -> modelMapper.map(videoProgress, VideoProgressDto.class))
                .orElseThrow(() -> new NotFoundException("Could not find video progress."));
    }

    public void LikeVideo(LikeRequestDto likeRequestDto, String userId, String videoId){
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Could not find content to evaluate."));

        evaluationService.checkAndPersistLike(likeRequestDto, userId, video);
    }
}
